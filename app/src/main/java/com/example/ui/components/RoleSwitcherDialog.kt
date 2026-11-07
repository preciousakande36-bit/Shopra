package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppRole
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*

@Composable
fun RoleSwitcherDialog(
    viewModel: ShoporaViewModel,
    onDismiss: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavyDark,
        title = {
            Column {
                Text(
                    text = "Switch Application Role",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Explore SHOPORA from any stakeholder viewpoint",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoleOptionCard(
                    title = "Customer Experience",
                    subtitle = "Browse global catalog, multi-vendor cart, local currencies, order tracking & chat",
                    icon = Icons.Default.Person,
                    badgeColor = ElectricBlue,
                    isSelected = currentRole == AppRole.CUSTOMER,
                    onClick = {
                        viewModel.setRole(AppRole.CUSTOMER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    title = "Independent Vendor (Tokyo Sound)",
                    subtitle = "Fulfill orders, input tracking numbers, add products, view earnings & payouts",
                    icon = Icons.Default.Storefront,
                    badgeColor = AccentGold,
                    isSelected = currentRole == AppRole.VENDOR,
                    onClick = {
                        viewModel.setRole(AppRole.VENDOR)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    title = "Shopora Official Store Manager",
                    subtitle = "Manage platform-owned products, flagship promotions & 100% direct revenue",
                    icon = Icons.Default.Verified,
                    badgeColor = SuccessGreen,
                    isSelected = currentRole == AppRole.OFFICIAL_STORE_MANAGER,
                    onClick = {
                        viewModel.setRole(AppRole.OFFICIAL_STORE_MANAGER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    title = "Marketplace Administrator",
                    subtitle = "Approve sellers, commission controls, immutable financial ledger & country config",
                    icon = Icons.Default.AdminPanelSettings,
                    badgeColor = ErrorRed,
                    isSelected = currentRole == AppRole.ADMIN,
                    onClick = {
                        viewModel.setRole(AppRole.ADMIN)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Slate300)
            }
        }
    )
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) NavyLight else NavySurface,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) badgeColor else Slate700
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = badgeColor, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Slate400,
                        lineHeight = 15.sp
                    )
                )
            }

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = badgeColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}
