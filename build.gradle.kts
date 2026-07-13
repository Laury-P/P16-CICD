// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    // Le moteur principal de l'application Android (AGP)
    alias(libs.plugins.android.application) apply false

    // Les plugins Kotlin pour compiler le code et l'interface Compose
    alias(libs.plugins.kotlin.compose) apply false

    // Les services Google (indispensable pour lier Firebase à ton projet)
    alias(libs.plugins.google.services) apply false

    // L'injection de dépendances (Hilt) et son outil d'analyse (KSP)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false

    // Pour recupérer la clé API dans le fichier local.properties
    alias(libs.plugins.secrets.gradle.plugin) apply false

    id("org.sonarqube") version "7.3.1.8318"
}

sonar {
    properties {
        property("sonar.organization", "laury-p")
        property("sonar.projectKey", "Laury-P_P16-CICD")
        property("sonar.projectName", "Eventorias_CICD")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")

        val sonarToken = System.getenv("SONAR_TOKEN") ?: ""
        property("sonar.token", sonarToken)

    }
}
allprojects {
    dependencyLocking {
        lockAllConfigurations()
    }
}
