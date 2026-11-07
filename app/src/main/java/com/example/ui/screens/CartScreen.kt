package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AppScreen
import com.example.ui.CartItemWithProduct
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun CartScreen(viewModel: ShoporaViewModel) {
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    // Group items by Vendor (Official Store vs Independent Sellers)
    val groupedByVendor = cartWithProducts.groupBy { it.product.vendorId }

    val totalSubtotalUsd = cartWithProducts.sumOf { it.product.priceMinorUsd * it.cartItem.quantity }
    // Total shipping: 0 for official store, vendor shipping fee for each distinct third-party seller
    val totalShippingUsd = groupedByVendor.entries.sumOf { (vendorId, items) ->
        if (vendorId == "shopora-official") 0L else items.first().product.shippingFeeMinorUsd
    }
    val taxRate = selectedCountry.taxPercent
    val estimatedTaxUsd = (totalSubtotalUsd * (taxRate / 100.0)).toLong()
    val grandTotalUsd = totalSubtotalUsd + totalShippingUsd + estimatedTaxUsd

    Scaffold(
        bottomBar = {
            if (cartWithProducts.isNotEmpty()) {
                Surface(
                    color = Color.White,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Summary breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal (${cartWithProducts.sumOf { it.cartItem.quantity }} items):", color = Slate600, fontSize = 13.sp)
                            Text(viewModel.formatPrice(totalSubtotalUsd), fontWeight = FontWeight.Bold, color = Slate900)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping (${groupedByVendor.size} seller packages):", color = Slate600, fontSize = 13.sp)
                            Text(if (totalShippingUsd == 0L) "FREE" else viewModel.formatPrice(totalShippingUsd), fontWeight = FontWeight.Bold, color = if (totalShippingUsd == 0L) SuccessGreen else Slate900)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Duties & Tax (${selectedCountry.name}):", color = Slate600, fontSize = 13.sp)
                            Text(viewModel.formatPrice(estimatedTaxUsd), fontWeight = FontWeight.Bold, color = Slate900)
                        }

                        Divider(color = Slate200, modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total in ${selectedCurrency.code}:", color = Slate500, fontSize = 11.sp)
                                Text(
                                    text = viewModel.formatPrice(grandTotalUsd),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = ElectricBlueDark
                                    )
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.CHECKOUT) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50)
                .padding(innerPadding)
        ) {
            // Header
            Surface(
                color = NavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Multi-Vendor Shopping Cart",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${cartWithProducts.size} item(s) from ${groupedByVendor.size} seller(s)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                        )
                    }

                    if (cartWithProducts.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Clear", color = ErrorRedLight, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (cartWithProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Slate100,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.RemoveShoppingCart, contentDescription = null, tint = Slate400, modifier = Modifier.size(40.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your shopping cart is empty",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate800)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Discover items from official brand flagships & global sellers",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Start Shopping")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Render sections grouped by seller
                    groupedByVendor.forEach { (vendorId, items) ->
                        val firstProduct = items.first().product
                        val isOfficial = firstProduct.isOfficialStore
                        val vendorName = firstProduct.vendorName

                        item(key = vendorId) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Vendor Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                if (isOfficial) Icons.Default.Verified else Icons.Default.Storefront,
                                                contentDescription = null,
                                                tint = if (isOfficial) AccentGoldDark else ElectricBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = vendorName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Slate900
                                                )
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isOfficial) AccentGold.copy(alpha = 0.15f) else Slate100
                                        ) {
                                            Text(
                                                text = if (isOfficial) "FREE SHIPPING" else "SHIPPING: ${viewModel.formatPrice(firstProduct.shippingFeeMinorUsd)}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    color = if (isOfficial) AccentGoldDark else Slate700
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Divider(color = Slate100, modifier = Modifier.padding(vertical = 10.dp))

                                    // Items from this vendor
                                    items.forEach { cartItemWithProd ->
                                        CartItemRow(
                                            item = cartItemWithProd,
                                            viewModel = viewModel
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemWithProduct,
    viewModel: ShoporaViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        AsyncImage(
            model = item.product.imageUrl,
            contentDescription = item.product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Slate100)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Slate900,
                    lineHeight = 18.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (item.cartItem.variant.isNotBlank()) {
                Text(
                    text = "Option: ${item.cartItem.variant}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = viewModel.formatPrice(item.product.priceMinorUsd),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = ElectricBlueDark
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Quantity controls
        Column(horizontalAlignment = Alignment.End) {
            IconButton(
                onClick = { viewModel.removeFromCart(item.cartItem.id) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = Slate400, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate100,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.updateCartQuantity(item.cartItem.id, item.cartItem.quantity - 1) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Text(
                        text = "${item.cartItem.quantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = { viewModel.updateCartQuantity(item.cartItem.id, item.cartItem.quantity + 1) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
