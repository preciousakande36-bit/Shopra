package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.VendorEntity
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun AdminControlScreen(viewModel: ShoporaViewModel) {
    val vendors by viewModel.allVendors.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.customerOrders.collectAsState()
    val ledger by viewModel.financialLedger.collectAsState()
    val countries by viewModel.allCountries.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: Overview, 1: Vendors, 2: Financial Ledger, 3: Markets

    val totalGmvMinor = orders.sumOf { it.grandTotalMinor }
    val commissionRevenueMinor = ledger.filter { it.entryType == "PLATFORM_COMMISSION" }.sumOf { it.netMinor }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(bottom = 80.dp)
    ) {
        // Admin Top Bar
        Surface(color = NavyDark, modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(8.dp), color = ErrorRed, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SHOPORA Control Center",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = "Marketplace Executive Administration",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ErrorRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                    ) {
                        Text(
                            text = "ADMIN LEVEL 1",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TabRow(
                    selectedTabIndex = selectedAdminTab,
                    containerColor = NavySurface,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                            color = ErrorRed
                        )
                    }
                ) {
                    Tab(selected = selectedAdminTab == 0, onClick = { selectedAdminTab = 0 }, text = { Text("Overview", fontSize = 11.sp) })
                    Tab(selected = selectedAdminTab == 1, onClick = { selectedAdminTab = 1 }, text = { Text("Vendors (${vendors.size})", fontSize = 11.sp) })
                    Tab(selected = selectedAdminTab == 2, onClick = { selectedAdminTab = 2 }, text = { Text("Ledger", fontSize = 11.sp) })
                    Tab(selected = selectedAdminTab == 3, onClick = { selectedAdminTab = 3 }, text = { Text("Markets", fontSize = 11.sp) })
                }
            }
        }

        when (selectedAdminTab) {
            0 -> AdminOverviewTab(
                totalGmvMinor = totalGmvMinor,
                commissionRevenueMinor = commissionRevenueMinor,
                vendorCount = vendors.size,
                productCount = products.size,
                orderCount = orders.size,
                viewModel = viewModel
            )
            1 -> AdminVendorsTab(vendors = vendors, viewModel = viewModel)
            2 -> AdminLedgerTab(ledger = ledger, viewModel = viewModel)
            3 -> AdminMarketsTab(countries = countries, viewModel = viewModel)
        }
    }
}

@Composable
private fun AdminOverviewTab(
    totalGmvMinor: Long,
    commissionRevenueMinor: Long,
    vendorCount: Int,
    productCount: Int,
    orderCount: Int,
    viewModel: ShoporaViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "Gross Merchandise Value",
                    value = viewModel.formatPrice(totalGmvMinor),
                    color = ElectricBlueDark,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Platform Commissions",
                    value = viewModel.formatPrice(commissionRevenueMinor),
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "Total Orders",
                    value = "$orderCount",
                    color = Slate800,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Active Vendors",
                    value = "$vendorCount",
                    color = AccentGoldDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Marketplace System Health", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Multi-vendor payment settlement engine: ACTIVE", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Cross-border currency conversion matrix: SYNCHRONIZED", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Zero-hidden-fees customer checkout protection: ENFORCED", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AdminVendorsTab(
    vendors: List<VendorEntity>,
    viewModel: ShoporaViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(vendors) { vendor ->
            Card(
                shape = RoundedCornerShape(12.dp),
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
                            text = vendor.businessName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (vendor.status) {
                                "APPROVED" -> SuccessGreenLight
                                "PENDING" -> AccentGoldLight
                                else -> ErrorRedLight
                            }
                        ) {
                            Text(
                                text = vendor.status,
                                color = when (vendor.status) {
                                    "APPROVED" -> SuccessGreen
                                    "PENDING" -> AccentGoldDark
                                    else -> ErrorRed
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Country: ${vendor.countryCode} • Commission: ${vendor.commissionRatePercent}% • Owner: ${vendor.ownerName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (vendor.status == "PENDING" || vendor.status == "SUSPENDED") {
                            Button(
                                onClick = { viewModel.approveVendor(vendor.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve Store", fontSize = 11.sp)
                            }
                        }

                        if (vendor.status == "APPROVED" && !vendor.isOfficial) {
                            OutlinedButton(
                                onClick = { viewModel.suspendVendor(vendor.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Suspend", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminLedgerTab(
    ledger: List<com.example.data.local.entities.FinancialLedgerEntity>,
    viewModel: ShoporaViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ledger) { entry ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${entry.entryType} • ${entry.transactionRef}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ElectricBlueDark)
                        )
                        Text(
                            text = entry.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (entry.status == "SETTLED") SuccessGreen else AccentGoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = entry.description, style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp))
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross: ${viewModel.formatConvertedMinor(entry.grossMinor, entry.currencyCode, entry.currencyCode)}", fontSize = 11.sp, color = Slate500)
                        Text("Fee: ${viewModel.formatConvertedMinor(entry.feeMinor, entry.currencyCode, entry.currencyCode)}", fontSize = 11.sp, color = Slate500)
                        Text("Net: ${viewModel.formatConvertedMinor(entry.netMinor, entry.currencyCode, entry.currencyCode)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMarketsTab(
    countries: List<com.example.data.local.entities.CountryMarketEntity>,
    viewModel: ShoporaViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(countries) { country ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(country.flag, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(country.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("Currency: ${country.currencyCode} (${country.currencySymbol}) • Tax: ${country.taxPercent}%", color = Slate500, fontSize = 11.sp)
                        }
                    }

                    Switch(
                        checked = country.isEnabled,
                        onCheckedChange = { viewModel.setCountryEnabled(country.code, it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
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
