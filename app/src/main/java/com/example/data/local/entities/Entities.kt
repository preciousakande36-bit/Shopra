package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val vendorId: String,
    val vendorName: String,
    val isOfficialStore: Boolean,
    val priceMinorUsd: Long, // base currency in USD cents
    val originalPriceMinorUsd: Long,
    val stock: Int,
    val rating: Double,
    val reviewCount: Int,
    val imageUrl: String,
    val brand: String,
    val originCountry: String,
    val returnDays: Int = 30,
    val estimatedDeliveryDays: Int = 5,
    val variantsJson: String = "",
    val specificationsJson: String = "",
    val isFeatured: Boolean = false,
    val isApproved: Boolean = true,
    val shippingFeeMinorUsd: Long = 499
)

@Entity(tableName = "vendors")
data class VendorEntity(
    @PrimaryKey val id: String,
    val businessName: String,
    val storeSlug: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val countryCode: String,
    val businessCategory: String,
    val storeDescription: String,
    val logoUrl: String = "",
    val rating: Double = 4.8,
    val reviewCount: Int = 120,
    val isVerified: Boolean = true,
    val isOfficial: Boolean = false,
    val status: String = "APPROVED", // APPROVED, PENDING, SUSPENDED
    val commissionRatePercent: Double = 10.0,
    val availableBalanceMinorUsd: Long = 0L,
    val pendingBalanceMinorUsd: Long = 0L,
    val totalSalesCount: Int = 0,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val displayOrder: Int
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val variant: String = "",
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryCountry: String,
    val currencyCode: String,
    val currencySymbol: String,
    val exchangeRateToUsd: Double,
    val subtotalMinor: Long,
    val shippingTotalMinor: Long,
    val taxTotalMinor: Long,
    val grandTotalMinor: Long,
    val paymentMethod: String,
    val paymentStatus: String, // PAID, PENDING, FAILED
    val fulfillmentStatus: String, // PROCESSING, SHIPPED, DELIVERED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_orders")
data class SellerOrderEntity(
    @PrimaryKey val id: String,
    val parentOrderId: String,
    val parentOrderNumber: String,
    val vendorId: String,
    val vendorName: String,
    val isOfficialStore: Boolean,
    val itemCount: Int,
    val subtotalMinor: Long,
    val shippingFeeMinor: Long,
    val commissionRatePercent: Double,
    val platformCommissionMinor: Long,
    val sellerNetEarningsMinor: Long,
    val currencyCode: String,
    val status: String, // PENDING_FULFILLMENT, PROCESSING, SHIPPED, DELIVERED
    val trackingCarrier: String = "",
    val trackingNumber: String = "",
    val estimatedDelivery: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val shippedAt: Long? = null,
    val deliveredAt: Long? = null
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sellerOrderId: String,
    val parentOrderId: String,
    val productId: String,
    val productTitle: String,
    val productImageUrl: String,
    val variant: String,
    val vendorId: String,
    val quantity: Int,
    val unitPriceMinor: Long,
    val totalPriceMinor: Long
)

@Entity(tableName = "financial_ledger")
data class FinancialLedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionRef: String,
    val parentOrderId: String,
    val sellerOrderId: String? = null,
    val vendorId: String? = null,
    val entryType: String, // CUSTOMER_PAYMENT, PLATFORM_COMMISSION, SELLER_SETTLEMENT, WITHDRAWAL_PAYOUT, REFUND
    val description: String,
    val grossMinor: Long,
    val feeMinor: Long,
    val netMinor: Long,
    val currencyCode: String,
    val status: String, // RECORDED, SETTLED, PENDING_RELEASE
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val vendorId: String,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val isVerifiedPurchase: Boolean = true,
    val countryCode: String = "US",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vendorId: String,
    val vendorName: String,
    val senderRole: String, // CUSTOMER, VENDOR
    val senderName: String,
    val message: String,
    val productRefTitle: String? = null,
    val orderRefId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetRole: String, // CUSTOMER, VENDOR, ADMIN
    val title: String,
    val message: String,
    val type: String, // ORDER, SHIPPING, PAYMENT, VENDOR, SYSTEM
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "countries")
data class CountryMarketEntity(
    @PrimaryKey val code: String,
    val name: String,
    val currencyCode: String,
    val currencySymbol: String,
    val flag: String,
    val standardShippingMinorUsd: Long,
    val taxPercent: Double,
    val isEnabled: Boolean = true
)

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val code: String,
    val name: String,
    val symbol: String,
    val rateToUsd: Double, // units per 1 USD
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "payout_requests")
data class PayoutRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vendorId: String,
    val vendorName: String,
    val amountMinorUsd: Long,
    val payoutMethod: String,
    val accountDetails: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val requestedAt: Long = System.currentTimeMillis()
)
