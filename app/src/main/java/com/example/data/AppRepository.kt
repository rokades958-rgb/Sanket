package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val appDao: AppDao) {

    // Exposures
    val allProducts: Flow<List<ProductEntity>> = appDao.getAllProducts()
    val cartItems: Flow<List<CartItemEntity>> = appDao.getCartItems()
    val allOrders: Flow<List<OrderEntity>> = appDao.getAllOrders()
    val allCoupons: Flow<List<CouponEntity>> = appDao.getAllCoupons()
    val allPartners: Flow<List<DeliveryPartnerEntity>> = appDao.getAllPartners()
    val allUsers: Flow<List<UserProfileEntity>> = appDao.getAllUserProfiles()

    // Products Management
    suspend fun getProductById(id: Int): ProductEntity? = appDao.getProductById(id)
    suspend fun addProduct(product: ProductEntity) = appDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = appDao.updateProduct(product)
    suspend fun deleteProduct(id: Int) = appDao.deleteProductById(id)

    // Cart Management
    suspend fun addToCart(productId: Int, weight: String, price: Double, qty: Int) {
        val existing = appDao.getCartItems().first().find { it.productId == productId && it.chosenWeight == weight }
        if (existing != null) {
            appDao.updateCartQty(existing.id, existing.quantity + qty)
        } else {
            appDao.insertCartItem(CartItemEntity(productId = productId, chosenWeight = weight, price = price, quantity = qty))
        }
    }
    suspend fun updateCartQuantity(id: Int, quantity: Int) {
        if (quantity <= 0) {
            appDao.deleteCartItem(id)
        } else {
            appDao.updateCartQty(id, quantity)
        }
    }
    suspend fun updateDirectCartItem(cartItem: CartItemEntity) {
        appDao.updateCartItem(cartItem)
    }
    suspend fun removeFromCart(id: Int) = appDao.deleteCartItem(id)
    suspend fun clearCart() = appDao.clearCart()

    // Orders Management
    suspend fun placeOrder(order: OrderEntity): Long = appDao.insertOrder(order)
    suspend fun updateOrder(order: OrderEntity) = appDao.updateOrder(order)
    suspend fun getOrderById(id: Int): OrderEntity? = appDao.getOrderById(id)

    // Coupon Management
    suspend fun addCoupon(coupon: CouponEntity) = appDao.insertCoupon(coupon)
    suspend fun deleteCoupon(code: String) = appDao.deleteCoupon(code)

    // User Profile Management
    suspend fun getUserProfile(phone: String): UserProfileEntity? = appDao.getUserByPhone(phone)
    suspend fun saveUserProfile(profile: UserProfileEntity) = appDao.insertUserProfile(profile)

    // Address Management
    fun getAddressesByPhone(phone: String): Flow<List<AddressEntity>> = appDao.getAddressesByPhone(phone)
    suspend fun saveAddress(address: AddressEntity) = appDao.insertAddress(address)
    suspend fun deleteAddress(id: Int) = appDao.deleteAddressById(id)

    // Delivery Partner Management
    suspend fun addPartner(partner: DeliveryPartnerEntity) = appDao.insertPartner(partner)
    suspend fun updatePartner(partner: DeliveryPartnerEntity) = appDao.updatePartner(partner)
    suspend fun getPartnerById(id: Int): DeliveryPartnerEntity? = appDao.getPartnerById(id)

    // Initialization check (Seeds standard inventory for "SS Fresh Basket")
    suspend fun checkAndSeedDatabase() {
        val count = appDao.getAllProducts().first().size
        if (count == 0) {
            val veg = listOf(
                ProductEntity(
                    nameEn = "Fresh Palak (Spinach)", nameMr = "ताजी मेथी - पालक", nameHi = "ताज़ा पालक",
                    category = "Vegetables", weightOptions = "250g,500g,1kg", basePrice = 20.0, stockLevel = 50,
                    imageUrl = "palak", isRecommended = true, discountPercent = 10,
                    descriptionEn = "Fresh handpicked organic Spinach, high in iron and fiber.",
                    descriptionMr = "ताजी निवडलेली सेंद्रिय मेथी आणि पालक, लोह आणि फायबर समृद्ध.",
                    descriptionHi = "ताजा चुना हुआ जैविक पालक, आयरन और फाइबर से भरपूर।"
                ),
                ProductEntity(
                    nameEn = "Desi Tomato (टोमॅटो)", nameMr = "देशी टोमॅटो", nameHi = "टमाटर",
                    category = "Vegetables", weightOptions = "500g,1kg,2kg", basePrice = 30.0, stockLevel = 80,
                    imageUrl = "tomato", isRecommended = true, discountPercent = 20,
                    descriptionEn = "Juicy sun-ripened red tomatoes sourced directly from local Baramati farms.",
                    descriptionMr = "बारामती परिसरातील शेतकऱ्यांकडून थेट आणलेले रसाळ ताजे टोमॅटो.",
                    descriptionHi = "स्थानीय बारामती खेतों से सीधे प्राप्त रसीले धूप में पके टमाटर।"
                ),
                ProductEntity(
                    nameEn = "Fresh Onion (कांदा)", nameMr = "बारामती स्पेशल कांदा", nameHi = "प्याज़",
                    category = "Vegetables", weightOptions = "1kg,2kg,5kg", basePrice = 40.0, stockLevel = 150,
                    imageUrl = "onion", isRecommended = false, discountPercent = 5,
                    descriptionEn = "High-quality sweet and pungent onions, ideal for daily Maharashtrian dishes.",
                    descriptionMr = "उत्कृष्ट दर्जाचे गोड आणि तिखट कांदे, दैनंदिन जेवणासाठी सर्वोत्तम.",
                    descriptionHi = "उच्च गुणवत्ता के मीठे और तीखे प्याज, दैनिक भोजन के लिए उत्तम।"
                ),
                ProductEntity(
                    nameEn = "Organic Potatoes (बटाटा)", nameMr = "सेंद्रिय बटाटे", nameHi = "आलू",
                    category = "Vegetables", weightOptions = "1kg,2kg,5kg", basePrice = 35.0, stockLevel = 120,
                    imageUrl = "potato", isRecommended = true, discountPercent = 0,
                    descriptionEn = "Starchy, premium quality potatoes directly from rich soil.",
                    descriptionMr = "सेंद्रिय आणि पोषक तत्वांनी समृद्ध बटाटे, खाण्यासाठी सर्वोत्तम.",
                    descriptionHi = "स्टार्चयुक्त, समृद्ध मिट्टी से सीधे प्राप्त उत्कृष्ट गुणवत्ता वाले आलू।"
                ),
                ProductEntity(
                    nameEn = "Green Chillies", nameMr = "तिखट हिरवी मिरची", nameHi = "हरी मिर्च",
                    category = "Vegetables", weightOptions = "250g,500g", basePrice = 25.0, stockLevel = 30,
                    imageUrl = "chilli", isRecommended = false, discountPercent = 15,
                    descriptionEn = "Fiery green chillies to add dynamic taste to your traditional upma or amti.",
                    descriptionMr = "आपल्या उपमा किंवा आमटीमध्ये अस्सल गावरान चव आणणारी तिखट मिरची.",
                    descriptionHi = "आपके उपमा या सब्जी में असली तीखा स्वाद जोड़ने वाली हरी मिर्च।"
                ),
                ProductEntity(
                    nameEn = "Coriander / Kothimbir", nameMr = "ताजी कोथिंबीर जुडी", nameHi = "हरा धनिया",
                    category = "Vegetables", weightOptions = "1 Bunch,3 Bunches", basePrice = 15.0, stockLevel = 100,
                    imageUrl = "coriander", isRecommended = true, discountPercent = 0,
                    descriptionEn = "Aromatic, fresh-scented coriander bunches sourced daily.",
                    descriptionMr = "दररोज सकाळी ताजी आणलेली सुवासिक गावरान कोथिंबीर जुडी.",
                    descriptionHi = "रोज सुबह ताजी लाई गई सुगंधित हरी धनिया पत्ती।"
                )
            )
            val fruits = listOf(
                ProductEntity(
                    nameEn = "Alphonso Mango", nameMr = "हापूस आंबा (रत्नागिरी)", nameHi = "अल्फांसो आम",
                    category = "Fruits", weightOptions = "1 Piece,1 Dozen", basePrice = 75.0, stockLevel = 300,
                    imageUrl = "mango", isRecommended = true, discountPercent = 15,
                    descriptionEn = "Sweet, aromatic organic Alphonso mangoes, the king of fruits.",
                    descriptionMr = "अत्यंत गोड आणि सुवासिक रत्नागिरी हापूस आंबा, फळांचा राजा.",
                    descriptionHi = "मीठा और सुगंधित रत्नागिरी हापुस आम, फलों का राजा।"
                ),
                ProductEntity(
                    nameEn = "Golden Bananas", nameMr = "कडधान्य केळी", nameHi = "मीठा केला",
                    category = "Fruits", weightOptions = "6 Pieces,1 Dozen", basePrice = 45.0, stockLevel = 200,
                    imageUrl = "banana", isRecommended = true, discountPercent = 5,
                    descriptionEn = "Energy-packed sweet bananas, sourced freshly from nearby Purandar/Baramati orchards.",
                    descriptionMr = "ऊर्जेने समृद्ध आणि गोड केळी, पुरंदर परिसरातील बागांमधून थेट प्राप्त.",
                    descriptionHi = "ऊर्जा से भरपूर मीठे पके केले, पास के बगीचों से सीधे लाए गए।"
                ),
                ProductEntity(
                    nameEn = "Premium Red Apple", nameMr = "काश्मिरी सफरचंद", nameHi = "कश्मीरी सेब",
                    category = "Fruits", weightOptions = "500g,1kg", basePrice = 90.0, stockLevel = 60,
                    imageUrl = "apple", isRecommended = false, discountPercent = 10,
                    descriptionEn = "Crisp, sweet, and highly nutritious apples from Kashmiri orchards.",
                    descriptionMr = "काश्मीरमधून थेट आणलेली अत्यंत ताजी, गोड आणि कुरकुरीत सफरचंदे.",
                    descriptionHi = "कश्मीरी बागानों से सीधे ताजे, मीठे और कुरकुरे सेब।"
                )
            )
            val grocery = listOf(
                ProductEntity(
                    nameEn = "Organic Sharbati Atta", nameMr = "सेंद्रिय गोड गहू पीठ", nameHi = "शरबती गेहूं आटा",
                    category = "Grocery", weightOptions = "1kg,5kg,10kg", basePrice = 55.0, stockLevel = 100,
                    imageUrl = "atta", isRecommended = true, discountPercent = 8,
                    descriptionEn = "Pure premium wheat flour for softest, swelling Maharashtrian chapatis.",
                    descriptionMr = "चवदार आणि मऊ चपातीसाठी सर्वोत्तम निवडीचे सेंद्रिय गहू पीठ.",
                    descriptionHi = "नरम और फूली हुई रोटियों के लिए शुद्ध शरबती गेहूं का आटा।"
                ),
                ProductEntity(
                    nameEn = "Premium Basmati Rice", nameMr = "बासमती तांदूळ", nameHi = "बासमती चावल",
                    category = "Grocery", weightOptions = "1kg,5kg", basePrice = 110.0, stockLevel = 120,
                    imageUrl = "rice", isRecommended = false, discountPercent = 12,
                    descriptionEn = "Extra-long grain aromatic rice ideal for special biryanis and pulao.",
                    descriptionMr = "सुवासिक लांब दाण्याचा बासमती तांदूळ, पुलाव आणि बिर्याणीसाठी उत्तम.",
                    descriptionHi = "विशेष पुलाव और बिरयानी के लिए अतिरिक्त लंबे दानेदार सुगंधित चावल।"
                ),
                ProductEntity(
                    nameEn = "Pure Groundnut Oil", nameMr = "शुद्ध शेंगदाणा तेल", nameHi = "मूंगफली का तेल",
                    category = "Grocery", weightOptions = "1L,5L", basePrice = 185.0, stockLevel = 45,
                    imageUrl = "oil", isRecommended = true, discountPercent = 5,
                    descriptionEn = "Pure filtered groundnut oil for healthy traditional cooking.",
                    descriptionMr = "आरोग्यदायी स्वयंपाकासाठी शुद्ध गाळलेले घाण्याचे शेंगदाणे तेल.",
                    descriptionHi = "स्वास्थ्यवर्धक पारंपरिक खाना पकाने के लिए शुद्ध मूंगफली का तेल।"
                )
            )

            // Insert all products
            appDao.insertProducts(veg + fruits + grocery)

            // Seed default coupons
            appDao.insertCoupon(CouponEntity("FRESH50", 50, 199.0, "Get 50% off on your fresh groceries! Min order ₹199", "५०% सवलत मिळवा! किमान ऑर्डर ₹१९९", "५०% की छूट पाएं! न्यूनतम ऑर्डर ₹१९९"))
            appDao.insertCoupon(CouponEntity("MIDC10", 10, 150.0, "Flat 10% off for Baramati MIDC deliverable points", "बारामती एमआयडीसी रहिवाशांसाठी १०% सूट", "बारामती एमआईडीसी निवासियों के लिए १०% की छूट"))
            appDao.insertCoupon(CouponEntity("WELCOME50", 25, 100.0, "Welcome gift: 25% off on orders above ₹100", "पहिली ऑर्डर भेट: २५% सूट (किमान ₹१००)", "स्वागत उपहार: २५% की छूट (न्यूनतम ₹१००)"))

            // Seed default delivery partners
            appDao.insertPartner(DeliveryPartnerEntity(name = "Ketan Kulkarni", phone = "9876543210", isOnline = true, lifetimeDeliveries = 142, earningsToday = 650.0))
            appDao.insertPartner(DeliveryPartnerEntity(name = "Sujit Pawar", phone = "9112233445", isOnline = true, lifetimeDeliveries = 89, earningsToday = 480.0))
            appDao.insertPartner(DeliveryPartnerEntity(name = "Vaibhav Mane", phone = "8888777766", isOnline = true, lifetimeDeliveries = 195, earningsToday = 920.0))

            // Seed default customer profile to make login and guest browsing super quick
            appDao.insertUserProfile(UserProfileEntity(phone = "9999999999", fullName = "Guest User", role = "Customer", address = "Sector 3, Baramati MIDC", selectedLanguage = "en", loyaltyCoins = 250))
            appDao.insertUserProfile(UserProfileEntity(phone = "8888888888", fullName = "Dev Admin", role = "Admin", address = "Main HQ, Baramati MIDC", selectedLanguage = "en", loyaltyCoins = 9999))
            appDao.insertUserProfile(UserProfileEntity(phone = "7777777777", fullName = "Amit Patel", role = "Partner", address = "Delivery Hub 1, Baramati", selectedLanguage = "en", loyaltyCoins = 0))
        }
    }
}
