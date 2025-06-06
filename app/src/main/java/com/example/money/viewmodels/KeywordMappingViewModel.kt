package com.example.money.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.Database
import com.example.money.data.models.Category
import com.example.money.data.models.KeywordMapping
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class KeywordMappingViewModel : ViewModel() {
    private val realm = Database.db
    private val _mappings = MutableStateFlow<List<KeywordMapping>>(emptyList())
    val mappings: StateFlow<List<KeywordMapping>> = _mappings
    fun getAllCategories(): List<Category> {
        return realm.query<Category>().find()
    }


    init {
        fetchMappings()
    }

    private fun fetchMappings() {
        viewModelScope.launch {
            val results = realm.query<KeywordMapping>().find()
            _mappings.value = results
        }
    }

    fun updateKeyword(id: ObjectId, newKeyword: String) {
        realm.writeBlocking {
            val mapping = query<KeywordMapping>("_id == $0", id).first().find()
            mapping?.keyword = newKeyword
        }
        fetchMappings()
    }

    fun updateCategory(id: ObjectId, newCategory: String) {
        realm.writeBlocking {
            val mapping = query<KeywordMapping>("_id == $0", id).first().find()
            mapping?.categoryName = newCategory
        }
        fetchMappings()
    }

fun addMapping(mapping: KeywordMapping) {
    realm.writeBlocking {
        copyToRealm(mapping) // ✅ Actually use the passed object
    }
    fetchMappings()
}



    fun deleteMapping(mapping: KeywordMapping) {
        realm.writeBlocking {
            val itemToDelete = query<KeywordMapping>("_id == $0", mapping._id).first().find()
            itemToDelete?.let { delete(it) }
        }
        fetchMappings() // Refresh list after deletion
    }
}
