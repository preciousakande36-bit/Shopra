package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*

@Composable
fun CategoriesScreen(viewModel: ShoporaViewModel) {
    val categories by viewModel.allCategories.collectAsState()
    val products by viewModel.allProducts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Surface(
            color = NavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Explore Categories",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Browse curated merchandise across global departments",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )
            }
        }

        // Category Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { category ->
                val itemCount = products.count { it.categoryId == category.id }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setSelectedCategory(category.id)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            modifier = Modifier.size(56.dp)
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
                                Icon(icon, contentDescription = category.name, tint = ElectricBlue, modifier = Modifier.size(28.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            ),
                            maxLines = 1
                        )

                        Text(
                            text = "$itemCount Items",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
