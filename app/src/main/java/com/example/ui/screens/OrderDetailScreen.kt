package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.SellerOrderEntity
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun OrderDetailScreen(viewModel: ShoporaViewModel) {
    val selectedOrderId by viewModel.selectedOrderId.collectAsState()
    val allOrders by viewModel.customerOrders.collectAsState()
    val order = allOrders.find { it.id == selectedOrderId }

    val allSellerOrders by viewModel.allSellerOrders.collectAsState()
    val sellerOrders = remember(order, allSellerOrders) {
        if (order != null) allSellerOrders.filter { it.parentOrderId == order.id } else emptyList()
    }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order details not found")
        }
        return
    }

    Scaffold(
        topBar = {
            Surface(color = NavyDark, modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Order Details: ${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Parent Order Overview
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Consolidated Order Summary",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreenLight
                            ) {
                                Text(
                                    text = order.paymentStatus,
                                    style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Divider(color = Slate100)

                        Text("Recipient: ${order.customerName}", style = MaterialTheme.typography.bodySmall.copy(color = Slate800))
                        Text("Address: ${order.deliveryAddress}, ${order.deliveryCountry}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                        Text("Payment Method: ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))

                        Divider(color = Slate100)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Products Subtotal:", color = Slate600, fontSize = 12.sp)
                            Text(viewModel.formatConvertedMinor(order.subtotalMinor, order.currencySymbol, order.currencyCode), color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Multi-Vendor Shipping:", color = Slate600, fontSize = 12.sp)
                            Text(viewModel.formatConvertedMinor(order.shippingTotalMinor, order.currencySymbol, order.currencyCode), color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taxes & Duties (${order.deliveryCountry}):", color = Slate600, fontSize = 12.sp)
                            Text(viewModel.formatConvertedMinor(order.taxTotalMinor, order.currencySymbol, order.currencyCode), color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total:", fontWeight = FontWeight.Bold)
                            Text(
                                viewModel.formatConvertedMinor(order.grandTotalMinor, order.currencySymbol, order.currencyCode),
                                fontWeight = FontWeight.Black,
                                color = ElectricBlueDark,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "Seller Fulfillment Shipments (${sellerOrders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                )
            }

            // Individual Seller Fulfillment Orders
            items(sellerOrders) { sellerOrder ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (sellerOrder.isOfficialStore) Icons.Default.Verified else Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = if (sellerOrder.isOfficialStore) AccentGoldDark else ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sellerOrder.vendorName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (sellerOrder.status) {
                                    "DELIVERED" -> SuccessGreenLight
                                    "SHIPPED" -> ElectricBlue.copy(alpha = 0.15f)
                                    else -> AccentGoldLight
                                }
                            ) {
                                Text(
                                    text = sellerOrder.status,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = when (sellerOrder.status) {
                                            "DELIVERED" -> SuccessGreen
                                            "SHIPPED" -> ElectricBlue
                                            else -> AccentGoldDark
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tracking & Carrier Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Logistics Carrier: ${sellerOrder.trackingCarrier.ifEmpty { "Pending Carrier Assignment" }}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                )
                                Text(
                                    text = "Tracking Reference: ${sellerOrder.trackingNumber.ifEmpty { "Generating waybill..." }}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ElectricBlue, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                )
                                Text(
                                    text = "Estimated Delivery: ${sellerOrder.estimatedDelivery}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Timeline stages
                        TrackingTimeline(status = sellerOrder.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingTimeline(status: String) {
    val stages = listOf("Confirmed", "Processing", "Shipped", "Delivered")
    val currentIndex = when (status) {
        "PENDING_FULFILLMENT" -> 0
        "PROCESSING" -> 1
        "SHIPPED" -> 2
        "DELIVERED" -> 3
        else -> 1
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            val isDone = index <= currentIndex
            val isCurrent = index == currentIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDone) (if (isCurrent) ElectricBlue else SuccessGreen) else Slate200,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isDone) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        } else {
                            Text("${index + 1}", color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stage,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDone) Slate900 else Slate400
                    )
                )
            }

            if (index < stages.size - 1) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.5f)
                        .background(if (index < currentIndex) SuccessGreen else Slate200)
                )
            }
        }
    }
}
