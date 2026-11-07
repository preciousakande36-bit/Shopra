package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.*
import com.example.data.repository.ShoporaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppRole {
    CUSTOMER,
    VENDOR,
    ADMIN,
    OFFICIAL_STORE_MANAGER
}

enum class AppScreen {
    HOME,
    CATEGORIES,
    WISHLIST,
    CART,
    ACCOUNT,
    SEARCH,
    PRODUCT_DETAIL,
    CHECKOUT,
    ORDER_SUCCESS,
    ORDER_HISTORY,
    ORDER_DETAIL,
    VENDOR_DASHBOARD,
    ADMIN_CONTROL,
    CHAT,
    NOTIFICATIONS
}

enum class SortOrder {
    RELEVANCE,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    HIGHEST_RATED
}

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

class ShoporaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ShoporaRepository.getInstance(application)

    // Current Screen & Backstack
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<AppScreen>()

    // Current Role
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    // Selected Vendor for Vendor Portal (e.g. Tokyo Audio or Lagos Leather)
    private val _activeVendorId = MutableStateFlow("vendor-tokyo-audio")
    val activeVendorId: StateFlow<String> = _activeVendorId.asStateFlow()

    // Selected Product for Detail Screen
    private val _selectedProductId = MutableStateFlow<String?>(null)
    val selectedProductId: StateFlow<String?> = _selectedProductId.asStateFlow()

    // Selected Order for Detail Screen
    private val _selectedOrderId = MutableStateFlow<String?>(null)
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.RELEVANCE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Selected Country & Currency
    private val _selectedCountry = MutableStateFlow(
        CountryMarketEntity("US", "United States", "USD", "$", "🇺🇸", 499L, 7.5, true)
    )
    val selectedCountry: StateFlow<CountryMarketEntity> = _selectedCountry.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(
        CurrencyEntity("USD", "US Dollar", "$", 1.0)
    )
    val selectedCurrency: StateFlow<CurrencyEntity> = _selectedCurrency.asStateFlow()

    // UI Message / Toast / Alert
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Recent placed order ID
    private val _latestPlacedOrderId = MutableStateFlow<String?>(null)
    val latestPlacedOrderId: StateFlow<String?> = _latestPlacedOrderId.asStateFlow()

    // Active Chat Vendor
    private val _activeChatVendorId = MutableStateFlow("vendor-tokyo-audio")
    val activeChatVendorId: StateFlow<String> = _activeChatVendorId.asStateFlow()

    // Data streams from repository
    val allProducts: StateFlow<List<ProductEntity>> = repository.approvedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val officialStoreProducts: StateFlow<List<ProductEntity>> = repository.officialStoreProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVendors: StateFlow<List<VendorEntity>> = repository.allVendors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingVendors: StateFlow<List<VendorEntity>> = repository.pendingVendors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCountries: StateFlow<List<CountryMarketEntity>> = repository.allCountries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCurrencies: StateFlow<List<CurrencyEntity>> = repository.allCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlist: StateFlow<List<WishlistEntity>> = repository.wishlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSellerOrders: StateFlow<List<SellerOrderEntity>> = repository.allSellerOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialLedger: StateFlow<List<FinancialLedgerEntity>> = repository.allLedgerEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotificationEntity>> = repository.getNotificationsForRole("CUSTOMER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Items with resolved Product info
    val cartWithProducts: StateFlow<List<CartItemWithProduct>> = combine(
        repository.cartItems,
        allProducts
    ) { items, prods ->
        items.mapNotNull { item ->
            val product = prods.find { it.id == item.productId }
            if (product != null) CartItemWithProduct(item, product) else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Vendor Entity
    val activeVendor: StateFlow<VendorEntity?> = combine(
        _activeVendorId,
        allVendors
    ) { id, vendors ->
        vendors.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active Vendor's products
    val activeVendorProducts: StateFlow<List<ProductEntity>> = combine(
        _activeVendorId,
        allProducts
    ) { vendorId, products ->
        products.filter { it.vendorId == vendorId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Vendor's fulfillment orders
    val activeVendorOrders: StateFlow<List<SellerOrderEntity>> = combine(
        _activeVendorId,
        allSellerOrders
    ) { vendorId, orders ->
        orders.filter { it.vendorId == vendorId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Vendor's ledger entries
    val activeVendorLedger: StateFlow<List<FinancialLedgerEntity>> = combine(
        _activeVendorId,
        financialLedger
    ) { vendorId, entries ->
        entries.filter { it.vendorId == vendorId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureDataSeeded()
        }
    }

    // --- Navigation ---
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        } else if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            return true
        }
        return false
    }

    fun setRole(role: AppRole) {
        _currentRole.value = role
        when (role) {
            AppRole.CUSTOMER -> navigateTo(AppScreen.HOME)
            AppRole.VENDOR -> {
                _activeVendorId.value = "vendor-tokyo-audio"
                navigateTo(AppScreen.VENDOR_DASHBOARD)
            }
            AppRole.OFFICIAL_STORE_MANAGER -> {
                _activeVendorId.value = "shopora-official"
                navigateTo(AppScreen.VENDOR_DASHBOARD)
            }
            AppRole.ADMIN -> navigateTo(AppScreen.ADMIN_CONTROL)
        }
        showSnackbar("Switched to ${role.name.replace('_', ' ')} mode")
    }

    fun setActiveVendorId(vendorId: String) {
        _activeVendorId.value = vendorId
    }

    fun openProductDetail(productId: String) {
        _selectedProductId.value = productId
        navigateTo(AppScreen.PRODUCT_DETAIL)
    }

    fun openOrderDetail(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(AppScreen.ORDER_DETAIL)
    }

    fun openChatWithVendor(vendorId: String) {
        _activeChatVendorId.value = vendorId
        navigateTo(AppScreen.CHAT)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty() && _currentScreen.value != AppScreen.SEARCH) {
            navigateTo(AppScreen.SEARCH)
        }
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
        if (categoryId != null && _currentScreen.value != AppScreen.SEARCH) {
            navigateTo(AppScreen.SEARCH)
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setCountry(country: CountryMarketEntity) {
        _selectedCountry.value = country
        // Auto match currency if exists
        viewModelScope.launch {
            val curr = allCurrencies.value.find { it.code == country.currencyCode }
            if (curr != null) {
                _selectedCurrency.value = curr
            }
            showSnackbar("Market set to ${country.flag} ${country.name} (${country.currencyCode})")
        }
    }

    fun setCurrency(currency: CurrencyEntity) {
        _selectedCurrency.value = currency
        showSnackbar("Display currency changed to ${currency.code} (${currency.symbol})")
    }

    // --- Cart Actions ---
    fun addToCart(productId: String, variant: String = "", quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, variant, quantity)
            showSnackbar("Added to cart")
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, newQuantity)
        }
    }

    fun removeFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
            showSnackbar("Item removed from cart")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            showSnackbar("Shopping cart cleared")
        }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
            val isWishlisted = wishlist.value.any { it.productId == productId }
            showSnackbar(if (isWishlisted) "Removed from wishlist" else "Added to wishlist")
        }
    }

    // --- Multi-Vendor Checkout ---
    fun executeCheckout(
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        deliveryAddress: String,
        paymentMethodName: String
    ) {
        viewModelScope.launch {
            val result = repository.executeMultiVendorCheckout(
                customerName = customerName,
                customerEmail = customerEmail,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                deliveryCountry = _selectedCountry.value,
                selectedCurrency = _selectedCurrency.value,
                paymentMethodName = paymentMethodName
            )
            result.onSuccess { parentOrderId ->
                _latestPlacedOrderId.value = parentOrderId
                _selectedOrderId.value = parentOrderId
                navigateTo(AppScreen.ORDER_SUCCESS)
            }.onFailure { err ->
                showSnackbar("Checkout failed: ${err.message}")
            }
        }
    }

    // --- Vendor Fulfillment Actions ---
    fun updateOrderTracking(
        sellerOrderId: String,
        status: String,
        carrier: String,
        trackingNumber: String
    ) {
        viewModelScope.launch {
            repository.updateSellerOrderTracking(
                sellerOrderId = sellerOrderId,
                status = status,
                carrier = carrier,
                trackingNumber = trackingNumber
            )
            showSnackbar("Shipment tracking updated ($carrier: $trackingNumber)")
        }
    }

    fun markOrderDelivered(sellerOrderId: String) {
        viewModelScope.launch {
            repository.markSellerOrderDelivered(sellerOrderId)
            showSnackbar("Order marked as Delivered. Funds released to available balance!")
        }
    }

    fun addNewProduct(
        title: String,
        description: String,
        categoryId: String,
        priceUsd: Double,
        stock: Int,
        brand: String,
        variants: String,
        specs: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            val vendor = activeVendor.value ?: return@launch
            val newProd = ProductEntity(
                id = "prod-${UUID.randomUUID().toString().take(8)}",
                title = title,
                description = description,
                categoryId = categoryId,
                vendorId = vendor.id,
                vendorName = vendor.businessName,
                isOfficialStore = vendor.isOfficial,
                priceMinorUsd = (priceUsd * 100).toLong(),
                originalPriceMinorUsd = (priceUsd * 120).toLong(),
                stock = stock,
                rating = 5.0,
                reviewCount = 0,
                imageUrl = imageUrl.ifEmpty { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80" },
                brand = brand.ifEmpty { vendor.businessName },
                originCountry = vendor.countryCode,
                variantsJson = variants,
                specificationsJson = specs,
                isFeatured = false,
                isApproved = true,
                shippingFeeMinorUsd = if (vendor.isOfficial) 0L else 599L
            )
            repository.insertOrUpdateProduct(newProd)
            showSnackbar("Product '${newProd.title}' published successfully!")
        }
    }

    fun requestVendorPayout(amountUsd: Double, method: String, details: String) {
        viewModelScope.launch {
            val vendor = activeVendor.value ?: return@launch
            val amountMinor = (amountUsd * 100).toLong()
            val res = repository.requestPayout(
                vendorId = vendor.id,
                vendorName = vendor.businessName,
                amountMinorUsd = amountMinor,
                method = method,
                accountDetails = details
            )
            res.onSuccess {
                showSnackbar("Withdrawal request of $${amountUsd} submitted for approval")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Payout request failed")
            }
        }
    }

    // --- Admin Actions ---
    fun approveVendor(vendorId: String) {
        viewModelScope.launch {
            repository.updateVendorStatus(vendorId, "APPROVED")
            showSnackbar("Vendor approved for marketplace trading")
        }
    }

    fun suspendVendor(vendorId: String) {
        viewModelScope.launch {
            repository.updateVendorStatus(vendorId, "SUSPENDED")
            showSnackbar("Vendor has been suspended")
        }
    }

    fun setCountryEnabled(code: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setCountryEnabled(code, enabled)
            showSnackbar("Country $code ${if (enabled) "enabled" else "disabled"}")
        }
    }

    // --- Chat & Reviews ---
    fun sendChatMessage(vendorId: String, text: String, productRefTitle: String? = null) {
        viewModelScope.launch {
            val msg = ChatMessageEntity(
                vendorId = vendorId,
                vendorName = allVendors.value.find { it.id == vendorId }?.businessName ?: "Vendor",
                senderRole = if (_currentRole.value == AppRole.VENDOR) "VENDOR" else "CUSTOMER",
                senderName = if (_currentRole.value == AppRole.VENDOR) activeVendor.value?.businessName ?: "Seller" else "Customer",
                message = text,
                productRefTitle = productRefTitle
            )
            repository.sendChatMessage(msg)
        }
    }

    fun submitReview(productId: String, vendorId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val review = ReviewEntity(
                productId = productId,
                vendorId = vendorId,
                authorName = "Jordan Davis",
                rating = rating,
                comment = comment,
                isVerifiedPurchase = true,
                countryCode = _selectedCountry.value.code
            )
            repository.addReview(review)
            showSnackbar("Review submitted! Thank you for rating.")
        }
    }

    // --- Helper Formatting ---
    fun formatPrice(usdMinor: Long): String {
        val curr = _selectedCurrency.value
        val convertedMinor = repository.convertMinorUnits(usdMinor, curr.rateToUsd)
        return repository.formatPrice(convertedMinor, curr.symbol, curr.code)
    }

    fun formatConvertedMinor(convertedMinor: Long, symbol: String, code: String): String {
        return repository.formatPrice(convertedMinor, symbol, code)
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Chat messages stream for active chat vendor
    fun getActiveChatMessages(): Flow<List<ChatMessageEntity>> =
        repository.getMessagesForVendor(_activeChatVendorId.value)

    // Reviews stream for selected product
    fun getProductReviews(productId: String): Flow<List<ReviewEntity>> =
        repository.getReviewsForProduct(productId)
}
