package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.BudgetRepository
import com.example.mytravel.domain.model.Budget
import com.example.mytravel.domain.model.Rencana // Pastikan Rencana di-import
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class BudgetViewModel(
    private val budgetRepository: BudgetRepository = BudgetRepository()
) : ViewModel() {

    // Untuk ListRencanaScreen
    private val _rencanaList = MutableStateFlow<UiResult<List<Rencana>>>(UiResult.Loading)
    val rencanaList: StateFlow<UiResult<List<Rencana>>> = _rencanaList

    // Untuk RincianRencanaScreen
    private val _budgetItems = MutableStateFlow<UiResult<List<Budget>>>(UiResult.Loading)
    val budgetItems: StateFlow<UiResult<List<Budget>>> = _budgetItems

    // Untuk BuatBudgetScreen
    private val _addBudgetResult = MutableStateFlow<UiResult<Unit>?>(null)
    val addBudgetResult: StateFlow<UiResult<Unit>?> = _addBudgetResult

    fun loadAllRencana() {
        viewModelScope.launch {
            _rencanaList.value = UiResult.Loading
            try {
                val result = budgetRepository.fetchAllRencana()
                _rencanaList.value = UiResult.Success(result)
            } catch (e: Exception) {
                _rencanaList.value = UiResult.Error(e.message ?: "Gagal memuat daftar rencana")
            }
        }
    }

    fun loadBudgetsForRencana(rencanaId: Long) {
        viewModelScope.launch {
            _budgetItems.value = UiResult.Loading
            try {
                val result = budgetRepository.fetchBudgetsForRencana(rencanaId)
                _budgetItems.value = UiResult.Success(result)
            } catch (e: Exception) {
                _budgetItems.value = UiResult.Error(e.message ?: "Gagal memuat item budget")
            }
        }
    }

    fun addBudget(
        rencanaId: Long,
        title: String,
        nominal: Double,
        attachment: File?
    ) {
        viewModelScope.launch {
            _addBudgetResult.value = UiResult.Loading
            try {
                budgetRepository.addBudgetToRencana(rencanaId, title, nominal, attachment)
                _addBudgetResult.value = UiResult.Success(Unit)
                loadBudgetsForRencana(rencanaId) // Refresh list setelah menambah
            } catch (e: Exception) {
                _addBudgetResult.value = UiResult.Error(e.message ?: "Gagal menambahkan budget")
            }
        }
    }

    fun resetAddBudgetResult() {
        _addBudgetResult.value = null
    }
}
