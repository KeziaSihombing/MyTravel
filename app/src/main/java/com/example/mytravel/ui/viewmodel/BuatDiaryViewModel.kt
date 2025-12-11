package com.example.mytravel.ui.viewmodel




import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.mytravel.domain.model.DiaryEntry




class BuatDiaryViewModel : ViewModel() {
    private val repository = DiaryRepository()




    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()




    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()




    private val _selectedColor = MutableStateFlow("#FFFFFF")
    val selectedColor: StateFlow<String> = _selectedColor.asStateFlow()




    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()




    private val _imageBytes = MutableStateFlow<ByteArray?>(null)




    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()




    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()




    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }




    fun updateContent(newContent: String) {
        _content.value = newContent
    }




    fun updateColor(color: String) {
        _selectedColor.value = color
    }




    fun updateImage(uri: Uri?, bytes: ByteArray?) {
        _imageUri.value = uri
        _imageBytes.value = bytes
    }




    fun saveDiary() {
        viewModelScope.launch {
            _isSaving.value = true




            var imageUrl: String? = null
            if (_imageBytes.value != null) {
                imageUrl = repository.uploadImage(
                    _imageBytes.value!!,
                    "diary_${System.currentTimeMillis()}.jpg"
                )
            }




            val entry = DiaryEntry(
                title = _title.value,
                content = _content.value,
                imageUrl = imageUrl,
                color = _selectedColor.value,
                createdAt = repository.getCurrentTimestamp()
            )




            val success = repository.createDiary(entry)
            _isSaving.value = false
            _saveSuccess.value = success
        }
    }




    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}