package com.example.mycampus.data.repository



import com.example.mycampus.data.remote.NominatimApi
import com.example.mycampus.domain.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

data class GeocodeSuggestion(
    val title: String,
    val position: LatLng
)

@Singleton
class GeocodingRepository @Inject constructor(
    private val api: NominatimApi
) {
    suspend fun search(query: String, limit: Int = 5): List<GeocodeSuggestion> {
        if (query.isBlank()) return emptyList()
        return api.search(query = query, limit = limit).map {
            GeocodeSuggestion(
                title = it.display_name,
                position = LatLng(latitude = it.lat.toDouble(), longitude = it.lon.toDouble())
            )
        }
    }
}
