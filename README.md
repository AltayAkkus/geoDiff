# GeoDiff
A simple geoJson package

## Features:
* Import GeoJSON files
* Perform [geometry-aware hashing](https://altayakkus.dev/2024/05/26/geometry-aware-hashing/)
* Export as `com.locationtech.jts.geom.Geometry` or `GeometryCollection`


## Examples
### [Geometry-aware hashing](https://altayakkus.dev/2024/05/26/geometry-aware-hashing/)
```kotlin
package dev.altayakkus.geoDiff

fun main() {
    val a = listOf(34.21, 2.78)
    val b = listOf(3458.32, 2131.23)
    val c = listOf(0.0, 23.11)
    val d = listOf(0.3432, 3.0)

    val polygonCoordinates1 = listOf(
        listOf(a, b, c, d, a)
    )
    val polygonCoordinates2 = listOf(
        listOf(c, d, a, b, c)
    )

    val polygonGeometry1 = Geometry.Polygon(polygonCoordinates1)
    val polygonGeometry2 = Geometry.Polygon(polygonCoordinates2)
    println(polygonGeometry1 == polygonGeometry2)
    // outputs: true


    val lineStringCoordinates1 = listOf(
        a, b, c, d
    )
    val lineStringCoordinates2 = listOf(
        d, c, b, a
    )

    val lineStringGeometry1 = Geometry.LineString(lineStringCoordinates1)
    val lineStringGeometry2 = Geometry.LineString(lineStringCoordinates2)
    println(lineStringGeometry1 == lineStringGeometry2)
    // outputs: true
}
```
### Export to JTS format

```kotlin
package dev.altayakkus.geoDiff

fun main() {
    val a = listOf(34.21, 2.78)
    val b = listOf(3458.32, 2131.23)
    val c = listOf(0.0, 23.11)
    val d = listOf(0.3432, 3.0)

    val polygonCoordinates1 = listOf(
        listOf(a, b, c, d, a)
    )


    val polygonGeometry1 = Geometry.Polygon(polygonCoordinates1)
    println(polygonGeometry1.toJts())
    // Outputs: com.LocationTech Geometry object
}
```
### Compute overlay (efficiently)
```kotlin
package dev.altayakkus.geoDiff
import dev.altayakkus.geoDiff.utils.FeatureCollectionFactory
import org.locationtech.jts.operation.overlay.OverlayOp

fun main() {
  val collection1 = FeatureCollectionFactory.fromGeoJsonString("...")
  val collection2 = FeatureCollectionFactory.fromGeoJsonString("...")
  
  // reduce your comparison set, by using hashmaps etc.
  
  val geometryCollection1 = collection1.toJts()
  val geometryCollection2 = collection2.toJts()
  
  // Choose OverlayOp.DIFFERENCE, OverlayOp.INTERSECTION, OverlayOp.UNION or OverlayOp.SYMDIFFERENCE
  val overlay = OverlayOp.overlayOp(geometryCollection1, geometryCollection2, OverlayOp.DIFFERENCE)
  println(overlay)
  // you can turn this overlay back into GeoJSON again,
  // and now you have computed the difference/intersection/union between two FeatureCollections
}
```
