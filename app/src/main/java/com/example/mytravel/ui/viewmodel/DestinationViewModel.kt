package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.DestinationRepository
import com.example.mytravel.domain.model.Destination
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DestinationViewModel (
    private val repo: DestinationRepository = DestinationRepository()
) : ViewModel() {
    private val _destinations = MutableStateFlow<UiResult<List<Destination>>>(UiResult.Loading)
    val destinations: StateFlow<UiResult<List<Destination>>> = _destinations

    private val _destinationDetail = MutableStateFlow<UiResult<Destination>>(UiResult.Loading)
    val destinationDetail: StateFlow<UiResult<Destination>> = _destinationDetail

    fun loadDestinations() {
        _destinations.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val list = repo.fetchDestinations()
                _destinations.value = UiResult.Success(list)
            } catch (e: Exception) {
                _destinations.value = UiResult.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    fun getDestination(id: Long){
        _destinationDetail.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val result = repo.getDestination(id)
                if (result != null) {
                    _destinationDetail.value = UiResult.Success(result)
                } else {
                    _destinationDetail.value = UiResult.Error("Data tidak ditemukan")
                }
            } catch (e: Exception) {
                _destinationDetail.value = UiResult.Error(e.message ?: "Gagal memuat detail")
            }
        }
    }
}