package com.example.mycampus.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycampus.domain.model.PoiModel
import com.example.mycampus.domain.model.LatLng
import com.example.mycampus.domain.usecase.GetCurrentLocationUseCase
import com.example.mycampus.domain.usecase.GetRouteToEsaticUseCase
import com.example.mycampus.data.repository.GeocodeSuggestion
import com.example.mycampus.domain.usecase.SearchPlacesUseCase
import com.example.mycampus.ui.screens.map.RouteUi
import com.example.mycampus.ui.screens.map.toRouteUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mycampus.BuildConfig

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getRouteToEsaticUseCase: GetRouteToEsaticUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase
) : ViewModel() {
    data class MapDataState(
        val userLocation: LatLng? = null,
        val isLoading: Boolean = false,
        val locationError: Boolean = false,
        val esatic: PoiModel = PoiModel("ESATIC", LatLng(5.33964, -4.00827)),
        val library: PoiModel = PoiModel("Bibliothèque", LatLng(5.34000, -4.00850)),
        val routeToDestination: RouteUi? = null,
        val routingError: Boolean = false,
        val query: String = "",
        val suggestions: List<GeocodeSuggestion> = emptyList(),
        val searching: Boolean = false,
        val searchError: Boolean = false,
        val selectedDestination: GeocodeSuggestion? = null
    )

    private val _dataState = MutableStateFlow(MapDataState())
    val dataState: StateFlow<MapDataState> = _dataState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _dataState.value = _dataState.value.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _dataState.value = _dataState.value.copy(searching = true, searchError = false)
            delay(350)
            try {
                val results = searchPlacesUseCase(newQuery)
                _dataState.value = _dataState.value.copy(
                    searching = false,
                    suggestions = results,
                    searchError = false
                )
            } catch (e: Exception) {
                Log.e("MapViewModel", "Erreur de recherche", e)
                _dataState.value = _dataState.value.copy(
                    searching = false,
                    searchError = true,
                    suggestions = emptyList()
                )
            }
        }
    }

    fun onSuggestionClick(s: GeocodeSuggestion) {
        _dataState.value = _dataState.value.copy(
            selectedDestination = s,
            query = s.title,
            suggestions = emptyList()
        )
        val user = _dataState.value.userLocation ?: return
        viewModelScope.launch {
            try {
                val route = getRouteToEsaticUseCase(
                    apiKey = BuildConfig.ORS_API_KEY,
                    userLocation = user,
                    esatic = s.position
                )
                _dataState.value = _dataState.value.copy(
                    routeToDestination = route?.toRouteUi(),
                    routingError = route == null
                )
            } catch (e: Exception) {
                Log.e("MapViewModel", "Erreur de routage", e)
                _dataState.value = _dataState.value.copy(
                    routeToDestination = null,
                    routingError = true
                )
            }
        }
    }

    fun onSearchSubmit() {
        val first = _dataState.value.suggestions.firstOrNull() ?: return
        onSuggestionClick(first)
    }

    fun loadUserLocation() {
        Log.d("MapViewModel", "Début de loadUserLocation()")
        viewModelScope.launch {
            val shouldShowLoader =
                _dataState.value.locationError || _dataState.value.userLocation == null
            if (shouldShowLoader) {
                _dataState.value = _dataState.value.copy(
                    locationError = false,
                    isLoading = true
                )
            }
            try {
                val location: LatLng? = getCurrentLocationUseCase()
                if (location != null) {
                    _dataState.value = _dataState.value.copy(
                        isLoading = false,
                        userLocation = location,
                        locationError = false
                    )
                } else {
                    _dataState.value = _dataState.value.copy(
                        isLoading = false,
                        locationError = true,
                        userLocation = null
                    )
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Erreur de localisation", e)
                _dataState.value = _dataState.value.copy(
                    isLoading = false,
                    locationError = true,
                    userLocation = null
                )
            }
        }
    }
}
