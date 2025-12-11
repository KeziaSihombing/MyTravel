package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.PlanRepository
import com.example.mytravel.domain.model.Rencana
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PlanViewModel(private val planRepository: PlanRepository = PlanRepository()) : ViewModel() {

    // For the main list of plans
    private val _planList = MutableStateFlow<UiResult<List<Rencana>>>(UiResult.Loading)
    val planList: StateFlow<UiResult<List<Rencana>>> = _planList

    // For the detail of a single plan
    private val _planDetail = MutableStateFlow<UiResult<Rencana?>>(UiResult.Loading)
    val planDetail: StateFlow<UiResult<Rencana?>> = _planDetail

    // For the result of adding a plan
    private val _addPlanResult = MutableStateFlow<UiResult<Unit>?>(null)
    val addPlanResult: StateFlow<UiResult<Unit>?> = _addPlanResult

    fun loadAllPlans() {
        viewModelScope.launch {
            _planList.value = UiResult.Loading
            try {
                val result = planRepository.fetchAllPlans()
                _planList.value = UiResult.Success(result)
            } catch (e: Exception) {
                _planList.value = UiResult.Error(e.message ?: "Gagal memuat rencana")
            }
        }
    }

    fun loadPlanDetail(planId: Long) {
        viewModelScope.launch {
            _planDetail.value = UiResult.Loading
            try {
                val result = planRepository.fetchPlanById(planId)
                _planDetail.value = UiResult.Success(result)
            } catch (e: Exception) {
                _planDetail.value = UiResult.Error(e.message ?: "Gagal memuat detail rencana")
            }
        }
    }

    fun addPlan(rencana: Rencana, imageFile: File?) {
        viewModelScope.launch {
            _addPlanResult.value = UiResult.Loading
            try {
                planRepository.addPlan(rencana, imageFile)
                _addPlanResult.value = UiResult.Success(Unit)
            } catch (e: Exception) {
                _addPlanResult.value = UiResult.Error(e.message ?: "Gagal menambahkan rencana")
            }
        }
    }

    fun resetAddPlanResult() {
        _addPlanResult.value = null
    }
}
