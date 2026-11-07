package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppRole
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoporaTopBar(
    viewModel: ShoporaViewModel,
    onOpenMarketSheet: () -> Unit,
    onOpenRoleSwitcher: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val cartItems by viewModel.cartWithProducts.collectAsState()
    val totalCartCount = cartItems.sumOf { it.cartItem.quantity }

    Surface(
        color = NavyDark,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo & Role Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.HOME) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Shopora Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "SHOPORA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "GLOBAL MARKETPLACE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        )
                    }
                }

                // Actions: Country/Currency Selector + Role Switcher Chip + Cart
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Country & Currency Selector Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NavySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        modifier = Modifier.clickable { onOpenMarketSheet() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedCountry.flag, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedCurrency.code,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Market",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Role Chip (Tap to switch mode)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when (currentRole) {
                            AppRole.CUSTOMER -> ElectricBlue.copy(alpha = 0.2f)
                            AppRole.VENDOR -> AccentGold.copy(alpha = 0.2f)
                            AppRole.ADMIN -> ErrorRed.copy(alpha = 0.2f)
                            AppRole.OFFICIAL_STORE_MANAGER -> SuccessGreen.copy(alpha = 0.2f)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (currentRole) {
                                AppRole.CUSTOMER -> ElectricBlue
                                AppRole.VENDOR -> AccentGold
                                AppRole.ADMIN -> ErrorRed
                                AppRole.OFFICIAL_STORE_MANAGER -> SuccessGreen
                            }
                        ),
                        modifier = Modifier.clickable { onOpenRoleSwitcher() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val roleLabel = when (currentRole) {
                                AppRole.CUSTOMER -> "Customer"
                                AppRole.VENDOR -> "Vendor"
                                AppRole.ADMIN -> "Admin"
                                AppRole.OFFICIAL_STORE_MANAGER -> "Platform Store"
                            }
                            Text(
                                text = roleLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Cart Icon (with count badge)
                    if (currentRole == AppRole.CUSTOMER) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.CART) },
                            modifier = Modifier.size(38.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge(
                                            containerColor = AccentGoldDark,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = "$totalCartCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = "Shopping Cart",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
