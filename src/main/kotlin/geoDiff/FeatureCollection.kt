package dev.altayakkus.geoDiff

import org.locationtech.jts.geom.GeometryFactory

data class FeatureCollection(val type: String = "FeatureCollection", val features: List<Feature> = ArrayList()) {
    fun toJts(): org.locationtech.jts.geom.GeometryCollection {
        return featuresToGeometryCollection(features)
    }

    private fun featuresToGeometryCollection(features: List<Feature>): org.locationtech.jts.geom.GeometryCollection {
        val factory = GeometryFactory()
        val polygons = mutableListOf<org.locationtech.jts.geom.Geometry>()
        for (feature in features) {
            // TODO: This is trash, honestly.
            val geometry = when (feature.geometry) {
                is dev.altayakkus.geoDiff.Geometry.Polygon -> feature.geometry.toJts()
                is dev.altayakkus.geoDiff.Geometry.LineString -> feature.geometry.toJts()
            }
            polygons.add(geometry)
        }
        return factory.createGeometryCollection(polygons.toTypedArray())
    }
}