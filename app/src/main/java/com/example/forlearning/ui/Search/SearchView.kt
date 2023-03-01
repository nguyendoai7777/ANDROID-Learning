package com.example.forlearning.ui.Search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SearchView() {
    val viewModel = viewModel<SearchViewModel>()
    val st by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val persons by viewModel.persons.collectAsState()
    Column(Modifier.fillMaxSize()) {
        /*BasicTextField(value = "", onValueChange = {}, )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text("Search...") },
        )*/
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = st,
            onValueChange = viewModel::onSearch,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search...") },
            colors = TextFieldDefaults
                .textFieldColors( focusedIndicatorColor = Color(0xFFFF1493))

        )
        if (isSearching)
            CircularProgressIndicator()
        else
            LazyColumn(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(persons) {
                    Text(text = "${it.fName} ${it.lName}")
                }
                /*persons.map {
                    item {
                        Text(text = "${it.fName} ${it.lName}")
                    }
                }*/
            }
    }
}
