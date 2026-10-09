package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PriceAlert
import com.example.ui.MarketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val activeAlerts by viewModel.activeAlertsList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Price Alerts", color = PrimaryNavy, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLowest)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AmbientBackground)
                .padding(innerPadding)
        ) {
            // Summary header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alert Bell",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Set Watch Alert Matrix",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activeAlerts.size + 12} alert lines monitored currently",
                            color = OnPrimaryNavyContainer,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Text(
                text = "ACTIVE ROOM THRESHOLDS",
                color = TextGray,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (activeAlerts.isEmpty()) {
                // Friendly Empty State matching guidelines
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = TextGray.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No User Alerts Configured",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Select any product details and click 'Set Price Alert' to trace target drops instantly.",
                            color = TextGray,
                            fontSize = 12.sp,
                            maxLines = 2,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeAlerts) { alert ->
                        AlertItemCard(alert = alert, onDelete = { viewModel.deletePriceAlert(alert) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Simulated base alerts
            Text(
                text = "PRE-CONFIGURED SYSTEM TRACES",
                color = TextGray,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            val baseMocks = listOf(
                "Fresh Tomato" to 28.0,
                "Onion" to 34.0,
                "Potato" to 20.0
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(baseMocks) { mock ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceLowest, RoundedCornerShape(8.dp))
                            .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SecondaryGreenContainer),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = OnSecondaryGreenContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = mock.first,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Rule: Drop below ₹${mock.second.toInt()}/kg",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Traced",
                            color = SecondaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItemCard(alert: PriceAlert, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLowest, RoundedCornerShape(8.dp))
            .border(1.5.dp, SecondaryGreen, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SecondaryGreenContainer),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = OnSecondaryGreenContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = alert.itemName,
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy
            )
            Text(
                text = "Rule: Drop below ₹${alert.targetPrice.toInt()}/kg",
                color = TextGray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_alert_button")) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete Alert Rule",
                tint = AlertCrimson
            )
        }
    }
}
