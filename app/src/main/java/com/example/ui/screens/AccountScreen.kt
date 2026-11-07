package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppRole
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun AccountScreen(
    viewModel: ShoporaViewModel,
    onOpenMarketSheet: () -> Unit,
    onOpenRoleSwitcher: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val orders by viewModel.customerOrders.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()

    var showLegalDialog by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // User Profile Header
        item {
            Surface(
                color = NavyDark,
                modifier = Modifier.fillMaxWidth().statusBarsPadding()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElectricBlue,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("JD", fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Jordan Davis",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "jordan.davis@example.com • Verified Member",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                            )
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCountry.flag, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${selectedCountry.name} (${selectedCurrency.code})",
                                    style = MaterialTheme.typography.labelSmall.copy(color = AccentGold, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Persona & Role Switcher Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onOpenRoleSwitcher() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AccentGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Current Role: ${currentRole.name.replace('_', ' ')}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Tap to switch to Vendor, Platform Store, or Admin",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
                }
            }
        }

        // Shopping Shortcuts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column {
                    AccountOptionRow(
                        title = "Order History & Live Tracking",
                        subtitle = "${orders.size} orders placed across global sellers",
                        icon = Icons.Default.LocalShipping,
                        onClick = { viewModel.navigateTo(AppScreen.ORDER_HISTORY) }
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                    AccountOptionRow(
                        title = "Saved Wishlist",
                        subtitle = "${wishlist.size} favorite items saved",
                        icon = Icons.Default.Favorite,
                        onClick = { viewModel.navigateTo(AppScreen.WISHLIST) }
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                    AccountOptionRow(
                        title = "Country & Currency Settings",
                        subtitle = "${selectedCountry.name} • ${selectedCurrency.code} (${selectedCurrency.symbol})",
                        icon = Icons.Default.Language,
                        onClick = onOpenMarketSheet
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Role-specific Dashboards access
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column {
                    AccountOptionRow(
                        title = "Seller / Vendor Portal",
                        subtitle = "Manage inventory, orders, tracking, earnings & withdrawals",
                        icon = Icons.Default.Storefront,
                        onClick = { viewModel.setRole(AppRole.VENDOR) }
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                    AccountOptionRow(
                        title = "Platform Store Management",
                        subtitle = "Manage official Shopora Direct products & flagship catalog",
                        icon = Icons.Default.Verified,
                        onClick = { viewModel.setRole(AppRole.OFFICIAL_STORE_MANAGER) }
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                    AccountOptionRow(
                        title = "Marketplace Admin Control Center",
                        subtitle = "Platform metrics, commissions, vendor approval & audit ledger",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = { viewModel.setRole(AppRole.ADMIN) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Legal & Trust Information
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column {
                    AccountOptionRow(
                        title = "Multi-Vendor Marketplace Agreement",
                        subtitle = "Commission structure, dispute resolution & seller obligations",
                        icon = Icons.Default.Gavel,
                        onClick = { showLegalDialog = "SELLER_AGREEMENT" }
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                    AccountOptionRow(
                        title = "Global Privacy & Data Protection",
                        subtitle = "Compliance with GDPR, CCPA, and international consumer standards",
                        icon = Icons.Default.Security,
                        onClick = { showLegalDialog = "PRIVACY" }
                    )
                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                    AccountOptionRow(
                        title = "Terms of Service & Buyer Protection",
                        subtitle = "Zero-hidden-fee guarantee and verified purchase rights",
                        icon = Icons.Default.Policy,
                        onClick = { showLegalDialog = "TERMS" }
                    )
                }
            }
        }
    }

    // Legal Policy Dialog
    showLegalDialog?.let { type ->
        AlertDialog(
            onDismissRequest = { showLegalDialog = null },
            title = {
                Text(
                    text = when (type) {
                        "SELLER_AGREEMENT" -> "Marketplace Seller Agreement"
                        "PRIVACY" -> "International Privacy Policy"
                        else -> "Terms of Service & Buyer Protection"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (type) {
                        "SELLER_AGREEMENT" -> "Independent third-party vendors must undergo identity verification before publishing merchandise. All financial settlements are held in escrow until carrier tracking confirms delivery. Commission fees are calculated per vendor contract."
                        "PRIVACY" -> "SHOPORA enforces end-to-end data security. Customer credit card information is tokenized through PCI-DSS certified payment adapters (Stripe, Paystack, Flutterwave) and is never stored in plain text."
                        else -> "SHOPORA operates an open hybrid marketplace combining official direct goods with verified independent sellers. All orders guarantee transparent currency pricing, accurate tax/duties, and multi-vendor shipment tracking."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700, lineHeight = 18.sp)
                )
            },
            confirmButton = {
                Button(onClick = { showLegalDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                    Text("Understood")
                }
            }
        )
    }
}

@Composable
private fun AccountOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ElectricBlue.copy(alpha = 0.1f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = ElectricBlueDark, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp))
            }
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400, modifier = Modifier.size(18.dp))
    }
}
