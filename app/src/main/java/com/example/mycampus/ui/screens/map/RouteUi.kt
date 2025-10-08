// CHEMIN : app/src/main/java/com/example/mycampus/ui/screens/map/RouteUi.kt
package com.example.mycampus.ui.screens.map

import com.example.mycampus.domain.model.RouteSegment
import org.osmdroid.util.GeoPoint

data class RouteUi(
    val geoPoints: List<GeoPoint>
)

fun RouteSegment.toRouteUi(): RouteUi {
    return RouteUi(
        geoPoints = points.map { GeoPoint(it.latitude, it.longitude) }
    )
}
