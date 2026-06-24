package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import com.example.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItemEntity
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.data.AddressEntity
import com.example.data.OrderConverters
import com.example.data.OrderItem
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.random.Random
import androidx.compose.material.icons.automirrored.filled.*
import com.example.ui.AppViewModel
import com.example.ui.components.VeggieGraphic
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerScreens(
    viewModel: AppViewModel,
    innerPadding: PaddingValues
) {
    val screen by viewModel.customerScreen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(if (screen == "Splash") Modifier else Modifier.padding(innerPadding))
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (screen) {
            "Splash" -> PremiumSplashScreen(viewModel)
            "Login" -> CustomerLoginScreen(viewModel)
            "Home" -> CustomerHomeScreen(viewModel)
            "Details" -> ProductDetailsScreen(viewModel)
            "Cart" -> CartAndCheckoutScreen(viewModel)
            "Tracking" -> LiveTrackingScreen(viewModel)
            "History" -> OrderHistoryScreen(viewModel)
            "Support" -> CustomerSupportScreen(viewModel)
            else -> CustomerHomeScreen(viewModel)
        }
    }
}

@Composable
fun PremiumSplashScreen(viewModel: AppViewModel) {
    var animPhase by remember { mutableStateOf(1) } // Phase 1: Video Intro (0-1.5s), Phase 2: Logo Resolve (1.5-3.0s)
    var animationTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationTriggered = true
        // Step 1: Cinematic video intro begins
        delay(1500)
        animPhase = 2
        // Step 2: Logo and welcome message appear
        delay(1500)
        // Step 3: Autocomplete check session and route
        if (viewModel.currentUser.value != null) {
            viewModel.customerScreen.value = "Home"
        } else {
            viewModel.customerScreen.value = "Login"
        }
    }

    // Compose custom animations
    val videoScale by animateFloatAsState(
        targetValue = if (animPhase == 1) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "VideoScale"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (animPhase == 2) 1.0f else 0.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "LogoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (animPhase == 2) 1.0f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "LogoAlpha"
    )

    val welcomeAlpha by animateFloatAsState(
        targetValue = if (animPhase == 2) 1.0f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "WelcomeAlpha"
    )

    // Cinematic rotating radial ray animation representing the intro projection lens
    val infiniteTransition = rememberInfiniteTransition(label = "CinematicUIRays")
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RayRotation"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("premium_splash_screen")
            .background(Color(0xFF0F140F)) // Dark cinematic base canvas
    ) {
        // Render Intro Video Presentation (Phase 1)
        if (animPhase == 1) {
            // Elegant background image with high opacity zoom & crop
            Image(
                painter = painterResource(id = R.drawable.img_splash_bg_1782153010501),
                contentDescription = "Fresh vegetables landscape background",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = videoScale,
                        scaleY = videoScale,
                        alpha = 0.4f
                    ),
                contentScale = ContentScale.Crop
            )

            // Dynamic cosmic particle & ray glow layer to depict "High-End Video Motion Graphic"
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF66BB6A).copy(alpha = 0.25f * glowPulse),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Animated intro title cards or dynamic video play indicators
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Spinning cosmic eco circular loader that looks like an elegant video intro watermark
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer(rotationZ = rayRotation),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF2E7D32),
                                    Color(0xFF81C784).copy(alpha = 0.3f),
                                    Color(0xFF2E7D32)
                                )
                            ),
                            style = Stroke(width = 6.dp.toPx())
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "SS PRODUCTIONS PRESENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "THE CHRONICLES OF FRESHNESS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF81C784), CircleShape))
                    Text(
                        text = "PREMIUM 4K MOTION INTRO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81C784),
                        letterSpacing = 1.sp
                    )
                }
            }
        } else {
            // Render Logo and Welcome Message (Phase 2: 1.5s - 3.0s)
            Image(
                painter = painterResource(id = R.drawable.img_splash_bg_1782153010501),
                contentDescription = "Fresh organic vegetables crop",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Backdrop glassmorphism soft glow overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.88f),
                                Color.White.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .statusBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Premium SS Fresh Basket Logo (Dynamic Vector + Glowing Card)
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer(
                            scaleX = logoScale,
                            scaleY = logoScale,
                            alpha = logoAlpha
                        ),
                    shape = CircleShape,
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    border = BorderStroke(4.dp, Color(0xFF2E7D32)) // Premium green color
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.85f)
                                .background(Color(0xFFE8F5E9), CircleShape)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBasket,
                                contentDescription = "Basket Logo Icon",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SS",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color(0xFF1B5E20),
                                letterSpacing = 1.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Organic indicator leaf",
                            tint = Color(0xFF81C784),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-16).dp, y = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Welcome message and basket brand title
                Text(
                    text = "SS Fresh Basket",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(alpha = logoAlpha)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fresh From Farm To Home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .graphicsLayer(alpha = welcomeAlpha)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Superfast Delivery Delivered to Your Doorstep",
                    fontSize = 12.sp,
                    color = Color(0xFF556052),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .graphicsLayer(alpha = welcomeAlpha)
                )

                Spacer(modifier = Modifier.weight(1.2f))

                // Loading spinner
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer(alpha = welcomeAlpha)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Bolt, "delivery speed info logo", tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                        Text(
                            text = "Superfast 10-Minute Delivery Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// --- 1. Login Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLoginScreen(viewModel: AppViewModel) {
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var otpEntered by remember { mutableStateOf("") }

    val lang by viewModel.selectedLanguage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Brand Identity
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF2E7D32), Color(0xFF81C784)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingBasket,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SS Fresh Basket",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (lang == "mr") "ताजे भाजीपाला आणि घरपोच किराणा" else if (lang == "hi") "ताजा सब्जियां और किराना सीधे घर" else "Farm-Fresh Vegetables & Groceries Delivered",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Language quick toggles
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            listOf("en" to "English", "mr" to "मराठी", "hi" to "हिंदी").forEach { (code, label) ->
                val active = lang == code
                FilterChip(
                    selected = active,
                    onClick = { viewModel.changeLanguage(code) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (showOtpField) "Enter Verification Code" else "Mobile Number Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (!showOtpField) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name (optional)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("login_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it.filter { c -> c.isDigit() } },
                        label = { Text("10-Digit Mobile Number") },
                        leadingIcon = { Text(" +91 ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_phone_input"),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = "We sent an OTP to +91 $phone (Use any 4-digit code to log in)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = otpEntered,
                        onValueChange = { if (it.length <= 4) otpEntered = it.filter { c -> c.isDigit() } },
                        label = { Text("4-Digit OTP Code") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_otp_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!showOtpField) {
                            if (phone.length == 10) {
                                showOtpField = true
                                viewModel.showToast("OTP sent successfully to $phone")
                            } else {
                                viewModel.showToast("Please enter valid 10-digit number")
                            }
                        } else {
                            if (otpEntered.length == 4) {
                                viewModel.loginWithPhone(phone, name)
                            } else {
                                viewModel.showToast("Please enter 4-digit verification code")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (showOtpField) "Verify & Finish" else "Request mobile OTP", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Google login mock
        OutlinedButton(
            onClick = {
                viewModel.loginWithPhone("9876543299", "Google Shopper")
                viewModel.showToast("Google Authentication successful!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("google_login_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Google account", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { viewModel.loginAsGuest() },
            modifier = Modifier.testTag("guest_login_link")
        ) {
            Text("Browse store as Guest Shopper (No login required)", color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline, fontSize = 13.sp)
        }
    }
}

// --- 2. Customer Home Screen ---
@Composable
fun CustomerHomeScreen(viewModel: AppViewModel) {
    val searchVal by viewModel.searchInput.collectAsState()
    val activeCategory by viewModel.selectedCategory.collectAsState()
    val fullList by viewModel.products.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val lang by viewModel.selectedLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val filteredList = fullList.filter {
        val matchesCategory = activeCategory == "All" || it.category.equals(activeCategory, ignoreCase = true)
        val matchesSearch = it.localizedName(lang).contains(searchVal, ignoreCase = true) ||
                it.category.contains(searchVal, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Gorgeous top banner with search
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                // PROFILE SECTION
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("home_profile_section"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (currentUser?.fullName?.isNotEmpty() == true) currentUser?.fullName?.take(1)?.uppercase() ?: "B" else "G",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Welcome to SS Fresh Basket!",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (currentUser?.fullName?.isNotEmpty() == true) "Hi, ${currentUser!!.fullName} 👋" else "Hi, Guest Resident 👋",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // 10 Min delivery badge & Logout Action Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFFFFD54F), // Bright warning amber
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("ten_min_delivery_header_badge")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Bolt, null, tint = Color(0xFFE65100), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("10 MIN DELIVERY", fontWeight = FontWeight.Black, fontSize = 9.sp, color = Color(0xFFE65100))
                            }
                        }

                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .testTag("home_logout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Baramati Limit", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Baramati MIDC Deliveries", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("Fresh Basket HQ, Sector 3 • Within 15 KM", color = Color.White.copy(alpha = 0.82f), fontSize = 11.sp)
                    }

                    // Basket count bubble
                    IconButton(
                        onClick = { viewModel.customerScreen.value = "Cart" },
                        modifier = Modifier.testTag("basket_icon_home")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ShoppingBasket, "Basket", tint = Color.White, modifier = Modifier.size(28.dp))
                            if (cart.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cart.sumOf { it.quantity }.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Box
                OutlinedTextField(
                    value = searchVal,
                    onValueChange = { viewModel.searchInput.value = it },
                    placeholder = { Text(viewModel.label("search_placeholder"), color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.White.copy(alpha = 0.8f)) },
                    trailingIcon = {
                        if (searchVal.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchInput.value = "" }) {
                                Icon(Icons.Default.Clear, "Clear", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("home_search_bar"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // Main Lists
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            // Hot Offers Promotion Slider
            PromotionalBannersSection(lang)

            if (searchVal.isEmpty() && activeCategory == "All") {
                // Popular Products Section
                val popularList = fullList.filter { it.rating >= 4.4 }
                if (popularList.isNotEmpty()) {
                    PopularProductsCarouselSection(popularList, viewModel)
                }

                // Seasonal Vegetables Section
                val seasonalList = fullList.filter { it.category.equals("Vegetables", ignoreCase = true) }
                if (seasonalList.isNotEmpty()) {
                    SeasonalVegetablesSection(seasonalList, viewModel)
                }
            }

            // Shop by Category section
            Text(
                text = viewModel.label("categories"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val categories = listOf(
                    "All" to Icons.Default.AllInclusive,
                    "Vegetables" to Icons.Default.EnergySavingsLeaf,
                    "Fruits" to Icons.Default.Eco,
                    "Grocery" to Icons.Default.Inventory
                )
                categories.forEach { (cat, icon) ->
                    val active = activeCategory == cat
                    Card(
                        onClick = { viewModel.selectedCategory.value = cat },
                        modifier = Modifier
                            .width(100.dp)
                            .testTag("category_chip_$cat"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            contentColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (active) 2.dp else 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (cat == "All") viewModel.label("all") else if (cat == "Vegetables") viewModel.label("vegetables") else if (cat == "Fruits") viewModel.label("fruits") else viewModel.label("grocery"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Products Heading
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeCategory == "All") viewModel.label("recommended") else viewModel.label("categories") + ": " + activeCategory,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${filteredList.size} ITEMS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                )
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HourglassEmpty, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No fresh products match your search/filters.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            } else {
                // High Quality Product Grid/List
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredList.forEach { product ->
                        ProductItemCard(product, viewModel)
                    }
                }
            }
        }
    }
}

// Banner Promo Section
@Composable
fun PromotionalBannersSection(lang: String) {
    val bData = listOf(
        Triple("Baramati Fresh Harvest", "Directly sourced from farming areas • Delivered in hours", Color(0xFFE8F5E9)),
        Triple("30% Off on Green Leaves", "No coupon code required! Flat discounts applied", Color(0xFFFFF3E0)),
        Triple("Guaranteed Superfast delivery", "Baramati MIDC and surrounding premium locations", Color(0xFFE3F2FD))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        bData.forEach { (title, subtitle, bg) ->
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("PROMO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B2E1D))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(subtitle, fontSize = 11.sp, color = Color(0xFF3E5C41), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalActivity, null, tint = MaterialTheme.colorScheme.primary.copy(0.35f), modifier = Modifier.size(54.dp))
                    }
                }
            }
        }
    }
}

// Product row item presentation card
@Composable
fun ProductItemCard(product: ProductEntity, viewModel: AppViewModel) {
    val lang by viewModel.selectedLanguage.collectAsState()
    val options = product.weightOptions.split(",")
    var selectedOption by remember(product.id) { mutableStateOf(options.firstOrNull() ?: "500g") }

    val actualPrice = viewModel.calculateOptionPrice(product.basePrice, selectedOption)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE5ECE5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Gorgeous Canvas Vector Graphics
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8F1))
                    .clickable {
                        viewModel.selectedProduct.value = product
                        viewModel.customerScreen.value = "Details"
                    },
                contentAlignment = Alignment.Center
            ) {
                VeggieGraphic(id = product.imageUrl, modifier = Modifier.size(60.dp))

                // Recommended / Offer Badge
                if (product.discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color.Red, RoundedCornerShape(bottomEnd = 8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("${product.discountPercent}% OFF", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Middle & Right text details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(10.dp))
                    Text("${product.rating}", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFFECE0),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Bolt, null, tint = Color(0xFFE65100), modifier = Modifier.size(10.dp))
                            Text("10m Delivery", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                        }
                    }
                }

                Text(
                    text = product.localizedName(lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .clickable {
                            viewModel.selectedProduct.value = product
                            viewModel.customerScreen.value = "Details"
                        }
                )

                Text(
                    text = product.localizedDescription(lang),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Interactive Weight choices Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    options.forEach { opt ->
                        val active = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, if (active) MaterialTheme.colorScheme.primary else Color(0xFFC8DEC8), RoundedCornerShape(6.dp))
                                .background(if (active) MaterialTheme.colorScheme.primary.copy(0.12f) else Color.Transparent)
                                .clickable { selectedOption = opt }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(opt, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price and Add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (product.discountPercent > 0) {
                            val original = actualPrice / (1.0 - (product.discountPercent / 100.0))
                            Text(
                                "₹${String.format(Locale.US, "%.0f", original)}",
                                fontSize = 10.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.Gray
                            )
                        }
                        Text(
                            "₹${String.format(Locale.US, "%.2f", actualPrice)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (product.stockLevel <= 0) {
                        Text("Out of Stock", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else if (product.stockLevel <= 5) {
                        Column(horizontalAlignment = Alignment.End) {
                            Button(
                                onClick = { viewModel.addProductToCart(product, selectedOption) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("add_to_cart_btn_${product.id}"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(viewModel.label("add_to_cart"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Only ${product.stockLevel} Left", color = Color(0xFFFF9800), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.addProductToCart(product, selectedOption) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("add_to_cart_btn_${product.id}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(viewModel.label("add_to_cart"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- 3. Product Details Screen ---
@Composable
fun ProductDetailsScreen(viewModel: AppViewModel) {
    val p by viewModel.selectedProduct.collectAsState()
    val lang by viewModel.selectedLanguage.collectAsState()

    if (p == null) {
        viewModel.customerScreen.value = "Home"
        return
    }
    val product = p!!
    val options = product.weightOptions.split(",")
    var selectedOption by remember { mutableStateOf(options.firstOrNull() ?: "500g") }
    val calculatedPrice = viewModel.calculateOptionPrice(product.basePrice, selectedOption)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image Cover Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Brush.radialGradient(listOf(Color(0xFFE2F0D9), Color(0xFFC0DFB1)))),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { viewModel.customerScreen.value = "Home" },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.White.copy(0.85f), CircleShape)
                    .testTag("detail_back_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }

            VeggieGraphic(id = product.imageUrl, modifier = Modifier.size(160.dp))

            if (product.discountPercent > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(Color.Red, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("FLASH SALE: ${product.discountPercent}% OFF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Category & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.category.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                    Text(
                        "${product.rating} (${product.reviewsCount} organic reviews)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.localizedName(lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = "Product Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.localizedDescription(lang) + "\nThis premium batch has been double-graded, washed in clean mineral water, and sourced from organic growers near Baramati MIDC. Packed responsibly, keeping safety benchmarks paramount to preserve natural sweetness and nutrients.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Delivery Assurance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.06f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Celebration, "Fresh", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("100% Guaranteed Freshness", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Sourced at 4 AM directly from rural growers. Ready to serve instantly.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weights chooser
            Text(
                text = "Choose weight option / quantity:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    val active = selectedOption == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(0.5f))
                            .border(1.dp, if (active) MaterialTheme.colorScheme.primary else Color(0xFFC8DEC8), RoundedCornerShape(12.dp))
                            .clickable { selectedOption = option }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                option,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "₹${String.format(Locale.US, "%.0f", viewModel.calculateOptionPrice(product.basePrice, option))}",
                                fontSize = 11.sp,
                                color = if (active) MaterialTheme.colorScheme.onPrimary.copy(0.8f) else MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Add buttons line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Price", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Text(
                        "₹${String.format(Locale.US, "%.2f", calculatedPrice)}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = {
                        viewModel.addProductToCart(product, selectedOption)
                        viewModel.customerScreen.value = "Home"
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f)
                        .padding(start = 24.dp)
                        .testTag("detail_add_to_cart_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(viewModel.label("add_to_cart"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 4. Cart And Checkout Screen ---
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutScreen(viewModel: AppViewModel) {
    val cartList by viewModel.cartItems.collectAsState()
    val productList by viewModel.products.collectAsState()
    val couponsList by viewModel.coupons.collectAsState()
    val lang by viewModel.selectedLanguage.collectAsState()

    // Checkout Form States
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    var selectedSavedAddress by remember { mutableStateOf<AddressEntity?>(null) }

    var checkoutAddress by remember { mutableStateOf("Sector 3, Baramati MIDC, Pune") }
    var checkoutArea by remember { mutableStateOf("Baramati MIDC") }
    var checkoutPincode by remember { mutableStateOf("413133") }
    var checkoutLat by remember { mutableDoubleStateOf(18.1568) }
    var checkoutLng by remember { mutableDoubleStateOf(74.5761) }
    var checkoutName by remember { mutableStateOf(viewModel.currentUser.value?.fullName ?: "Resident") }
    var checkoutPhone by remember { mutableStateOf(viewModel.currentUser.value?.phone ?: "") }

    // Add Address Form Toggle
    var showAddressForm by remember { mutableStateOf(false) }
    var formName by remember { mutableStateOf(viewModel.currentUser.value?.fullName ?: "") }
    var formPhone by remember { mutableStateOf(viewModel.currentUser.value?.phone ?: "") }
    var formStreet by remember { mutableStateOf("") }
    var formArea by remember { mutableStateOf("Baramati MIDC") }
    var formPincode by remember { mutableStateOf("413133") }
    var formLat by remember { mutableDoubleStateOf(18.1568) }
    var formLng by remember { mutableDoubleStateOf(74.5761) }

    var selectedSlot by remember { mutableStateOf("Morning 7 AM - 10 AM") }
    var selectedPayment by remember { mutableStateOf("COD") }
    var deliveryRadiusKm by remember { mutableStateOf(4.5) } // Default 4.5km

    var couponInput by remember { mutableStateOf("") }
    val activeCouponCode by viewModel.activeCouponCode.collectAsState()

    // UPI Simulation Dialog Screen State
    var showUpiSimulation by remember { mutableStateOf(false) }
    var upiTxIdInput by remember { mutableStateOf("") }
    var selectedScreenshotType by remember { mutableStateOf<String?>(null) } // "gpay", "phonepe", "bhim", null
    var customProofAttached by remember { mutableStateOf<String?>(null) } // dynamic text preview of visual slip
    var isGpsSimulating by remember { mutableStateOf(false) }

    // Custom weight states
    var customWeightDialogItem by remember { mutableStateOf<CartItemEntity?>(null) }
    var customWeightInputText by remember { mutableStateOf("") }

    val subtotal = cartList.sumOf { it.price * it.quantity }
    // Shipping rate: ₹15 for ≤2km, +₹10 per excess km
    val shippingCharge = if (deliveryRadiusKm <= 2.0) 15.0 else 15.0 + ((deliveryRadiusKm - 2.0).coerceAtMost(13.0) * 10.0)

    val matchingCoupon = couponsList.find { it.code == activeCouponCode }
    val discount = if (matchingCoupon != null && subtotal >= matchingCoupon.minOrderAmount) {
        (subtotal * (matchingCoupon.discountPercent / 100.0))
    } else 0.0

    val grandTotal = (subtotal + shippingCharge - discount).coerceAtLeast(0.0)

    Column(modifier = Modifier.fillMaxSize()) {
        // App top header
        TopAppBar(
            title = { Text(viewModel.label("cart_title"), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { viewModel.customerScreen.value = "Home" }, modifier = Modifier.testTag("cart_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
        )

        if (cartList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.RemoveShoppingCart, "Empty", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your Fresh Basket grocery bag is empty!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Select nutritious vegetables & fruits from our Baramati catalog to complete your basket orders.", textAlign = TextAlign.Center, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { viewModel.customerScreen.value = "Home" }) {
                        Text("Add Veggies & Grocery Now")
                    }
                }
            }
            return
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Cart Items Summary
            Text("Basket Itemization:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            cartList.forEach { item ->
                val prod = productList.find { it.id == item.productId }
                val firstWeight = prod?.weightOptions?.split(",")?.firstOrNull() ?: "500g"
                val basePrice = prod?.basePrice ?: 0.0
                val pricePerKg = if (basePrice > 0.0) {
                    val clean = firstWeight.lowercase().trim()
                    val grams = when {
                        clean.endsWith("g") -> clean.removeSuffix("g").toDoubleOrNull() ?: 500.0
                        clean.endsWith("kg") -> (clean.removeSuffix("kg").toDoubleOrNull() ?: 1.0) * 1000.0
                        clean.contains("bunch") || clean.contains("piece") -> 250.0
                        else -> 1000.0
                    }
                    (basePrice / grams) * 1000.0
                } else 0.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("cart_item_card_${item.id}"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Product Basic Information Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            VeggieGraphic(id = prod?.imageUrl ?: "palak", modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod?.localizedName(lang) ?: "Item",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PriceChange, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Price per kg: ₹${String.format(Locale.US, "%.1f", pricePerKg)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                Text(
                                    text = "Rate for ${item.chosenWeight}: ₹${String.format(Locale.US, "%.1f", item.price)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            // Dynamic Quantity Adjuster & Total Price Display
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateCartQty(item, false) },
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                            .testTag("qty_minus_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Remove, "Minus", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${item.quantity}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateCartQty(item, true) },
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                            .testTag("qty_plus_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Add, "Plus", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "₹${String.format(Locale.US, "%.1f", item.price * item.quantity)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "(${item.quantity} selected)",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Interactive Weight Selection Options Row
                        Text("Weight Options (250g, 500g, 1kg, 2kg, Custom):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val weightsList = listOf("250g", "500g", "1kg", "2kg")
                        val currentWeight = item.chosenWeight
                        val isCustomWeight = currentWeight !in weightsList

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            weightsList.forEach { wOption ->
                                val active = currentWeight == wOption
                                FilterChip(
                                    selected = active,
                                    onClick = { viewModel.updateCartWeight(item, wOption) },
                                    label = { Text(wOption, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.height(28.dp)
                                )
                            }

                            // Custom Weight Option
                            val activeCustom = isCustomWeight
                            FilterChip(
                                selected = activeCustom,
                                onClick = {
                                    customWeightDialogItem = item
                                    customWeightInputText = if (isCustomWeight) currentWeight else "750g"
                                },
                                label = {
                                    Text(
                                        text = if (isCustomWeight) "Custom: $currentWeight" else "Custom Weight",
                                        fontSize = 11.sp,
                                        fontWeight = if (isCustomWeight) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.height(28.dp).testTag("custom_weight_chip_${item.id}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery charge calculation slider (Up to 15km radius)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECE5)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsBike, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dynamic Delivery Distance (Baramati MIDC Limit)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select shipment radius (1 KM to 15 KM Limit):", fontSize = 12.sp, color = Color.Gray)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = deliveryRadiusKm.toFloat(),
                            onValueChange = { deliveryRadiusKm = it.toDouble() },
                            valueRange = 1f..15f,
                            steps = 14,
                            modifier = Modifier.weight(1f).testTag("radius_slider")
                        )
                        Text(
                            "${String.format(Locale.US, "%.1f", deliveryRadiusKm)} KM",
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    Text(
                        "Shipping rate structure: ₹15 flat fee up to 2 KM, then +₹10 per added KM. Delivering up to 15 KM boundary.",
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Customer Saved Address Book UI
            Text("Select Billing & Shipping Address:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))

            if (savedAddresses.isEmpty()) {
                Surface(
                    color = Color.Yellow.copy(0.08f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("No saved addresses found.", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Please click \"Add New Address\" below to pin your home coordinates, Village name, and mobile link.", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            } else {
                Text("Saved Addresses (${savedAddresses.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                // Horizontal address list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    savedAddresses.forEach { addr ->
                        val isSel = selectedSavedAddress?.id == addr.id
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable {
                                    selectedSavedAddress = addr
                                    checkoutAddress = addr.fullAddress
                                    checkoutArea = addr.areaVillage
                                    checkoutPincode = addr.pinCode
                                    checkoutLat = addr.latitude
                                    checkoutLng = addr.longitude
                                    checkoutName = addr.name
                                    checkoutPhone = addr.phone
                                    // Automatically estimate radius dynamically based on coordinates
                                    val dist = Math.sqrt(Math.pow((addr.latitude - 18.1568), 2.0) + Math.pow((addr.longitude - 74.5761), 2.0)) * 60.0
                                    deliveryRadiusKm = dist.coerceIn(1.0, 15.0)
                                },
                            border = BorderStroke(2.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.5f)),
                            colors = CardDefaults.cardColors(containerColor = if (isSel) MaterialTheme.colorScheme.primary.copy(0.04f) else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isSel) Icons.Default.CheckCircle else Icons.Default.Home,
                                        null,
                                        tint = if (isSel) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(addr.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(addr.fullAddress, fontSize = 11.sp, color = Color.DarkGray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Area: ${addr.areaVillage} • ${addr.pinCode}", fontSize = 10.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📞 ${addr.phone}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { viewModel.deleteUserAddress(addr.id) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Address form
            Button(
                onClick = { showAddressForm = !showAddressForm },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(if (showAddressForm) Icons.Default.Close else Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (showAddressForm) "Cancel New Address Form" else "Add New Shipping Address")
            }

            if (showAddressForm) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Add New Shipping Address details:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = formName,
                            onValueChange = { formName = it },
                            label = { Text("Customer Name [MANDATORY]") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = formPhone,
                            onValueChange = { formPhone = it },
                            label = { Text("Mobile Number [MANDATORY]") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = formStreet,
                            onValueChange = { formStreet = it },
                            label = { Text("Flat/House No, Street, Road [MANDATORY]") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            singleLine = false,
                            maxLines = 2
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formArea,
                                onValueChange = { formArea = it },
                                label = { Text("Area / City / Village") },
                                modifier = Modifier.weight(1f).padding(bottom = 6.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = formPincode,
                                onValueChange = { formPincode = it },
                                label = { Text("Pincode") },
                                modifier = Modifier.weight(1f).padding(bottom = 6.dp),
                                singleLine = true
                            )
                        }

                        // GPS BUTTON
                        Button(
                            onClick = {
                                isGpsSimulating = true
                                formLat = 18.1500 + Random.nextDouble(-0.02, 0.02)
                                formLng = 74.5700 + Random.nextDouble(-0.02, 0.02)
                                formPincode = "4131" + listOf("33", "02", "11").random()
                                formArea = listOf("Baramati MIDC", "Saraswati Nagar", "Bhigwan Chowk", "Vidyanagar").random()
                                formStreet = "Flat No. " + (100..400).random() + ", Landmark Residency Road"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            if (isGpsSimulating) {
                                CircularProgressIndicator(color = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Vibrating GPS Sensors...")
                            } else {
                                Icon(Icons.Default.MyLocation, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pull Current GPS Location Coordinates")
                            }
                        }

                        // GOOGLE MAPS PICKER SIMULATION (Interactive Canvas widget)
                        Text("Google Maps Real-Time Location Picker (Tap to drop pin):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                                .border(1.dp, Color.LightGray)
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        // Map the tap coordinates into Baramati boundaries using fixed coordinate ratios
                                        val percentX = (offset.x / 800f).coerceIn(0f, 1f)
                                        val percentY = (offset.y / 360f).coerceIn(0f, 1f)
                                        formLat = 18.1400 + (1.0 - percentY) * 0.03
                                        formLng = 74.5500 + percentX * 0.04
                                        val regions = listOf(
                                            "Baramati Station" to "413102",
                                            "MIDC Sector I" to "413133",
                                            "Vidya Pratishthan Area" to "413133",
                                            "Bhigwan Road Bypass" to "413102",
                                            "Gaothan Village Outskirts" to "413111"
                                        )
                                        val reg = regions.random()
                                        formArea = reg.first
                                        formPincode = reg.second
                                        formStreet = "Plot #" + (1..150).random() + " near " + reg.first + " Highway"
                                    }
                                }
                        ) {
                            // Map Canvas Visuals
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw main river/canal line
                                drawLine(color = Color(0xFF29B6F6), start = androidx.compose.ui.geometry.Offset(0f, size.height * 0.3f), end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.4f), strokeWidth = 10f)

                                // Draw major highway road
                                drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, 0f), end = androidx.compose.ui.geometry.Offset(size.width * 0.3f, size.height), strokeWidth = 12f)
                                drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(0f, size.height * 0.7f), end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.6f), strokeWidth = 8f)

                                // Draw railway track
                                drawLine(color = Color.DarkGray, start = androidx.compose.ui.geometry.Offset(0f, size.height * 0.1f), end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.2f), strokeWidth = 4f)

                                // Draw green gardens circles
                                drawCircle(Color(0xFFC8E6C9), radius = 60f, center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.8f))
                                drawCircle(Color(0xFFC8E6C9), radius = 80f, center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.25f))

                                // Draw passive landmark markers
                                drawCircle(Color.Gray, radius = 6f, center = androidx.compose.ui.geometry.Offset(size.width * 0.22f, size.height * 0.12f)) // station
                                drawCircle(Color.Gray, radius = 6f, center = androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height * 0.75f)) // midc

                                // Current selected Pin Marker!
                                val percentX = ((formLng - 74.5500) / 0.04).coerceIn(0.0, 1.0).toFloat()
                                val percentY = (1.0 - ((formLat - 18.1400) / 0.03)).coerceIn(0.0, 1.0).toFloat()
                                val markerX = percentX * size.width
                                val markerY = percentY * size.height

                                // Draw red pin pulse
                                drawCircle(Color.Red.copy(alpha = 0.3f), radius = 24f, center = androidx.compose.ui.geometry.Offset(markerX, markerY))
                                drawCircle(Color.Red, radius = 8f, center = androidx.compose.ui.geometry.Offset(markerX, markerY))
                            }

                            // Dynamic location label overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(0.7f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Dropped Pin: [${String.format(Locale.US, "%.5f", formLat)}, ${String.format(Locale.US, "%.5f", formLng)}]",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Map Guide Overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .background(Color.White.copy(0.9f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Map Simulation: Tap coordinate area to relocate Pin", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Submit address button
                        Button(
                            onClick = {
                                if (formStreet.isEmpty() || formName.isEmpty() || formPhone.isEmpty()) {
                                    viewModel.showToast("Please fill all mandatory address parameters")
                                } else {
                                    viewModel.saveNewAddress(formName, formPhone, formStreet, formArea, formPincode, formLat, formLng)
                                    showAddressForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirm & Persist Address")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text inputs mirroring active shipping parameters
            Card(
                border = BorderStroke(1.dp, Color.LightGray.copy(0.5f)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FCEF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ACTIVE DISPATCH ADDRESS INFO:", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Recipient: $checkoutName • 📞 $checkoutPhone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Delivery To: $checkoutAddress", fontSize = 12.sp)
                    Text("Village/Area: $checkoutArea • City: Baramati • Pincode: $checkoutPincode", fontSize = 11.sp, color = Color.Gray)
                    Text("Coordinates: Lat ${String.format(Locale.US, "%.4f", checkoutLat)} • Lng ${String.format(Locale.US, "%.4f", checkoutLng)}", fontSize = 10.sp, color = Color.Blue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Slot Toggle
            Text(viewModel.label("delivery_slot") + ":", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Morning 7 AM - 10 AM", "Afternoon 12 PM - 3 PM", "Evening 5 PM - 8 PM").forEach { slot ->
                    val active = selectedSlot == slot
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.3f))
                            .border(1.dp, if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(0.3f), RoundedCornerShape(8.dp))
                            .clickable { selectedSlot = slot }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(slot, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Coupons Engine Box
            Text("Vouchers & Promotional Codes:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponInput,
                    onValueChange = { couponInput = it },
                    placeholder = { Text("Enter Coupon (e.g. FRESH50)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f).testTag("coupon_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val valid = viewModel.applyCoupon(couponInput)
                        if (valid) couponInput = ""
                    },
                    modifier = Modifier.testTag("apply_coupon_btn")
                ) {
                    Text("Apply")
                }
            }

            if (activeCouponCode.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Promo Activated: $activeCouponCode", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    IconButton(onClick = { viewModel.removeCoupon() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Text(viewModel.label("payment_method") + ":", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cash On Delivery Option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPayment = "COD" }
                        .testTag("pay_cod_btn"),
                    border = BorderStroke(1.5.dp, if (selectedPayment == "COD") MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.4f)),
                    colors = CardDefaults.cardColors(containerColor = if (selectedPayment == "COD") MaterialTheme.colorScheme.primary.copy(0.08f) else Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Payments, "COD", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Cash on Delivery", fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }

                // Instant UPI Simulator Option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPayment = "UPI" }
                        .testTag("pay_upi_btn"),
                    border = BorderStroke(1.5.dp, if (selectedPayment == "UPI") MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.4f)),
                    colors = CardDefaults.cardColors(containerColor = if (selectedPayment == "UPI") MaterialTheme.colorScheme.primary.copy(0.08f) else Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.QrCodeScanner, "UPI", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("UPI QR scan / GPAY", fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Receipt Summary
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6F2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Payment Receipt", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross subtotal", fontSize = 13.sp)
                        Text("₹${String.format(Locale.US, "%.2f", subtotal)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery charge (${String.format(Locale.US, "%.1f", deliveryRadiusKm)} KM)", fontSize = 13.sp)
                        Text("₹${String.format(Locale.US, "%.2f", shippingCharge)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (discount > 0.0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Coupon Discount", fontSize = 13.sp, color = Color(0xFF2E7D32))
                            Text("-₹${String.format(Locale.US, "%.2f", discount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total to pay", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("₹${String.format(Locale.US, "%.2f", grandTotal)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Action Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, Color.LightGray.copy(0.4f))
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (selectedPayment == "UPI") {
                        showUpiSimulation = true
                    } else {
                        viewModel.checkoutAndPlaceOrder(
                            address = checkoutAddress,
                            areaVillage = checkoutArea,
                            pinCode = checkoutPincode,
                            latitude = checkoutLat,
                            longitude = checkoutLng,
                            deliverySlot = selectedSlot,
                            paymentMethod = "COD",
                            deliveryAreaRadiusKm = deliveryRadiusKm,
                            upiTransactionId = null,
                            paymentProofBase64 = null,
                            paymentStatus = "Pending COD"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_checkout_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(viewModel.label("place_order") + " (₹${String.format(Locale.US, "%.0f", grandTotal)})", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }

    // UPI Simulation Dialog Screen (with QR Code, UPI ID copy, screenshot upload simulated select)
    if (showUpiSimulation) {
        AlertDialog(
            onDismissRequest = { showUpiSimulation = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (upiTxIdInput.trim().isEmpty()) {
                            viewModel.showToast("Please enter an 8-12 digit UPI Ref / Txn ID")
                        } else {
                            showUpiSimulation = false
                            viewModel.checkoutAndPlaceOrder(
                                address = checkoutAddress,
                                areaVillage = checkoutArea,
                                pinCode = checkoutPincode,
                                latitude = checkoutLat,
                                longitude = checkoutLng,
                                deliverySlot = selectedSlot,
                                paymentMethod = "UPI QR Scan",
                                deliveryAreaRadiusKm = deliveryRadiusKm,
                                upiTransactionId = upiTxIdInput.trim(),
                                paymentProofBase64 = customProofAttached ?: "visual_gpay_receipt_${upiTxIdInput.trim()}",
                                paymentStatus = "Paid via UPI"
                            )
                        }
                    },
                    modifier = Modifier.testTag("confirm_upi_payment")
                ) {
                    Text("Verify & Submit Order")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpiSimulation = false }) {
                    Text("Change Method")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Secure BHIM UPI Scanner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Scan to Pay: \u20B9${String.format(Locale.US, "%.2f", grandTotal)}", fontWeight = FontWeight.Black, color = Color(0xFF1B5E20), fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // UPI QR CODE VECTOR RENDERING IN COMPOSE
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(1.5.dp, Color.Gray.copy(0.4f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw corner alignment squares representing QR pattern
                            drawRect(color = Color.Black, topLeft = androidx.compose.ui.geometry.Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(35f, 35f))
                            drawRect(color = Color.White, topLeft = androidx.compose.ui.geometry.Offset(10f, 10f), size = androidx.compose.ui.geometry.Size(15f, 15f))

                            drawRect(color = Color.Black, topLeft = androidx.compose.ui.geometry.Offset(size.width - 35f, 0f), size = androidx.compose.ui.geometry.Size(35f, 35f))
                            drawRect(color = Color.White, topLeft = androidx.compose.ui.geometry.Offset(size.width - 25f, 10f), size = androidx.compose.ui.geometry.Size(15f, 15f))

                            drawRect(color = Color.Black, topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - 35f), size = androidx.compose.ui.geometry.Size(35f, 35f))
                            drawRect(color = Color.White, topLeft = androidx.compose.ui.geometry.Offset(10f, size.height - 25f), size = androidx.compose.ui.geometry.Size(15f, 15f))

                            // Draw central logo circle
                            drawCircle(color = Color(0xFF2E7D32), radius = 14f, center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f))

                            // Random dotted lines mocking QR payload data!
                            for (i in 0..10) {
                                drawLine(
                                    color = Color.Black.copy(alpha = if (i % 2 == 0) 0.9f else 0.4f),
                                    start = androidx.compose.ui.geometry.Offset(45f + i * 8f, 15f),
                                    end = androidx.compose.ui.geometry.Offset(45f + i * 8f, size.height - 45f),
                                    strokeWidth = 4f
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // UPI ID display and Copy button
                    Surface(
                        color = Color.LightGray.copy(0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("Merchant UPI ID:", fontSize = 10.sp, color = Color.Gray)
                                Text("ssfreshbasket@okhdfcbank", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(
                                onClick = { viewModel.showToast("UPI ID Copied to Clipboard!") },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Copy ID", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // UPI Transaction/Ref ID Input
                    OutlinedTextField(
                        value = upiTxIdInput,
                        onValueChange = { upiTxIdInput = it },
                        label = { Text("UPI Trxn Ref ID (UPI ID)") },
                        placeholder = { Text("e.g. 129038472938") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // MOCK PAYMENT SCREENSHOT UPLOADER SECTION
                    Text("Payment Screenshot Verification proof:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("gpay" to "GooglePay", "phonepe" to "PhonePe", "bhim" to "BHIM").forEach { (type, name) ->
                            val active = selectedScreenshotType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) Color(0xFFC8E6C9) else Color.LightGray.copy(0.3f))
                                    .border(1.2.dp, if (active) Color(0xFF2E7D32) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedScreenshotType = type
                                        customProofAttached = "visual_proof_${type}_paid_₹${grandTotal}_txn_id_${(100000..999999).random()}"
                                        viewModel.showToast("$name Payment Screenshot Upload Simulated Successfully!")
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // PREVIEW ATTACHED SCREENSHOT
                    if (customProofAttached != null) {
                        Card(
                            border = BorderStroke(1.2.dp, Color(0xFF4CAF50)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, "Success", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Screenshot Attached Successfully!", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF2E7D32))
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                // Render a beautiful visual confirmation receipt mockup of GPay/PhonePe to substitute custom Image
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(85.dp)
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                        .border(0.5.dp, Color(0xFFC8E6C9), RoundedCornerShape(6.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "${selectedScreenshotType?.uppercase() ?: "GPAY"} TRANSACTION RECEIPT",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp,
                                                color = Color(0xFF2E7D32)
                                            )
                                            Text("SUCCESS ✅", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color(0xFF2E7D32))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Sent To: SS Fresh Market Ltd.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Paid Amount: ₹${String.format(Locale.US, "%.1f", grandTotal)}", fontSize = 10.sp)
                                        Text("Ref ID: tx_ref_971${upiTxIdInput.ifEmpty { "102938" }}", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(1.dp, Color.Gray.copy(0.3f), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Please Tap GPAY/PhonePe above to snap payment screenshot", fontSize = 10.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }
        )
    }

    if (customWeightDialogItem != null) {
        val targetItem = customWeightDialogItem!!
        AlertDialog(
            onDismissRequest = { customWeightDialogItem = null },
            confirmButton = {
                Button(
                    onClick = {
                        val finalWeightText = customWeightInputText.trim()
                        if (finalWeightText.isNotEmpty()) {
                            viewModel.updateCartWeight(targetItem, finalWeightText)
                        }
                        customWeightDialogItem = null
                    },
                    modifier = Modifier.testTag("apply_custom_weight_btn")
                ) {
                    Text("Apply Custom Weight")
                }
            },
            dismissButton = {
                TextButton(onClick = { customWeightDialogItem = null }) {
                    Text("Dismiss")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonitorWeight, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enter Custom Weight", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Specify customized quantity size representation for checkout delivery tracking.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customWeightInputText,
                        onValueChange = { customWeightInputText = it },
                        label = { Text("Weight size (e.g. 750g, 1.5kg, 3kg)") },
                        placeholder = { Text("750g") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_weight_input_field")
                    )
                }
            }
        )
    }
}

// --- 5. Live Tracking Screen ---
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(viewModel: AppViewModel) {
    val trackingOrder by viewModel.selectedOrderForTracking.collectAsState()
    val partnerList by viewModel.partners.collectAsState()

    if (trackingOrder == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Select an active order from History to track.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.customerScreen.value = "History" }) {
                    Text("Go to Orders History")
                }
            }
        }
        return
    }

    val order = trackingOrder!!
    val items = OrderConverters().toOrderItemList(order.itemsJson)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Tracker Top Header
        TopAppBar(
            title = { Text("Track Order #${order.id}", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { viewModel.customerScreen.value = "Home" }, modifier = Modifier.testTag("tracking_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Live Status visualizer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE5ECE5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Secure Order tracking system", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Order ID: #SSFB-2026-${order.id}", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Text("Delivery Status: ${order.status}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
                    
                    val expectedDeliveryTime = when (order.status) {
                        "Pending" -> "Scheduled Slot: ${order.deliverySlot} (Expected dispatch within 1 hour)"
                        "Confirmed" -> "Expected Delivery in approx 30-45 minutes"
                        "Out for Delivery" -> "Expected Arrival in approx 10-15 minutes (Rider en-route!)"
                        "Delivered" -> "Delivered at ${java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(java.util.Date(order.dateEpochMs + 25 * 60 * 1000))}"
                        else -> "Estimation Unavailable"
                    }
                    
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Expected Delivery Time:", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text(expectedDeliveryTime, fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Text("Current Delivery Progress", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))

                    val steps = listOf("Pending", "Confirmed", "Out for Delivery", "Delivered")
                    val currentIdx = steps.indexOf(order.status).coerceAtLeast(0)

                    steps.forEachIndexed { idx, step ->
                        val isDone = idx <= currentIdx
                        val isCurrent = idx == currentIdx

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        color = if (isDone) MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.6f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = if (step == "Pending") viewModel.label("status_pending")
                                else if (step == "Confirmed") viewModel.label("status_confirmed")
                                else if (step == "Out for Delivery") viewModel.label("status_dispatched")
                                else viewModel.label("status_delivered"),
                                fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else if (isDone) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                                fontSize = if (isCurrent) 14.sp else 13.sp
                            )
                        }
                    }

                    if (order.status == "Delivered") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delivered successfully! Thank you for ordering from SS Fresh Basket.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            }
                        }
                    } else if (order.status == "Cancelled") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Cancel, null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("This order has been Cancelled.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // OTP validation container (required to proceed delivery handshakes)
            if (order.status != "Delivered" && order.status != "Cancelled") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.08f)),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(viewModel.label("otp_label") + " Code:", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text("Provide code to rider upon vegetable delivery checking.", fontSize = 11.sp, color = Color.Gray)
                        }

                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(order.otpCode, fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery Partner Section
            val partner = partnerList.find { it.id == order.assignedPartnerId }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECE5))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Your Assigned Delivery Partner:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(partner?.name ?: "Baramati Logistics Lead", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("SS Fresh Fleet • Phone: ${partner?.phone ?: "9876543210"}", fontSize = 11.sp, color = Color.Gray)
                        }

                        // Call support
                        IconButton(onClick = { viewModel.showToast("Initiating dialer to partner: ${partner?.phone ?: "9876543210"}") }) {
                            Icon(Icons.Default.Call, "Call", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Specifications Breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECE5))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Order Receipt Specifications:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    items.forEach { itm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${itm.productName} (${itm.chosenWeight}) x${itm.quantity}", fontSize = 12.sp)
                            Text("₹${String.format(Locale.US, "%.2f", itm.price * itm.quantity)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 12.sp, color = Color.Gray)
                        Text("₹${String.format(Locale.US, "%.2f", order.subtotal)}", fontSize = 12.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Charges", fontSize = 12.sp, color = Color.Gray)
                        Text("₹${String.format(Locale.US, "%.2f", order.deliveryCharges)}", fontSize = 12.sp)
                    }

                    if (order.discountAmount > 0.0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Promo Saved", fontSize = 12.sp, color = Color.Green)
                            Text("-₹${String.format(Locale.US, "%.2f", order.discountAmount)}", fontSize = 12.sp, color = Color.Green)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Grand Paid Total", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("₹${String.format(Locale.US, "%.2f", order.totalAmount)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cancel action during Emergency situation
            if (order.status == "Pending" || order.status == "Confirmed") {
                Button(
                    onClick = { viewModel.cancelCustomerOrder(order) },
                    modifier = Modifier.fillMaxWidth().testTag("emergency_cancel_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Cancel, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Emergency Order Cancellation", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.reorderOrder(order) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reorder items in 1-Click", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 6. Order History Screen ---
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(viewModel: AppViewModel) {
    val ordersList by viewModel.orders.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Your Orders History", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { viewModel.customerScreen.value = "Home" }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
        )

        if (ordersList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.History, "No orders", modifier = Modifier.size(48.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No past fresh basket purchases record found.", fontWeight = FontWeight.Bold)
                }
            }
            return
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6FBF7))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ordersList.forEach { o ->
                    val dateStr = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(o.dateEpochMs))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.selectedOrderForTracking.value = o
                            viewModel.customerScreen.value = "Tracking"
                        }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Order ID: #${o.id}", fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (o.status == "Delivered") Color(0xFFC8E6C9)
                                            else if (o.status == "Cancelled") Color(0xFFFFCDD2)
                                            else Color(0xFFFFE082),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = o.status,
                                        color = if (o.status == "Delivered") Color(0xFF1B5E20)
                                        else if (o.status == "Cancelled") Color.Red
                                        else Color(0xFFE65100),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text("Date Ordered: $dateStr", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            val itms = OrderConverters().toOrderItemList(o.itemsJson)
                            Text(itms.joinToString { "${it.productName} (${it.chosenWeight}) x${it.quantity}" }, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Paid: ₹${String.format(Locale.US, "%.1f", o.totalAmount)}", fontWeight = FontWeight.Black)

                                Button(
                                    onClick = { viewModel.reorderOrder(o) },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Reorder", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 7. Customer Support Screen ---
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerSupportScreen(viewModel: AppViewModel) {
    var ticketSubject by remember { mutableStateOf("") }
    var ticketBody by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(viewModel.label("support_title"), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { viewModel.customerScreen.value = "Home" }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Direct Contact options
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instant Farm-Fresh Helpdesk", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Need help with an item or late delivery? Get connected immediately.", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showToast("Launching direct support WhatsApp: +91 9588667788") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Icon(Icons.Default.Share, "WhatsApp")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.showToast("Dialing direct telephone support line: +91 2112 258800") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, "Dial")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Support", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Ticket Generator Form
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Raise Return/Refund Request", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ticketSubject,
                        onValueChange = { ticketSubject = it },
                        label = { Text("Quality Concern or Refund Subject") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ticketBody,
                        onValueChange = { ticketBody = it },
                        label = { Text("Details (Describe item, weight state, or damage)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (ticketSubject.isNotEmpty() && ticketBody.isNotEmpty()) {
                                viewModel.showToast("Support ticket registered successfully. Ticket ref #${(100000..999999).random()}")
                                ticketSubject = ""
                                ticketBody = ""
                            } else {
                                viewModel.showToast("Fill complete form details")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register Ticket Concern")
                    }
                }
            }
        }
    }
}

@Composable
fun PopularProductsCarouselSection(products: List<ProductEntity>, viewModel: AppViewModel) {
    val lang by viewModel.selectedLanguage.collectAsState()

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🔥 Popular Products",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "Trending hot picks in Baramati MIDC",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            products.forEach { product ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .testTag("popular_product_card_${product.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE5ECE5))
                ) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                viewModel.selectedProduct.value = product
                                viewModel.customerScreen.value = "Details"
                            }
                            .padding(8.dp)
                    ) {
                        // Image & Rating / Delivery Tag
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F8F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            VeggieGraphic(id = product.imageUrl, modifier = Modifier.size(56.dp))

                            // Superfast Delivery Badge on Popular items
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(Color(0xFFFFF9C4), RoundedCornerShape(bottomEnd = 6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, null, tint = Color(0xFFE65100), modifier = Modifier.size(10.dp))
                                    Text("10m", color = Color(0xFFE65100), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Rating Badge
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(topStart = 6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(10.dp))
                                Text("${product.rating}", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.localizedName(lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = product.weightOptions.split(",").firstOrNull() ?: "500g",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹${String.format(Locale.US, "%.2f", product.basePrice)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )

                            IconButton(
                                onClick = { viewModel.addProductToCart(product, product.weightOptions.split(",").firstOrNull() ?: "500g") },
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .testTag("add_popular_btn_${product.id}")
                            ) {
                                Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonalVegetablesSection(products: List<ProductEntity>, viewModel: AppViewModel) {
    val lang by viewModel.selectedLanguage.collectAsState()

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🌱 Seasonal Vegetables",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "Freshly harvested, pesticide-free local crop",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            products.forEach { product ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .testTag("seasonal_product_card_${product.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE5ECE5))
                ) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                viewModel.selectedProduct.value = product
                                viewModel.customerScreen.value = "Details"
                            }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F8F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            VeggieGraphic(id = product.imageUrl, modifier = Modifier.size(56.dp))

                            // High Freshness Tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(bottomStart = 6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("100% Organic", color = Color(0xFF2E7D32), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }

                            // 10 Min Delivery Bullet
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color(0xFFFFEB3B), RoundedCornerShape(topEnd = 6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, null, tint = Color.Black, modifier = Modifier.size(9.dp))
                                    Spacer(modifier = Modifier.width(1.dp))
                                    Text("10m", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.localizedName(lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = product.weightOptions.split(",").firstOrNull() ?: "500g",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹${String.format(Locale.US, "%.2f", product.basePrice)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )

                            IconButton(
                                onClick = { viewModel.addProductToCart(product, product.weightOptions.split(",").firstOrNull() ?: "500g") },
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .testTag("add_seasonal_btn_${product.id}")
                            ) {
                                Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
