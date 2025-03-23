
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.money.components.navigation.Routes

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)


val bottomNavItems = listOf(
    BottomNavItem(
        title = "Expenses",
        selectedIcon = Icons.Filled.Upload,
        unselectedIcon = Icons.Filled.Upload,
        route = Routes.Expenses.route
    ),
    BottomNavItem(
        title = "Analytics",
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Filled.Analytics,
        route = Routes.Analytics.route
    ),
    BottomNavItem(
        title = "Add",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add,
        route = Routes.Add.route
    ),
    BottomNavItem(
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Filled.Settings,
        route = Routes.Settings.route
    )
)