package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.MarketViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout(viewModel)
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: MarketViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Synchronize bottom highlights to match mock expectations
    val activeTab = when {
        currentRoute == "home" -> "home"
        currentRoute?.startsWith("comparison") == true -> "search"
        currentRoute?.startsWith("details") == true -> "alerts"
        currentRoute?.startsWith("history") == true -> "alerts"
        currentRoute == "alerts" -> "alerts"
        currentRoute == "profile" -> "profile"
        else -> "home"
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceLowest,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == "home",
                    onClick = { navController.navigate("home") { popUpTo("home") { inclusive = false } } },
                    icon = { Icon(imageVector = if (activeTab == "home") Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnSecondaryGreenContainer,
                        selectedTextColor = SecondaryGreen,
                        indicatorColor = SecondaryGreenContainer,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )

                NavigationBarItem(
                    selected = activeTab == "search",
                    onClick = {
                        // Navigate to Tomato comparison by default
                        navController.navigate("comparison/Fresh Tomato")
                    },
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnSecondaryGreenContainer,
                        selectedTextColor = SecondaryGreen,
                        indicatorColor = SecondaryGreenContainer,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )

                NavigationBarItem(
                    selected = activeTab == "alerts",
                    onClick = { navController.navigate("alerts") },
                    icon = { Icon(imageVector = if (activeTab == "alerts") Icons.Filled.NotificationsActive else Icons.Outlined.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnSecondaryGreenContainer,
                        selectedTextColor = SecondaryGreen,
                        indicatorColor = SecondaryGreenContainer,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )

                NavigationBarItem(
                    selected = activeTab == "profile",
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(imageVector = if (activeTab == "profile") Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnSecondaryGreenContainer,
                        selectedTextColor = SecondaryGreen,
                        indicatorColor = SecondaryGreenContainer,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDetails = { name ->
                        navController.navigate("details/$name")
                    },
                    onNavigateToAlerts = {
                        navController.navigate("alerts")
                    }
                )
            }

            composable(
                route = "details/{productName}",
                arguments = listOf(navArgument("productName") { type = NavType.StringType })
            ) { backStackEntry ->
                val productName = backStackEntry.arguments?.getString("productName") ?: "Fresh Tomato"
                DetailsScreen(
                    viewModel = viewModel,
                    productName = productName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHistory = { name ->
                        navController.navigate("history/$name")
                    }
                )
            }

            composable(
                route = "history/{productName}",
                arguments = listOf(navArgument("productName") { type = NavType.StringType })
            ) { backStackEntry ->
                val productName = backStackEntry.arguments?.getString("productName") ?: "Fresh Tomato"
                HistoryScreen(
                    viewModel = viewModel,
                    productName = productName,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "comparison/{productName}",
                arguments = listOf(navArgument("productName") { type = NavType.StringType })
            ) { backStackEntry ->
                ComparisonScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("alerts") {
                AlertsScreen(viewModel = viewModel)
            }

            composable("profile") {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmbientBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Avatar Placeholder with initials
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(PrimaryNavyContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SC", // Sushant Chauhan
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sushant Chauhan",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy
            )
        )
        Text(
            text = "sushantchauhan08@gmail.com",
            color = TextGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Option cards
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileOptionRow(
                    icon = Icons.Default.LocationOn,
                    title = "Primary Mandi Location",
                    value = "Aligarh, UP"
                )
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))
                ProfileOptionRow(
                    icon = Icons.Default.CurrencyExchange,
                    title = "Currency Standard",
                    value = "INR (₹)"
                )
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))
                ProfileOptionRow(
                    icon = Icons.Default.Security,
                    title = "Data Persistence",
                    value = "Encrypted Local (SQL)"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Market Watcher v1.0.0",
            color = TextGray.copy(alpha = 0.6f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryNavy,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = PrimaryNavy,
                fontSize = 15.sp
            )
        }

        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = SecondaryGreen,
            fontSize = 14.sp
        )
    }
}
