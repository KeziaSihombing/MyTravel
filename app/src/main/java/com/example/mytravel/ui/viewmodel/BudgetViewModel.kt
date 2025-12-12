package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.BudgetRepository
import com.example.mytravel.domain.model.Budget
import com.example.mytravel.domain.model.Rencana
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File

data class RencanaWithTotal(
    val rencana: Rencana,
    val total: Double
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository = BudgetRepository()
) : ViewModel() {

    private val _rencanaWithTotals = MutableStateFlow<UiResult<List<RencanaWithTotal>>>(UiResult.Loading)
    val rencanaWithTotals: StateFlow<UiResult<List<RencanaWithTotal>>> = _rencanaWithTotals

    private val _budgetItems = MutableStateFlow<UiResult<List<Budget>>>(UiResult.Loading)
    val budgetItems: StateFlow<UiResult<List<Budget>>> = _budgetItems

    private val _budgetToEdit = MutableStateFlow<UiResult<Budget?>>(UiResult.Loading)
    val budgetToEdit: StateFlow<UiResult<Budget?>> = _budgetToEdit

    private val _addBudgetResult = MutableStateFlow<UiResult<Unit>?>(null)
    val addBudgetResult: StateFlow<UiResult<Unit>?> = _addBudgetResult

    private val _updateBudgetResult = MutableStateFlow<UiResult<Unit>?>(null)
    val updateBudgetResult: StateFlow<UiResult<Unit>?> = _updateBudgetResult

    private val _deleteBudgetResult = MutableStateFlow<UiResult<Unit>?>(null)
    val deleteBudgetResult: StateFlow<UiResult<Unit>?> = _deleteBudgetResult

    init {
        listenToAllBudgetChanges()
    }

    private fun listenToAllBudgetChanges() {
        viewModelScope.launch {
            budgetRepository.listenToAnyBudgetChange().collect { 
                _updateRencanaTotals()
            }
        }
    }

    private suspend fun _updateRencanaTotals() {
        _rencanaWithTotals.value = UiResult.Loading
        try {
            val allRencana = budgetRepository.fetchAllRencana()
            val allBudgets = budgetRepository.fetchAllBudgets()

            val budgetsByRencanaId = allBudgets.groupBy { it.rencanaId }

            val result = allRencana.map { rencana ->
                val total = budgetsByRencanaId[rencana.id]?.sumOf { it.nominal } ?: 0.0
                RencanaWithTotal(rencana, total)
            }
            _rencanaWithTotals.value = UiResult.Success(result)
        } catch (e: Exception) {
            _rencanaWithTotals.value = UiResult.Error(e.message ?: "Gagal memuat daftar rencana")
        }
    }

    fun loadInitialData() {
        viewModelScope.launch { 
            _updateRencanaTotals()
        }
    }

    fun getBudgetDetails(budgetId: Long) {
        viewModelScope.launch {
            _budgetToEdit.value = UiResult.Loading
            try {
                val budget = budgetRepository.getBudgetById(budgetId)
                _budgetToEdit.value = UiResult.Success(budget)
            } catch (e: Exception) {
                _budgetToEdit.value = UiResult.Error(e.message ?: "Gagal memuat detail budget")
            }
        }
    }

    fun listenForBudgetUpdates(rencanaId: Long) {
        viewModelScope.launch {
            budgetRepository.listenToBudgetChanges(rencanaId)
                .catch { e -> 
                    _budgetItems.value = UiResult.Error(e.message ?: "Gagal mendengarkan perubahan")
                }
                .collect { budgetList ->
                    _budgetItems.value = UiResult.Success(budgetList)
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
            } catch (e: Exception) {
                _addBudgetResult.value = UiResult.Error(e.message ?: "Gagal menambahkan budget")
            }
        }
    }

    fun resetAddBudgetResult() {
        _addBudgetResult.value = null
    }
    
    fun updateBudget(budgetId: Long, title: String, nominal: Double) {
        viewModelScope.launch {
            _updateBudgetResult.value = UiResult.Loading
            try {
                budgetRepository.updateBudget(budgetId, title, nominal)
                _updateBudgetResult.value = UiResult.Success(Unit)
            } catch (e: Exception) {
                _updateBudgetResult.value = UiResult.Error(e.message ?: "Gagal menyimpan perubahan")
            }
        }
    }

    fun resetUpdateBudgetResult() {
        _updateBudgetResult.value = null
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            _deleteBudgetResult.value = UiResult.Loading
            try {
                budgetRepository.deleteBudget(budgetId)
                _deleteBudgetResult.value = UiResult.Success(Unit)
            } catch (e: Exception) {
                _deleteBudgetResult.value = UiResult.Error(e.message ?: "Gagal menghapus budget")
            }
        }
    }
    
    fun resetDeleteBudgetResult() {
        _deleteBudgetResult.value = null
    }
}