package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.data.Product
import com.example.ui.MarketViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MarketViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val activeAlerts by viewModel.activeAlertsList.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AmbientBackground)
            .verticalScroll(scrollState)
            .statusBarsPadding()
    ) {
        // 1. Header (Location Selector & Notification Bell)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Location Pin",
                tint = PrimaryNavy,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Aligarh, Uttar Pradesh",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onNavigateToAlerts,
                modifier = Modifier.testTag("notification_button")
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = PrimaryNavy
                    )
                    // Notification red badge
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(ColorError, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }

        // 2. Search Box
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search products", color = TextGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = TextGray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(4.dp), // 4px styling shape
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLow,
                unfocusedContainerColor = SurfaceContainerLow,
                disabledContainerColor = SurfaceContainerLow,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("search_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mandi AI Price Oracle Section
        val geminiLoading by viewModel.geminiLoading.collectAsState()
        val geminiError by viewModel.geminiError.collectAsState()

        if (searchQuery.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .border(1.dp, PrimaryNavy.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .testTag("mandi_ai_oracle_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini Spark",
                            tint = PrimaryNavyContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mandi AI Price Oracle",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fetch live estimations & predicted mandi prices for '$searchQuery' in Aligarh using the Gemini API.",
                        color = TextGray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (geminiLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryNavyContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Consulting Aligarh Mandi rates...",
                                fontSize = 14.sp,
                                color = PrimaryNavy
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.queryGeminiForCrop(searchQuery) { cropName ->
                                    onNavigateToDetails(cropName)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavyContainer),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                                .testTag("query_gemini_button")
                        ) {
                            Text("Query Gemini AI for: \"$searchQuery\"", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    geminiError?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = err,
                            color = ColorError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 3. Featured Hero Card "Fresh Tomato"
        val tomatoProduct = viewModel.productsList.first()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
                .testTag("hero_tomato_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = tomatoProduct.imageUrl,
                        contentDescription = "Fresh Tomatoes Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // AI Insight Chip
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .background(PrimaryNavyContainer, RoundedCornerShape(100.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI INSIGHT",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tomatoProduct.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy,
                                fontSize = 24.sp
                            )
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Current Price",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                            )
                            Text(
                                text = "₹${tomatoProduct.currentPrice.toInt()}/${tomatoProduct.unit}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavyContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tomorrow / After 3 days predictions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Tomorrow",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                                )
                                Text(
                                    text = "₹${tomatoProduct.forecastTomorrow.toInt()}/${tomatoProduct.unit}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryGreen
                                    )
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(4.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "After 3 days",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                                )
                                Text(
                                    text = "₹${tomatoProduct.forecastPlus3Days.toInt()}/${tomatoProduct.unit}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryGreen
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onNavigateToDetails(tomatoProduct.name) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavyContainer),
                        shape = RoundedCornerShape(4.dp), // 4px radius
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("view_details_button")
                    ) {
                        Text(
                            text = "View Details",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow Forward",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Categories Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            )
            Text(
                text = "See all",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PrimaryNavyContainer,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { /* See All action */ }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryItem(
                title = "Veg & Fruits",
                icon = Icons.Default.Eco,
                selectedColor = Color(0xFFA5F09B), // light green highlighted
                isSelected = selectedCategory == "Veg & Fruits"
            ) { viewModel.setCategory("Veg & Fruits") }

            CategoryItem(
                title = "Grocery",
                icon = Icons.Default.ShoppingBasket,
                selectedColor = Color(0xFFDCDFFF),
                isSelected = selectedCategory == "Grocery"
            ) { viewModel.setCategory("Grocery") }

            CategoryItem(
                title = "Dairy",
                icon = Icons.Default.Egg,
                selectedColor = Color(0xFFFFE0B2),
                isSelected = selectedCategory == "Dairy"
            ) { viewModel.setCategory("Dairy") }

            CategoryItem(
                title = "Meat & Eggs",
                icon = Icons.Default.Restaurant,
                selectedColor = Color(0xFFFFCDD2),
                isSelected = selectedCategory == "Meat & Eggs"
            ) { viewModel.setCategory("Meat & Eggs") }

            CategoryItem(
                title = "Fuel",
                icon = Icons.Default.LocalGasStation,
                selectedColor = Color(0xFFECEFF1),
                isSelected = selectedCategory == "Fuel"
            ) { viewModel.setCategory("Fuel") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Price Drop Today Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price Drop Today",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = SecondaryGreenContainer),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "LIVE",
                    color = OnSecondaryGreenContainer,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal scrolling items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredProducts.forEach { product ->
                PriceDropCard(
                    product = product,
                    onClick = { onNavigateToDetails(product.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Overall Market Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("overall_market_card"),
            colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Overall Market",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Stability Confidence: 84%",
                            color = OnPrimaryNavyContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Trending Metric",
                        tint = OnPrimaryNavyContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom Graphical bar chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Simulated status stability indices
                    val stabilityWeights = listOf(28, 42, 35, 50, 85, 48, 62, 30)
                    stabilityWeights.forEachIndexed { idx, height ->
                        val isHighlighted = idx == 4 // 84-85% peak stability day
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(height.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isHighlighted) SecondaryGreenContainer else OnPrimaryNavyContainer.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7. Bottom Double metrics cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Est savings Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SecondaryGreenContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Piggy bank",
                            tint = OnSecondaryGreenContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Est. Savings",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                        )
                        Text(
                            text = "₹450/mo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )
                    }
                }
            }

            // Alerts set card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
                    .clickable { onNavigateToAlerts() },
                colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ColorErrorContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alerts Set",
                            tint = OnColorErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Alerts Set",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                        )
                        // Dynamic calculation + mock active alert indicator
                        val alertCount = activeAlerts.size + 12
                        Text(
                            text = "$alertCount active",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun CategoryItem(
    title: String,
    icon: ImageVector,
    selectedColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    if (isSelected) selectedColor else SurfaceContainer,
                    RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) OnSecondaryGreenContainer else PrimaryNavy,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isSelected) SecondaryGreen else TextGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            ),
            maxLines = 1
        )
    }
}

@Composable
fun PriceDropCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular thumbnail of item
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Trend Indicator
                val isPriceDrop = product.changePercent < 0
                val trendColor = if (isPriceDrop) SecondaryGreen else AlertCrimson
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = if (isPriceDrop) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                        contentDescription = "Trend Icon",
                        tint = trendColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${if (product.changePercent > 0) "+" else ""}${product.changePercent.toInt()}%",
                        color = trendColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "₹${product.currentPrice.toInt()}/kg",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavyContainer
                )
            )
        }
    }
}
