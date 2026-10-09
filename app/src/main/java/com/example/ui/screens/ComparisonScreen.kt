package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MarketOption
import com.example.ui.MarketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    viewModel: MarketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product by viewModel.selectedProduct.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Market Comparison", color = PrimaryNavy, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Location focus mock */ }) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = "Mandi Location", tint = PrimaryNavy)
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
            // 1. Current Viewing Item row
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
                        text = "CURRENTLY VIEWING",
                        color = OnPrimaryNavyContainer,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${product.name} (1 kg)",
                        color = PrimaryNavy,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // 2. Average Market Price Blue Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("average_market_price_card"),
                colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Average Market Price",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "₹${product.currentPrice.toInt() - 1}", // e.g. ₹31
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 38.sp
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = "Trend down",
                                tint = SecondaryGreenContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "-2.4%",
                                color = SecondaryGreenContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Past 24h",
                            color = OnPrimaryNavyContainer,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Section Header "NEARBY OPTIONS"
            Text(
                text = "NEARBY OPTIONS",
                color = TextGray,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. List of market comparison cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                product.nearbyMarkets.forEach { option ->
                    MarketComparisonCard(option = option)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. User-reported Disclaimer note card
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
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Prices are user-reported and may vary at the time of purchase. Use the star ratings to gauge accuracy based on community feedback.",
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
fun MarketComparisonCard(option: MarketOption) {
    val isCheapest = option.badge == "CHEAPEST"
    val borderHighlightColor = if (isCheapest) SecondaryGreen else OutlineVariant
    val borderHighlightThickness = if (isCheapest) 1.5.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderHighlightThickness, borderHighlightColor, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Hot Row (Market Name, rating stars, price, Badge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Stars row + numerical score (rating)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRatingBar(rating = option.rating)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${option.rating})",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Price and Badge on the right
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${option.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isCheapest) SecondaryGreen else PrimaryNavyContainer,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (option.badge != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCheapest) SecondaryGreenContainer else SurfaceContainerLow
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = option.badge,
                                color = if (isCheapest) OnSecondaryGreenContainer else PrimaryNavy,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(10.dp))

            // Cold Row (Distance icon text, Update time icon text)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Location Arrow icon",
                        tint = TextGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = option.distance,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Clock update icon",
                        tint = TextGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = option.updatedText,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingBar(rating: Double) {
    Row {
        val filledStars = rating.toInt()
        val hasHalfStar = (rating - filledStars) >= 0.5

        for (i in 1..5) {
            when {
                i <= filledStars -> {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Filled",
                        tint = AlertAmber,
                        modifier = Modifier.size(14.dp)
                    )
                }
                i == filledStars + 1 && hasHalfStar -> {
                    Icon(
                        imageVector = Icons.Default.StarHalf,
                        contentDescription = "Star Half",
                        tint = AlertAmber,
                        modifier = Modifier.size(14.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Star Empty",
                        tint = OutlineVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
