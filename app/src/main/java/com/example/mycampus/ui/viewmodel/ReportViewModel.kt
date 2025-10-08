package com.example.mycampus.ui.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycampus.data.repository.ReportRepository
import com.example.mycampus.domain.model.MediaType
import com.example.mycampus.domain.model.Report
import com.example.mycampus.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository,
    private val locationHelper: LocationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            repository.getAllReports().collect { reportList ->
                _reports.value = reportList
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null) }
    }

    fun setMediaUri(uri: Uri?, mediaType: MediaType) {
        _uiState.update {
            it.copy(
                mediaUri = uri,
                mediaType = mediaType,
                showMediaPreview = uri != null
            )
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, locationError = null) }
            try {
                val location = locationHelper.getCurrentLocation()

                if (location != null) {
                    _uiState.update {
                        it.copy(
                            currentLocation = location,
                            isLoadingLocation = false,
                            locationError = null
                        )
                    }
                } else {
                    // Si getCurrentLocation retourne null, essayer getLastKnownLocation
                    val lastLocation = locationHelper.getLastKnownLocation()
                    if (lastLocation != null) {
                        _uiState.update {
                            it.copy(
                                currentLocation = lastLocation,
                                isLoadingLocation = false,
                                locationError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoadingLocation = false,
                                locationError = "Impossible de récupérer la localisation. Vérifiez que le GPS est activé."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingLocation = false,
                        locationError = "Erreur de localisation: ${e.message}"
                    )
                }
            }
        }
    }

    fun submitReport() {
        viewModelScope.launch {
            if (!validateForm()) {
                return@launch
            }

            _uiState.update { it.copy(isSubmitting = true) }

            try {
                val currentState = _uiState.value
                val location = currentState.currentLocation

                if (location == null) {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            locationError = "Localisation requise"
                        )
                    }
                    return@launch
                }

                val mediaPath = currentState.mediaUri?.toString()

                val report = Report(
                    title = currentState.title,
                    description = currentState.description,
                    mediaPath = mediaPath,
                    mediaType = currentState.mediaType,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                repository.insertReport(report)

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSubmitted = true,
                        showSuccessDialog = true
                    )
                }

                resetForm()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        generalError = "Erreur lors de la soumission: ${e.message}"
                    )
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val currentState = _uiState.value
        var isValid = true

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Le titre est requis") }
            isValid = false
        } else if (currentState.title.length < 3) {
            _uiState.update { it.copy(titleError = "Le titre doit contenir au moins 3 caractères") }
            isValid = false
        }

        if (currentState.description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "La description est requise") }
            isValid = false
        } else if (currentState.description.length < 10) {
            _uiState.update { it.copy(descriptionError = "La description doit contenir au moins 10 caractères") }
            isValid = false
        }

        if (currentState.currentLocation == null) {
            _uiState.update { it.copy(locationError = "La localisation est requise") }
            isValid = false
        }

        return isValid
    }

    fun resetForm() {
        _uiState.value = ReportUiState()
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false) }
    }

    fun dismissMediaPreview() {
        _uiState.update { it.copy(showMediaPreview = false) }
    }
}

data class ReportUiState(
    val title: String = "",
    val description: String = "",
    val mediaUri: Uri? = null,
    val mediaType: MediaType = MediaType.NONE,
    val currentLocation: Location? = null,
    val isLoadingLocation: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val showMediaPreview: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val locationError: String? = null,
    val generalError: String? = null
)
