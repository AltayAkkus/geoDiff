package dev.altayakkus.geoDiff

data class Feature(
    val type: String = "Feature",
    val properties: Map<String, Any>,
    val geometry: Geometry
)
