package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun CheckoutScreen(viewModel: ShoporaViewModel) {
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    var customerName by remember { mutableStateOf("Jordan Davis") }
    var customerEmail by remember { mutableStateOf("jordan.davis@example.com") }
    var customerPhone by remember { mutableStateOf("+1 202 555 0184") }
    var deliveryAddress by remember { mutableStateOf("742 Evergreen Terrace, Suite 4B") }
    var selectedPaymentMethod by remember { mutableStateOf("Stripe - Global Card & Apple Pay") }
    var isProcessing by remember { mutableStateOf(false) }

    val groupedByVendor = cartWithProducts.groupBy { it.product.vendorId }
    val totalSubtotalUsd = cartWithProducts.sumOf { it.product.priceMinorUsd * it.cartItem.quantity }
    val totalShippingUsd = groupedByVendor.entries.sumOf { (vendorId, items) ->
        if (vendorId == "shopora-official") 0L else items.first().product.shippingFeeMinorUsd
    }
    val taxRate = selectedCountry.taxPercent
    val estimatedTaxUsd = (totalSubtotalUsd * (taxRate / 100.0)).toLong()
    val grandTotalUsd = totalSubtotalUsd + totalShippingUsd + estimatedTaxUsd

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
                        text = "Secure Global Checkout",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Payable in ${selectedCurrency.code}:", color = Slate500, fontSize = 11.sp)
                        Text(
                            text = viewModel.formatPrice(grandTotalUsd),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = ElectricBlueDark
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (customerName.isNotBlank() && deliveryAddress.isNotBlank()) {
                                isProcessing = true
                                viewModel.executeCheckout(
                                    customerName = customerName,
                                    customerEmail = customerEmail,
                                    customerPhone = customerPhone,
                                    deliveryAddress = deliveryAddress,
                                    paymentMethodName = selectedPaymentMethod
                                )
                            }
                        },
                        enabled = !isProcessing && cartWithProducts.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.height(48.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Confirm & Pay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Customer Details & Shipping Address
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(6.dp), color = ElectricBlue, modifier = Modifier.size(22.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delivery Country & Address", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Destination: ${selectedCountry.flag} ${selectedCountry.name} (${selectedCountry.code})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate800)
                                )
                                Text("Standard Tax: ${selectedCountry.taxPercent}%", color = Slate500, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Full Recipient Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Street Address, Suite / Apartment, City") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customerEmail,
                                onValueChange = { customerEmail = it },
                                label = { Text("Email (Order Tracking)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Phone") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Step 2: Multi-Vendor Shipments Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(6.dp), color = ElectricBlue, modifier = Modifier.size(22.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Multi-Vendor Fulfillment Packages (${groupedByVendor.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "To guarantee quality and speed, each seller prepares and dispatches their portion of your order independently.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp, lineHeight = 16.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        groupedByVendor.forEach { (vendorId, items) ->
                            val isOfficial = vendorId == "shopora-official"
                            val vendorName = items.first().product.vendorName
                            val shippingFee = if (isOfficial) 0L else items.first().product.shippingFeeMinorUsd

                            Surface(
                                shape = RoundedCornerShape(10.dp),
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
                                                if (isOfficial) Icons.Default.Verified else Icons.Default.Storefront,
                                                contentDescription = null,
                                                tint = if (isOfficial) AccentGoldDark else ElectricBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Package: $vendorName",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                            )
                                        }
                                        Text(
                                            text = "${items.sumOf { it.cartItem.quantity }} item(s) • Est. Delivery in 3-5 days",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                        )
                                    }

                                    Text(
                                        text = if (isOfficial) "FREE" else viewModel.formatPrice(shippingFee),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOfficial) SuccessGreen else Slate900
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step 3: Payment Provider Selection
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(6.dp), color = ElectricBlue, modifier = Modifier.size(22.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("3", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Payment Method", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val paymentMethods = listOf(
                            "Stripe - Global Card & Apple Pay",
                            "Paystack - African Cards, Bank Transfer & USSD",
                            "Flutterwave - Multi-Currency & Mobile Money",
                            "Digital Wallet - Instant Balance"
                        )

                        paymentMethods.forEach { method ->
                            val isSelected = method == selectedPaymentMethod
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) ElectricBlue.copy(alpha = 0.08f) else Slate50,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) ElectricBlue else Slate200
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedPaymentMethod = method }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentMethod = method },
                                        colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = method,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = Slate900
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step 4: Transparent Cost Breakdown (No Hidden Fees)
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
                        Text("Fee Transparency Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Items Subtotal:", color = Slate600, fontSize = 13.sp)
                            Text(viewModel.formatPrice(totalSubtotalUsd), color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Combined Shipping across ${groupedByVendor.size} sellers:", color = Slate600, fontSize = 13.sp)
                            Text(if (totalShippingUsd == 0L) "FREE" else viewModel.formatPrice(totalShippingUsd), color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Import Duties & Tax (${selectedCountry.taxPercent}%):", color = Slate600, fontSize = 13.sp)
                            Text(viewModel.formatPrice(estimatedTaxUsd), color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Hidden Marketplace Fees:", color = SuccessGreen, fontSize = 13.sp)
                            Text("$0.00 (Guaranteed)", color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Final Total:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                viewModel.formatPrice(grandTotalUsd),
                                fontWeight = FontWeight.Black,
                                color = ElectricBlueDark,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
