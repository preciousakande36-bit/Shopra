package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReviewEntity
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(viewModel: ShoporaViewModel) {
    val selectedId by viewModel.selectedProductId.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val product = allProducts.find { it.id == selectedId }
    val wishlist by viewModel.wishlist.collectAsState()
    val isWishlisted = wishlist.any { it.productId == selectedId }
    val selectedCountry by viewModel.selectedCountry.collectAsState()

    var selectedVariant by remember { mutableStateOf("") }
    var showReviewDialog by remember { mutableStateOf(false) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
        return
    }

    val variantsList = remember(product.variantsJson) {
        if (product.variantsJson.isNotBlank()) {
            product.variantsJson.split(",").map { it.trim() }
        } else emptyList()
    }

    LaunchedEffect(variantsList) {
        if (variantsList.isNotEmpty() && selectedVariant.isEmpty()) {
            selectedVariant = variantsList.first()
        }
    }

    val reviewsFlow = remember(product.id) { viewModel.getProductReviews(product.id) }
    val reviews by reviewsFlow.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Wishlist toggle
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { viewModel.toggleWishlist(product.id) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) ErrorRed else Slate700
                            )
                        }
                    }

                    // Add to Cart button
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(product.id, selectedVariant, 1)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Buy Now button
                    Button(
                        onClick = {
                            viewModel.addToCart(product.id, selectedVariant, 1)
                            viewModel.navigateTo(AppScreen.CART)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Buy Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top Nav Header with Back button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyDark)
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    IconButton(onClick = { viewModel.navigateTo(AppScreen.CART) }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                    }
                }
            }

            // Image Gallery Hero
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Slate100)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Origin & Official Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (product.isOfficialStore) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NavyDark.copy(alpha = 0.95f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SHOPORA OFFICIAL FLAGSHIP",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AccentGold,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NavySurface.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "Origin: ${product.originCountry} • Verified Seller",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Title, Price & Stock Section
            item {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                lineHeight = 26.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating Bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${product.reviewCount} global reviews",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreenLight
                            ) {
                                Text(
                                    text = "In Stock (${product.stock} available)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pricing
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = viewModel.formatPrice(product.priceMinorUsd),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = ElectricBlueDark
                                )
                            )

                            if (product.originalPriceMinorUsd > product.priceMinorUsd) {
                                Text(
                                    text = viewModel.formatPrice(product.originalPriceMinorUsd),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Slate400,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                )

                                val discountPercent = ((product.originalPriceMinorUsd - product.priceMinorUsd) * 100 / product.originalPriceMinorUsd).toInt()
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ErrorRedLight
                                ) {
                                    Text(
                                        text = "-$discountPercent%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ErrorRed,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Prices converted automatically based on selected currency. All taxes calculated at checkout with zero hidden fees.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate500,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Variant Selector
            if (variantsList.isNotEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Select Variant / Options",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(variantsList) { variant ->
                                    val isSelected = variant == selectedVariant
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) ElectricBlue else Slate100,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) ElectricBlue else Slate300
                                        ),
                                        modifier = Modifier.clickable { selectedVariant = variant }
                                    ) {
                                        Text(
                                            text = variant,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Slate800
                                            ),
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Seller Profile & Chat Card
            item {
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
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
                                    color = if (product.isOfficialStore) AccentGold.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.1f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (product.isOfficialStore) Icons.Default.Verified else Icons.Default.Storefront,
                                            contentDescription = null,
                                            tint = if (product.isOfficialStore) AccentGoldDark else ElectricBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = product.vendorName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900
                                            )
                                        )
                                        if (product.isOfficialStore) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = AccentGold, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(
                                        text = if (product.isOfficialStore) "Official Brand Flagship • Direct Manufacturer" else "Verified Independent Seller • Origin ${product.originCountry}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Slate500,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Chat with Seller button
                            OutlinedButton(
                                onClick = {
                                    viewModel.openChatWithVendor(product.vendorId)
                                },
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Chat", tint = ElectricBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Shipping, Delivery & Guarantee Info
            item {
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Delivery & Guarantees",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (product.isOfficialStore) "Free Worldwide Express Delivery" else "Standard International Shipping (${viewModel.formatPrice(product.shippingFeeMinorUsd)})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                )
                                Text(
                                    text = "Estimated arrival: 3-5 business days to ${selectedCountry.name}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AssignmentReturn, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${product.returnDays}-Day Buyer Protection & Return Policy",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                )
                                Text(
                                    text = "Full refund guarantee if item is defective or not as described.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AccentGoldDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Shopora Authenticity Guarantee",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                )
                                Text(
                                    text = "Inspected and certified authentic before shipment.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }
            }

            // Description & Specs
            item {
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Product Description",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Slate700,
                                lineHeight = 20.sp
                            )
                        )

                        if (product.specificationsJson.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Technical Specifications",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate100,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    val specs = product.specificationsJson.split(";")
                                    specs.forEach { spec ->
                                        if (spec.isNotBlank()) {
                                            Text(
                                                text = "• ${spec.trim()}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Slate800,
                                                    fontSize = 12.sp
                                                ),
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Customer Reviews Section
            item {
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Customer Reviews (${reviews.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )

                            TextButton(onClick = { showReviewDialog = true }) {
                                Text("Write Review", color = ElectricBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet. Be the first verified buyer to review!",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            reviews.forEach { rev ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = rev.authorName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Slate900
                                                )
                                            )
                                            if (rev.isVerifiedPurchase) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = SuccessGreenLight
                                                ) {
                                                    Text(
                                                        text = "Verified Buyer",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = SuccessGreen,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Row {
                                            repeat(rev.rating) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(13.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = rev.comment,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Slate700,
                                            lineHeight = 17.sp
                                        )
                                    )
                                    Divider(color = Slate100, modifier = Modifier.padding(top = 10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        var ratingVal by remember { mutableStateOf(5) }
        var reviewText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Review Product") },
            text = {
                Column {
                    Text("Select Rating:")
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= ratingVal) AccentGold else Slate300,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { ratingVal = star }
                                    .padding(2.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = { Text("Write your honest experience with this product...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewText.isNotBlank()) {
                            viewModel.submitReview(product.id, product.vendorId, ratingVal, reviewText)
                            showReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
