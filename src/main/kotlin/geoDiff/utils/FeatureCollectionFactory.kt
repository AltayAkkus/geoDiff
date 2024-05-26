package dev.altayakkus.geoDiff.utils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import dev.altayakkus.geoDiff.*

object FeatureCollectionFactory {
    private val objectMapper = ObjectMapper()

    fun fromGeoJson(geoJsonFile: File): FeatureCollection {
        val geoJsonString = geoJsonFile.readText()
        return fromGeoJsonString(geoJsonString)
    }

    fun fromGeoJsonString(geoJsonString: String): FeatureCollection {
        val geoJsonObject = objectMapper.readValue<Map<String, Any>>(geoJsonString)

        val type = geoJsonObject["type"] as? String
        if (type != "FeatureCollection") {
            throw IllegalArgumentException("Input is not a FeatureCollection GeoJSON")
        }

        val features = (geoJsonObject["features"] as? List<Map<String, Any>>)?.map { featureJson ->
            val properties = featureJson["properties"] as? Map<String, Any> ?: mapOf()
            val geometryJson = featureJson["geometry"] as? Map<String, Any>
            val geometry = geometryJson?.let { parseGeometry(it) }
                ?: throw IllegalArgumentException("Geometry is missing or invalid")
            Feature(properties = properties, geometry = geometry)
        } ?: emptyList()

        return FeatureCollection(type = type, features = features)
    }

    private fun parseGeometry(geometryJson: Map<String, Any>): Geometry {
        val type = geometryJson["type"] as? String
        val coordinates = geometryJson["coordinates"]
        return when (type) {
            "Polygon" -> {
                val polygonCoordinates = coordinates as? List<List<List<Double>>>
                    ?: throw IllegalArgumentException("Invalid Polygon coordinates")
                Geometry.Polygon(coordinates = polygonCoordinates)
            }
            "LineString" -> {
                val lineStringCoordinates = coordinates as? List<List<Double>>
                    ?: throw IllegalArgumentException("Invalid LineString coordinates")
                Geometry.LineString(coordinates = lineStringCoordinates)
            }
            else -> throw IllegalArgumentException("Unsupported geometry type: $type")
        }
    }
}
