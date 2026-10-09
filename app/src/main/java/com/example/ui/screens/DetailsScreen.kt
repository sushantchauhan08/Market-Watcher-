package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.MarketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: MarketViewModel,
    productName: String,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sync current product choice
    LaunchedEffect(productName) {
        viewModel.selectProduct(productName)
    }

    val product by viewModel.selectedProduct.collectAsState()
    val isBookmarked by viewModel.isProductBookmarked(product.name).collectAsState(initial = false)

    var showAlertSetupDialog by remember { mutableStateOf(false) }
    var targetPriceInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${product.name} Details", color = PrimaryNavy, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share mock action */ }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = PrimaryNavy)
                    }
                    IconButton(onClick = { /* Alert mock notification toggle */ }) {
                        Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = "Alerts", tint = PrimaryNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLowest)
            )
        },
        bottomBar = {
            // Screen 2 Bottom CTA Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = SurfaceLowest
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            targetPriceInput = (product.currentPrice - 2.0).toInt().toString()
                            showAlertSetupDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavyContainer),
                        shape = RoundedCornerShape(4.dp), // 4px brand radius
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("set_price_alert_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alert icon",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Set Price Alert", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp))
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { viewModel.toggleProductBookmark(product.name) }
                            .testTag("bookmark_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Toggle",
                            tint = if (isBookmarked) SecondaryGreen else PrimaryNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AmbientBackground)
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1. Trending Banner Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                AsyncImage(
                    model = product.bannerImageUrl,
                    contentDescription = "${product.name} Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // TRENDING overlay chip
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(PrimaryNavy, RoundedCornerShape(100.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Fire Trend icon",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TRENDING",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // 2. Title block
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy,
                                fontSize = 30.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Pin icon",
                                tint = TextGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = product.location, color = TextGray, fontSize = 14.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CURRENT PRICE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextGray,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "₹${product.currentPrice.toInt()}/${product.unit}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavyContainer,
                                fontSize = 28.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. AI prediction & Tomorrow's Forecast Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Price Prediction",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )

                    // Confidence Meter Badge
                    Row(
                        modifier = Modifier
                            .background(SecondaryGreenContainer, RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SecondaryGreen),
                            shape = CircleShape,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "High Confidence (${product.forecastConfidence}%)",
                            color = OnSecondaryGreenContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Forecast Hero Metric Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SecondaryGreen, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tomorrow's Forecast",
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextGray)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = "Drop icon",
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${product.trendPercentTomorrow}%",
                                    color = SecondaryGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${product.forecastTomorrow}0",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecondaryGreen,
                                fontSize = 34.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Price table/list of upcoming days
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        product.dailyForecasts.forEachIndexed { index, forecast ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = forecast.first,
                                    color = if (forecast.first == "Today") PrimaryNavy else TextGray,
                                    fontWeight = if (forecast.first == "Today") FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Visual arrow indicator
                                    val isIncreasing = index == 4 // +7 days is 34.0 (higher than 32.0 today)
                                    val indColor = if (isIncreasing) AlertCrimson else SecondaryGreen
                                    val indIcon = if (isIncreasing) Icons.Default.TrendingUp else Icons.Default.TrendingDown

                                    if (forecast.first != "Today") {
                                        Icon(
                                            imageVector = indIcon,
                                            contentDescription = "Forecast change indicator",
                                            tint = indColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Text(
                                        text = "₹${forecast.second}0",
                                        fontWeight = FontWeight.Bold,
                                        color = if (forecast.first == "Today") PrimaryNavy else TextGray,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            if (index < product.dailyForecasts.size - 1) {
                                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. AI Insights Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Insights bulb",
                                tint = OnPrimaryNavyContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Insights",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = product.aiInsights,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 22.sp,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { onNavigateToHistory(product.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = OnPrimaryNavyContainer.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .border(1.dp, OnPrimaryNavyContainer.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "View Full Analysis",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Arrow right icon",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Modal dialog to set a custom alert price
    if (showAlertSetupDialog) {
        AlertDialog(
            onDismissRequest = { showAlertSetupDialog = false },
            title = { Text("Set Price Alert for ${product.name}") },
            text = {
                Column {
                    Text("We'll alert you as soon as the retail price falls below this threshold.", color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = targetPriceInput,
                        onValueChange = { targetPriceInput = it },
                        label = { Text("Target Alert Price (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = targetPriceInput.toDoubleOrNull()
                        if (parsed != null) {
                            viewModel.addPriceAlert(product.name, parsed)
                        }
                        showAlertSetupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavyContainer)
                ) {
                    Text("Save Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlertSetupDialog = false }) {
                    Text("Cancel", color = PrimaryNavy)
                }
            }
        )
    }
}
