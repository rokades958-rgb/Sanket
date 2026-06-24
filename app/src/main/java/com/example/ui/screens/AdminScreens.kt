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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.components.VeggieGraphic
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminScreens(viewModel: AppViewModel, innerPadding: PaddingValues) {
    val aState by viewModel.adminScreen.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(0xFFF5F9F6))
    ) {
        // Administrative top coordinator bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("SS Fresh Administration Portal", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("Baramati Headquarters HQ Console • Secured", color = Color.White.copy(0.8f), fontSize = 11.sp)
            }

            IconButton(onClick = { viewModel.showToast("Admin Session verified.") }) {
                Icon(Icons.Default.AdminPanelSettings, "Verified", tint = Color.White)
            }
        }

        // Horizontal Screen Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Dashboard", "Products", "Orders", "Coupons", "Partners").forEach { tab ->
                val active = aState == tab
                FilterChip(
                    selected = active,
                    onClick = { viewModel.adminScreen.value = tab },
                    label = { Text(tab, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("admin_tab_$tab")
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (aState) {
                "Dashboard" -> AdminMainDashboardTab(viewModel)
                "Products" -> AdminProductCatalogTab(viewModel)
                "Orders" -> AdminActiveOrdersManagerTab(viewModel)
                "Coupons" -> AdminPromoCouponsTab(viewModel)
                "Partners" -> AdminDeliveryFleetsTab(viewModel)
                else -> AdminMainDashboardTab(viewModel)
            }
        }
    }
}

