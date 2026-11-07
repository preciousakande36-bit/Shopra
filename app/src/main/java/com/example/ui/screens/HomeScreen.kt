package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(viewModel: ShoporaViewModel) {
    val products by viewModel.allProducts.collectAsState()
    val officialProducts by viewModel.officialStoreProducts.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val vendors by viewModel.allVendors.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var localSearchText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Search Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDark)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = localSearchText,
                    onValueChange = {
                        localSearchText = it
                        viewModel.setSearchQuery(it)
                    },
                    placeholder = {
                        Text(
                            "Search 50,000+ products, official stores, brands...",
                            fontSize = 13.sp,
                            color = Slate400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = ElectricBlue)
                    },
                    trailingIcon = {
                        if (localSearchText.isNotEmpty()) {
                            IconButton(onClick = {
                                localSearchText = ""
                                viewModel.setSearchQuery("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Hero Promotional Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(NavyDark, NavySurface, ElectricBlueDark)
                            )
                        )
                ) {
                    // Try to display generated hero banner drawable
                    Image(
                        painter = painterResource(id = R.drawable.shopora_hero_banner_1793989790774),
                        contentDescription = "Shopora Global Exclusive",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay with CTA
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, NavyDark.copy(alpha = 0.85f))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentGold
                                ) {
                                    Text(
                                        text = "GLOBAL EXCLUSIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NavyDark,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Worldwide Multi-Vendor Shopping",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Direct platform store & verified artisan sellers",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate300,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.CATEGORIES) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Explore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Category Horizontal Strip
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    )
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.CATEGORIES) }) {
                        Text("See All", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    viewModel.setSelectedCategory(category.id)
                                }
                                .width(68.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Slate100,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val icon = when (category.id) {
                                        "phones" -> Icons.Default.Smartphone
                                        "computers" -> Icons.Default.Laptop
                                        "electronics" -> Icons.Default.Headphones
                                        "fashion" -> Icons.Default.Checkroom
                                        "shoes" -> Icons.Default.ShoppingBag
                                        "beauty" -> Icons.Default.Spa
                                        "home" -> Icons.Default.Home
                                        "sports" -> Icons.Default.FitnessCenter
                                        else -> Icons.Default.Category
                                    }
                                    Icon(
                                        icon,
                                        contentDescription = category.name,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = Slate700
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Official Platform Store Spotlight Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified Flagship",
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Shopora Direct Official Store",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
                        ) {
                            Text(
                                text = "FLAGSHIP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Sold directly by the platform. 100% Genuine Guaranteed • 2-Year International Global Warranty • Free Worldwide Express Delivery",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Horizontal list of official products
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(officialProducts) { prod ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NavySurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                                modifier = Modifier
                                    .width(180.dp)
                                    .clickable { viewModel.openProductDetail(prod.id) }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = prod.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = viewModel.formatPrice(prod.priceMinorUsd),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = AccentGold
                                            )
                                        )
                                        Text(
                                            text = "Free Ship",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SuccessGreen,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Featured Global Independent Vendors Showcase
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Featured Global Vendors",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vendors.filter { !it.isOfficial && it.status == "APPROVED" }) { vendor ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .width(220.dp)
                                .clickable {
                                    viewModel.openChatWithVendor(vendor.id)
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = ElectricBlue.copy(alpha = 0.1f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Storefront,
                                                contentDescription = vendor.businessName,
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = vendor.businessName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${vendor.countryCode} • ${vendor.businessCategory}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Slate500,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = vendor.storeDescription,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate600,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = AccentGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${vendor.rating} (${vendor.reviewCount})",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = "Chat Seller",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElectricBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // All Products Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending International Marketplace",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                )
                Text(
                    text = "${products.size} Items",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }
        }

        // Products Grid items (2-column layout rendered in pairs)
        val productPairs = products.chunked(2)
        items(productPairs) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProductCard(product = pair[0], viewModel = viewModel)
                }
                if (pair.size > 1) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(product = pair[1], viewModel = viewModel)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
