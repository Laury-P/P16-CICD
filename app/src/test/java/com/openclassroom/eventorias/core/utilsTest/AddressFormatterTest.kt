package com.openclassroom.eventorias.core.utilsTest

import com.openclassroom.eventorias.core.utils.toAPIUrl
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class AddressFormatterTest {

    @ParameterizedTest(name = "Adresse originale: \"{0}\"")
    @MethodSource("provideAddressesForEncoding")
    fun `given various addresses, when toAPIUrl is called, then URL contains correctly encoded address`(
        inputAddress: String
    ) {
        // Act
        val resultUrl = inputAddress.toAPIUrl()

        // Assert
        // 1. On vérifie que l'URL ne contient plus d'espaces bruts (signe que l'encodage a agi)
        assertFalse(
            resultUrl.contains(" "),
            "L'URL contient encore des espaces bruts non encodés : $resultUrl"
        )

        // 2. On s'assure que les paramètres essentiels 'center' et 'key' sont bien présents dans l'URL
        assertTrue(resultUrl.contains("center="), "Le paramètre center est manquant")
        assertTrue(resultUrl.contains("&key="), "Le paramètre key est manquant")
    }

    @ParameterizedTest(name = "Zoom: {0}, Size: \"{1}\", MapType: \"{2}\"")
    @MethodSource("provideMapConfigurations")
    fun `given specific configurations, when toAPIUrl is called, then URL contains correct parameters`(
        zoom: Int,
        size: String,
        mapType: String
    ) {
        // Arrange
        val address = "Paris"

        // Act
        val resultUrl = address.toAPIUrl(zoom = zoom, size = size, mapType = mapType)

        // Assert
        assertTrue(resultUrl.contains("zoom=$zoom"), "Le paramètre zoom est incorrect")
        assertTrue(resultUrl.contains("size=$size"), "Le paramètre size est incorrect")
        assertTrue(resultUrl.contains("maptype=$mapType"), "Le paramètre maptype est incorrect")
    }

    companion object {
        // Fournit un jeu de données de test pour valider l'encodage des caractères spéciaux
        @JvmStatic
        fun provideAddressesForEncoding(): Stream<String> {
            return Stream.of(
                "1 rue de la paix",
                "10 Avenue des Champs-Élysées, Paris",
                "R&D Center, London",
                "Chambre #4, Rue de l'Église"
            )
        }

        // Fournit un jeu de données pour tester la flexibilité des configurations de la carte
        @JvmStatic
        fun provideMapConfigurations(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(12, "340x144", "roadmap"),
                Arguments.of(15, "600x300", "satellite"),
                Arguments.of(18, "170x72", "hybrid")
            )
        }
    }
}