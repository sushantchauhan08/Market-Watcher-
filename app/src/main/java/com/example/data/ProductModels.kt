package com.example.data

data class MarketOption(
    val name: String,
    val rating: Double,
    val price: Double,
    val distance: String,
    val updatedText: String,
    val badge: String? = null // "CHEAPEST", "STANDARD"
)

data class PricePoint(
    val label: String, // e.g. "Feb 1", "Feb 15", "Today"
    val price: Double
)

data class Product(
    val name: String,
    val category: String, // "Veg & Fruits", "Grocery", "Dairy", "Meat & Eggs", "Fuel"
    val currentPrice: Double,
    val unit: String = "kg",
    val location: String = "Aligarh Mandi, UP",
    val imageUrl: String,
    val bannerImageUrl: String,
    val changePercent: Double, // positive or negative
    val forecastTomorrow: Double,
    val forecastPlus3Days: Double,
    val forecastConfidence: Int, // e.g. 94 or 90
    val trendPercentTomorrow: Double, // e.g. -3.1
    val isHourlyTrendUp: Boolean = false,
    val priceHistory30Days: List<PricePoint>,
    val highestPrice: Double,
    val lowestPrice: Double,
    val averagePrice: Double,
    val volatility: String, // "Stable", "Low", "Medium", "High"
    val aiInsights: String,
    val nearbyMarkets: List<MarketOption>,
    val dailyForecasts: List<Pair<String, Double>> // "Today", "Tomorrow", "+2 Days", "+3 Days", "+7 Days"
)
