package com.example.data.local.daos

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isApproved = 1 ORDER BY isFeatured DESC, rating DESC")
    fun getAllApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND isOfficialStore = 1")
    fun getOfficialStoreProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND categoryId = :categoryId")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR vendorName LIKE '%' || :query || '%')")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE vendorId = :vendorId")
    fun getProductsForVendor(vendorId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProductsAdmin(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("UPDATE products SET isApproved = :approved WHERE id = :productId")
    suspend fun setApprovalStatus(productId: String, approved: Boolean)
}

@Dao
interface VendorDao {
    @Query("SELECT * FROM vendors ORDER BY isOfficial DESC, rating DESC")
    fun getAllVendors(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors WHERE id = :id")
    suspend fun getVendorById(id: String): VendorEntity?

    @Query("SELECT * FROM vendors WHERE id = :id")
    fun observeVendorById(id: String): Flow<VendorEntity?>

    @Query("SELECT * FROM vendors WHERE status = 'PENDING'")
    fun getPendingVendors(): Flow<List<VendorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: VendorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendors(vendors: List<VendorEntity>)

    @Update
    suspend fun updateVendor(vendor: VendorEntity)

    @Query("UPDATE vendors SET status = :status WHERE id = :vendorId")
    suspend fun updateVendorStatus(vendorId: String, status: String)

    @Query("UPDATE vendors SET availableBalanceMinorUsd = availableBalanceMinorUsd + :amountMinor, totalSalesCount = totalSalesCount + 1 WHERE id = :vendorId")
    suspend fun creditVendorEarnings(vendorId: String, amountMinor: Long)

    @Query("UPDATE vendors SET availableBalanceMinorUsd = availableBalanceMinorUsd - :amountMinor WHERE id = :vendorId")
    suspend fun debitVendorEarnings(vendorId: String, amountMinor: Long)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND variant = :variant LIMIT 1")
    suspend fun getCartItem(productId: String, variant: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun getWishlist(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    fun isInWishlist(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM seller_orders WHERE parentOrderId = :parentOrderId")
    fun getSellerOrdersForParent(parentOrderId: String): Flow<List<SellerOrderEntity>>

    @Query("SELECT * FROM seller_orders WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getSellerOrdersForVendor(vendorId: String): Flow<List<SellerOrderEntity>>

    @Query("SELECT * FROM seller_orders ORDER BY createdAt DESC")
    fun getAllSellerOrders(): Flow<List<SellerOrderEntity>>

    @Query("SELECT * FROM order_items WHERE parentOrderId = :parentOrderId")
    fun getOrderItemsForParent(parentOrderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE sellerOrderId = :sellerOrderId")
    fun getOrderItemsForSellerOrder(sellerOrderId: String): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSellerOrders(orders: List<SellerOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE seller_orders SET status = :status, trackingCarrier = :carrier, trackingNumber = :trackingNum, shippedAt = :shippedAt WHERE id = :sellerOrderId")
    suspend fun updateSellerOrderShipment(sellerOrderId: String, status: String, carrier: String, trackingNum: String, shippedAt: Long)

    @Query("UPDATE seller_orders SET status = :status, deliveredAt = :deliveredAt WHERE id = :sellerOrderId")
    suspend fun updateSellerOrderDelivery(sellerOrderId: String, status: String, deliveredAt: Long)
}

@Dao
interface FinancialLedgerDao {
    @Query("SELECT * FROM financial_ledger ORDER BY timestamp DESC")
    fun getAllLedgerEntries(): Flow<List<FinancialLedgerEntity>>

    @Query("SELECT * FROM financial_ledger WHERE vendorId = :vendorId ORDER BY timestamp DESC")
    fun getLedgerForVendor(vendorId: String): Flow<List<FinancialLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: FinancialLedgerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntries(entries: List<FinancialLedgerEntity>)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE vendorId = :vendorId ORDER BY timestamp ASC")
    fun getMessagesForVendor(vendorId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT DISTINCT vendorId FROM chat_messages")
    fun getActiveChatVendorIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE vendorId = :vendorId")
    suspend fun markVendorMessagesRead(vendorId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications WHERE targetRole = :role OR targetRole = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForRole(role: String): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE targetRole = :role")
    suspend fun markAllRead(role: String)
}

@Dao
interface MarketDao {
    @Query("SELECT * FROM countries WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledCountries(): Flow<List<CountryMarketEntity>>

    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryMarketEntity>>

    @Query("SELECT * FROM currencies ORDER BY code ASC")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE code = :code LIMIT 1")
    suspend fun getCurrency(code: String): CurrencyEntity?

    @Query("UPDATE countries SET isEnabled = :enabled WHERE code = :code")
    suspend fun setCountryEnabled(code: String, enabled: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryMarketEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("UPDATE currencies SET rateToUsd = :rate, lastUpdated = :updated WHERE code = :code")
    suspend fun updateRate(code: String, rate: Double, updated: Long)
}

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payout_requests WHERE vendorId = :vendorId ORDER BY requestedAt DESC")
    fun getPayoutsForVendor(vendorId: String): Flow<List<PayoutRequestEntity>>

    @Query("SELECT * FROM payout_requests ORDER BY requestedAt DESC")
    fun getAllPayouts(): Flow<List<PayoutRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayoutRequest(request: PayoutRequestEntity)

    @Query("UPDATE payout_requests SET status = :status WHERE id = :id")
    suspend fun updatePayoutStatus(id: Long, status: String)
}
