package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppViewModel
import com.example.ui.screens.AdminScreens
import com.example.ui.screens.CustomerScreens
import com.example.ui.screens.DriverScreens
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val viewModel: AppViewModel = viewModel()
    val activeRole by viewModel.currentRole.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val customerScreen by viewModel.customerScreen.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Live Toast updates trigger
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        // Persistent Modern Dynamic Floating Interface Selector BottomBar
        bottomBar = {
            if (activeRole != "Customer" || customerScreen != "Splash") {
                RoleSelectionBottomBar(
                    activeRole = activeRole,
                    onRoleSelected = { role -> viewModel.switchRole(role) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Edge-to-Edge System bars safe paddings integration
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Crossfade(targetState = activeRole, label = "RoleTransition") { role ->
                    when (role) {
                        "Customer" -> {
                            CustomerScaffoldLayout(viewModel, innerPadding)
                        }
                        "Partner" -> {
                            DriverScreens(viewModel, innerPadding)
                        }
                        "Admin" -> {
                            AdminScreens(viewModel, innerPadding)
                        }
                        else -> {
                            CustomerScaffoldLayout(viewModel, innerPadding)
                        }
                    }
                }
            }
        }
    }
}

// Scaffold specifically wrapping Customer views to provide smooth sub-navigations
@Composable
fun CustomerScaffoldLayout(
    viewModel: AppViewModel,
    globalPadding: PaddingValues
) {
    val activeSubScreen by viewModel.customerScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val basketItemsCount = cartItems.sumOf { it.quantity }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (activeSubScreen != "Login" && activeSubScreen != "Splash") {
                NavigationBar(
                    modifier = Modifier
                        .height(72.dp)
                        .testTag("customer_bottom_nav"),
                    tonalElevation = 8.dp
                ) {
                    // Home Tab
                    NavigationBarItem(
                        selected = activeSubScreen == "Home" || activeSubScreen == "Details",
                        onClick = { viewModel.customerScreen.value = "Home" },
                        icon = { Icon(Icons.Default.Storefront, "Shop") },
                        label = { Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_shop_tab")
                    )

                    // Cart Basket Tab with count badges
                    NavigationBarItem(
                        selected = activeSubScreen == "Cart",
                        onClick = { viewModel.customerScreen.value = "Cart" },
                        icon = {
                            Box {
                                Icon(Icons.Default.ShoppingBasket, "Basket")
                                if (basketItemsCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 8.dp, y = (-4).dp)
                                            .background(Color.Red, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("$basketItemsCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
                        label = { Text("Basket", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_basket_tab")
                    )

                    // Tracking / Progress active tab
                    NavigationBarItem(
                        selected = activeSubScreen == "Tracking",
                        onClick = { viewModel.customerScreen.value = "Tracking" },
                        icon = { Icon(Icons.Default.Explore, "Track") },
                        label = { Text("Track", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_track_tab")
                    )

                    // History orders archives tab
                    NavigationBarItem(
                        selected = activeSubScreen == "History",
                        onClick = { viewModel.customerScreen.value = "History" },
                        icon = { Icon(Icons.Default.ReceiptLong, "Archive") },
                        label = { Text("Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_logs_tab")
                    )

                    // Supports center desk
                    NavigationBarItem(
                        selected = activeSubScreen == "Support",
                        onClick = { viewModel.customerScreen.value = "Support" },
                        icon = { Icon(Icons.Default.SupportAgent, "Support") },
                        label = { Text("Help", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_help_tab")
                    )
                }
            }
        }
    ) { customerPadding ->
        // Custom padding calculation to avoid overlapping
        val combinedPadding = PaddingValues(
            top = globalPadding.calculateTopPadding(),
            bottom = customerPadding.calculateBottomPadding() + globalPadding.calculateBottomPadding()
        )
        CustomerScreens(viewModel = viewModel, innerPadding = combinedPadding)
    }
}

@Composable
fun RoleSelectionBottomBar(
    activeRole: String,
    onRoleSelected: (String) -> Unit
) {
    // Elegant system persona selector at the bottom to transition between screens
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color(0xFF1B221C), // Deep Forest background for control bar
        tonalElevation = 4.dp
    ) {
        Column {
            Divider(color = Color.White.copy(0.12f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "INTERFACE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.LightGray.copy(0.7f),
                    letterSpacing = 1.sp
                )

                // Customer Persona
                RoleSelectionBadge(
                    label = "Customer Portal",
                    isActive = activeRole == "Customer",
                    icon = Icons.Default.Person,
                    onClick = { onRoleSelected("Customer") },
                    modifier = Modifier.testTag("role_customer_tab")
                )

                // Partner Rider Persona
                RoleSelectionBadge(
                    label = "Rider Device",
                    isActive = activeRole == "Partner",
                    icon = Icons.Default.LocalShipping,
                    onClick = { onRoleSelected("Partner") },
                    modifier = Modifier.testTag("role_partner_tab")
                )

                // Administrative Console Persona
                RoleSelectionBadge(
                    label = "HQ Admin console",
                    isActive = activeRole == "Admin",
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = { onRoleSelected("Admin") },
                    modifier = Modifier.testTag("role_admin_tab")
                )
            }
        }
    }
}

@Composable
fun RoleSelectionBadge(
    label: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Color(0xFF2E7D32) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isActive) Color.Transparent else Color.LightGray.copy(0.4f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color.LightGray,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = if (isActive) Color.White else Color.LightGray
            )
        }
    }
}
