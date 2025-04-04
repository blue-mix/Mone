package com.example.money.pages.OnboardingScreens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.money.R
import com.example.money.viewmodels.ExpensesViewModel

@Composable
fun PermissionRequestPage(
    onPermissionGranted: () -> Unit,
    expensesViewModel: ExpensesViewModel
) {
    val context = LocalContext.current

    var permissionGranted by remember { mutableStateOf(false) }

    val requiredPermissions = listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionGranted = result.values.all { it }
    }

    fun checkPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // First check or request
    LaunchedEffect(Unit) {
        if (checkPermissions()) {
            permissionGranted = true
        } else {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    // When granted, call refresh and proceed
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            expensesViewModel.setPermissionGranted(true)
            onPermissionGranted()
            // Trigger page transition (e.g., next onboarding page)
        }
    }

    if (!permissionGranted) {
        expensesViewModel.setPermissionGranted(false)

        PermissionExplanationUI(
            title = "Grant SMS Permissions",
            description = "We need access to SMS to automatically read bank messages and track your expenses.",
            imageRes = R.drawable.sms,
            buttonText = "Grant Permission",
            onButtonClick = { permissionLauncher.launch(requiredPermissions.toTypedArray()) }
        )
    } else {
        PermissionExplanationUI(
            title = "Grant SMS Permissions",
            description = "We need access to SMS...",
            imageRes = R.drawable.sms,
            buttonText = "Permission Granted",
            onButtonClick = {},
            isEnabled = false
        )
    }

}

@Composable
fun PermissionExplanationUI(
    title: String,
    description: String,
    imageRes: Int,
    buttonText: String,
    onButtonClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = title, style = MaterialTheme.typography.headlineSmall, color = Color.White)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onButtonClick,
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(buttonText)
        }
    }
}
