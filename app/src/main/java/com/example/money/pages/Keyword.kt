package com.example.money.pages

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.money.components.CategoryBadge
import com.example.money.components.TableRow
import com.example.money.models.Category
import com.example.money.models.KeywordMapping
import com.example.money.ui.theme.BackgroundElevated
import com.example.money.ui.theme.DividerColor
import com.example.money.ui.theme.Shapes
import com.example.money.utils.generateColorForCategory
import com.example.money.viewmodels.KeywordMappingViewModel

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
    val searchBarState = remember { mutableStateOf(SearchBarState.CLOSED) }
    val searchQuery = remember { mutableStateOf("") }

    val filteredMappings = mappings.filter {
        it.keyword.contains(searchQuery.value, ignoreCase = true)||
                it.categoryName.contains(searchQuery.value, ignoreCase = true)
    }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var selectedMapping by remember { mutableStateOf<KeywordMapping?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Crossfade(
                targetState = searchBarState.value,
                animationSpec = tween(durationMillis = 300),
                label = "Search bar transition"
            ) { state ->
                when (state) {
                    SearchBarState.CLOSED -> {
                        MediumTopAppBar(
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
                    }

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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)

        ) {
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
                        MappingItemPreview (mapping = mapping, onClick = { })
                        Divider(
                            modifier = Modifier
                                .padding(start = 16.dp), thickness = 1.dp, color = DividerColor
                        )
                    }
                }
            }



            Button(
                onClick = { viewModel.addMapping() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Mapping")
            }
        }
    }
}

@Composable
fun MappingItemPreview(
    mapping: KeywordMapping,
    onClick: () -> Unit
) {
    TableRow(
        label = mapping.keyword,
        hasArrow = true,
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



@Composable
fun MappingItem(
    mapping: KeywordMapping,
    viewModel: KeywordMappingViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ID: ${mapping._id.toHexString().take(6)}", style = MaterialTheme.typography.labelSmall)
            IconButton(onClick = { viewModel.deleteMapping(mapping) }) {
                Icon(Icons.Default.Close, contentDescription = "Delete Mapping")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mapping.keyword,
            onValueChange = { viewModel.updateKeyword(mapping._id, it) },
            label = { Text("Keyword") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mapping.categoryName,
            onValueChange = { viewModel.updateCategory(mapping._id, it) },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
