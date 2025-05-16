package com.pperotti.android.sparq.demoapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pperotti.android.sparq.demoapp.data.items.ItemRepository
import com.pperotti.android.sparq.demoapp.data.items.ItemListResult
import com.pperotti.android.sparq.demoapp.data.common.DataResponse

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: ItemRepository
) : ViewModel() {

    // A Job is required so you can cancel a running coroutine
    private var fetchJob: Job? = null

    // StateFlow to hold the UI state
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> get() = _uiState

    // Request items from repository and convert them to UI items
    fun requestData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {

            // Indicates the UI that loading should be presented
            _uiState.value = MainUiState.Loading

            when (val response = repository.fetchItemList()) {
                is DataResponse.Success ->
                    transformSuccessResponse(response.result)

                is DataResponse.Error -> {
                    _uiState.value = MainUiState.Error(
                        response.message
                    )
                }
            }
        }
    }

    private fun transformSuccessResponse(itemListResult: ItemListResult) {
        val resultList: MutableList<MainListItemUiState> = mutableListOf()
        itemListResult.items.forEach { item ->
            resultList.add(
                MainListItemUiState(
                    id = item.id,
                    title = item.title ?: "",
                    description = item.description ?: ""
                )
            )
        }

        // Publish Items to the UI
        _uiState.value = MainUiState.Success(items = resultList)
    }
}
