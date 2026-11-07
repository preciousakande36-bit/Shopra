package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.AppRole
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun ShoporaBottomBar(
    viewModel: ShoporaViewModel
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartWithProducts.collectAsState()
    val cartCount = cartItems.sumOf { it.cartItem.quantity }

    NavigationBar(
        containerColor = NavyDark,
        contentColor = Slate300,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        when (currentRole) {
            AppRole.CUSTOMER -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ElectricBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.CATEGORIES,
                    onClick = { viewModel.navigateTo(AppScreen.CATEGORIES) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.CATEGORIES) Icons.Filled.GridView else Icons.Outlined.GridView,
                            contentDescription = "Categories"
                        )
                    },
                    label = { Text("Categories") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ElectricBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.WISHLIST,
                    onClick = { viewModel.navigateTo(AppScreen.WISHLIST) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.WISHLIST) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist"
                        )
                    },
                    label = { Text("Wishlist") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ElectricBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.CART,
                    onClick = { viewModel.navigateTo(AppScreen.CART) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = AccentGoldDark,
                                        contentColor = Color.White
                                    ) {
                                        Text("$cartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ElectricBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ACCOUNT,
                    onClick = { viewModel.navigateTo(AppScreen.ACCOUNT) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.ACCOUNT) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Account"
                        )
                    },
                    label = { Text("Account") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ElectricBlue,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
            }

            AppRole.VENDOR, AppRole.OFFICIAL_STORE_MANAGER -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.VENDOR_DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.VENDOR_DASHBOARD) },
                    icon = {
                        Icon(Icons.Default.Storefront, contentDescription = "Store Overview")
                    },
                    label = { Text("Store") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDark,
                        selectedTextColor = Color.White,
                        indicatorColor = AccentGold,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    icon = {
                        Icon(Icons.Default.Store, contentDescription = "Customer View")
                    },
                    label = { Text("Shop View") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDark,
                        selectedTextColor = Color.White,
                        indicatorColor = AccentGold,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ACCOUNT,
                    onClick = { viewModel.navigateTo(AppScreen.ACCOUNT) },
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = "Account Settings")
                    },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyDark,
                        selectedTextColor = Color.White,
                        indicatorColor = AccentGold,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
            }

            AppRole.ADMIN -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN_CONTROL,
                    onClick = { viewModel.navigateTo(AppScreen.ADMIN_CONTROL) },
                    icon = {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Console")
                    },
                    label = { Text("Control") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ErrorRed,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    icon = {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Live Marketplace")
                    },
                    label = { Text("Market") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ErrorRed,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ACCOUNT,
                    onClick = { viewModel.navigateTo(AppScreen.ACCOUNT) },
                    icon = {
                        Icon(Icons.Default.ManageAccounts, contentDescription = "Settings")
                    },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = ErrorRed,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    )
                )
            }
        }
    }
}
