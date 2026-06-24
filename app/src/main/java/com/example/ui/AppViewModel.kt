package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database.appDao())

    // --- Core Global Reactive Sourced Data ---
    val products = repository.allProducts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val cartItems = repository.cartItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val orders = repository.allOrders.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val coupons = repository.allCoupons.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val partners = repository.allPartners.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val profiles = repository.allUsers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- State Variables ---
    val currentRole = MutableStateFlow("Customer") // "Customer", "Partner", "Admin"
    val currentUser = MutableStateFlow<UserProfileEntity?>(null)
    val selectedLanguage = MutableStateFlow("en") // "en", "mr", "hi"

    // Backstack / Routing State
    val customerScreen = MutableStateFlow("Splash") // "Splash", "Login", "Home", "Details", "Cart", "Tracking", "History", "Support"
    val partnerScreen = MutableStateFlow("Register") // "Register", "Home", "Earnings"
    val adminScreen = MutableStateFlow("Dashboard")   // "Dashboard", "Products", "Orders", "Coupons", "Partners"

    // Selection Specifics
    val selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedOrderForTracking = MutableStateFlow<OrderEntity?>(null)

    // Applied Coupon Code
    val activeCouponCode = MutableStateFlow("")
    val searchInput = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All") // "All", "Vegetables", "Fruits", "Grocery"

    // Partner Online State
    val partnerOnline = MutableStateFlow(true)

    // UI Toast or Notifications
    val toastMessage = MutableStateFlow<String?>(null)

    // Saved addresses for the active shopper
    val savedAddresses = MutableStateFlow<List<AddressEntity>>(emptyList())

    // Admin real-time notifications
    val adminNotifications = MutableStateFlow<List<String>>(emptyList())
    private var lastOrderCount = -1

    init {
        viewModelScope.launch {
            // Seed database at launch if empty
            repository.checkAndSeedDatabase()

            val sharedPrefs = application.getSharedPreferences("ss_fresh_basket_prefs", android.content.Context.MODE_PRIVATE)
            val savedPhone = sharedPrefs.getString("logged_in_phone", null)

            if (savedPhone != null) {
                val profile = repository.getUserProfile(savedPhone)
                if (profile != null) {
                    currentUser.value = profile
                    selectedLanguage.value = profile.selectedLanguage
                    reloadAddresses()
                } else {
                    currentUser.value = null
                }
            } else {
                currentUser.value = null
            }
        }

        // Keep savedAddresses in sync with user changes
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    reloadAddresses()
                } else {
                    savedAddresses.value = emptyList()
                }
            }
        }

        // Real-Time notification alerts for newly created orders (Admin view)
        viewModelScope.launch {
            repository.allOrders.collect { orderList ->
                if (lastOrderCount == -1) {
                    lastOrderCount = orderList.size
                } else if (orderList.size > lastOrderCount) {
                    lastOrderCount = orderList.size
                    val latest = orderList.firstOrNull()
                    if (latest != null) {
                        val message = "🚨 New Order #${latest.id} placed by ${latest.customerName} (PIN: ${latest.pinCode}) for \u20B9${latest.totalAmount}!"
                        adminNotifications.value = listOf(message) + adminNotifications.value
                        showToast(message)
                    }
                } else {
                    lastOrderCount = orderList.size
                }
            }
        }
    }

    fun reloadAddresses() {
        viewModelScope.launch {
            val phone = currentUser.value?.phone ?: return@launch
            repository.getAddressesByPhone(phone).collect {
                savedAddresses.value = it
            }
        }
    }

    fun saveNewAddress(name: String, phone: String, fullAddress: String, area: String, pincode: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            val customerPhone = currentUser.value?.phone ?: "9999999999"
            val newAddr = AddressEntity(
                customerPhone = customerPhone,
                name = name,
                phone = phone,
                fullAddress = fullAddress,
                areaVillage = area,
                pinCode = pincode,
                latitude = lat,
                longitude = lng
            )
            repository.saveAddress(newAddr)
            showToast("Address Saved Successfully!")
            reloadAddresses()
        }
    }

    fun deleteUserAddress(id: Int) {
        viewModelScope.launch {
            repository.deleteAddress(id)
            showToast("Address matching deleted.")
            reloadAddresses()
        }
    }

    // --- Toast Handler ---
    fun showToast(msg: String) {
        toastMessage.value = msg
    }

    fun clearToast() {
        toastMessage.value = null
    }

    // --- Localization dictionary ---
    fun label(key: String): String {
        val lang = selectedLanguage.value
        val map = mapOf(
            "app_name" to mapOf("en" to "SS Fresh Basket", "mr" to "एसएस फ्रेश बास्केट", "hi" to "एसएस फ्रेश बास्केट"),
            "search_placeholder" to mapOf("en" to "Search fresh veggies, fruits...", "mr" to "ताजी भाजीपाला आणि फळे शोधा...", "hi" to "ताजी सब्जियां और फल खोजें..."),
            "categories" to mapOf("en" to "Shop by Category", "mr" to "वर्गानुसार खरेदी", "hi" to "श्रेणी के अनुसार खरीदें"),
            "all" to mapOf("en" to "All", "mr" to "सर्व", "hi" to "सभी"),
            "vegetables" to mapOf("en" to "Vegetables", "mr" to "भाजीपाला", "hi" to "सब्जियां"),
            "fruits" to mapOf("en" to "Fruits", "mr" to "फळे", "hi" to "फल"),
            "grocery" to mapOf("en" to "Grocery", "mr" to "किराणा", "hi" to "किराना"),
            "offers_heading" to mapOf("en" to "Today's Hot Offers", "mr" to "आजच्या खास ऑफर्स", "hi" to "आज के हॉट ऑफर्स"),
            "recommended" to mapOf("en" to "Recommended for You", "mr" to "तुमच्यासाठी शिफारस केलेले", "hi" to "आपके लिए अनुशंसित"),
            "add_to_cart" to mapOf("en" to "Add to Cart", "mr" to "पिशवीत टाका", "hi" to "कार्ट में डालें"),
            "buy_now" to mapOf("en" to "Buy Now", "mr" to "लगेच खरेदी करा", "hi" to "अभी खरीदें"),
            "back" to mapOf("en" to "Back", "mr" to "मागे", "hi" to "पीछे"),
            "cart_title" to mapOf("en" to "Your Basket Items", "mr" to "तुमची टोपली", "hi" to "आपकी टोकरी"),
            "delivery_slot" to mapOf("en" to "Scheduled Delivery Slot", "mr" to "डिलिव्हरी वेळेचा स्लॉट", "hi" to "वितरण समय स्लॉट"),
            "payment_method" to mapOf("en" to "Payment Method", "mr" to "पेमेंट पद्धत", "hi" to "भुगतान का प्रकार"),
            "place_order" to mapOf("en" to "Place Fresh Order", "mr" to "ऑर्डर नोंदवा", "hi" to "ऑर्डर पुष्टि करें"),
            "support_title" to mapOf("en" to "SS Fresh Support Center", "mr" to "एसएस सपोर्ट केंद्र", "hi" to "एसएस सपोर्ट केंद्र"),
            "order_tracking" to mapOf("en" to "Live Order Tracking", "mr" to "थेट ऑर्डर ट्रॅकिंग", "hi" to "लाइव ऑर्डर ट्रैकिंग"),
            "otp_label" to mapOf("en" to "Delivery Complete OTP", "mr" to "डिलिव्हरी पूर्णता ओटीपी", "hi" to "वितरण पूर्णता ओटीपी"),
            "status_pending" to mapOf("en" to "Pending", "mr" to "प्रलंबित", "hi" to "लंबित"),
            "status_confirmed" to mapOf("en" to "Confirmed", "mr" to "स्वीकारले", "hi" to "स्वीकृत"),
            "status_dispatched" to mapOf("en" to "Out for Delivery", "mr" to "डिलिव्हरीसाठी बाहेर", "hi" to "वितरण के लिए बाहर"),
            "status_delivered" to mapOf("en" to "Delivered", "mr" to "डिलिव्हर झाले", "hi" to "वितरित"),
            "status_cancelled" to mapOf("en" to "Cancelled", "mr" to "रद्द केले", "hi" to "रद्द")
        )
        return map[key]?.get(lang) ?: key
    }

    // --- Authentication Actions ---
    fun loginAsGuest() {
        viewModelScope.launch {
            val guest = "9999999999"
            val profile = repository.getUserProfile(guest) ?: UserProfileEntity(phone = guest, fullName = "Guest Shopper", role = "Customer")
            repository.saveUserProfile(profile)

            val sharedPrefs = getApplication<Application>().getSharedPreferences("ss_fresh_basket_prefs", android.content.Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("logged_in_phone", guest).apply()

            currentUser.value = profile
            selectedLanguage.value = profile.selectedLanguage
            customerScreen.value = "Home"
            showToast("Logged in as Guest shopper")
        }
    }

    fun loginWithPhone(phone: String, name: String) {
        viewModelScope.launch {
            if (phone.length < 10) {
                showToast("Please enter a valid 10-digit phone number")
                return@launch
            }
            val cleanPhone = phone.trim()
            val existing = repository.getUserProfile(cleanPhone)
            val profile = existing ?: UserProfileEntity(phone = cleanPhone, fullName = name.ifEmpty { "Customer" }, role = "Customer", selectedLanguage = selectedLanguage.value)
            repository.saveUserProfile(profile)

            val sharedPrefs = getApplication<Application>().getSharedPreferences("ss_fresh_basket_prefs", android.content.Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("logged_in_phone", cleanPhone).apply()

            currentUser.value = profile
            customerScreen.value = "Home"
            showToast("Welcome ${profile.fullName}! Mobile Verification Successful.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("ss_fresh_basket_prefs", android.content.Context.MODE_PRIVATE)
            sharedPrefs.edit().remove("logged_in_phone").apply()
            currentUser.value = null
            customerScreen.value = "Login"
            showToast("Logged out successfully!")
        }
    }

    fun loginAsPartner(phone: String, name: String) {
        viewModelScope.launch {
            if (phone.length < 10) {
                showToast("Please enter a valid 10-digit phone number")
                return@launch
            }
            val cleanPhone = phone.trim()
            val existing = repository.getUserProfile(cleanPhone)
            val profile = existing?.copy(role = "Partner") ?: UserProfileEntity(phone = cleanPhone, fullName = name.ifEmpty { "Fleet Rider" }, role = "Partner", selectedLanguage = selectedLanguage.value)
            repository.saveUserProfile(profile)
            currentUser.value = profile
            partnerScreen.value = "Home"
            showToast("Welcome driver ${profile.fullName}!")
        }
    }

    fun switchRole(role: String) {
        viewModelScope.launch {
            currentRole.value = role
            if (role == "Customer") {
                val p = repository.getUserProfile("9999999999")
                currentUser.value = p
                customerScreen.value = "Home"
            } else if (role == "Admin") {
                val p = repository.getUserProfile("8888888888")
                currentUser.value = p
                adminScreen.value = "Dashboard"
            } else if (role == "Partner") {
                val p = repository.getUserProfile("7777777777")
                currentUser.value = p
                partnerScreen.value = "Home"
            }
            showToast("Switched to $role Interface")
        }
    }

    fun changeLanguage(lang: String) {
        viewModelScope.launch {
            selectedLanguage.value = lang
            currentUser.value?.let { profile ->
                val updated = profile.copy(selectedLanguage = lang)
                repository.saveUserProfile(updated)
                currentUser.value = updated
            }
            showToast("Language changed successfully")
        }
    }

    // --- Cart Actions ---
    fun addProductToCart(product: ProductEntity, weight: String) {
        viewModelScope.launch {
            val pricePerOption = calculateOptionPrice(product.basePrice, weight)
            repository.addToCart(product.id, weight, pricePerOption, 1)
            showToast("Added ${product.localizedName(selectedLanguage.value)} ($weight) to cart!")
        }
    }

    fun updateCartQty(cartItem: CartItemEntity, isAddition: Boolean) {
        viewModelScope.launch {
            val newQty = if (isAddition) cartItem.quantity + 1 else cartItem.quantity - 1
            repository.updateCartQuantity(cartItem.id, newQty)
        }
    }

    fun updateCartWeight(cartItem: CartItemEntity, newWeight: String) {
        viewModelScope.launch {
            val product = products.value.find { it.id == cartItem.productId }
            if (product != null) {
                val newPrice = calculateOptionPrice(product.basePrice, newWeight)
                val updatedItem = cartItem.copy(chosenWeight = newWeight, price = newPrice)
                repository.updateDirectCartItem(updatedItem)
                showToast("Updated ${product.localizedName(selectedLanguage.value)} weight to $newWeight!")
            }
        }
    }

    fun removeCartItem(cartItem: CartItemEntity) {
        viewModelScope.launch {
            repository.removeFromCart(cartItem.id)
            showToast("Removed item")
        }
    }

    fun applyCoupon(code: String): Boolean {
        val list = coupons.value
        val coupon = list.find { it.code.trim().equals(code.trim(), ignoreCase = true) }
        return if (coupon != null) {
            activeCouponCode.value = coupon.code
            showToast("Coupon Applied! ₹${coupon.discountPercent}% Off")
            true
        } else {
            showToast("Invalid Coupon Code")
            false
        }
    }

    fun removeCoupon() {
        activeCouponCode.value = ""
        showToast("Coupon removed")
    }

    // Pricing math utilities
    fun calculateOptionPrice(basePrice: Double, weight: String): Double {
        val clean = weight.lowercase().trim()
        
        when (clean) {
            "250g" -> return basePrice
            "500g" -> return basePrice * 1.8
            "1 bunch" -> return basePrice
            "6 pieces" -> return basePrice
            "1 piece" -> return basePrice
            "100g" -> return basePrice
            "1l" -> return basePrice
            "1kg" -> return basePrice * 3.5 // Bundled standard discount rate
            "2kg" -> return basePrice * 6.5
            "5kg" -> return basePrice * 15.0
            "10kg" -> return basePrice * 28.0
            "3 bunches" -> return basePrice * 2.5
            "1 dozen" -> return basePrice * 1.8
            "1 box" -> return basePrice * 8.5
            "5l" -> return basePrice * 4.6
        }

        // Proportional parsing fallback for custom grams / kg
        return try {
            if (clean.endsWith("g")) {
                val valNum = clean.removeSuffix("g").toDoubleOrNull() ?: 250.0
                (valNum / 250.0) * basePrice
            } else if (clean.endsWith("kg")) {
                val valNum = clean.removeSuffix("kg").toDoubleOrNull() ?: 1.0
                valNum * (basePrice * 3.5)
            } else {
                basePrice
            }
        } catch (e: Exception) {
            basePrice
        }
    }

    // --- Order Placement ---
    fun checkoutAndPlaceOrder(
        address: String,
        areaVillage: String,
        pinCode: String,
        latitude: Double,
        longitude: Double,
        deliverySlot: String,
        paymentMethod: String,
        deliveryAreaRadiusKm: Double,
        upiTransactionId: String? = null,
        paymentProofBase64: String? = null,
        paymentStatus: String = "Pending"
    ) {
        viewModelScope.launch {
            val cartList = cartItems.value
            if (cartList.isEmpty()) {
                showToast("Your cart is empty!")
                return@launch
            }

            val currentProducts = products.value
            val orderItems = cartList.mapNotNull { cartItem ->
                val p = currentProducts.find { it.id == cartItem.productId } ?: return@mapNotNull null
                OrderItem(
                    productId = p.id,
                    productName = p.localizedName(selectedLanguage.value),
                    chosenWeight = cartItem.chosenWeight,
                    price = cartItem.price,
                    quantity = cartItem.quantity,
                    imageUrl = p.imageUrl
                )
            }

            val subtotalVal = cartList.sumOf { it.price * it.quantity }
            // Rate: ₹15 flat base charges for first 2KM, then ₹10 per excess km up to 15km
            val deliveryChargesVal = if (deliveryAreaRadiusKm <= 2.0) 15.0 else 15.0 + ((deliveryAreaRadiusKm - 2.0).coerceAtMost(13.0) * 10.0)

            val appliedCoupon = coupons.value.find { it.code == activeCouponCode.value }
            val discountVal = if (appliedCoupon != null && subtotalVal >= appliedCoupon.minOrderAmount) {
                (subtotalVal * (appliedCoupon.discountPercent / 100.0))
            } else 0.0

            val totalVal = (subtotalVal + deliveryChargesVal - discountVal).coerceAtLeast(0.0)

            val randomOtp = (1000..9999).random().toString() // Simple randomly generated delivery validation OTP!

            val newOrder = OrderEntity(
                customerName = currentUser.value?.fullName ?: "Guest",
                customerContact = currentUser.value?.phone ?: "9999999999",
                customerAddress = address,
                itemsJson = OrderConverters().fromOrderItemList(orderItems),
                subtotal = subtotalVal,
                deliveryCharges = deliveryChargesVal,
                discountAmount = discountVal,
                totalAmount = totalVal,
                paymentMethod = paymentMethod,
                status = "Pending",
                deliverySlot = deliverySlot,
                otpCode = randomOtp,
                assignedPartnerId = partners.value.firstOrNull()?.id ?: 0,
                dateEpochMs = System.currentTimeMillis(),
                areaVillage = areaVillage,
                pinCode = pinCode,
                latitude = latitude,
                longitude = longitude,
                paymentStatus = paymentStatus,
                upiTransactionId = upiTransactionId,
                paymentProofBase64 = paymentProofBase64
            )

            // Subtract from database inventory quantities
            currentProducts.forEach { p ->
                val cartOfThis = cartList.find { it.productId == p.id }
                if (cartOfThis != null) {
                    val updatedStock = (p.stockLevel - cartOfThis.quantity).coerceAtLeast(0)
                    repository.updateProduct(p.copy(stockLevel = updatedStock))
                }
            }

            val placedId = repository.placeOrder(newOrder)
            val savedOrder = repository.getOrderById(placedId.toInt())
            if (savedOrder != null) {
                selectedOrderForTracking.value = savedOrder
            }

            repository.clearCart()
            activeCouponCode.value = ""
            customerScreen.value = "Tracking"
            showToast("Order placed successfully! OTP for verification: $randomOtp")
        }
    }

    // Reorder Action
    fun reorderOrder(oldOrder: OrderEntity) {
        viewModelScope.launch {
            val oldItems = OrderConverters().toOrderItemList(oldOrder.itemsJson)
            repository.clearCart()
            oldItems.forEach { item ->
                repository.addToCart(
                    productId = item.productId,
                    weight = item.chosenWeight,
                    price = item.price,
                    qty = item.quantity
                )
            }
            customerScreen.value = "Cart"
            showToast("Reordered. Items loaded to your basket.")
        }
    }

    // Cancel Active Customer Order
    fun cancelCustomerOrder(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "Cancelled")
            repository.updateOrder(updated)
            if (selectedOrderForTracking.value?.id == order.id) {
                selectedOrderForTracking.value = updated
            }
            showToast("Order cancelled successfully!")
        }
    }

    // --- Delivery Partner Actions ---
    fun togglePartnerAvailability() {
        partnerOnline.value = !partnerOnline.value
        val status = if (partnerOnline.value) "Online" else "Offline"
        showToast("You are now $status")
    }

    fun acceptDeliveryOrder(order: OrderEntity) {
        viewModelScope.launch {
            val partner = partners.value.firstOrNull() ?: return@launch
            val updated = order.copy(status = "Confirmed", assignedPartnerId = partner.id)
            repository.updateOrder(updated)
            showToast("Delivery accepted successfully! Out to Farmer Hub.")
        }
    }

    fun markOrderOutForDelivery(order: OrderEntity) {
        viewModelScope.launch {
            val updated = order.copy(status = "Out for Delivery")
            repository.updateOrder(updated)
            showToast("Order is out for delivery!")
        }
    }

    fun verifyDeliveryOtpAndComplete(order: OrderEntity, enteredOtp: String, proofImgBase64: String?) {
        viewModelScope.launch {
            if (order.otpCode != enteredOtp.trim()) {
                showToast("Incorrect Delivery OTP. Verification Failed!")
                return@launch
            }
            val partner = partners.value.firstOrNull() ?: return@launch
            val updated = order.copy(
                status = "Delivered",
                deliveryProofBase64 = proofImgBase64 ?: "Mock Base64 Signature/Photo Verified"
            )
            repository.updateOrder(updated)

            // Add earnings to driver (e.g., ₹50 flat payment per completed drop)
            val updatedPartner = partner.copy(
                lifetimeDeliveries = partner.lifetimeDeliveries + 1,
                earningsToday = partner.earningsToday + 50.0
            )
            repository.updatePartner(updatedPartner)

            showToast("OTP Verified! Order marked DELIVERED. +₹50 added to balance.")
        }
    }

    // --- Admin Control Actions ---
    fun adminDeleteProduct(id: Int) {
        viewModelScope.launch {
            repository.deleteProduct(id)
            showToast("Product deleted successfully")
        }
    }

    fun adminSaveProduct(
        id: Int,
        nameEn: String, nameMr: String, nameHi: String,
        category: String,
        weightOptions: String,
        basePrice: Double,
        stockLevel: Int,
        imageUrl: String,
        isRec: Boolean,
        disc: Int,
        descEn: String, descMr: String, descHi: String
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                id = if (id == 0) 0 else id,
                nameEn = nameEn, nameMr = nameMr, nameHi = nameHi,
                category = category,
                weightOptions = weightOptions,
                basePrice = basePrice,
                stockLevel = stockLevel,
                imageUrl = imageUrl,
                isRecommended = isRec,
                discountPercent = disc,
                descriptionEn = descEn, descriptionMr = descMr, descriptionHi = descHi
            )
            if (id == 0) {
                repository.addProduct(product)
                showToast("New Product loaded in catalog!")
            } else {
                repository.updateProduct(product)
                showToast("Catalog updated successfully!")
            }
        }
    }

    fun adminCreateCoupon(code: String, discount: Int, minAmt: Double, desc: String) {
        viewModelScope.launch {
            if (code.isEmpty()) {
                showToast("Code cannot be empty")
                return@launch
            }
            val coupon = CouponEntity(
                code = code.trim().uppercase(),
                discountPercent = discount,
                minOrderAmount = minAmt,
                descriptionEn = desc,
                descriptionMr = desc,
                descriptionHi = desc
            )
            repository.addCoupon(coupon)
            showToast("Coupon ${coupon.code} activated!")
        }
    }

    fun adminDeleteCoupon(code: String) {
        viewModelScope.launch {
            repository.deleteCoupon(code)
            showToast("Coupon deactivated")
        }
    }

    fun adminUpdateOrderStatus(order: OrderEntity, status: String) {
        viewModelScope.launch {
            val updated = order.copy(status = status)
            repository.updateOrder(updated)
            showToast("Order status updated -> $status")
        }
    }

    fun adminAssignPartner(order: OrderEntity, partnerId: Int) {
        viewModelScope.launch {
            val updated = order.copy(assignedPartnerId = partnerId)
            repository.updateOrder(updated)
            showToast("Order assigned to Partner")
        }
    }

    fun adminOnboardRider(name: String, phone: String) {
        viewModelScope.launch {
            repository.addPartner(DeliveryPartnerEntity(name = name, phone = phone, isOnline = true, lifetimeDeliveries = 0, earningsToday = 0.0))
            showToast("Onboarded rider fleet successfully!")
        }
    }
}
