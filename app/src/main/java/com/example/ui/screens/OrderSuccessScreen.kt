package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun OrderSuccessScreen(viewModel: ShoporaViewModel) {
    val orderId by viewModel.latestPlacedOrderId.collectAsState()
    val allOrders by viewModel.customerOrders.collectAsState()
    val order = allOrders.find { it.id == orderId } ?: allOrders.firstOrNull()

    val allSellerOrders by viewModel.allSellerOrders.collectAsState()
    val sellerOrders = remember(order, allSellerOrders) {
        if (order != null) allSellerOrders.filter { it.parentOrderId == order.id } else emptyList()
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Continue Shopping")
                    }

                    Button(
                        onClick = {
                            if (order != null) {
                                viewModel.openOrderDetail(order.id)
                            } else {
                                viewModel.navigateTo(AppScreen.ORDER_HISTORY)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Track Packages")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    shape = CircleShape,
                    color = SuccessGreen,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Payment Confirmed!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Slate900
                    )
                )

                Text(
                    text = "Order Number: ${order?.orderNumber ?: "SHP-2026"}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlueDark
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Thank you for shopping with SHOPORA. A payment confirmation receipt and live tracking updates have been sent to your registered email.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate600,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Multi-Vendor fulfillment cards
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Separate Seller Packages (${sellerOrders.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Text(
                            text = "Each seller prepares and dispatches their parcel independently to ensure prompt handling.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        sellerOrders.forEach { sellerOrder ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Slate50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                if (sellerOrder.isOfficialStore) Icons.Default.Verified else Icons.Default.Storefront,
                                                contentDescription = null,
                                                tint = if (sellerOrder.isOfficialStore) AccentGoldDark else ElectricBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = sellerOrder.vendorName,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                            )
                                        }
                                        Text(
                                            text = "${sellerOrder.itemCount} items • ${sellerOrder.estimatedDelivery}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                        )
                                        Text(
                                            text = "Status: ${sellerOrder.status}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ElectricBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = ElectricBlue.copy(alpha = 0.1f)
                                    ) {
                                        Icon(
                                            Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = ElectricBlue,
                                            modifier = Modifier.padding(8.dp).size(20.dp)
                                        )
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
