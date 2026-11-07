package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersHistoryScreen(viewModel: ShoporaViewModel) {
    val orders by viewModel.customerOrders.collectAsState()
    val allSellerOrders by viewModel.allSellerOrders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(bottom = 80.dp)
    ) {
        Surface(color = NavyDark, modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "My Orders (${orders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = Slate400, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No orders placed yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Your placed multi-vendor orders will appear here", color = Slate500, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    val sellerOrdersCount = allSellerOrders.count { it.parentOrderId == order.id }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openOrderDetail(order.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.orderNumber,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = ElectricBlueDark
                                    )
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (order.fulfillmentStatus) {
                                        "DELIVERED" -> SuccessGreenLight
                                        "SHIPPED" -> ElectricBlue.copy(alpha = 0.15f)
                                        else -> AccentGoldLight
                                    }
                                ) {
                                    Text(
                                        text = order.fulfillmentStatus,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = when (order.fulfillmentStatus) {
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

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Placed: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(order.createdAt))} • $sellerOrdersCount Vendor Package(s)",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Paid: ${viewModel.formatConvertedMinor(order.grandTotalMinor, order.currencySymbol, order.currencyCode)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Track Details", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
