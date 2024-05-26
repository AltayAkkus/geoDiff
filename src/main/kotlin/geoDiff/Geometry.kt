package dev.altayakkus.geoDiff
import dev.altayakkus.geoDiff.enums.GeometryType
import dev.altayakkus.geoDiff.utils.Hashing
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LinearRing

sealed class Geometry() {
    abstract val type: GeometryType

    data class Polygon(val coordinates: List<List<List<Double>>>) : Geometry() {
        init {
            for (ring in coordinates) {
                if (ring.size < 4) {
                    throw IllegalArgumentException("A polygon must have at least 4 coordinates.")
                }

                if (ring.first() != ring.last()) {
                    throw IllegalArgumentException("The first and last coordinates must be the same.")
                }

                val uniqueCoords = ring.dropLast(1).toSet()
                if (uniqueCoords.size != ring.size - 1) {
                    throw IllegalArgumentException("There should be no duplicate coordinates except the first and last.")
                }
            }
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Geometry.Polygon) return false

            // TODO: Support exterior ring items (RFC 7946 3.1.6). Now we throw them away.
            return Hashing.reorderCoordinates(other.coordinates.flatten()) == Hashing.reorderCoordinates(coordinates.flatten())
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            // Consistent, area-aware reordering
            // TODO: Support exterior ring items (RFC 7946 3.1.6). Now we throw them away.
            val reorderedCoords = Hashing.reorderCoordinates(coordinates.flatten())

            for (coordinate in reorderedCoords) {
                val coordinateHash = coordinate.hashCode()
                result = 31 * result + coordinateHash
            }

            return result
        }

        fun toJts(): org.locationtech.jts.geom.Geometry {
            val geometryFactory = GeometryFactory()

            val jtsCoordinates = coordinates.flatten().map { coord ->
                Coordinate(coord[0], coord[1])
            }.toTypedArray()

            val shell: LinearRing = geometryFactory.createLinearRing(jtsCoordinates)

            return geometryFactory.createPolygon(shell, null)
        }

        override val type: GeometryType = GeometryType.Polygon
    }

    data class LineString(val coordinates: List<List<Double>>) : Geometry() {
        init {
            if (coordinates.size < 2) {
                throw IllegalArgumentException("A line string must have at least 2 coordinates.")
            }

            val uniqueCoords = coordinates.toSet()
            if (uniqueCoords.size != coordinates.size) {
                throw IllegalArgumentException("There should be no duplicate coordinates.")
            }
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Geometry.LineString) return false

            return Hashing.reorderCoordinates(other.coordinates, lineString = true) == Hashing.reorderCoordinates(coordinates, lineString = true)
        }
        override fun hashCode(): Int {
            var result = type.hashCode()
            // Consistent, area-aware reordering
            val reorderedCoords = Hashing.reorderCoordinates(coordinates, lineString = true)

            for (coordinate in reorderedCoords) {
                val coordinateHash = coordinate.hashCode()
                result = 31 * result + coordinateHash
            }

            return result
        }

        fun toJts(): org.locationtech.jts.geom.Geometry {
            val geometryFactory = GeometryFactory()

            val jtsCoordinates = coordinates.map { coord ->
                Coordinate(coord[0], coord[1])
            }.toTypedArray()

            return geometryFactory.createLineString(jtsCoordinates)
        }

        override val type: GeometryType = GeometryType.LineString
    }

}