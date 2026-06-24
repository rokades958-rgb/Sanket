package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY category ASC, id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Update
    suspend fun updateProduct(product: ProductEntity)


    // --- Cart Items ---
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :qty WHERE id = :id")
    suspend fun updateCartQty(id: Int, qty: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()


    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY dateEpochMs DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)


    // --- Coupons ---
    @Query("SELECT * FROM coupons")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Query("DELETE FROM coupons WHERE code = :code")
    suspend fun deleteCoupon(code: String)


    // --- User Profile ---
    @Query("SELECT * FROM user_profiles WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>


    // --- Addresses ---
    @Query("SELECT * FROM addresses WHERE customerPhone = :phone")
    fun getAddressesByPhone(phone: String): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddressById(id: Int)


    // --- Delivery Partners ---
    @Query("SELECT * FROM delivery_partners")
    fun getAllPartners(): Flow<List<DeliveryPartnerEntity>>

    @Query("SELECT * FROM delivery_partners WHERE id = :partnerId")
    suspend fun getPartnerById(partnerId: Int): DeliveryPartnerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: DeliveryPartnerEntity)

    @Update
    suspend fun updatePartner(partner: DeliveryPartnerEntity)
}
