package com.example.mycampus.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.mycampus.domain.model.News
import com.example.mycampus.domain.usecase.GetNewsUseCase
import com.example.mycampus.domain.usecase.RefreshNewsUseCase
import javax.inject.Inject

data class NewsState(
    val news: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val refreshNewsUseCase: RefreshNewsUseCase // 1. Injecter le nouveau UseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val newsState: StateFlow<NewsState> = _state.asStateFlow()

    init {

        loadNews(forceRefresh = true) // 2. Appeler avec un rafraîchissement
    }

    fun loadNews(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // Collecter le Flow de la BDD pour avoir les mises à jour en temps réel
            getNewsUseCase()
                .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                .catch { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
                .collect { news ->
                    _state.update { it.copy(isLoading = false, news = news) }
                }
        }

        // Si un rafraîchissement est demandé, appeler l'API
        if (forceRefresh) {
            viewModelScope.launch {
                try {
                    refreshNewsUseCase()
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.message ?: "Erreur de connexion") }
                }
            }
        }
    }
}
