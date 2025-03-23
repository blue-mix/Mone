package com.example.money.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.db
import com.example.money.data.models.Category
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoriesState(
    val newCategoryColor: Color = Color.White,
    val newCategoryName: String = "",
    val colorPickerShowing: Boolean = false,
    val categories: List<Category> = listOf(),
    val selectedCategoryToDelete: Category? = null
)

class CategoriesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriesState())
    val uiState: StateFlow<CategoriesState> = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                categories = db.query<Category>().find()
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            db.query<Category>().asFlow().collect { changes ->
                _uiState.update { currentState ->
                    currentState.copy(categories = changes.list)
                }
            }
        }
    }

    fun setNewCategoryColor(color: Color) {
        _uiState.update { it.copy(newCategoryColor = color) }
    }

    fun setNewCategoryName(name: String) {
        _uiState.update { it.copy(newCategoryName = name) }
    }

    fun showColorPicker() {
        _uiState.update { it.copy(colorPickerShowing = true) }
    }

    fun hideColorPicker() {
        _uiState.update { it.copy(colorPickerShowing = false) }
    }

    fun createNewCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            db.write {
                copyToRealm(
                    Category(
                        _uiState.value.newCategoryName,
                        _uiState.value.newCategoryColor
                    )
                )
            }
            _uiState.update {
                it.copy(newCategoryColor = Color.White, newCategoryName = "")
            }
        }
    }

    fun setCategoryToDelete(category: Category) {
        _uiState.update { it.copy(selectedCategoryToDelete = category) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(selectedCategoryToDelete = null) }
    }

    fun confirmDelete() {
        val category = _uiState.value.selectedCategoryToDelete ?: return
        viewModelScope.launch(Dispatchers.IO) {
            db.write {
                val toDelete = query<Category>("_id == $0", category._id).first().find()
                toDelete?.let { delete(it) }
            }
            _uiState.update { it.copy(selectedCategoryToDelete = null) }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            db.write {
                val deletingCategory = query<Category>("_id == $0", category._id).first().find()
                deletingCategory?.let { delete(it) }
            }
        }
    }
}
