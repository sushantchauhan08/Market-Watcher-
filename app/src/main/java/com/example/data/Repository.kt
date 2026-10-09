package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository(private val dao: MarketDao) {

    // --- DB Price Alerts ---
    val allAlerts: Flow<List<PriceAlert>> = dao.getAllAlerts()

    suspend fun insertAlert(alert: PriceAlert) = dao.insertAlert(alert)
    suspend fun deleteAlert(alert: PriceAlert) = dao.deleteAlert(alert)
    suspend fun deleteAlertById(id: Int) = dao.deleteAlertById(id)

    // --- DB Favorites / Bookmarks ---
    val allFavorites: Flow<List<FavoriteItem>> = dao.getAllFavorites()
    fun isFavoriteFlow(name: String) = dao.isFavoriteItemFlow(name)
    suspend fun isFavorite(name: String) = dao.isFavoriteItem(name)
    suspend fun toggleFavorite(name: String) {
        val current = dao.isFavoriteItem(name)
        if (current) {
            dao.deleteFavoriteByName(name)
        } else {
            dao.insertFavorite(FavoriteItem(itemName = name, isFavorite = true))
        }
    }

    // --- Static Product Catalog ---
    val products = listOf(
        Product(
            name = "Fresh Tomato",
            category = "Veg & Fruits",
            currentPrice = 32.0,
            unit = "kg",
            location = "Aligarh Mandi, UP",
            imageUrl = "https://images.unsplash.com/photo-1595855759920-86582396756a?auto=format&fit=crop&q=80&w=600",
            bannerImageUrl = "https://images.unsplash.com/photo-1546473533-f0b15145c24e?auto=format&fit=crop&q=80&w=600",
            changePercent = -5.0, // down 5% today
            forecastTomorrow = 30.0,
            forecastPlus3Days = 28.0,
            forecastConfidence = 90,
            trendPercentTomorrow = -3.1, // down 3.1%
            isHourlyTrendUp = false,
            priceHistory30Days = listOf(
                PricePoint("Feb 1", 28.0),
                PricePoint("Feb 5", 29.5),
                PricePoint("Feb 10", 29.0),
                PricePoint("Feb 15", 33.0),
                PricePoint("Feb 20", 35.5),
                PricePoint("Feb 25", 31.0),
                PricePoint("Today", 32.0)
            ),
            highestPrice = 42.0,
            lowestPrice = 26.0,
            averagePrice = 33.0,
            volatility = "Low",
            aiInsights = "Prices are expected to dip over the next 3 days due to increased supply from nearby regions. Market data suggests a temporary surplus from the Aligarh belt. Consider delaying large bulk purchases until +3 days for maximum savings.",
            nearbyMarkets = listOf(
                MarketOption("Market C", 4.2, 28.0, "1.2km away", "Updated 10m ago", "CHEAPEST"),
                MarketOption("Market A", 4.8, 30.0, "0.8km away", "Updated 1h ago", "STANDARD"),
                MarketOption("Market D", 3.5, 32.0, "2.5km away", "Updated 30m ago"),
                MarketOption("Market B", 2.1, 34.0, "3.1km away", "Updated 4h ago")
            ),
            dailyForecasts = listOf(
                "Today" to 32.0,
                "Tomorrow" to 31.0,
                "+2 Days" to 30.0,
                "+3 Days" to 29.0,
                "+7 Days" to 34.0
            )
        ),
        Product(
            name = "Potato",
            category = "Veg & Fruits",
            currentPrice = 24.0,
            unit = "kg",
            location = "Aligarh Mandi, UP",
            imageUrl = "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&q=80&w=600",
            bannerImageUrl = "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&q=80&w=600",
            changePercent = 2.0, // up 2% today
            forecastTomorrow = 24.5,
            forecastPlus3Days = 26.0,
            forecastConfidence = 85,
            trendPercentTomorrow = 2.1, // up 2.1%
            isHourlyTrendUp = true,
            priceHistory30Days = listOf(
                PricePoint("Feb 1", 20.0),
                PricePoint("Feb 5", 21.0),
                PricePoint("Feb 10", 22.5),
                PricePoint("Feb 15", 23.0),
                PricePoint("Feb 20", 23.5),
                PricePoint("Feb 25", 23.8),
                PricePoint("Today", 24.0)
            ),
            highestPrice = 28.0,
            lowestPrice = 19.5,
            averagePrice = 23.2,
            volatility = "Stable",
            aiInsights = "Potato supply remains firm across most central agricultural grids. A marginal price appreciation is expected due to cold storage logistics, which will taper off in 10 days.",
            nearbyMarkets = listOf(
                MarketOption("Market A", 4.1, 23.0, "0.5km away", "Updated 40m ago", "CHEAPEST"),
                MarketOption("Market C", 4.3, 24.0, "1.5km away", "Updated 1h ago", "STANDARD"),
                MarketOption("Market D", 3.2, 24.8, "2.2km away", "Updated 2h ago"),
                MarketOption("Market B", 2.0, 26.0, "3.5km away", "Updated 5h ago")
            ),
            dailyForecasts = listOf(
                "Today" to 24.0,
                "Tomorrow" to 24.5,
                "+2 Days" to 25.0,
                "+3 Days" to 26.0,
                "+7 Days" to 25.5
            )
        ),
        Product(
            name = "Onion",
            category = "Veg & Fruits",
            currentPrice = 38.0,
            unit = "kg",
            location = "Aligarh Mandi, UP",
            imageUrl = "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=600",
            bannerImageUrl = "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=600",
            changePercent = -1.5, // down 1.5% today
            forecastTomorrow = 37.0,
            forecastPlus3Days = 35.0,
            forecastConfidence = 92,
            trendPercentTomorrow = -2.6,
            isHourlyTrendUp = false,
            priceHistory30Days = listOf(
                PricePoint("Feb 1", 45.0),
                PricePoint("Feb 5", 43.5),
                PricePoint("Feb 10", 41.0),
                PricePoint("Feb 15", 39.0),
                PricePoint("Feb 20", 38.5),
                PricePoint("Feb 25", 38.2),
                PricePoint("Today", 38.0)
            ),
            highestPrice = 48.0,
            lowestPrice = 36.0,
            averagePrice = 41.5,
            volatility = "Medium",
            aiInsights = "Onion yields in Maharashtra and Madhya Pradesh markets have spiked. High shipping volume is anticipated to slash local market prices steadily over the next two weeks.",
            nearbyMarkets = listOf(
                MarketOption("Market C", 4.5, 36.0, "1.4km away", "Updated 5m ago", "CHEAPEST"),
                MarketOption("Market A", 4.7, 37.5, "1.1km away", "Updated 2h ago"),
                MarketOption("Market D", 3.0, 38.0, "2.9km away", "Updated 1h ago"),
                MarketOption("Market B", 2.5, 40.0, "3.8km away", "Updated 4h ago")
            ),
            dailyForecasts = listOf(
                "Today" to 38.0,
                "Tomorrow" to 37.0,
                "+2 Days" to 36.5,
                "+3 Days" to 35.0,
                "+7 Days" to 39.0
            )
        )
    )

    fun getProductByName(name: String): Product? {
        return products.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
