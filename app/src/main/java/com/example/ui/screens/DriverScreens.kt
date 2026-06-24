package com.example.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.data.OrderItem
import com.example.data.OrderConverters
import com.example.ui.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriverScreens(viewModel: AppViewModel, innerPadding: PaddingValues) {
    val currentDriverUser by viewModel.currentUser.collectAsState()
    val rState by viewModel.partnerScreen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (currentDriverUser == null || currentDriverUser?.role != "Partner") {
            DriverLoginScreen(viewModel)
        } else {
            when (rState) {
                "Home" -> DriverHomeScreen(viewModel)
                "Earnings" -> DriverReportsScreen(viewModel)
                else -> DriverHomeScreen(viewModel)
            }
        }
    }
}

// --- 1. Driver Login Screen ---
@Composable
fun DriverLoginScreen(viewModel: AppViewModel) {
    var phone by remember { mutableStateOf("7777777777") } // Amit Patel pre-populated!
    var name by remember { mutableStateOf("Amit Patel") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsBike,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
            )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SS Fresh - Partner Hub",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Baramati MIDC Local Delivery Logistics",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Secure Partner Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Driver Legal Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Logistics Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.loginAsPartner(phone, name)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("driver_login_submit"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Secure Portal Authorize", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 2. Driver Main Dashboard ---
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DriverHomeScreen(viewModel: AppViewModel) {
    val activePartnerUser by viewModel.currentUser.collectAsState()
    val isOnline by viewModel.partnerOnline.collectAsState()
    val ordersList by viewModel.orders.collectAsState()

    // Filter orders which are assigned/processing
    val activeDeliveries = ordersList.filter {
        it.status != "Delivered" && it.status != "Cancelled"
    }

    var selectedOrderToComplete by remember { mutableStateOf<OrderEntity?>(null) }
    var inputOtp by remember { mutableStateOf("") }
    var deliveryProofMockUrl by remember { mutableStateOf("Mock Signature Received") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Driver Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = activePartnerUser?.fullName ?: "Active Fleet Rider",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text("Baramati MIDC Station • Online", color = Color.White.copy(0.8f), fontSize = 11.sp)
            }

            // Online/Offline sliding toggles
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isOnline) "ONLINE" else "OFFLINE",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = isOnline,
                    onCheckedChange = { viewModel.togglePartnerAvailability() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.LightGray
                    ),
                    modifier = Modifier.testTag("driver_online_switch")
                )
            }
        }

        // Sub Navigation
        TabRow(selectedTabIndex = 0) {
            Tab(selected = true, onClick = { viewModel.partnerScreen.value = "Home" }) {
                Text("Active Logistics", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = false, onClick = { viewModel.partnerScreen.value = "Earnings" }) {
                Text("Earnings Report", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
        }

        if (!isOnline) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.CloudOff, "Offline", modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("You are offline.", fontWeight = FontWeight.Bold)
                    Text("Toggle switch to online state to start accepting farm-fresh deliveries.", textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
                }
            }
            return
        }

        // Active Deliveries List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Assigned Fresh Crates (${activeDeliveries.size}):", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            if (activeDeliveries.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalShipping, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("All quiet! No fresh deliveries assigned currently.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                activeDeliveries.forEach { order ->
                    val orderItems = OrderConverters().toOrderItemList(order.itemsJson)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Delivery Cargo #${order.id}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(order.status, color = Color(0xFF0D47A1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Customer: ${order.customerName} • 📞 ${order.customerContact}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Address: ${order.customerAddress}", fontSize = 12.sp, color = Color.DarkGray)
                            if (order.pinCode.isNotEmpty() || order.areaVillage.isNotEmpty()) {
                                Text("Pincode: ${order.pinCode} • Area: ${order.areaVillage}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text("Coordinates: Lat ${String.format(Locale.US, "%.5f", order.latitude)} • Lng ${String.format(Locale.US, "%.5f", order.longitude)}", fontSize = 10.sp, color = Color(0xFF1976D2))
                            Text("Slot: ${order.deliverySlot} • payment: ${order.paymentMethod} (${order.paymentStatus})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (order.paymentStatus.contains("Paid")) Color(0xFF2E7D32) else Color(0xFFE65100))

                            Spacer(modifier = Modifier.height(6.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Vegetables Package:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            orderItems.forEach { item ->
                                Text("• ${item.productName} (${item.chosenWeight}) x${item.quantity}", fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Your Pay: ₹50.00",
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 14.sp
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Simulated Navigation trigger
                                    IconButton(
                                        onClick = { viewModel.showToast("Optimizing routing coords to ${order.customerAddress}... Sourced via Maps") },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(0.12f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Navigation, "Navigate", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    // Customer calling trigger
                                    IconButton(
                                        onClick = { viewModel.showToast("Initiating dialer calling to Customer: ${order.customerContact}") },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(0.12f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Phone, "Call", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive status transitions
                            if (order.status == "Pending") {
                                Button(
                                    onClick = { viewModel.acceptDeliveryOrder(order) },
                                    modifier = Modifier.fillMaxWidth().testTag("accept_order_${order.id}")
                                ) {
                                    Text("Accept Delivery Request")
                                }
                            } else if (order.status == "Confirmed") {
                                Button(
                                    onClick = { viewModel.markOrderOutForDelivery(order) },
                                    modifier = Modifier.fillMaxWidth().testTag("dispatch_order_${order.id}")
                                ) {
                                    Text("Depart from Farmer Station (Dispatch)")
                                }
                            } else if (order.status == "Out for Delivery") {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(0.08f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Security Clearance & OTP Closing:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))

                                        OutlinedTextField(
                                            value = inputOtp,
                                            onValueChange = { inputOtp = it },
                                            label = { Text("Enter Customer 4-Digit OTP") },
                                            modifier = Modifier.fillMaxWidth().testTag("driver_otp_input"),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Photo Upload simulator
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (deliveryProofMockUrl.isNotEmpty()) "✓ Photo Proof Secured" else "No Photo Uploaded",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (deliveryProofMockUrl.isNotEmpty()) Color(0xFF2E7D32) else Color.Red
                                            )
                                            TextButton(onClick = {
                                                deliveryProofMockUrl = "Signature Verified/Photo Taken - Epoch ${System.currentTimeMillis()}"
                                                viewModel.showToast("Captured delivery box package at client doorstep!")
                                            }) {
                                                Icon(Icons.Default.PhotoCamera, null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Snaps Doorstep Photo")
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = {
                                                if (inputOtp.length == 4) {
                                                    viewModel.verifyDeliveryOtpAndComplete(order, inputOtp, deliveryProofMockUrl)
                                                    inputOtp = ""
                                                } else {
                                                    viewModel.showToast("Enter a valid 4-digit OTP code")
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("verify_otp_btn_${order.id}"),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Verify OTP & Close Delivery Contract", fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 3. Driver Reports Dashboard ---
@Composable
fun DriverReportsScreen(viewModel: AppViewModel) {
    val ordersList by viewModel.orders.collectAsState()
    val driverProfileState by viewModel.currentUser.collectAsState()

    val completedDeliveries = ordersList.filter {
        it.status == "Delivered"
    }

    val totalEarnings = completedDeliveries.size * 50.0

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${driverProfileState?.fullName}'s Fleet Earnings",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text("Baramati Stations Analytics", color = Color.White.copy(0.8f), fontSize = 11.sp)
            }

            IconButton(onClick = { viewModel.partnerScreen.value = "Home" }) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Stats summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(modifier = Modifier.weight(1.3f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Gross Balance", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            "₹${String.format(Locale.US, "%.2f", totalEarnings)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("₹50.00 flat per drop bonus", fontSize = 10.sp, color = Color.LightGray)
                    }
                }

                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Deliveries Done", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            "${completedDeliveries.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("Success Rate: 100%", fontSize = 10.sp, color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("Historical Completed Orders:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (completedDeliveries.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No deliveries closed today yet.", color = Color.Gray)
                }
            } else {
                completedDeliveries.forEach { order ->
                    val formattedDate = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.dateEpochMs))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Order Verified ID #${order.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Date: $formattedDate", fontSize = 11.sp, color = Color.Gray)
                                Text("Radius: ${order.customerAddress}", fontSize = 11.sp, color = Color.DarkGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("+₹50.00", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32), fontSize = 14.sp)
                                Text("CREDITED", fontSize = 9.sp, color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
