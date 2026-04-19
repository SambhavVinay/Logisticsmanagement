package com.example.madecie3.api

import retrofit2.Response
import retrofit2.http.*

// ── Data Models ──────────────────────────────────────────────────────────────

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)

data class CartProduct(
    val productId: Int,
    val quantity: Int
)

data class Cart(
    val id: Int,
    val userId: Int,
    val products: List<CartProduct>
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

// ── API Interface ─────────────────────────────────────────────────────────────

interface FakeStoreApi {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Products
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>

    // Carts
    @GET("carts")
    suspend fun getCarts(): Response<List<Cart>>

    @GET("carts/{id}")
    suspend fun getCart(@Path("id") id: Int): Response<Cart>

    // Users
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @POST("users")
    suspend fun createUser(@Body user: Map<String, String>): Response<User>
}
