package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.PlanRepository
import com.example.mytravel.domain.model.Plan
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PlanViewModel(
    private val repo: PlanRepository = PlanRepository()
) : ViewModel() {

    private val _plans = MutableStateFlow<UiResult<List<Plan>>>(UiResult.Loading)
    val plans: StateFlow<UiResult<List<Plan>>> = _plans

    private val _adding = MutableStateFlow<UiResult<Plan>?>(null)
    val adding: StateFlow<UiResult<Plan>?> = _adding

    private val _planDetail = MutableStateFlow<UiResult<Plan>?>(null)
    val planDetail: StateFlow<UiResult<Plan>?> = _planDetail

    fun loadPlans() {
        _plans.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val list = repo.fetchPlans()
                _plans.value = UiResult.Success(list)
            } catch (e: Exception) {
                _plans.value = UiResult.Error(e.message ?: "Gagal memuat")
            }
        }
    }

    fun addPlan(
        destinationId: Long,
        title: String,
        description: String,
        imageFile: File?
    ) {
        _adding.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val plan = repo.addPlan(destinationId, title, description, imageFile)
                _adding.value = UiResult.Success(plan)
                loadPlans()
            } catch (e: Exception) {
                _adding.value = UiResult.Error(e.message ?: "Gagal menambah rencana")
            }
        }
    }

    fun loadPlanDetail(id: Long) {
        _planDetail.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val result = repo.getPlan(id)
                if (result != null) {
                    _planDetail.value = UiResult.Success(result)
                } else {
                    _planDetail.value = UiResult.Error("Plan tidak ditemukan")
                }
            } catch (e: Exception) {
                _planDetail.value = UiResult.Error(e.message ?: "Gagal memuat detail plan")
            }
        }
    }

    suspend fun getPlan(id: Long): Plan? = repo.getPlan(id)

    fun resetAddingState() {
        _adding.value = null
    }
}