package com.example.forlearning.ui.Search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow(personList)
    val persons = searchText
        .debounce(500L)
        .onEach { _isSearching.update { true } }
        .combine(_persons) { __text, __persons ->
            if (__text.isBlank()) {
                __persons
            } else {
                delay(1500L)
                __persons.filter {
                    it.match(__text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _persons.value
        )

    fun onSearch(text: String) {
        _searchText.value = text

    }

}