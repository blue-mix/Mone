package com.example.money.pages

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.money.R
import com.example.money.components.custom.TableRow
import com.example.money.components.custom.UnstyledTextField
import com.example.money.data.models.Category
import com.example.money.ui.theme.*
import com.example.money.viewmodels.CategoriesViewModel
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Categories(
    navController: NavController,
    vm: CategoriesViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val colorPickerController = rememberColorPickerController()
    var selectedCategoryForDeletion by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Categories") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = TopAppBarBackground),

                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector =Icons.Default.ArrowBack,
                            contentDescription = "Settings"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Settings",
                            style = Typography.titleMedium // or bodyLarge for more emphasis
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AnimatedVisibility(visible = true) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(Shapes.large)
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(
                            uiState.categories,
                            key = { _, category -> category.name }) { index, category ->

                            SwipeableActionsBox(
                                endActions = listOf(
                                    SwipeAction(
                                        icon = painterResource(R.drawable.delete),
                                        background = Destructive,
                                        onSwipe = {
                                            selectedCategoryForDeletion = category
                                        }
                                    )
                                ),
                                modifier = Modifier.animateItemPlacement()
                            ) {
                                TableRow(modifier = Modifier.background(BackgroundElevated)
                                    .heightIn(min =56.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                            .fillMaxHeight()
                                    ) {
                                        Surface(
                                            color = category.color,
                                            shape = CircleShape,
                                            border = BorderStroke(2.dp, Color.White),
                                            modifier = Modifier.size(16.dp)
                                        ) {}
                                        Text(
                                            category.name,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            style = Typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            if (index < uiState.categories.size - 1) {
                                Row(
                                    modifier = Modifier.background(BackgroundElevated).height(1.dp)
                                ) {
                                    Divider(
                                        modifier = Modifier.padding(start = 16.dp),
                                        thickness = 1.dp,
                                        color = DividerColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Color Picker Dialog
            if (uiState.colorPickerShowing) {
                Dialog(onDismissRequest = vm::hideColorPicker) {
                    Surface(color = BackgroundElevated, shape = Shapes.large) {
                        Column(modifier = Modifier.padding(30.dp)) {
                            Text("Select a color", style = Typography.titleLarge)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AlphaTile(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    controller = colorPickerController
                                )
                            }
                            HsvColorPicker(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(10.dp),
                                controller = colorPickerController,
                                onColorChanged = { envelope ->
                                    vm.setNewCategoryColor(envelope.color)
                                }
                            )
                            TextButton(
                                onClick = vm::hideColorPicker,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp)
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }

            // New Category Input Row
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = vm::showColorPicker,
                    shape = CircleShape,
                    color = uiState.newCategoryColor,
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier.size(24.dp)
                ) {}

                Surface(
                    color = BackgroundElevated,
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f)
                        .padding(start = 16.dp),
                    shape = Shapes.large
                ) {
                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                        UnstyledTextField(
                            value = uiState.newCategoryName,
                            onValueChange = vm::setNewCategoryName,
                            placeholder = { Text("Category name") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = vm::createNewCategory,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Create category")
                }
            }

            // Delete Confirmation Dialog
            if (selectedCategoryForDeletion != null) {
                AlertDialog(
                    onDismissRequest = { selectedCategoryForDeletion = null },
                    title = { Text("Delete Category") },
                    text = {
                        Text("Are you sure you want to delete '${selectedCategoryForDeletion?.name}'? This cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.deleteCategory(selectedCategoryForDeletion!!)
                            selectedCategoryForDeletion = null
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            selectedCategoryForDeletion = null
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CategoriesPreview() {
    MoneyTheme {
        Categories(navController = rememberNavController())
    }
}
