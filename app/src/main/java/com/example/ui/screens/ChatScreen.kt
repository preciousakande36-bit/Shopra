package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChatMessageEntity
import com.example.ui.ShoporaViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(viewModel: ShoporaViewModel) {
    val activeVendorId by viewModel.activeChatVendorId.collectAsState()
    val allVendors by viewModel.allVendors.collectAsState()
    val vendor = allVendors.find { it.id == activeVendorId }

    val messagesFlow = remember(activeVendorId) { viewModel.getActiveChatMessages() }
    val messages by messagesFlow.collectAsState(initial = emptyList())

    var inputMessage by remember { mutableStateOf("") }

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

                    Icon(
                        if (vendor?.isOfficial == true) Icons.Default.Verified else Icons.Default.Storefront,
                        contentDescription = null,
                        tint = if (vendor?.isOfficial == true) AccentGold else ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = vendor?.businessName ?: "Marketplace Seller",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = if (vendor?.isOfficial == true) "Official Direct Support • Instant Response" else "Verified Global Seller • Active Now",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 10.sp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Quick inquiry chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        item {
                            SuggestionChip(
                                onClick = { inputMessage = "Is this item currently in stock for express dispatch?" },
                                label = { Text("Is this in stock?", fontSize = 11.sp) }
                            )
                        }
                        item {
                            SuggestionChip(
                                onClick = { inputMessage = "How many days for delivery to my country?" },
                                label = { Text("Estimated delivery days?", fontSize = 11.sp) }
                            )
                        }
                        item {
                            SuggestionChip(
                                onClick = { inputMessage = "Does this product come with international warranty?" },
                                label = { Text("Warranty terms?", fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = { Text("Type message to seller...", fontSize = 13.sp) },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    viewModel.sendChatMessage(activeVendorId, inputMessage)
                                    inputMessage = ""
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = ElectricBlue,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate100)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isCustomer = msg.senderRole == "CUSTOMER"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isCustomer) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isCustomer) 16.dp else 4.dp,
                            bottomEnd = if (isCustomer) 4.dp else 16.dp
                        ),
                        color = if (isCustomer) ElectricBlue else Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (msg.productRefTitle != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCustomer) NavyDark.copy(alpha = 0.2f) else Slate100,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = "Regarding: ${msg.productRefTitle}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCustomer) Color.White.copy(alpha = 0.9f) else Slate600
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = msg.message,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isCustomer) Color.White else Slate900,
                                    lineHeight = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp)),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = if (isCustomer) Color.White.copy(alpha = 0.7f) else Slate400
                                ),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
