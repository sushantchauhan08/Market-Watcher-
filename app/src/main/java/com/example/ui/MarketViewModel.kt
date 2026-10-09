package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MarketDatabase.getDatabase(application)
    private val repository = Repository(db.marketDao())

    // All available static items in catalog
    val productsList = repository.products

    // Dynamic products list including any added via Gemini API search
    private val _dynamicProductsList = MutableStateFlow<List<Product>>(repository.products)
    val dynamicProductsList: StateFlow<List<Product>> = _dynamicProductsList.asStateFlow()

    // Gemini API integration states
    private val geminiRepo = GeminiRepository()

    private val _geminiLoading = MutableStateFlow(false)
    val geminiLoading: StateFlow<Boolean> = _geminiLoading.asStateFlow()

    private val _geminiError = MutableStateFlow<String?>(null)
    val geminiError: StateFlow<String?> = _geminiError.asStateFlow()

    // Search queries & filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Screen-level state: Selected Product name
    private val _selectedProduct = MutableStateFlow<Product>(repository.products.first())
    val selectedProduct: StateFlow<Product> = _selectedProduct.asStateFlow()

    // Chart timeline filter: "7 Days", "30 Days", "6 Months", "1 Year"
    private val _selectedHistoryRange = MutableStateFlow("30 Days")
    val selectedHistoryRange: StateFlow<String> = _selectedHistoryRange.asStateFlow()

    // Room Persistent states:
    val activeAlertsList: StateFlow<List<PriceAlert>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteItemsList: StateFlow<List<FavoriteItem>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Computed filtered product list
    val filteredProducts: StateFlow<List<Product>> = combine(
        _searchQuery,
        _selectedCategory,
        _dynamicProductsList
    ) { query, category, list ->
        list.filter { product ->
            val matchesQuery = product.name.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || product.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.products)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = if (_selectedCategory.value == category) "All" else category
    }

    fun queryGeminiForCrop(cropName: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _geminiLoading.value = true
            _geminiError.value = null
            try {
                val geminiResponse = geminiRepo.fetchMarketPriceFromGemini(cropName)
                if (geminiResponse != null) {
                    val newProduct = geminiResponse.toProduct()
                    // Add or overwrite in dynamic list
                    val currentList = _dynamicProductsList.value.toMutableList()
                    val existingIdx = currentList.indexOfFirst { it.name.equals(newProduct.name, ignoreCase = true) }
                    if (existingIdx >= 0) {
                        currentList[existingIdx] = newProduct
                    } else {
                        currentList.add(newProduct)
                    }
                    _dynamicProductsList.value = currentList
                    _selectedProduct.value = newProduct
                    onSuccess(newProduct.name)
                } else {
                    _geminiError.value = "Ensure you set a valid GEMINI_API_KEY inside the Secrets panel tab on Google AI Studio."
                }
            } catch (e: Exception) {
                _geminiError.value = "Network or API Key failure. Please retry."
            } finally {
                _geminiLoading.value = false
            }
        }
    }

    fun selectProduct(name: String) {
        val prod = _dynamicProductsList.value.firstOrNull { it.name.equals(name, ignoreCase = true) }
        if (prod != null) {
            _selectedProduct.value = prod
        }
    }

    fun setHistoryRange(range: String) {
        _selectedHistoryRange.value = range
    }

    // Is current product bookmarked?
    fun isProductBookmarked(name: String): Flow<Boolean> {
        return repository.isFavoriteFlow(name)
    }

    fun toggleProductBookmark(name: String) {
        viewModelScope.launch {
            repository.toggleFavorite(name)
        }
    }

    // Add alert
    fun addPriceAlert(name: String, targetPrice: Double, comparison: String = "LESS_THAN") {
        viewModelScope.launch {
            repository.insertAlert(
                PriceAlert(
                    itemName = name,
                    targetPrice = targetPrice,
                    comparisonType = comparison,
                    isActive = true
                )
            )
        }
    }

    fun deletePriceAlert(alert: PriceAlert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }

    fun deleteAlertById(id: Int) {
        viewModelScope.launch {
            repository.deleteAlertById(id)
        }
    }
}
