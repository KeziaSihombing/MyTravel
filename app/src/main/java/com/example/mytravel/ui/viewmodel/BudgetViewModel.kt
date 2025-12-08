package com.example.mytravel.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.model.BudgetItem
import com.example.mytravel.data.repository.AuthRepository
import com.example.mytravel.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetViewModel : ViewModel() {

    private val budgetRepository = BudgetRepository()
    private val authRepository = AuthRepository()

    private val _budgetItems = MutableStateFlow<List<BudgetItem>>(emptyList())
    val budgetItems: StateFlow<List<BudgetItem>> = _budgetItems.asStateFlow()

    val title = mutableStateOf("")
    val nominal = mutableStateOf("")
    val imageUri = mutableStateOf<Uri?>(null)
    private var imageBytes = mutableStateOf<ByteArray?>(null)

    init {
        loadBudgetItems()
    }

    fun setImageBytes(bytes: ByteArray?) {
        imageBytes.value = bytes
    }

    private fun loadBudgetItems() {
        viewModelScope.launch {
            val user = authRepository.currentSession()?.user
            user?.let {
                _budgetItems.value = budgetRepository.getBudgetItems(it.id)
            }
        }
    }

    fun addBudgetItem() {
        viewModelScope.launch {
            val user = authRepository.currentSession()?.user
            user?.let { currentUser ->
                var imageUrl: String? = null
                imageBytes.value?.let {
                    imageUrl = budgetRepository.uploadBudgetImage(it, currentUser.id)
                }

                val newBudgetItem = BudgetItem(
                    userId = currentUser.id,
                    title = title.value,
                    nominal = nominal.value.toDoubleOrNull() ?: 0.0,
                    imageUrl = imageUrl // Diganti dari attachmentUrl
                )
                budgetRepository.addBudgetItem(newBudgetItem)
                loadBudgetItems() // Refresh the list
                // Clear fields
                title.value = ""
                nominal.value = ""
                imageUri.value = null
                imageBytes.value = null
            }
        }
    }
}
