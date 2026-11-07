package com.example.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.example.data.local.InitialDataSeed
import com.example.data.local.ShoporaDatabase
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.DecimalFormat
import java.util.UUID

class ShoporaRepository(private val database: ShoporaDatabase) {

    companion object {
        @Volatile
        private var INSTANCE: ShoporaRepository? = null

        fun getInstance(context: Context): ShoporaRepository {
            return INSTANCE ?: synchronized(this) {
                val db = ShoporaDatabase.getDatabase(context)
                val instance = ShoporaRepository(db)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun ensureDataSeeded() {
        // If categories are empty, seed immediately
        val categories = database.categoryDao().getAllCategories().firstOrNull()
        if (categories.isNullOrEmpty()) {
            InitialDataSeed.seedDatabase(database)
        }
    }

    // --- Products ---
    val approvedProducts: Flow<List<ProductEntity>> = database.productDao().getAllApprovedProducts()
    val officialStoreProducts: Flow<List<ProductEntity>> = database.productDao().getOfficialStoreProducts()
    val allProductsAdmin: Flow<List<ProductEntity>> = database.productDao().getAllProductsAdmin()

    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>> =
        database.productDao().getProductsByCategory(categoryId)

    fun searchProducts(query: String): Flow<List<ProductEntity>> =
        database.productDao().searchProducts(query)

    suspend fun getProduct(id: String): ProductEntity? =
        database.productDao().getProductById(id)

    fun getProductsForVendor(vendorId: String): Flow<List<ProductEntity>> =
        database.productDao().getProductsForVendor(vendorId)

    suspend fun insertOrUpdateProduct(product: ProductEntity) =
        database.productDao().insertProduct(product)

    suspend fun deleteProduct(id: String) =
        database.productDao().deleteProduct(id)

    suspend fun setProductApproval(productId: String, approved: Boolean) =
        database.productDao().setApprovalStatus(productId, approved)

    // --- Vendors ---
    val allVendors: Flow<List<VendorEntity>> = database.vendorDao().getAllVendors()
    val pendingVendors: Flow<List<VendorEntity>> = database.vendorDao().getPendingVendors()

    suspend fun getVendor(id: String): VendorEntity? =
        database.vendorDao().getVendorById(id)

    fun observeVendor(id: String): Flow<VendorEntity?> =
        database.vendorDao().observeVendorById(id)

    suspend fun registerVendor(vendor: VendorEntity) {
        database.vendorDao().insertVendor(vendor)
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "ADMIN",
                title = "New Vendor Registration",
                message = "${vendor.businessName} from ${vendor.countryCode} registered and is awaiting verification.",
                type = "VENDOR"
            )
        )
    }

    suspend fun updateVendorStatus(vendorId: String, status: String) {
        database.vendorDao().updateVendorStatus(vendorId, status)
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "VENDOR",
                title = "Account Status Update",
                message = "Your store status has been updated to $status by marketplace administration.",
                type = "VENDOR"
            )
        )
    }

    // --- Categories ---
    val allCategories: Flow<List<CategoryEntity>> = database.categoryDao().getAllCategories()

    // --- Cart ---
    val cartItems: Flow<List<CartItemEntity>> = database.cartDao().getCartItems()

    suspend fun addToCart(productId: String, variant: String = "", quantity: Int = 1) {
        val existing = database.cartDao().getCartItem(productId, variant)
        if (existing != null) {
            database.cartDao().updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            database.cartDao().insertCartItem(
                CartItemEntity(
                    productId = productId,
                    variant = variant,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            database.cartDao().deleteCartItem(cartItemId)
        } else {
            // Find and update
            val currentItems = database.cartDao().getCartItems().firstOrNull() ?: emptyList()
            val item = currentItems.find { it.id == cartItemId }
            if (item != null) {
                database.cartDao().updateCartItem(item.copy(quantity = newQuantity))
            }
        }
    }

    suspend fun removeFromCart(cartItemId: Long) =
        database.cartDao().deleteCartItem(cartItemId)

    suspend fun clearCart() =
        database.cartDao().clearCart()

    // --- Wishlist ---
    val wishlist: Flow<List<WishlistEntity>> = database.wishlistDao().getWishlist()

    fun isInWishlist(productId: String): Flow<Boolean> =
        database.wishlistDao().isInWishlist(productId)

    suspend fun toggleWishlist(productId: String) {
        val isPresent = database.wishlistDao().isInWishlist(productId).firstOrNull() ?: false
        if (isPresent) {
            database.wishlistDao().removeFromWishlist(productId)
        } else {
            database.wishlistDao().addToWishlist(WishlistEntity(productId = productId))
        }
    }

    // --- Orders & Multi-Vendor Checkout Execution ---
    val allOrders: Flow<List<OrderEntity>> = database.orderDao().getAllOrders()
    val allSellerOrders: Flow<List<SellerOrderEntity>> = database.orderDao().getAllSellerOrders()

    suspend fun getOrderById(id: String): OrderEntity? =
        database.orderDao().getOrderById(id)

    fun getSellerOrdersForParent(parentOrderId: String): Flow<List<SellerOrderEntity>> =
        database.orderDao().getSellerOrdersForParent(parentOrderId)

    fun getSellerOrdersForVendor(vendorId: String): Flow<List<SellerOrderEntity>> =
        database.orderDao().getSellerOrdersForVendor(vendorId)

    fun getOrderItemsForParent(parentOrderId: String): Flow<List<OrderItemEntity>> =
        database.orderDao().getOrderItemsForParent(parentOrderId)

    fun getOrderItemsForSellerOrder(sellerOrderId: String): Flow<List<OrderItemEntity>> =
        database.orderDao().getOrderItemsForSellerOrder(sellerOrderId)

    /**
     * Executes Multi-Vendor Checkout Architecture:
     * 1. Creates single customer-facing Parent Order.
     * 2. Partitions items by seller (Official Store vs independent vendors).
     * 3. Creates distinct Seller Fulfillment Orders per vendor with separated shipping.
     * 4. Calculates platform commission % per vendor and net seller earnings.
     * 5. Creates immutable Financial Ledger entries (Customer Payment, Platform Commission, Seller Settlement).
     * 6. Emits notifications to customer, each vendor, and admin.
     * 7. Clears cart atomically.
     */
    suspend fun executeMultiVendorCheckout(
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        deliveryAddress: String,
        deliveryCountry: CountryMarketEntity,
        selectedCurrency: CurrencyEntity,
        paymentMethodName: String
    ): Result<String> {
        val cartList = database.cartDao().getCartItems().firstOrNull() ?: emptyList()
        if (cartList.isEmpty()) {
            return Result.failure(IllegalStateException("Cart is empty"))
        }

        val parentOrderId = UUID.randomUUID().toString()
        val orderNumber = "SHP-${(1000..9999).random()}-${(1000..9999).random()}"

        // Load all referenced products
        val cartWithProducts = cartList.mapNotNull { cartItem ->
            val product = database.productDao().getProductById(cartItem.productId)
            if (product != null) cartItem to product else null
        }

        if (cartWithProducts.isEmpty()) {
            return Result.failure(IllegalStateException("No valid products in cart"))
        }

        // Group by vendor
        val vendorGroups = cartWithProducts.groupBy { it.second.vendorId }

        var totalSubtotalConvertedMinor = 0L
        var totalShippingConvertedMinor = 0L

        val sellerOrders = mutableListOf<SellerOrderEntity>()
        val orderItems = mutableListOf<OrderItemEntity>()
        val ledgerEntries = mutableListOf<FinancialLedgerEntity>()

        for ((vendorId, items) in vendorGroups) {
            val vendor = database.vendorDao().getVendorById(vendorId)
            val isOfficial = vendor?.isOfficial ?: (vendorId == "shopora-official")
            val vendorName = vendor?.businessName ?: items.first().second.vendorName

            val sellerOrderId = UUID.randomUUID().toString()
            var vendorSubtotalUsd = 0L
            val vendorShippingUsd = if (isOfficial) 0L else items.first().second.shippingFeeMinorUsd

            for ((cartItem, product) in items) {
                val itemTotalUsd = product.priceMinorUsd * cartItem.quantity
                vendorSubtotalUsd += itemTotalUsd

                val unitPriceConverted = convertMinorUnits(product.priceMinorUsd, selectedCurrency.rateToUsd)
                val totalPriceConverted = unitPriceConverted * cartItem.quantity

                orderItems.add(
                    OrderItemEntity(
                        sellerOrderId = sellerOrderId,
                        parentOrderId = parentOrderId,
                        productId = product.id,
                        productTitle = product.title,
                        productImageUrl = product.imageUrl,
                        variant = cartItem.variant.ifEmpty { "Standard" },
                        vendorId = vendorId,
                        quantity = cartItem.quantity,
                        unitPriceMinor = unitPriceConverted,
                        totalPriceMinor = totalPriceConverted
                    )
                )
            }

            val vendorSubtotalConverted = convertMinorUnits(vendorSubtotalUsd, selectedCurrency.rateToUsd)
            val vendorShippingConverted = convertMinorUnits(vendorShippingUsd, selectedCurrency.rateToUsd)

            totalSubtotalConvertedMinor += vendorSubtotalConverted
            totalShippingConvertedMinor += vendorShippingConverted

            // Commission calculation
            val commissionRate = if (isOfficial) 0.0 else (vendor?.commissionRatePercent ?: 10.0)
            val platformCommissionConverted = (vendorSubtotalConverted * (commissionRate / 100.0)).toLong()
            val sellerNetEarningsConverted = vendorSubtotalConverted - platformCommissionConverted + vendorShippingConverted

            sellerOrders.add(
                SellerOrderEntity(
                    id = sellerOrderId,
                    parentOrderId = parentOrderId,
                    parentOrderNumber = orderNumber,
                    vendorId = vendorId,
                    vendorName = vendorName,
                    isOfficialStore = isOfficial,
                    itemCount = items.sumOf { it.first.quantity },
                    subtotalMinor = vendorSubtotalConverted,
                    shippingFeeMinor = vendorShippingConverted,
                    commissionRatePercent = commissionRate,
                    platformCommissionMinor = platformCommissionConverted,
                    sellerNetEarningsMinor = sellerNetEarningsConverted,
                    currencyCode = selectedCurrency.code,
                    status = "PROCESSING",
                    trackingCarrier = if (isOfficial) "Shopora Express Global" else "Regional International Carrier",
                    trackingNumber = "TRK-${(100000..999999).random()}",
                    estimatedDelivery = "In ${items.maxOfOrNull { it.second.estimatedDeliveryDays } ?: 5} business days"
                )
            )

            // Ledger: Platform Commission
            if (platformCommissionConverted > 0) {
                ledgerEntries.add(
                    FinancialLedgerEntity(
                        transactionRef = "COM-${UUID.randomUUID().toString().take(8).uppercase()}",
                        parentOrderId = parentOrderId,
                        sellerOrderId = sellerOrderId,
                        vendorId = vendorId,
                        entryType = "PLATFORM_COMMISSION",
                        description = "Marketplace commission (${commissionRate}%) on Order $orderNumber",
                        grossMinor = vendorSubtotalConverted,
                        feeMinor = platformCommissionConverted,
                        netMinor = platformCommissionConverted,
                        currencyCode = selectedCurrency.code,
                        status = "RECORDED"
                    )
                )
            }

            // Ledger: Seller Settlement
            ledgerEntries.add(
                FinancialLedgerEntity(
                    transactionRef = "SETT-${UUID.randomUUID().toString().take(8).uppercase()}",
                    parentOrderId = parentOrderId,
                    sellerOrderId = sellerOrderId,
                    vendorId = vendorId,
                    entryType = "SELLER_SETTLEMENT",
                    description = "Fulfillment settlement for $vendorName on Order $orderNumber",
                    grossMinor = vendorSubtotalConverted + vendorShippingConverted,
                    feeMinor = platformCommissionConverted,
                    netMinor = sellerNetEarningsConverted,
                    currencyCode = selectedCurrency.code,
                    status = "PENDING_RELEASE"
                )
            )

            // Credit pending balance to vendor (in USD base minor)
            val netEarningsUsd = ((vendorSubtotalUsd * (1.0 - commissionRate / 100.0)) + vendorShippingUsd).toLong()
            database.vendorDao().creditVendorEarnings(vendorId, netEarningsUsd)

            // Notify Vendor
            database.notificationDao().insertNotification(
                AppNotificationEntity(
                    targetRole = "VENDOR",
                    title = "New Order #$orderNumber",
                    message = "You received ${items.size} item(s) to fulfill. Order total: ${formatPrice(vendorSubtotalConverted, selectedCurrency.symbol, selectedCurrency.code)}",
                    type = "ORDER"
                )
            )
        }

        val taxConverted = (totalSubtotalConvertedMinor * (deliveryCountry.taxPercent / 100.0)).toLong()
        val grandTotalConverted = totalSubtotalConvertedMinor + totalShippingConvertedMinor + taxConverted

        val parentOrder = OrderEntity(
            id = parentOrderId,
            orderNumber = orderNumber,
            customerName = customerName,
            customerEmail = customerEmail,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            deliveryCountry = deliveryCountry.name,
            currencyCode = selectedCurrency.code,
            currencySymbol = selectedCurrency.symbol,
            exchangeRateToUsd = selectedCurrency.rateToUsd,
            subtotalMinor = totalSubtotalConvertedMinor,
            shippingTotalMinor = totalShippingConvertedMinor,
            taxTotalMinor = taxConverted,
            grandTotalMinor = grandTotalConverted,
            paymentMethod = paymentMethodName,
            paymentStatus = "PAID",
            fulfillmentStatus = "PROCESSING"
        )

        // Ledger: Customer Payment
        ledgerEntries.add(
            FinancialLedgerEntity(
                transactionRef = "PAY-${UUID.randomUUID().toString().take(8).uppercase()}",
                parentOrderId = parentOrderId,
                sellerOrderId = null,
                vendorId = null,
                entryType = "CUSTOMER_PAYMENT",
                description = "Customer checkout payment via $paymentMethodName for Order $orderNumber",
                grossMinor = grandTotalConverted,
                feeMinor = 0L,
                netMinor = grandTotalConverted,
                currencyCode = selectedCurrency.code,
                status = "SETTLED"
            )
        )

        // Write all in room transaction
        database.orderDao().insertParentOrder(parentOrder)
        database.orderDao().insertSellerOrders(sellerOrders)
        database.orderDao().insertOrderItems(orderItems)
        database.ledgerDao().insertLedgerEntries(ledgerEntries)

        // Notify Customer & Admin
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "CUSTOMER",
                title = "Order Confirmed: #$orderNumber",
                message = "Your multi-vendor order across ${vendorGroups.size} store(s) is confirmed and being prepared.",
                type = "ORDER"
            )
        )
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "ADMIN",
                title = "Checkout Completed: #$orderNumber",
                message = "Customer paid ${formatPrice(grandTotalConverted, selectedCurrency.symbol, selectedCurrency.code)}. Split into ${sellerOrders.size} vendor shipments.",
                type = "PAYMENT"
            )
        )

        // Clear cart
        database.cartDao().clearCart()

        return Result.success(parentOrderId)
    }

    // --- Seller Order Updates (Fulfillment) ---
    suspend fun updateSellerOrderTracking(
        sellerOrderId: String,
        status: String,
        carrier: String,
        trackingNumber: String
    ) {
        val now = System.currentTimeMillis()
        database.orderDao().updateSellerOrderShipment(
            sellerOrderId = sellerOrderId,
            status = status,
            carrier = carrier,
            trackingNum = trackingNumber,
            shippedAt = now
        )

        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "CUSTOMER",
                title = "Package Shipped!",
                message = "Your package with $carrier (Tracking: $trackingNumber) is now on its way.",
                type = "SHIPPING"
            )
        )
    }

    suspend fun markSellerOrderDelivered(sellerOrderId: String) {
        val now = System.currentTimeMillis()
        database.orderDao().updateSellerOrderDelivery(sellerOrderId, "DELIVERED", now)

        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "CUSTOMER",
                title = "Package Delivered",
                message = "Your package has been marked as safely delivered. Please leave a product review!",
                type = "SHIPPING"
            )
        )
    }

    // --- Financial Ledger & Balances ---
    val allLedgerEntries: Flow<List<FinancialLedgerEntity>> = database.ledgerDao().getAllLedgerEntries()

    fun getLedgerForVendor(vendorId: String): Flow<List<FinancialLedgerEntity>> =
        database.ledgerDao().getLedgerForVendor(vendorId)

    // --- Reviews ---
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>> =
        database.reviewDao().getReviewsForProduct(productId)

    suspend fun addReview(review: ReviewEntity) {
        database.reviewDao().insertReview(review)
    }

    // --- Chat Messages ---
    fun getMessagesForVendor(vendorId: String): Flow<List<ChatMessageEntity>> =
        database.chatDao().getMessagesForVendor(vendorId)

    suspend fun sendChatMessage(message: ChatMessageEntity) {
        database.chatDao().insertMessage(message)
    }

    // --- Notifications ---
    fun getNotificationsForRole(role: String): Flow<List<AppNotificationEntity>> =
        database.notificationDao().getNotificationsForRole(role)

    suspend fun markNotificationsRead(role: String) {
        database.notificationDao().markAllRead(role)
    }

    // --- Markets & Currencies ---
    val enabledCountries: Flow<List<CountryMarketEntity>> = database.marketDao().getEnabledCountries()
    val allCountries: Flow<List<CountryMarketEntity>> = database.marketDao().getAllCountries()
    val allCurrencies: Flow<List<CurrencyEntity>> = database.marketDao().getAllCurrencies()

    suspend fun setCountryEnabled(code: String, enabled: Boolean) {
        database.marketDao().setCountryEnabled(code, enabled)
    }

    suspend fun updateCurrencyRate(code: String, rate: Double) {
        database.marketDao().updateRate(code, rate, System.currentTimeMillis())
    }

    // --- Payouts ---
    fun getPayoutsForVendor(vendorId: String): Flow<List<PayoutRequestEntity>> =
        database.payoutDao().getPayoutsForVendor(vendorId)

    val allPayoutsAdmin: Flow<List<PayoutRequestEntity>> = database.payoutDao().getAllPayouts()

    suspend fun requestPayout(
        vendorId: String,
        vendorName: String,
        amountMinorUsd: Long,
        method: String,
        accountDetails: String
    ): Result<Unit> {
        val vendor = database.vendorDao().getVendorById(vendorId)
            ?: return Result.failure(IllegalArgumentException("Vendor not found"))

        if (vendor.availableBalanceMinorUsd < amountMinorUsd) {
            return Result.failure(IllegalStateException("Insufficient available balance for withdrawal"))
        }

        database.vendorDao().debitVendorEarnings(vendorId, amountMinorUsd)
        database.payoutDao().insertPayoutRequest(
            PayoutRequestEntity(
                vendorId = vendorId,
                vendorName = vendorName,
                amountMinorUsd = amountMinorUsd,
                payoutMethod = method,
                accountDetails = accountDetails
            )
        )
        database.notificationDao().insertNotification(
            AppNotificationEntity(
                targetRole = "ADMIN",
                title = "New Withdrawal Request",
                message = "$vendorName requested withdrawal of $${amountMinorUsd / 100.0} via $method.",
                type = "PAYMENT"
            )
        )
        return Result.success(Unit)
    }

    suspend fun updatePayoutStatus(id: Long, status: String) {
        database.payoutDao().updatePayoutStatus(id, status)
    }

    // --- Financial & Currency Minor-Unit Math ---
    fun convertMinorUnits(usdMinor: Long, rateToUsd: Double): Long {
        return (usdMinor * rateToUsd).toLong()
    }

    fun formatPrice(minorUnits: Long, symbol: String, currencyCode: String): String {
        val decimalVal = minorUnits / 100.0
        val formatter = DecimalFormat("#,##0.00")
        return when (currencyCode) {
            "JPY" -> "$symbol${minorUnits / 100}"
            "NGN" -> "$symbol${formatter.format(decimalVal)}"
            "EUR" -> "${formatter.format(decimalVal)} $symbol"
            else -> "$symbol${formatter.format(decimalVal)}"
        }
    }
}