// --- 1. Dashboard Tab ---
@Composable
fun AdminMainDashboardTab(viewModel: AppViewModel) {
    val ordersList by viewModel.orders.collectAsState()
    val profilesList by viewModel.profiles.collectAsState()
    val fleetList by viewModel.partners.collectAsState()

    val completedOrders = ordersList.filter { it.status == "Delivered" }
    val totalRevenue = completedOrders.sumOf { it.totalAmount }
    val pendingOrdersCount = ordersList.filter { it.status == "Pending" || it.status == "Confirmed" }.size
    val alerts by viewModel.adminNotifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Real-Time Notification board
        if (alerts.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = BorderStroke(1.5.dp, Color(0xFFFFE082)),
                modifier = Modifier.fillMaxWidth().testTag("realtime_admin_notification_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, "Alerts", tint = Color(0xFFE65100))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Real-Time Order Alerts (${alerts.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                        }
                        IconButton(onClick = { viewModel.adminNotifications.value = emptyList() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    alerts.take(5).forEach { alrt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(alrt, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
        // High level parameters grid
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Collections", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${String.format(Locale.US, "%.1f", totalRevenue)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("From ${completedOrders.size} deliveries", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Underway Tasks", fontSize = 11.sp, color = Color.Gray)
                    Text("$pendingOrdersCount pending", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9800))
                    Text("Total orders: ${ordersList.size}", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Registered Clients", fontSize = 11.sp, color = Color.Gray)
                    Text("${profilesList.size + 15} Shoppers", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Baramati jurisdiction", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Active Fleet Size", fontSize = 11.sp, color = Color.Gray)
                    Text("${fleetList.size} Active Riders", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("Online status active", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }

        // Custom Canvas Charts for Sales Analytics
        Text("Weekly Sales Trends (Baramati District HQ):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Revenue (Latest 7 days)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                // Beautiful interactive Canvas graph showing sales progression
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFFE8F5E9).copy(alpha = 0.5f))
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val levels = listOf(0.2f, 0.35f, 0.42f, 0.30f, 0.55f, 0.72f, 0.88f) // simulated heights

                    val w = size.width
                    val h = size.height
                    val spacing = w / (levels.size + 1)

                    var lastX = 0f
                    var lastY = 0f

                    levels.forEachIndexed { idx, pct ->
                        val cx = (idx + 1) * spacing
                        val cy = h - (pct * (h - 40.dp.toPx()))

                        // Draw bars
                        drawRect(
                            color = Color(0xFF2E7D32).copy(0.2f),
                            topLeft = androidx.compose.ui.geometry.Offset(cx - 15f, cy),
                            size = androidx.compose.ui.geometry.Size(30f, h - cy)
                        )

                        // Draw lines connection dots
                        if (idx > 0) {
                            drawLine(
                                color = Color(0xFF2E7D32),
                                start = androidx.compose.ui.geometry.Offset(lastX, lastY),
                                end = androidx.compose.ui.geometry.Offset(cx, cy),
                                strokeWidth = 5f
                            )
                        }

                        // Draw circles dots
                        drawCircle(
                            color = Color(0xFF2E7D32),
                            radius = 8f,
                            center = androidx.compose.ui.geometry.Offset(cx, cy)
                        )

                        lastX = cx
                        lastY = cy
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                        Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }
        }

        // Live Low Stock Warnings (Threshold <= 5 units)
        val productList by viewModel.products.collectAsState()
        val lowStockProducts = productList.filter { it.stockLevel <= 5 }

        Text("Active Alerts / Actions required:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        if (lowStockProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text("✓ Clean bill: All catalog items have safe stock levels.", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            lowStockProducts.forEach { p ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Low Stock Warning: ${p.nameEn}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(
                            text = if (p.stockLevel <= 0) "OUT OF STOCK" else "Only ${p.stockLevel} units remaining",
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- 2. Products Tab ---
@Composable
fun AdminProductCatalogTab(viewModel: AppViewModel) {
    val fullList by viewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedToEdit by remember { mutableStateOf<ProductEntity?>(null) }

    // Dialog state variables
    var nameEn by remember { mutableStateOf("") }
    var nameMr by remember { mutableStateOf("") }
    var nameHi by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Vegetables") }
    var weightOptions by remember { mutableStateOf("250g,500g,1kg") }
    var basePrice by remember { mutableStateOf("30.0") }
    var stockLevel by remember { mutableStateOf("100") }
    var imageUrl by remember { mutableStateOf("palak") }
    var isRecommended by remember { mutableStateOf(false) }
    var discountPercent by remember { mutableStateOf("0") }
    var descEn by remember { mutableStateOf("") }
    var descMr by remember { mutableStateOf("") }
    var descHi by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        selectedToEdit = null
                        nameEn = ""
                        nameMr = ""
                        nameHi = ""
                        category = "Vegetables"
                        weightOptions = "250g,500g,1kg"
                        basePrice = "40.0"
                        stockLevel = "80"
                        imageUrl = "palak"
                        isRecommended = false
                        discountPercent = "0"
                        descEn = "Loaded organic fresh stock."
                        descMr = "ताजा आणि सेंद्रिय."
                        descHi = "ताजा जैविक।"
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_add_product_fab"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Fresh Crate Product", fontWeight = FontWeight.Bold)
                }
            }

            // Product Listing
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fullList.forEach { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(0xFFE5ECE5)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VeggieGraphic(id = p.imageUrl, modifier = Modifier.size(50.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.nameEn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Cat: ${p.category} • Base Price: ₹${p.basePrice}", fontSize = 11.sp, color = Color.Gray)
                                Text("Stock: ${p.stockLevel} units remaining", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (p.stockLevel <= 5) Color.Red else Color.DarkGray)
                            }

                            // Quick edit lines
                            Row {
                                IconButton(
                                    onClick = {
                                        selectedToEdit = p
                                        nameEn = p.nameEn
                                        nameMr = p.nameMr
                                        nameHi = p.nameHi
                                        category = p.category
                                        weightOptions = p.weightOptions
                                        basePrice = p.basePrice.toString()
                                        stockLevel = p.stockLevel.toString()
                                        imageUrl = p.imageUrl
                                        isRecommended = p.isRecommended
                                        discountPercent = p.discountPercent.toString()
                                        descEn = p.descriptionEn
                                        descMr = p.descriptionMr
                                        descHi = p.descriptionHi
                                        showAddDialog = true
                                    },
                                    modifier = Modifier.testTag("edit_product_${p.id}")
                                ) {
                                    Icon(Icons.Default.Edit, "Edit", tint = Color.Blue)
                                }

                                IconButton(
                                    onClick = { viewModel.adminDeleteProduct(p.id) },
                                    modifier = Modifier.testTag("delete_product_${p.id}")
                                ) {
                                    Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add / Edit Dialog Screen
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.adminSaveProduct(
                                id = selectedToEdit?.id ?: 0,
                                nameEn = nameEn, nameMr = nameMr, nameHi = nameHi,
                                category = category,
                                weightOptions = weightOptions,
                                basePrice = basePrice.toDoubleOrNull() ?: 30.0,
                                stockLevel = stockLevel.toIntOrNull() ?: 50,
                                imageUrl = imageUrl,
                                isRec = isRecommended,
                                disc = discountPercent.toIntOrNull() ?: 0,
                                descEn = descEn, descMr = descMr, descHi = descHi
                            )
                            showAddDialog = false
                        },
                        modifier = Modifier.testTag("admin_p_save_btn")
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Close") }
                },
                title = { Text(if (selectedToEdit == null) "Launch New Product" else "Modify Product Details") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("Product Heading (EN)") }, modifier = Modifier.fillMaxWidth().testTag("add_p_name"))
                        OutlinedTextField(value = nameMr, onValueChange = { nameMr = it }, label = { Text("Product Heading (Marathi - मराठी)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = nameHi, onValueChange = { nameHi = it }, label = { Text("Product Heading (Hindi - हिंदी)") }, modifier = Modifier.fillMaxWidth())

                        // Category Dropdowns
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Category: ", style = MaterialTheme.typography.bodyMedium)
                            listOf("Vegetables", "Fruits", "Grocery").forEach { cat ->
                                val active = category == cat
                                FilterChip(
                                    selected = active,
                                    onClick = { category = cat },
                                    label = { Text(cat, fontSize = 10.sp) },
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }

                        OutlinedTextField(value = weightOptions, onValueChange = { weightOptions = it }, label = { Text("Options (comma separated, e.g. 250g,500g)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = basePrice, onValueChange = { basePrice = it }, label = { Text("Base Price (in ₹)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = stockLevel, onValueChange = { stockLevel = it }, label = { Text("Available Stock Qty") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = discountPercent, onValueChange = { discountPercent = it }, label = { Text("Active Discount %") }, modifier = Modifier.fillMaxWidth())

                        // Graphics code selector
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Graphics Identifier (e.g. palak, tomato, onion)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(value = descEn, onValueChange = { descEn = it }, label = { Text("Long Description Details") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        }
    }
}

// --- 3. Orders Tab ---
@Composable
fun AdminActiveOrdersManagerTab(viewModel: AppViewModel) {
    val ordersList by viewModel.orders.collectAsState()
    val partnerFleet by viewModel.partners.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ordersList.forEach { order ->
            val oItems = OrderConverters().toOrderItemList(order.itemsJson)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order #${order.id}", fontWeight = FontWeight.Bold)
                        Text(order.status, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }

                    Text("Contact: ${order.customerName} • ${order.customerContact}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    
                    // Detailed Address & Geo-coordinates
                    Text("Shipment Address: ${order.customerAddress}", fontSize = 11.sp, color = Color.Gray)
                    if (!order.areaVillage.isNullOrEmpty() || !order.pinCode.isNullOrEmpty()) {
                        Text("Area/Village: ${order.areaVillage ?: "Baramati MIDC"} • Pincode: ${order.pinCode ?: "413133"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    if (order.latitude != 0.0 && order.longitude != 0.0) {
                        Text("📍 GPS Coords Picker Locked: [${String.format(Locale.US, "%.5f", order.latitude)}, ${String.format(Locale.US, "%.5f", order.longitude)}]", fontSize = 10.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                    }

                    // Payment details
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = if (order.paymentStatus?.contains("Paid") == true) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, if (order.paymentStatus?.contains("Paid") == true) Color(0xFFC8E6C9) else Color(0xFFFFE082))
                    ) {
                        Text(
                            "💳 Payment: ${order.paymentMethod} • Status: ${order.paymentStatus ?: "Unpaid"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (order.paymentStatus?.contains("Paid") == true) Color(0xFF2E7D32) else Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (!order.upiTransactionId.isNullOrEmpty()) {
                        Text("UPI Ref transaction ID: ${order.upiTransactionId}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    }

                    // Uploaded Screenshot Visual Proof
                    if (!order.paymentProofBase64.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        var showReceiptModal by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .clickable { showReceiptModal = true }
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFE0F2F1), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF80CBC4), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ReceiptLong, null, tint = Color(0xFF00796B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Click to View Attached Payment Screenshot Receipts", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                            }
                        }

                        if (showReceiptModal) {
                            AlertDialog(
                                onDismissRequest = { showReceiptModal = false },
                                confirmButton = {
                                    TextButton(onClick = { showReceiptModal = false }) { Text("Dismiss Proof") }
                                },
                                title = { Text("Secured Transaction Receipt Screenshot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                                text = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.2.dp, Color(0xFF4CAF50)),
                                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("SECURE UPI TRANSACTION SLIP", fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color(0xFF2E7D32))
                                                    Text("PAID ✅", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF2E7D32))
                                                }
                                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                                Text("Recipient: SS Fresh Market Ltd", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("Customer name: ${order.customerName}", fontSize = 11.sp)
                                                Text("Recipient Reference ID: ${order.customerContact}", fontSize = 11.sp)
                                                Text("UPI Ref transaction ID: ${order.upiTransactionId ?: "19028374928"}", fontSize = 11.sp, color = Color.Gray)
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text("Transacted Amount: ₹${String.format(Locale.US, "%.2f", order.totalAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Timestamp: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(order.dateEpochMs))}", fontSize = 9.sp, color = Color.Gray)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("*This visual screenshot receipt displays the direct proof generated by GPay or PhonePe payment process.", fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                    }
                                }
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 6.dp))

                    oItems.forEach { itm ->
                        Text("• ${itm.productName} (${itm.chosenWeight}) x${itm.quantity}", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Update Shipment Contract Status Directly:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Pending", "Confirmed", "Out for Delivery", "Delivered", "Cancelled").forEach { statusLabel ->
                            val current = order.status == statusLabel
                            Button(
                                onClick = { viewModel.adminUpdateOrderStatus(order, statusLabel) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (current) MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.3f),
                                    contentColor = if (current) Color.White else Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("admin_status_${order.id}_$statusLabel")
                            ) {
                                Text(statusLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Logistics dispatch manual override
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Assign Logistics Captain / Driver:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        partnerFleet.forEach { rider ->
                            val isChosen = order.assignedPartnerId == rider.id
                            FilterChip(
                                selected = isChosen,
                                onClick = { viewModel.adminAssignPartner(order, rider.id) },
                                label = { Text(rider.name, fontSize = 10.sp) },
                                modifier = Modifier.testTag("assign_${order.id}_${rider.id}")
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 4. Coupons Tab ---
@Composable
fun AdminPromoCouponsTab(viewModel: AppViewModel) {
    val couponsList by viewModel.coupons.collectAsState()

    var code by remember { mutableStateOf("") }
    var discPercent by remember { mutableStateOf("20") }
    var minAmount by remember { mutableStateOf("150.0") }
    var couponDesc by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Generate Fresh Platform Promo Coupon", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Discount Promo Code (e.g. BARAMATI25)") }, modifier = Modifier.fillMaxWidth().testTag("admin_coupon_code"))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = discPercent, onValueChange = { discPercent = it }, label = { Text("Discount Percentage Off (%)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = minAmount, onValueChange = { minAmount = it }, label = { Text("Minimum Purchase Cap (in ₹)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = couponDesc, onValueChange = { couponDesc = it }, label = { Text("Description Details (multilingual support notes)") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.adminCreateCoupon(code, discPercent.toIntOrNull() ?: 15, minAmount.toDoubleOrNull() ?: 100.0, couponDesc.ifEmpty { "Get massive savings on ordering." })
                        code = ""
                        couponDesc = ""
                    },
                    modifier = Modifier.fillMaxWidth().testTag("admin_save_coupon_btn")
                ) {
                    Text("Validate and Activate Coupon Live")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Active Coupons running on Portal:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        couponsList.forEach { c ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(c.code, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                        Text(c.descriptionEn, fontSize = 11.sp, color = Color.Gray)
                        Text("Min Order Requirement: ₹${c.minOrderAmount} • Value: ${c.discountPercent}% Off", fontSize = 12.sp)
                    }

                    IconButton(onClick = { viewModel.adminDeleteCoupon(c.code) }, modifier = Modifier.testTag("delete_coupon_${c.code}")) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                    }
                }
            }
        }
    }
}

// --- 5. Partners Tab ---
@Composable
fun AdminDeliveryFleetsTab(viewModel: AppViewModel) {
    val partners by viewModel.partners.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Register/Onboard Delivery Captain", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Legal Name") }, modifier = Modifier.fillMaxWidth().testTag("admin_rider_name"))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Registered Contact Number") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (name.isNotEmpty() && phone.isNotEmpty()) {
                            viewModel.adminOnboardRider(name, phone)
                            name = ""
                            phone = ""
                        } else {
                            viewModel.showToast("Enter complete rider parameters")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("admin_save_rider_btn")
                ) {
                    Text("Onboard & Sync Logistics Device")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Logistics Fleet Roster:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        partners.forEach { r ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary.copy(0.12f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(r.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Mobile: ${r.phone} • Deliveries: ${r.lifetimeDeliveries}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(if (r.isOnline) Color(0xFFC8E6C9) else Color(0xFFFFCDD2), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(if (r.isOnline) "ACTIVE" else "OFFLINE", color = if (r.isOnline) Color(0xFF1B5E20) else Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
