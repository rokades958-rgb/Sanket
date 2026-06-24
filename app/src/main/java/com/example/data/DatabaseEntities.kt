package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameEn: String,
    val nameMr: String,
    val nameHi: String,
    val category: String, // "Vegetables", "Fruits", "Grocery"
    val weightOptions: String, // Serialized as comma separated "250g,500g,1kg"
    val basePrice: Double, // Price for the first option
    val stockLevel: Int, // Number of items or weight grams
    val imageUrl: String, // Local resource index or online URL
    val isRecommended: Boolean = false,
    val discountPercent: Int = 0,
    val descriptionEn: String,
    val descriptionMr: String,
    val descriptionHi: String,
    val rating: Float = 4.5f,
    val reviewsCount: Int = 12
) {
    fun localizedName(lang: String): String = when (lang) {
        "mr" -> nameMr
        "hi" -> nameHi
        else -> nameEn
    }

    fun localizedDescription(lang: String): String = when (lang) {
        "mr" -> descriptionMr
        "hi" -> descriptionHi
        else -> descriptionEn
    }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val chosenWeight: String, // e.g., "500g"
    val price: Double,
    val quantity: Int
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerContact: String,
    val customerAddress: String,
    val itemsJson: String, // JSON serialization of ordered items
    val subtotal: Double,
    val deliveryCharges: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val paymentMethod: String, // "COD" or "UPI"
    val status: String, // "Pending", "Confirmed", "Out for Delivery", "Delivered", "Cancelled"
    val deliverySlot: String, // Scheduled Delivery Slot
    val otpCode: String, // OTP verification code, e.g. 5249
    val assignedPartnerId: Int = 0,
    val deliveryProofBase64: String? = null,
    val dateEpochMs: Long = System.currentTimeMillis(),
    val areaVillage: String = "",
    val pinCode: String = "",
    val latitude: Double = 18.156,
    val longitude: Double = 74.576,
    val paymentStatus: String = "Pending", // "Pending", "COD Approved", "Paid via UPI", "Screenshot Verified"
    val upiTransactionId: String? = null,
    val paymentProofBase64: String? = null // payment receipt screenshot base64
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerPhone: String,
    val name: String,
    val phone: String,
    val fullAddress: String,
    val areaVillage: String,
    val pinCode: String,
    val latitude: Double = 18.156,
    val longitude: Double = 74.576
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val discountPercent: Int,
    val minOrderAmount: Double,
    val descriptionEn: String,
    val descriptionMr: String,
    val descriptionHi: String
) {
    fun localizedDescription(lang: String): String = when (lang) {
        "mr" -> descriptionMr
        "hi" -> descriptionHi
        else -> descriptionEn
    }
}

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val phone: String,
    val fullName: String,
    val role: String, // "Customer", "Partner", "Admin"
    val address: String = "",
    val selectedLanguage: String = "en",
    val loyaltyCoins: Int = 0
)

@Entity(tableName = "delivery_partners")
data class DeliveryPartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val isOnline: Boolean = true,
    val currentVolume: Int = 0,
    val lifetimeDeliveries: Int = 0,
    val earningsToday: Double = 0.0
)

data class OrderItem(
    val productId: Int,
    val productName: String,
    val chosenWeight: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

class OrderConverters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, OrderItem::class.java)
    private val adapter = moshi.adapter<List<OrderItem>>(listType)

    @TypeConverter
    fun fromOrderItemList(items: List<OrderItem>?): String {
        return items?.let { adapter.toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toOrderItemList(json: String?): List<OrderItem> {
        return json?.let { adapter.fromJson(it) } ?: emptyList()
    }
}
