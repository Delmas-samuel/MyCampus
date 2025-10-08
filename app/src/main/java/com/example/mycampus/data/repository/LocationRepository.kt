package com.example.mycampus.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.mycampus.domain.model.LatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    context: Context
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng? {
        // Step 1: lastLocation (fast, cached)
        try {
            val lastLocation = fusedClient.lastLocation.await()
            if (lastLocation != null) {
                Log.d(
                    "LocationRepository",
                    "Récupéré via lastLocation: ${lastLocation.latitude}, ${lastLocation.longitude}"
                )
                return LatLng(lastLocation.latitude, lastLocation.longitude)
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Erreur avec lastLocation", e)
        }

        Log.d("LocationRepository", "lastLocation est null, demande de getCurrentLocation...")
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val currentLocation = fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (currentLocation != null) {
                Log.d(
                    "LocationRepository",
                    "Récupéré via getCurrentLocation: ${currentLocation.latitude}, ${currentLocation.longitude}"
                )
                LatLng(currentLocation.latitude, currentLocation.longitude)
            } else {
                Log.d("LocationRepository", "Aucune position trouvée, fallback sur ESATIC")
                LatLng(5.33964, -4.00827)
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Erreur en demandant la position actuelle", e)
            LatLng(5.33964, -4.00827)
        }
    }
}
