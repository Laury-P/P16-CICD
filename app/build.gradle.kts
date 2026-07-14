import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.secrets.gradle.plugin)
    id("jacoco")
}

android {
    val keystoreFile = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "../P16_eventorias")
    val storePass = System.getenv("ANDROID_STORE_PASS") ?: ""
    val keyAlias = System.getenv("ANDROID_RELEASE_KEY_ALIAS") ?: ""
    val keyPass = System.getenv("ANDROID_RELEASE_KEY_PASS") ?: ""

    signingConfigs {
        if (keystoreFile.exists() && storePass.isNotEmpty() && keyAlias.isNotEmpty() && keyPass.isNotEmpty()) {
            create("release") {
                storeFile = keystoreFile
                storePassword = storePass
                this.keyAlias = keyAlias
                keyPassword = keyPass
            }
        }
    }
    namespace = "com.openclassroom.eventorias"
    //noinspection GradleDependency
    compileSdk = 36

    defaultConfig {
        applicationId = "com.openclassroom.eventorias"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            // securisation et obfuscation du code
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig =
            true // Permet de générer les classes qui permettent d'acceder à l'API Key dans local.properties
    }

    packaging {
        resources {
            // Exclut les fichiers de licence de JUnit qui provoquent le conflit
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"

            // Par sécurité, on peut aussi ajouter ces exclusions classiques souvent liées à JUnit 5 / MockK
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/NOTICE"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "Reporting"
    description = "Generate Jacoco coverage reports for Debug build"

    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*Hilt*.*",
        "**/hilt_aggregated_deps/**",
        "**/*_Factory.class",
        "**/*_MembersInjector.class",
        "**/*_ModuleNameHolder_*.class",
        "**/*_HiltModules*.*",
        "**/Dagger*.*",
        "**/Hilt*.*",
        "**/*_Provide*Factory*.*",
        "**/BR.class",
        "**/DataBinderMapperImpl*.*",
        "**/DataBindingInfo.*",
        "**/databinding/*Binding.*",
        "**/ComposableSingletons$*.*",
        "**/AnimatedVisibilityScope$*.*",
        "**/AnimatedContent$*.*",
        "**/Navigation*.*",
        "**/*_GeneratedInjector.class",
        "**/*_HiltModules_*.class"
    )

    val kotlinClasses = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val javaClasses = fileTree("${project.layout.buildDirectory.get()}/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
        exclude(fileFilter)
    }

    sourceDirectories.setFrom(
        files(
            "${project.projectDir}/src/main/java",
            "${project.projectDir}/src/main/kotlin"
        )
    )

    classDirectories.setFrom(files(kotlinClasses, javaClasses))

    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            "outputs/code_coverage/debugAndroidTest/connected/**/coverage.ec" // Utilisation de ** au cas où le nom du dossier de l'émulateur change sur GitHub
        )
    })
}

dependencies {

    // --- NOYAU & UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.accompanist.permissions)

    // --- JETPACK COMPOSE (Géré par la BOM, pas de version individuelle à écrire) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.icons.extended)
    implementation(libs.androidx.compose.ui.text.google.fonts)

    // Outil pour voir tes aperçus d'écrans dans Android Studio (uniquement en mode debug)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // --- MVVM & INJECTION DE DÉPENDANCES ---
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // KSP génère le code Hilt à la compilation
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.hilt.navigation.compose)

    // --- DATASTORE ---
    implementation(libs.datastore)

    // --- SERVICES EXTERNES (Firebase & Images) ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.coil.compose)

    // --- 🧪 TESTS UNITAIRES (Dossier test/ - JUnit 5 & MockK) ---
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.junit.platform.launcher)

    // --- 🧪 TESTS D'INTÉGRATION & UI (Dossier androidTest/ - JUnit 4 & Compose UI) ---
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.espresso.intent)

    // Requis pour inspecter l'arborescence Compose pendant les tests d'UI
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

secrets {
    defaultPropertiesFileName = "local.properties"
}

sonar {
    properties {

        val jacocoReportPath = "${layout.buildDirectory.get().asFile.absolutePath}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        property("sonar.coverage.jacoco.xmlReportPaths", jacocoReportPath)

        property("sonar.exclusions", "**/mipmap*/*.webp, **/mipmap*/*.png, **/res/**/*")
    }
}
