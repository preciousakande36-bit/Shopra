package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SellerOrderEntity
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(viewModel: ShoporaViewModel) {
    val activeVendor by viewModel.activeVendor.collectAsState()
    val allVendors by viewModel.allVendors.collectAsState()
    val vendorProducts by viewModel.activeVendorProducts.collectAsState()
    val vendorOrders by viewModel.activeVendorOrders.collectAsState()
    val vendorLedger by viewModel.activeVendorLedger.collectAsState()
    val categories by viewModel.allCategories.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Orders (Fulfillment), 2: Products, 3: Earnings & Payout
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showPayoutDialog by remember { mutableStateOf(false) }
    var showTrackingDialogForOrder by remember { mutableStateOf<SellerOrderEntity?>(null) }
    var showVendorPickerMenu by remember { mutableStateOf(false) }

    val vendor = activeVendor ?: allVendors.firstOrNull()

    if (vendor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentGold)
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    containerColor = AccentGold,
                    contentColor = NavyDark
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50)
                .padding(innerPadding)
                .padding(bottom = 80.dp)
        ) {
            // Vendor Top Header with Switcher
            Surface(
                color = NavyDark,
                modifier = Modifier.fillMaxWidth().statusBarsPadding()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (vendor.isOfficial) AccentGold else ElectricBlue,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (vendor.isOfficial) Icons.Default.Verified else Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = if (vendor.isOfficial) NavyDark else Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = vendor.businessName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Switch Store", tint = Slate400, modifier = Modifier.clickable { showVendorPickerMenu = true })
                                }
                                Text(
                                    text = if (vendor.isOfficial) "Official Platform Store • 0% Commission" else "${vendor.countryCode} • Commission: ${vendor.commissionRatePercent}% • Status: ${vendor.status}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = AccentGold, fontSize = 11.sp)
                                )
                            }
                        }

                        // Switch Store Dropdown
                        Box {
                            DropdownMenu(
                                expanded = showVendorPickerMenu,
                                onDismissRequest = { showVendorPickerMenu = false }
                            ) {
                                allVendors.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text("${v.businessName} (${if (v.isOfficial) "Official" else v.countryCode})") },
                                        onClick = {
                                            viewModel.setActiveVendorId(v.id)
                                            showVendorPickerMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = NavySurface,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = AccentGold
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Overview", fontSize = 11.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Orders (${vendorOrders.size})", fontSize = 11.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Catalog (${vendorProducts.size})", fontSize = 11.sp) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("Earnings", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> VendorOverviewTab(vendor = vendor, orders = vendorOrders, products = vendorProducts, viewModel = viewModel)
                1 -> VendorOrdersFulfillmentTab(
                    orders = vendorOrders,
                    viewModel = viewModel,
                    onOpenTracking = { showTrackingDialogForOrder = it }
                )
                2 -> VendorCatalogTab(products = vendorProducts, viewModel = viewModel)
                3 -> VendorEarningsTab(
                    vendor = vendor,
                    ledger = vendorLedger,
                    viewModel = viewModel,
                    onRequestPayout = { showPayoutDialog = true }
                )
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        var prodTitle by remember { mutableStateOf("") }
        var prodDesc by remember { mutableStateOf("") }
        var prodPrice by remember { mutableStateOf("") }
        var prodStock by remember { mutableStateOf("25") }
        var prodBrand by remember { mutableStateOf(vendor.businessName) }
        var prodVariants by remember { mutableStateOf("Standard Edition") }
        var prodSpecs by remember { mutableStateOf("Material: Premium; Warranty: 1-Year") }
        var prodCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "electronics") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Upload New Product", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 400.dp)) {
                    item {
                        OutlinedTextField(
                            value = prodTitle,
                            onValueChange = { prodTitle = it },
                            label = { Text("Product Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = prodDesc,
                            onValueChange = { prodDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = prodPrice,
                                onValueChange = { prodPrice = it },
                                label = { Text("Price (USD)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = prodStock,
                                onValueChange = { prodStock = it },
                                label = { Text("Inventory Stock") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = prodVariants,
                            onValueChange = { prodVariants = it },
                            label = { Text("Variants (Comma-separated)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = prodSpecs,
                            onValueChange = { prodSpecs = it },
                            label = { Text("Specifications") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceVal = prodPrice.toDoubleOrNull() ?: 50.0
                        val stockVal = prodStock.toIntOrNull() ?: 10
                        if (prodTitle.isNotBlank()) {
                            viewModel.addNewProduct(
                                title = prodTitle,
                                description = prodDesc,
                                categoryId = prodCatId,
                                priceUsd = priceVal,
                                stock = stockVal,
                                brand = prodBrand,
                                variants = prodVariants,
                                specs = prodSpecs,
                                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80"
                            )
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGoldDark)
                ) {
                    Text("Publish Product", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Update Tracking & Fulfillment Dialog
    showTrackingDialogForOrder?.let { orderToShip ->
        var carrierName by remember { mutableStateOf("DHL Express Worldwide") }
        var trackingNum by remember { mutableStateOf("DHL-${(1000000..9999999).random()}") }

        AlertDialog(
            onDismissRequest = { showTrackingDialogForOrder = null },
            title = { Text("Fulfill Shipment: ${orderToShip.parentOrderNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter carrier and tracking details to dispatch this customer package.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )
                    OutlinedTextField(
                        value = carrierName,
                        onValueChange = { carrierName = it },
                        label = { Text("Shipping Carrier (e.g. DHL, FedEx, UPS, Local)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = trackingNum,
                        onValueChange = { trackingNum = it },
                        label = { Text("Tracking Number / Waybill ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderTracking(
                            sellerOrderId = orderToShip.id,
                            status = "SHIPPED",
                            carrier = carrierName,
                            trackingNumber = trackingNum
                        )
                        showTrackingDialogForOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Confirm Shipment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTrackingDialogForOrder = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Request Payout Dialog
    if (showPayoutDialog) {
        var payoutAmount by remember { mutableStateOf("${vendor.availableBalanceMinorUsd / 100.0}") }
        var payoutMethod by remember { mutableStateOf("Direct Bank Wire Transfer") }
        var accountDetails by remember { mutableStateOf("IBAN: US94 0001 2345 6789 0123 / Swift: BOFAUS3N") }

        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            title = { Text("Request Earnings Withdrawal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Available Balance: $${vendor.availableBalanceMinorUsd / 100.0}",
                        style = MaterialTheme.typography.bodySmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold)
                    )
                    OutlinedTextField(
                        value = payoutAmount,
                        onValueChange = { payoutAmount = it },
                        label = { Text("Amount to Withdraw (USD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = payoutMethod,
                        onValueChange = { payoutMethod = it },
                        label = { Text("Payout Method (Wire, Stripe Connect, PayPal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountDetails,
                        onValueChange = { accountDetails = it },
                        label = { Text("Account Details / Beneficiary") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payoutAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.requestVendorPayout(amount, payoutMethod, accountDetails)
                            showPayoutDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Submit Payout Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun VendorOverviewTab(
    vendor: com.example.data.local.entities.VendorEntity,
    orders: List<SellerOrderEntity>,
    products: List<ProductEntity>,
    viewModel: ShoporaViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Stat Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Available Balance",
                    value = "$${vendor.availableBalanceMinorUsd / 100.0}",
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending Escrow",
                    value = "$${vendor.pendingBalanceMinorUsd / 100.0}",
                    color = AccentGoldDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Orders",
                    value = "${orders.size}",
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Products",
                    value = "${products.size}",
                    color = Slate700,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Store Verification status card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Business Verification", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (vendor.isVerified) SuccessGreenLight else AccentGoldLight
                        ) {
                            Text(
                                text = if (vendor.isVerified) "VERIFIED" else "PENDING",
                                color = if (vendor.isVerified) SuccessGreen else AccentGoldDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Business Entity: ${vendor.businessName}", style = MaterialTheme.typography.bodySmall)
                    Text("Owner: ${vendor.ownerName} • Contact: ${vendor.email}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                    Text("Commission Agreement: ${vendor.commissionRatePercent}% marketplace rate", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                }
            }
        }
    }
}

@Composable
private fun VendorOrdersFulfillmentTab(
    orders: List<SellerOrderEntity>,
    viewModel: ShoporaViewModel,
    onOpenTracking: (SellerOrderEntity) -> Unit
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No fulfillment orders assigned to this store yet.")
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders) { order ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order: ${order.parentOrderNumber}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ElectricBlueDark)
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (order.status) {
                                "DELIVERED" -> SuccessGreenLight
                                "SHIPPED" -> ElectricBlue.copy(alpha = 0.15f)
                                else -> AccentGoldLight
                            }
                        ) {
                            Text(
                                text = order.status,
                                color = when (order.status) {
                                    "DELIVERED" -> SuccessGreen
                                    "SHIPPED" -> ElectricBlue
                                    else -> AccentGoldDark
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Items: ${order.itemCount} | Net Payout: ${viewModel.formatConvertedMinor(order.sellerNetEarningsMinor, order.currencyCode, order.currencyCode)}", style = MaterialTheme.typography.bodySmall)

                    if (order.trackingNumber.isNotBlank()) {
                        Text("Carrier: ${order.trackingCarrier} | Tracking: ${order.trackingNumber}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (order.status != "DELIVERED") {
                            Button(
                                onClick = { onOpenTracking(order) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (order.status == "SHIPPED") "Edit Tracking" else "Enter Tracking & Ship", fontSize = 11.sp)
                            }

                            if (order.status == "SHIPPED") {
                                Button(
                                    onClick = { viewModel.markOrderDelivered(order.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Confirm Delivered", fontSize = 11.sp)
                                }
                            }
                        } else {
                            Text("Package completed and settled.", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorCatalogTab(
    products: List<ProductEntity>,
    viewModel: ShoporaViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products) { prod ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = prod.imageUrl,
                        contentDescription = prod.title,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Slate100)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(prod.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                        Text("Price: ${viewModel.formatPrice(prod.priceMinorUsd)} • Stock: ${prod.stock}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                    }
                    IconButton(onClick = { viewModel.openProductDetail(prod.id) }) {
                        Icon(Icons.Default.Visibility, contentDescription = "View", tint = ElectricBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorEarningsTab(
    vendor: com.example.data.local.entities.VendorEntity,
    ledger: List<com.example.data.local.entities.FinancialLedgerEntity>,
    viewModel: ShoporaViewModel,
    onRequestPayout: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Available for Withdrawal", color = Slate400, fontSize = 12.sp)
                    Text(
                        "$${vendor.availableBalanceMinorUsd / 100.0}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRequestPayout,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Payout Wire", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Financial Ledger & Settlement History", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        }

        if (ledger.isEmpty()) {
            item {
                Text("No ledger transactions recorded yet.", color = Slate500, fontSize = 12.sp)
            }
        } else {
            items(ledger) { entry ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(entry.entryType, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElectricBlueDark)
                            Text(entry.status, fontSize = 11.sp, color = if (entry.status == "SETTLED") SuccessGreen else AccentGoldDark, fontWeight = FontWeight.Bold)
                        }
                        Text(entry.description, style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp))
                        Text("Net: ${viewModel.formatConvertedMinor(entry.netMinor, entry.currencyCode, entry.currencyCode)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 11.sp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = color))
        }
    }
}
