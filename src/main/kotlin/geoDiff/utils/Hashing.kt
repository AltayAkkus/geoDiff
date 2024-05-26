package dev.altayakkus.geoDiff.utils

class Hashing {
    companion object {
        fun reorderCoordinates(coordinates: List<List<Double>>, lineString: Boolean = false): List<List<Double>> {
            if (coordinates.isEmpty()) return coordinates

            var coordinateSet = coordinates
            // If we have a line string, we need to choose the canonical coordinate from the first and last coordinates
            if (lineString) {
                coordinateSet = listOf(coordinates.first(), coordinates.last())
            }

            // Find the canonical coordinate
            var minCoordinate = coordinateSet.first()
            for (coordinate in coordinateSet) {
                if (coordinate[0] < minCoordinate[0]) {
                    minCoordinate = coordinate
                } else if (coordinate[0] == minCoordinate[0] && coordinate[1] < minCoordinate[1]) {
                    minCoordinate = coordinate
                }
            }

            // Reorder the coordinates so the canonical coordinate is first
            return if (lineString) {
                // Reverse the list if the last coordinate is the canonical one
                if (minCoordinate == coordinates.last()) {
                    coordinates.reversed()
                } else {
                    coordinates
                }
            } else {
                // Rotate the list for polygons
                val index = coordinates.indexOf(minCoordinate)
                val reordered = coordinates.subList(index, coordinates.size - 1) + coordinates.subList(0, index)
                // Ensure the list is closed by appending the canonical coordinate at the end
                if (reordered.last() != reordered.first()) {
                    reordered + listOf(reordered.first())
                } else {
                    reordered
                }
            }
        }
    }
}