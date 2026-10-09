package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.MarketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MarketViewModel,
    productName: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product by viewModel.selectedProduct.collectAsState()
    val activeAlerts by viewModel.activeAlertsList.collectAsState()
    val selectedRange by viewModel.selectedHistoryRange.collectAsState()

    var alertSettingActive by remember { mutableStateOf(false) }

    // Check if user already set an alert for 30.0
    val hasAlertSet = activeAlerts.any { it.itemName.equals(product.name, ignoreCase = true) && it.targetPrice <= 30.0 }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Price History", color = PrimaryNavy, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share action */ }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = PrimaryNavy)
                    }
                },
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
                .verticalScroll(scrollState)
        ) {
            // 1. Top Mini-Header row (Item Thumbnail & Price Stats)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(SurfaceLowest, RoundedCornerShape(8.dp))
                    .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )
                    Text(
                        text = "Retail Price / ${product.unit}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${product.currentPrice.toInt() + 1}", // matching ₹33 on screen 3
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = "Drop icon",
                            tint = SecondaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2.4%",
                            color = SecondaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Tab Bar Selector ranges (7 Days, 30 Days, 6 Months, 1 Year)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(SurfaceContainerHigh, RoundedCornerShape(6.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val ranges = listOf("7 Days", "30 Days", "6 Months", "1 Year")
                ranges.forEach { range ->
                    val isRangeSelected = range == selectedRange
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isRangeSelected) PrimaryNavyContainer else Color.Transparent)
                            .clickable { viewModel.setHistoryRange(range) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = range,
                            color = if (isRangeSelected) Color.White else PrimaryNavy,
                            fontWeight = if (isRangeSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Trend Analysis Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp)),
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
                            text = "Trend Analysis",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )

                        // Legend dot indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SecondaryGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Price Baseline",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CUSTOM CANVAS PRICE SPLINE GRAPH
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            // Draw horizontal baseline
                            val basePriceLineY = height * 0.55f
                            drawLine(
                                color = OutlineVariant.copy(alpha = 0.4f),
                                start = Offset(0f, basePriceLineY),
                                end = Offset(width, basePriceLineY),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                                strokeWidth = 2f
                            )

                            // Define dynamic price coordinates representing tomato graph
                            val points = listOf(
                                Offset(0f, height * 0.72f),
                                Offset(width * 0.15f, height * 0.70f),
                                Offset(width * 0.33f, height * 0.65f),
                                Offset(width * 0.50f, height * 0.35f), // highlight dot day
                                Offset(width * 0.65f, height * 0.33f),
                                Offset(width * 0.80f, height * 0.62f),
                                Offset(width, height * 0.38f)
                            )

                            // Draw curve using bezier path logic
                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    val previous = points[i - 1]
                                    val current = points[i]
                                    val cpX = (previous.x + current.x) / 2
                                    cubicTo(cpX, previous.y, cpX, current.y, current.x, current.y)
                                }
                            }

                            drawPath(
                                path = path,
                                color = SecondaryGreen,
                                style = Stroke(width = 2.5.dp.toPx()) // 2.5px thickness matching style rules!
                            )

                            // Draw dotted indicator line & highlight dot on 4th coordinate
                            val highlightDot = points[3]
                            drawLine(
                                color = PrimaryNavy.copy(alpha = 0.5f),
                                start = highlightDot,
                                end = Offset(highlightDot.x, height),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f),
                                strokeWidth = 1.5f.dp.toPx()
                            )

                            drawCircle(
                                color = PrimaryNavy,
                                radius = 6.dp.toPx(),
                                center = highlightDot
                            )

                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = highlightDot
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Labels below plot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Feb 1", color = TextGray, fontSize = 12.sp)
                        Text(text = "Feb 15", color = TextGray, fontSize = 12.sp)
                        Text(text = "Today", color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Metrics Grid (2x2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGridCard(
                    title = "HIGHEST",
                    value = "₹${product.highestPrice.toInt()}",
                    icon = Icons.Default.TrendingUp,
                    iconColor = AlertCrimson,
                    modifier = Modifier.weight(1f)
                )

                MetricGridCard(
                    title = "LOWEST",
                    value = "₹${product.lowestPrice.toInt()}",
                    icon = Icons.Default.TrendingDown,
                    iconColor = SecondaryGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGridCard(
                    title = "AVERAGE",
                    value = "₹${product.averagePrice.toInt()}",
                    icon = Icons.Default.Scale, // scale icon
                    iconColor = PrimaryNavyContainer,
                    modifier = Modifier.weight(1f)
                )

                VolatilityGridCard(
                    title = "VOLATILITY",
                    value = product.volatility,
                    icon = Icons.Default.Water, // wave/liquidity icon
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Price Drop Alert CTA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("price_drop_alert_card"),
                colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Price Drop Alert",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Get notified immediately when price hits ₹30 or lower.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.addPriceAlert(product.name, 30.0)
                                alertSettingActive = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceLowest),
                            shape = RoundedCornerShape(4.dp), // 4px button
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                imageVector = if (hasAlertSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Alert logo",
                                tint = PrimaryNavy
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasAlertSet) "Alert Saved" else "Alert Me",
                                color = PrimaryNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // TOGGLE indicator display list element
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (hasAlertSet) {
                                    // Remove alert trigger to toggle off
                                    val matched = activeAlerts.firstOrNull { it.itemName.equals(product.name, ignoreCase = true) && it.targetPrice <= 30.0 }
                                    if (matched != null) {
                                        viewModel.deletePriceAlert(matched)
                                    }
                                } else {
                                    viewModel.addPriceAlert(product.name, 30.0)
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (hasAlertSet) SecondaryGreenContainer else OnPrimaryNavyContainer.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Toggle bell icon",
                                tint = if (hasAlertSet) OnSecondaryGreenContainer else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (hasAlertSet) "TOGGLE ON" else "TOGGLE OFF",
                            color = if (hasAlertSet) SecondaryGreenContainer else OnPrimaryNavyContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Seasonal Tip Note Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info icon",
                        tint = PrimaryNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Prices for ${product.name} have remained stable over the last 14 days. Historical data suggests a possible seasonal drop next month.",
                        fontSize = 13.sp,
                        color = TextGray,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun MetricGridCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, OutlineVariant, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextGray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            )
        }
    }
}

@Composable
fun VolatilityGridCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, OutlineVariant, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AlertAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextGray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                // STABLE indicator badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = SecondaryGreenContainer),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "STABLE",
                        color = OnSecondaryGreenContainer,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
