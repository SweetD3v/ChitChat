package com.demo.chitchat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ThemeUiState(
    val currentPos: Int = 0
)

class ThemeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    fun updatePosition(position: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentPos = position
            )
        }
    }
}