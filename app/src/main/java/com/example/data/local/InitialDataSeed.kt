package com.example.data.local

import com.example.data.local.entities.*

object InitialDataSeed {
    suspend fun seedDatabase(database: ShoporaDatabase) {
        val countries = listOf(
            CountryMarketEntity("US", "United States", "USD", "$", "🇺🇸", 499L, 7.5, true),
            CountryMarketEntity("GB", "United Kingdom", "GBP", "£", "🇬🇧", 599L, 20.0, true),
            CountryMarketEntity("DE", "Germany (EU)", "EUR", "€", "🇩🇪", 550L, 19.0, true),
            CountryMarketEntity("NG", "Nigeria", "NGN", "₦", "🇳🇬", 350L, 7.5, true),
            CountryMarketEntity("KE", "Kenya", "KES", "KSh", "🇰🇪", 400L, 16.0, true),
            CountryMarketEntity("GH", "Ghana", "GHS", "GH₵", "🇬🇭", 400L, 12.5, true),
            CountryMarketEntity("CA", "Canada", "CAD", "C$", "🇨🇦", 600L, 13.0, true),
            CountryMarketEntity("AU", "Australia", "AUD", "A$", "🇦🇺", 700L, 10.0, true),
            CountryMarketEntity("ZA", "South Africa", "ZAR", "R", "🇿🇦", 450L, 15.0, true),
            CountryMarketEntity("AE", "United Arab Emirates", "AED", "د.إ", "🇦🇪", 500L, 5.0, true),
            CountryMarketEntity("JP", "Japan", "JPY", "¥", "🇯🇵", 600L, 10.0, true)
        )
        database.marketDao().insertCountries(countries)

        val currencies = listOf(
            CurrencyEntity("USD", "US Dollar", "$", 1.0),
            CurrencyEntity("EUR", "Euro", "€", 0.92),
            CurrencyEntity("GBP", "British Pound", "£", 0.79),
            CurrencyEntity("NGN", "Nigerian Naira", "₦", 1540.0),
            CurrencyEntity("KES", "Kenyan Shilling", "KSh", 130.0),
            CurrencyEntity("GHS", "Ghanaian Cedi", "GH₵", 15.4),
            CurrencyEntity("CAD", "Canadian Dollar", "C$", 1.38),
            CurrencyEntity("AUD", "Australian Dollar", "A$", 1.52),
            CurrencyEntity("ZAR", "South African Rand", "R", 18.25),
            CurrencyEntity("AED", "UAE Dirham", "د.إ", 3.67),
            CurrencyEntity("JPY", "Japanese Yen", "¥", 152.0)
        )
        database.marketDao().insertCurrencies(currencies)

        val categories = listOf(
            CategoryEntity("phones", "Phones & Tablets", "Smartphone", 1),
            CategoryEntity("computers", "Computers & Laptops", "Computer", 2),
            CategoryEntity("electronics", "Audio & Electronics", "Headphones", 3),
            CategoryEntity("fashion", "Fashion & Luxury", "Checkroom", 4),
            CategoryEntity("shoes", "Shoes & Sneakers", "Footwear", 5),
            CategoryEntity("beauty", "Beauty & Care", "Spa", 6),
            CategoryEntity("home", "Home & Living", "Home", 7),
            CategoryEntity("sports", "Sports & Fitness", "FitnessCenter", 8)
        )
        database.categoryDao().insertCategories(categories)

        val vendors = listOf(
            VendorEntity(
                id = "shopora-official",
                businessName = "Shopora Direct Official Store",
                storeSlug = "shopora-official",
                ownerName = "Shopora Global Operations",
                email = "official@shopora.market",
                phone = "+1 (800) 555-0199",
                countryCode = "US",
                businessCategory = "Official Flagship",
                storeDescription = "The official brand store for SHOPORA. Direct manufacturer warranties, express global logistics, and authentic guaranteed quality.",
                rating = 4.95,
                reviewCount = 4820,
                isVerified = true,
                isOfficial = true,
                status = "APPROVED",
                commissionRatePercent = 0.0,
                availableBalanceMinorUsd = 1425000L,
                pendingBalanceMinorUsd = 189000L,
                totalSalesCount = 3840
            ),
            VendorEntity(
                id = "vendor-tokyo-audio",
                businessName = "Tokyo Sound Innovations",
                storeSlug = "tokyo-audio",
                ownerName = "Kenji Sato",
                email = "kenji@tokyoaudio.jp",
                phone = "+81 3 5555 0142",
                countryCode = "JP",
                businessCategory = "Audio & Electronics",
                storeDescription = "Master-grade planar IEMs, precision DAC amplifiers, and audiophile acoustic gear designed in Akihabara, Tokyo.",
                rating = 4.90,
                reviewCount = 310,
                isVerified = true,
                isOfficial = false,
                status = "APPROVED",
                commissionRatePercent = 9.0,
                availableBalanceMinorUsd = 489000L,
                pendingBalanceMinorUsd = 82000L,
                totalSalesCount = 520
            ),
            VendorEntity(
                id = "vendor-lagos-leather",
                businessName = "Lagos Heritage Artisans",
                storeSlug = "lagos-heritage",
                ownerName = "Amara Okonkwo",
                email = "contact@lagosheritage.ng",
                phone = "+234 803 555 0122",
                countryCode = "NG",
                businessCategory = "Fashion & Luxury",
                storeDescription = "Handmade vegetable-tanned leather goods, bespoke luxury weekender bags, and heritage footwear crafted in Lagos.",
                rating = 4.86,
                reviewCount = 215,
                isVerified = true,
                isOfficial = false,
                status = "APPROVED",
                commissionRatePercent = 10.0,
                availableBalanceMinorUsd = 345000L,
                pendingBalanceMinorUsd = 45000L,
                totalSalesCount = 310
            ),
            VendorEntity(
                id = "vendor-nordic-knit",
                businessName = "Stockholm Studio Wear",
                storeSlug = "stockholm-studio",
                ownerName = "Astrid Lind",
                email = "astrid@stockholmstudio.se",
                phone = "+46 8 123 4567",
                countryCode = "DE",
                businessCategory = "Fashion & Outerwear",
                storeDescription = "Sustainable Scandinavian outerwear, extra-fine merino wool cardigans, and weather-resistant minimalist techwear.",
                rating = 4.88,
                reviewCount = 240,
                isVerified = true,
                isOfficial = false,
                status = "APPROVED",
                commissionRatePercent = 8.5,
                availableBalanceMinorUsd = 612000L,
                pendingBalanceMinorUsd = 94000L,
                totalSalesCount = 415
            ),
            VendorEntity(
                id = "vendor-milan-watches",
                businessName = "Milano Timepieces SARL",
                storeSlug = "milano-timepieces",
                ownerName = "Marco Bellini",
                email = "marco@milanotime.it",
                phone = "+39 02 555 9811",
                countryCode = "DE",
                businessCategory = "Watches & Jewelry",
                storeDescription = "Italian precision chronographs, sapphire crystal displays, and aerospace-grade stainless steel bracelets.",
                rating = 4.70,
                reviewCount = 14,
                isVerified = false,
                isOfficial = false,
                status = "PENDING",
                commissionRatePercent = 12.0,
                availableBalanceMinorUsd = 0L,
                pendingBalanceMinorUsd = 0L,
                totalSalesCount = 0
            )
        )
        database.vendorDao().insertVendors(vendors)

        val products = listOf(
            ProductEntity(
                id = "prod-shopora-apex",
                title = "Shopora Apex Pro 5G (512GB Natural Titanium)",
                description = "Flagship smartphone engineered with 3nm processor, 120Hz LTPO ProMotion display, periscope 10x optical zoom camera, and full satellite SOS connectivity. 2-Year International Global Warranty included.",
                categoryId = "phones",
                vendorId = "shopora-official",
                vendorName = "Shopora Direct Official Store",
                isOfficialStore = true,
                priceMinorUsd = 99900L, // $999.00
                originalPriceMinorUsd = 119900L,
                stock = 142,
                rating = 4.94,
                reviewCount = 1280,
                imageUrl = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&q=80",
                brand = "Shopora Direct",
                originCountry = "US",
                returnDays = 30,
                estimatedDeliveryDays = 3,
                variantsJson = "Natural Titanium (512GB), Space Black (512GB), Desert Gold (1TB)",
                specificationsJson = "Screen: 6.8\" OLED 120Hz; RAM: 16GB; Storage: 512GB; Battery: 5400mAh; 5G Global Bands",
                isFeatured = true,
                isApproved = true,
                shippingFeeMinorUsd = 0L // Free shipping from official store
            ),
            ProductEntity(
                id = "prod-shopora-ultrabook",
                title = "Shopora UltraBook Pro 16\" Studio Edition",
                description = "Creator workstation laptop with 4K Mini-LED HDR display, 32-core neural graphics engine, 64GB Unified RAM, 2TB PCIe NVMe SSD, and 22-hour battery life. All-aluminum unibody chassis.",
                categoryId = "computers",
                vendorId = "shopora-official",
                vendorName = "Shopora Direct Official Store",
                isOfficialStore = true,
                priceMinorUsd = 169900L, // $1,699.00
                originalPriceMinorUsd = 189900L,
                stock = 45,
                rating = 4.96,
                reviewCount = 840,
                imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80",
                brand = "Shopora Direct",
                originCountry = "US",
                returnDays = 30,
                estimatedDeliveryDays = 3,
                variantsJson = "Midnight Aluminum (64GB/2TB), Stellar Silver (32GB/1TB)",
                specificationsJson = "CPU: 16-Core M-Silicon; Display: 16.2\" 4K Liquid Retina XDR; Ports: 3x Thunderbolt 4, HDMI, SDXC",
                isFeatured = true,
                isApproved = true,
                shippingFeeMinorUsd = 0L
            ),
            ProductEntity(
                id = "prod-shopora-soundsphere",
                title = "Shopora SoundSphere Spatial Audio Headphones",
                description = "Ultra-premium wireless over-ear headphones with custom bio-cellulose drivers, 45dB hybrid ANC, multi-point Bluetooth 5.4 LE, lossless USB-C audio DAC, and 60-hour playtime.",
                categoryId = "electronics",
                vendorId = "shopora-official",
                vendorName = "Shopora Direct Official Store",
                isOfficialStore = true,
                priceMinorUsd = 27900L, // $279.00
                originalPriceMinorUsd = 34900L,
                stock = 310,
                rating = 4.91,
                reviewCount = 2100,
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                brand = "Shopora Direct",
                originCountry = "US",
                returnDays = 30,
                estimatedDeliveryDays = 4,
                variantsJson = "Matte Black, Glacier Silver, Navy Gold",
                specificationsJson = "Drivers: 40mm Bio-Cellulose; Battery: 60h; Codecs: LDAC, aptX Adaptive, AAC; ANC: Hybrid Adaptive",
                isFeatured = true,
                isApproved = true,
                shippingFeeMinorUsd = 0L
            ),
            ProductEntity(
                id = "prod-tokyo-iem",
                title = "Tokyo NeoWave Planar In-Ear Studio Monitors",
                description = "Handcrafted in Akihabara with 14.5mm planar magnetic transducers and CNC aerospace-grade aluminum cavities. Comes with 8-core silver-plated MMCX modular balanced cable.",
                categoryId = "electronics",
                vendorId = "vendor-tokyo-audio",
                vendorName = "Tokyo Sound Innovations",
                isOfficialStore = false,
                priceMinorUsd = 18900L, // $189.00
                originalPriceMinorUsd = 22900L,
                stock = 62,
                rating = 4.89,
                reviewCount = 145,
                imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800&q=80",
                brand = "Tokyo Sound",
                originCountry = "JP",
                returnDays = 14,
                estimatedDeliveryDays = 6,
                variantsJson = "Gunmetal Black (3.5mm/4.4mm), Mirror Chrome (3.5mm/4.4mm)",
                specificationsJson = "Transducer: 14.5mm Planar; Impedance: 16 Ohm; Sensitivity: 104dB; Frequency: 10Hz-42kHz",
                isFeatured = false,
                isApproved = true,
                shippingFeeMinorUsd = 799L
            ),
            ProductEntity(
                id = "prod-lagos-duffel",
                title = "Lagos Heritage Handcrafted Voyager Leather Duffel",
                description = "Bespoke travel duffel crafted from full-grain vegetable-tanned Nigerian leather with solid brass hardware, YKK Excella zippers, reinforced boot, and water-resistant wax cotton lining.",
                categoryId = "fashion",
                vendorId = "vendor-lagos-leather",
                vendorName = "Lagos Heritage Artisans",
                isOfficialStore = false,
                priceMinorUsd = 24000L, // $240.00
                originalPriceMinorUsd = 29000L,
                stock = 28,
                rating = 4.88,
                reviewCount = 92,
                imageUrl = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800&q=80",
                brand = "Lagos Heritage",
                originCountry = "NG",
                returnDays = 21,
                estimatedDeliveryDays = 7,
                variantsJson = "Cognac Tan 50L, Onyx Black 50L, Havana Brown 50L",
                specificationsJson = "Dimensions: 52 x 28 x 26 cm; Weight: 1.8kg; Material: Grade-A Full Grain Cowhide",
                isFeatured = true,
                isApproved = true,
                shippingFeeMinorUsd = 1200L
            ),
            ProductEntity(
                id = "prod-lagos-loafers",
                title = "Eko Artisan Hand-Stitched Penny Loafers",
                description = "Classic penny loafers constructed with Goodyear welt stitching, supple calfskin upper, cushioned leather insoles, and stacked leather heel. Hand-burnished by master cobblers in Lagos.",
                categoryId = "shoes",
                vendorId = "vendor-lagos-leather",
                vendorName = "Lagos Heritage Artisans",
                isOfficialStore = false,
                priceMinorUsd = 14500L, // $145.00
                originalPriceMinorUsd = 17500L,
                stock = 34,
                rating = 4.84,
                reviewCount = 76,
                imageUrl = "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=800&q=80",
                brand = "Lagos Heritage",
                originCountry = "NG",
                returnDays = 14,
                estimatedDeliveryDays = 7,
                variantsJson = "Size 41 / Chestnut, Size 42 / Chestnut, Size 43 / Black, Size 44 / Black",
                specificationsJson = "Construction: Goodyear Welt; Upper: Full Grain Calfskin; Sole: Dainite Rubber Insert",
                isFeatured = false,
                isApproved = true,
                shippingFeeMinorUsd = 950L
            ),
            ProductEntity(
                id = "prod-nordic-coat",
                title = "Nordic StormShield Merino Wool Tech Overcoat",
                description = "Modern tailoring meets technical innovation. Premium 100% boiled merino wool bonded with a breathable waterproof membrane. Thermally insulated for harsh winters down to -15°C.",
                categoryId = "fashion",
                vendorId = "vendor-nordic-knit",
                vendorName = "Stockholm Studio Wear",
                isOfficialStore = false,
                priceMinorUsd = 31000L, // $310.00
                originalPriceMinorUsd = 36000L,
                stock = 19,
                rating = 4.90,
                reviewCount = 112,
                imageUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=800&q=80",
                brand = "Stockholm Studio",
                originCountry = "DE",
                returnDays = 30,
                estimatedDeliveryDays = 5,
                variantsJson = "Size M / Charcoal Grey, Size L / Charcoal Grey, Size XL / Deep Navy",
                specificationsJson = "Shell: 100% Merino Wool 450gsm; Lining: Recycled Primaloft Gold; Waterproof: 15,000mm",
                isFeatured = true,
                isApproved = true,
                shippingFeeMinorUsd = 850L
            ),
            ProductEntity(
                id = "prod-nordic-pack",
                title = "Stockholm Aerotech Roll-Top Commuter Pack (26L)",
                description = "Engineered for daily urban cyclists and global commuters. Waterproof Cordura 500D fabric, magnetic Fidlock buckles, dedicated padded 16\" laptop compartment, and ergonomic air-mesh back panel.",
                categoryId = "home",
                vendorId = "vendor-nordic-knit",
                vendorName = "Stockholm Studio Wear",
                isOfficialStore = false,
                priceMinorUsd = 12500L, // $125.00
                originalPriceMinorUsd = 14500L,
                stock = 55,
                rating = 4.87,
                reviewCount = 138,
                imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
                brand = "Stockholm Studio",
                originCountry = "DE",
                returnDays = 30,
                estimatedDeliveryDays = 5,
                variantsJson = "Stealth Black (26L), Olive Drab (26L)",
                specificationsJson = "Capacity: 26L; Fabric: Cordura 500D TPU; Buckles: Fidlock V-Buckle; Laptop: up to 16.5 inch",
                isFeatured = false,
                isApproved = true,
                shippingFeeMinorUsd = 650L
            )
        )
        database.productDao().insertProducts(products)

        val reviews = listOf(
            ReviewEntity(
                productId = "prod-shopora-apex",
                vendorId = "shopora-official",
                authorName = "David Henderson",
                rating = 5,
                comment = "Unbelievable device! Ordered with international delivery to London and arrived within 3 days. Titanium build feels incredible.",
                isVerifiedPurchase = true,
                countryCode = "GB"
            ),
            ReviewEntity(
                productId = "prod-shopora-apex",
                vendorId = "shopora-official",
                authorName = "Chioma Adebayo",
                rating = 5,
                comment = "Camera quality is on another level. Battery easily lasts 2 days on heavy usage. Shopora Official Store packaging was pristine.",
                isVerifiedPurchase = true,
                countryCode = "NG"
            ),
            ReviewEntity(
                productId = "prod-lagos-duffel",
                vendorId = "vendor-lagos-leather",
                authorName = "Alexander Müller",
                rating = 5,
                comment = "The leather aroma and brass finish on this bag is top-tier luxury. You can see the handcraftsmanship in every single stitch.",
                isVerifiedPurchase = true,
                countryCode = "DE"
            ),
            ReviewEntity(
                productId = "prod-tokyo-iem",
                vendorId = "vendor-tokyo-audio",
                authorName = "Marcus Vance",
                rating = 5,
                comment = "Acoustic resolution is phenomenal. The planar driver handles fast transients effortlessly. Well packed from Tokyo.",
                isVerifiedPurchase = true,
                countryCode = "US"
            )
        )
        database.reviewDao().insertReviews(reviews)

        // Seed initial notifications
        val notifications = listOf(
            AppNotificationEntity(
                targetRole = "CUSTOMER",
                title = "Welcome to SHOPORA Global",
                message = "Experience the worldwide multi-vendor marketplace with certified official products and global artisan vendors.",
                type = "SYSTEM"
            ),
            AppNotificationEntity(
                targetRole = "VENDOR",
                title = "Vendor Portal Activated",
                message = "Your store is active and verified. Manage your products, fulfill multi-vendor orders, and request payouts seamlessly.",
                type = "VENDOR"
            ),
            AppNotificationEntity(
                targetRole = "ADMIN",
                title = "Pending Vendor Verification",
                message = "Milano Timepieces SARL submitted registration documents for review.",
                type = "VENDOR"
            )
        )
        for (notif in notifications) {
            database.notificationDao().insertNotification(notif)
        }

        // Seed initial demo orders showing multi-vendor split-order execution!
        val parentOrderId = "order-shpora-1001"
        val parentOrder = OrderEntity(
            id = parentOrderId,
            orderNumber = "SHP-2026-9812",
            customerName = "Jordan Davis",
            customerEmail = "jordan.davis@example.com",
            customerPhone = "+1 202 555 0184",
            deliveryAddress = "742 Evergreen Terrace, Suite 4B",
            deliveryCountry = "US",
            currencyCode = "USD",
            currencySymbol = "$",
            exchangeRateToUsd = 1.0,
            subtotalMinor = 127800L, // $1,278.00 ($999 + $279)
            shippingTotalMinor = 0L,
            taxTotalMinor = 9585L, // 7.5%
            grandTotalMinor = 137385L,
            paymentMethod = "Stripe - Visa ending in 4242",
            paymentStatus = "PAID",
            fulfillmentStatus = "SHIPPED",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
        database.orderDao().insertParentOrder(parentOrder)

        val sellerOrderOfficial = SellerOrderEntity(
            id = "so-1001-official",
            parentOrderId = parentOrderId,
            parentOrderNumber = "SHP-2026-9812",
            vendorId = "shopora-official",
            vendorName = "Shopora Direct Official Store",
            isOfficialStore = true,
            itemCount = 2,
            subtotalMinor = 127800L,
            shippingFeeMinor = 0L,
            commissionRatePercent = 0.0,
            platformCommissionMinor = 0L,
            sellerNetEarningsMinor = 127800L,
            currencyCode = "USD",
            status = "SHIPPED",
            trackingCarrier = "DHL Express Worldwide",
            trackingNumber = "DHL-US-98231054",
            estimatedDelivery = "Tomorrow by 2:00 PM",
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            shippedAt = System.currentTimeMillis() - 86400000L
        )
        database.orderDao().insertSellerOrders(listOf(sellerOrderOfficial))

        val orderItems = listOf(
            OrderItemEntity(
                sellerOrderId = "so-1001-official",
                parentOrderId = parentOrderId,
                productId = "prod-shopora-apex",
                productTitle = "Shopora Apex Pro 5G (512GB Natural Titanium)",
                productImageUrl = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&q=80",
                variant = "Natural Titanium (512GB)",
                vendorId = "shopora-official",
                quantity = 1,
                unitPriceMinor = 99900L,
                totalPriceMinor = 99900L
            ),
            OrderItemEntity(
                sellerOrderId = "so-1001-official",
                parentOrderId = parentOrderId,
                productId = "prod-shopora-soundsphere",
                productTitle = "Shopora SoundSphere Spatial Audio Headphones",
                productImageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                variant = "Matte Black",
                vendorId = "shopora-official",
                quantity = 1,
                unitPriceMinor = 27900L,
                totalPriceMinor = 27900L
            )
        )
        database.orderDao().insertOrderItems(orderItems)

        // Seed initial ledger entries
        val ledgerEntries = listOf(
            FinancialLedgerEntity(
                transactionRef = "TXN-9812-CUST",
                parentOrderId = parentOrderId,
                sellerOrderId = null,
                vendorId = null,
                entryType = "CUSTOMER_PAYMENT",
                description = "Customer checkout payment via Stripe for order SHP-2026-9812",
                grossMinor = 137385L,
                feeMinor = 0L,
                netMinor = 137385L,
                currencyCode = "USD",
                status = "SETTLED"
            ),
            FinancialLedgerEntity(
                transactionRef = "TXN-9812-OFFICIAL",
                parentOrderId = parentOrderId,
                sellerOrderId = "so-1001-official",
                vendorId = "shopora-official",
                entryType = "SELLER_SETTLEMENT",
                description = "Official store direct sales settlement for order SHP-2026-9812",
                grossMinor = 127800L,
                feeMinor = 0L,
                netMinor = 127800L,
                currencyCode = "USD",
                status = "SETTLED"
            )
        )
        database.ledgerDao().insertLedgerEntries(ledgerEntries)

        // Chat conversation seed
        val chatSeed = listOf(
            ChatMessageEntity(
                vendorId = "vendor-tokyo-audio",
                vendorName = "Tokyo Sound Innovations",
                senderRole = "CUSTOMER",
                senderName = "Jordan Davis",
                message = "Hello! Does the Tokyo NeoWave Planar IEM come with both 3.5mm and 4.4mm balanced termination plugs?",
                productRefTitle = "Tokyo NeoWave Planar In-Ear Studio Monitors",
                timestamp = System.currentTimeMillis() - 3600000L * 4,
                isRead = true
            ),
            ChatMessageEntity(
                vendorId = "vendor-tokyo-audio",
                vendorName = "Tokyo Sound Innovations",
                senderRole = "VENDOR",
                senderName = "Kenji Sato (Tokyo Sound)",
                message = "Konnichiwa Jordan! Yes, it includes our swappable quick-lock modular jack with both 3.5mm single-ended and 4.4mm Pentaconn balanced adapters in the box.",
                productRefTitle = "Tokyo NeoWave Planar In-Ear Studio Monitors",
                timestamp = System.currentTimeMillis() - 3600000L * 3,
                isRead = true
            )
        )
        for (msg in chatSeed) {
            database.chatDao().insertMessage(msg)
        }
    }
}
