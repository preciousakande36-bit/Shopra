package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.ShoporaViewModel
import com.example.ui.components.MarketSelectorBottomSheet
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.components.ShoporaBottomBar
import com.example.ui.components.ShoporaTopBar
import com.example.ui.screens.*
import com.example.ui.theme.NavyDark
import com.example.ui.theme.ShoporaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoporaTheme {
                val viewModel: ShoporaViewModel = viewModel()
                ShoporaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ShoporaApp(viewModel: ShoporaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showMarketSheet by remember { mutableStateOf(false) }
    var showRoleSwitcherDialog by remember { mutableStateOf(false) }

    // Android Hardware / Gesture Back Navigation Support
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateBack()
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearSnackbar()
            }
        }
    }

    Scaffold(
        topBar = {
            // TopBar shown on primary tabs; detail screens render their own specialized headers
            if (currentScreen in listOf(
                    AppScreen.HOME,
                    AppScreen.CATEGORIES,
                    AppScreen.WISHLIST,
                    AppScreen.CART,
                    AppScreen.ACCOUNT,
                    AppScreen.VENDOR_DASHBOARD,
                    AppScreen.ADMIN_CONTROL
                )
            ) {
                ShoporaTopBar(
                    viewModel = viewModel,
                    onOpenMarketSheet = { showMarketSheet = true },
                    onOpenRoleSwitcher = { showRoleSwitcherDialog = true }
                )
            }
        },
        bottomBar = {
            // Adaptive Bottom Navigation shown on standard browsing screens
            if (currentScreen in listOf(
                    AppScreen.HOME,
                    AppScreen.CATEGORIES,
                    AppScreen.WISHLIST,
                    AppScreen.CART,
                    AppScreen.ACCOUNT,
                    AppScreen.VENDOR_DASHBOARD,
                    AppScreen.ADMIN_CONTROL
                )
            ) {
                ShoporaBottomBar(viewModel = viewModel)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NavyDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.CATEGORIES -> CategoriesScreen(viewModel = viewModel)
                AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                AppScreen.WISHLIST -> WishlistScreen(viewModel = viewModel)
                AppScreen.CART -> CartScreen(viewModel = viewModel)
                AppScreen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                AppScreen.ORDER_SUCCESS -> OrderSuccessScreen(viewModel = viewModel)
                AppScreen.ORDER_HISTORY -> OrdersHistoryScreen(viewModel = viewModel)
                AppScreen.ORDER_DETAIL -> OrderDetailScreen(viewModel = viewModel)
                AppScreen.VENDOR_DASHBOARD -> VendorDashboardScreen(viewModel = viewModel)
                AppScreen.ADMIN_CONTROL -> AdminControlScreen(viewModel = viewModel)
                AppScreen.CHAT -> ChatScreen(viewModel = viewModel)
                AppScreen.ACCOUNT -> AccountScreen(
                    viewModel = viewModel,
                    onOpenMarketSheet = { showMarketSheet = true },
                    onOpenRoleSwitcher = { showRoleSwitcherDialog = true }
                )
                AppScreen.NOTIFICATIONS -> OrdersHistoryScreen(viewModel = viewModel)
            }
        }

        // Global Market Selector Bottom Sheet
        if (showMarketSheet) {
            MarketSelectorBottomSheet(
                viewModel = viewModel,
                onDismiss = { showMarketSheet = false }
            )
        }

        // Persona & Role Switcher Modal Dialog
        if (showRoleSwitcherDialog) {
            RoleSwitcherDialog(
                viewModel = viewModel,
                onDismiss = { showRoleSwitcherDialog = false }
            )
        }
    }
}
