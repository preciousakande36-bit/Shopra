package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.daos.*
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        VendorEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        OrderEntity::class,
        SellerOrderEntity::class,
        OrderItemEntity::class,
        FinancialLedgerEntity::class,
        ReviewEntity::class,
        ChatMessageEntity::class,
        AppNotificationEntity::class,
        CountryMarketEntity::class,
        CurrencyEntity::class,
        PayoutRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShoporaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun vendorDao(): VendorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun ledgerDao(): FinancialLedgerDao
    abstract fun reviewDao(): ReviewDao
    abstract fun chatDao(): ChatDao
    abstract fun notificationDao(): NotificationDao
    abstract fun marketDao(): MarketDao
    abstract fun payoutDao(): PayoutDao

    companion object {
        @Volatile
        private var INSTANCE: ShoporaDatabase? = null

        fun getDatabase(context: Context): ShoporaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShoporaDatabase::class.java,
                    "shopora_marketplace.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    InitialDataSeed.seedDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
