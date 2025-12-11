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

// Data class baru untuk menggabungkan Rencana dengan totalnya
data class RencanaWithTotal(
    val rencana: Rencana,
    val total: Double
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository = BudgetRepository()
) : ViewModel() {

    // StateFlow untuk daftar rencana dengan totalnya
    private val _rencanaWithTotals = MutableStateFlow<UiResult<List<RencanaWithTotal>>>(UiResult.Loading)
    val rencanaWithTotals: StateFlow<UiResult<List<RencanaWithTotal>>> = _rencanaWithTotals

    // StateFlow untuk item budget di layar rincian
    private val _budgetItems = MutableStateFlow<UiResult<List<Budget>>>(UiResult.Loading)
    val budgetItems: StateFlow<UiResult<List<Budget>>> = _budgetItems

    // StateFlow untuk hasil penambahan budget
    private val _addBudgetResult = MutableStateFlow<UiResult<Unit>?>(null)
    val addBudgetResult: StateFlow<UiResult<Unit>?> = _addBudgetResult

    init {
        // Mulai mendengarkan semua perubahan budget saat ViewModel dibuat
        listenToAllBudgetChanges()
    }

    // Fungsi baru untuk mendengarkan SEMUA perubahan di tabel budget
    private fun listenToAllBudgetChanges() {
        viewModelScope.launch {
            budgetRepository.listenToAnyBudgetChange().collect { 
                _updateRencanaTotals()
            }
        }
    }

    // Fungsi internal untuk mengambil semua data dan menghitung total
    private suspend fun _updateRencanaTotals() {
        _rencanaWithTotals.value = UiResult.Loading
        try {
            val allRencana = budgetRepository.fetchAllRencana()
            val allBudgets = budgetRepository.fetchAllBudgets()

            // Kelompokkan budget berdasarkan rencana_id
            val budgetsByRencanaId = allBudgets.groupBy { it.rencanaId }

            // Buat daftar RencanaWithTotal
            val result = allRencana.map { rencana ->
                val total = budgetsByRencanaId[rencana.id]?.sumOf { it.nominal } ?: 0.0
                RencanaWithTotal(rencana, total)
            }
            _rencanaWithTotals.value = UiResult.Success(result)
        } catch (e: Exception) {
            _rencanaWithTotals.value = UiResult.Error(e.message ?: "Gagal memuat daftar rencana")
        }
    }

    // Fungsi ini dipanggil dari ListBudgetScreen saat pertama kali dibuka
    fun loadInitialData() {
        viewModelScope.launch { 
            _updateRencanaTotals()
        }
    }

    fun listenForBudgetUpdates(rencanaId: Long) {
        viewModelScope.launch {
            _budgetItems.value = UiResult.Loading
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
}
