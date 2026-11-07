package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.SortOrder
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun SearchScreen(viewModel: ShoporaViewModel) {
    val allProducts by viewModel.allProducts.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    var filterSellerType by remember { mutableStateOf("ALL") } // ALL, OFFICIAL, VENDORS
    var localSearchText by remember(searchQuery) { mutableStateOf(searchQuery) }

    // Filter and sort products
    val filteredProducts = remember(allProducts, searchQuery, selectedCategoryId, sortOrder, filterSellerType) {
        var list = allProducts

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.vendorName.lowercase().contains(q)
            }
        }

        if (selectedCategoryId != null) {
            list = list.filter { it.categoryId == selectedCategoryId }
        }

        when (filterSellerType) {
            "OFFICIAL" -> list = list.filter { it.isOfficialStore }
            "VENDORS" -> list = list.filter { !it.isOfficialStore }
        }

        when (sortOrder) {
            SortOrder.RELEVANCE -> list.sortedByDescending { it.isFeatured }
            SortOrder.PRICE_LOW_HIGH -> list.sortedBy { it.priceMinorUsd }
            SortOrder.PRICE_HIGH_LOW -> list.sortedByDescending { it.priceMinorUsd }
            SortOrder.HIGHEST_RATED -> list.sortedByDescending { it.rating }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(bottom = 80.dp)
    ) {
        // Search Input Bar
        Surface(color = NavyDark, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = localSearchText,
                    onValueChange = {
                        localSearchText = it
                        viewModel.setSearchQuery(it)
                    },
                    placeholder = { Text("Search products, brands, stores...", color = Slate400, fontSize = 13.sp) },
                    leadingIcon = {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
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

                Spacer(modifier = Modifier.height(10.dp))

                // Seller Filter Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = filterSellerType == "ALL",
                        onClick = { filterSellerType = "ALL" },
                        label = { Text("All Sellers", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White,
                            containerColor = NavySurface,
                            labelColor = Slate300
                        )
                    )

                    FilterChip(
                        selected = filterSellerType == "OFFICIAL",
                        onClick = { filterSellerType = "OFFICIAL" },
                        label = { Text("Official Store", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = AccentGold, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGoldDark,
                            selectedLabelColor = Color.White,
                            containerColor = NavySurface,
                            labelColor = Slate300
                        )
                    )

                    FilterChip(
                        selected = filterSellerType == "VENDORS",
                        onClick = { filterSellerType = "VENDORS" },
                        label = { Text("Independent Vendors", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlueDark,
                            selectedLabelColor = Color.White,
                            containerColor = NavySurface,
                            labelColor = Slate300
                        )
                    )
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { viewModel.setSelectedCategory(null) },
                    label = { Text("All Categories") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(allCategories) { cat ->
                FilterChip(
                    selected = selectedCategoryId == cat.id,
                    onClick = { viewModel.setSelectedCategory(cat.id) },
                    label = { Text(cat.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Sort Bar & Result Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredProducts.size} Products Found",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            )

            // Sort Dropdown Button
            var sortMenuExpanded by remember { mutableStateOf(false) }
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate300),
                    modifier = Modifier.clickable { sortMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", tint = Slate700, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (sortOrder) {
                                SortOrder.RELEVANCE -> "Featured"
                                SortOrder.PRICE_LOW_HIGH -> "Price: Low to High"
                                SortOrder.PRICE_HIGH_LOW -> "Price: High to Low"
                                SortOrder.HIGHEST_RATED -> "Highest Rated"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Featured / Relevance") },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.RELEVANCE)
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Price: Low to High") },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.PRICE_LOW_HIGH)
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Price: High to Low") },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.PRICE_HIGH_LOW)
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Highest Rated") },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.HIGHEST_RATED)
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Product Grid List
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = "No products",
                        tint = Slate400,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching products found",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                    )
                    Text(
                        text = "Try clearing filters or searching another keyword",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }
        } else {
            val pairs = filteredProducts.chunked(2)
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pairs) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
    }
}
