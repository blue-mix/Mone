package com.example.money.pages

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.money.components.custom.CategoryBadge
import com.example.money.components.custom.TableRow
import com.example.money.data.models.Category
import com.example.money.data.models.KeywordMapping
import com.example.money.ui.theme.BackgroundElevated
import com.example.money.ui.theme.DividerColor
import com.example.money.ui.theme.Shapes
import com.example.money.utils.generateColorForCategory
import com.example.money.viewmodels.KeywordMappingViewModel
import kotlinx.coroutines.launch

enum class SearchBarState {
    OPENED, CLOSED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordMappingEditor(
    navController: NavController,
    viewModel: KeywordMappingViewModel
) {
    val mappings by viewModel.mappings.collectAsState()
    val categories = viewModel.getAllCategories()
    val searchBarState = remember { mutableStateOf(SearchBarState.CLOSED) }
    val searchQuery = remember { mutableStateOf("") }

    val filteredMappings = mappings.filter {
        it.keyword.contains(searchQuery.value, ignoreCase = true) ||
                it.categoryName.contains(searchQuery.value, ignoreCase = true)
    }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var selectedMapping by remember { mutableStateOf<KeywordMapping?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var isNewMapping by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Crossfade(
                targetState = searchBarState.value,
                animationSpec = tween(durationMillis = 300),
                label = "Search bar transition"
            ) { state ->
                when (state) {
                    SearchBarState.CLOSED -> MediumTopAppBar(
                        title = { Text("Keyword → Category Mappings") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                searchBarState.value = SearchBarState.OPENED
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    )

                    SearchBarState.OPENED -> {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }

                        OutlinedTextField(
                            value = searchQuery.value,
                            onValueChange = { searchQuery.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .focusRequester(focusRequester),
                            placeholder = { Text("Search keywords or categories") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (searchQuery.value.isNotEmpty()) {
                                        searchQuery.value = ""
                                    } else {
                                        searchBarState.value = SearchBarState.CLOSED
                                    }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                cursorColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New Mapping") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                onClick = {
                    selectedMapping = KeywordMapping().apply {
                        keyword = ""
                        categoryName = ""
                    }
                    isNewMapping = true
                    showSheet = true
                    coroutineScope.launch { sheetState.show() }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (filteredMappings.isEmpty()) {
                Text(
                    "No mappings found",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(Shapes.large)
                        .background(BackgroundElevated)
                        .fillMaxWidth()
                ) {
                    items(filteredMappings, key = { it._id.toHexString() }) { mapping ->
                        MappingItemPreview(mapping = mapping) {
                            selectedMapping = mapping
                            isNewMapping = false
                            showSheet = true
                            coroutineScope.launch { sheetState.show() }
                        }
                        Divider(
                            modifier = Modifier.padding(start = 16.dp),
                            thickness = 1.dp,
                            color = DividerColor
                        )
                    }
                }
            }

            if (showSheet && selectedMapping != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showSheet = false
                        selectedMapping = null
                    },
                    sheetState = sheetState
                ) {
                    MappingEditSheet(
                        mapping = selectedMapping!!,
                        isNew = isNewMapping,
                        categories = categories,
                        viewModel = viewModel,
                        onClose = {
                            showSheet = false
                            selectedMapping = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MappingItemPreview(mapping: KeywordMapping, onClick: () -> Unit) {
    TableRow(
        label = mapping.keyword,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        detailContent = {
            CategoryBadge(
                category = Category(
                    name = mapping.categoryName,
                    color = generateColorForCategory(mapping.categoryName)
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappingEditSheet(
    mapping: KeywordMapping,
    isNew: Boolean,
    categories: List<Category>,
    viewModel: KeywordMappingViewModel,
    onClose: () -> Unit
) {
    var keyword by remember { mutableStateOf(mapping.keyword) }
    var selectedCategory by remember { mutableStateOf(mapping.categoryName) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            if (isNew) "Add Mapping" else "Edit Mapping",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("Keyword") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category.name
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            if (!isNew) {
                Button(
                    onClick = {
                        viewModel.deleteMapping(mapping)
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }

            Button(onClick = {
                if (keyword.isNotBlank() && selectedCategory.isNotBlank()) {
                    if (isNew) {
                        viewModel.addMapping(KeywordMapping().apply {
                            this.keyword = keyword
                            this.categoryName = selectedCategory
                        })
                    } else {
                        viewModel.updateKeyword(mapping._id, keyword)
                        viewModel.updateCategory(mapping._id, selectedCategory)
                    }
                    onClose()
                }
            }) {
                Text("Save")
            }
        }
    }
}
