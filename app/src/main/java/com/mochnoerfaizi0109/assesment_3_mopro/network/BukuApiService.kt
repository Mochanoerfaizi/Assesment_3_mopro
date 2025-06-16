package com.mochnoerfaizi0109.assesment_3_mopro.network

import com.mochnoerfaizi0109.assesment_3_mopro.model.Buku
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://api-buku-production-127f.up.railway.app/"

data class GoogleLoginRequest(
    val token: String
)

data class AuthResponse(
    val id: Int,
    val email: String,
    val accessToken: String
)
// ---------------------------------------------

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BukuApiService {
    // Auth
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): AuthResponse


    @GET("api/buku")
    suspend fun getBuku(): List<Buku>


    @Multipart
    @POST("api/buku")
    suspend fun addBuku(
        @Header("Authorization") token: String,
        @Part("nama_buku") namaBuku: RequestBody,
        @Part("penulis_buku") penulisBuku: RequestBody,
        @Part gambar: MultipartBody.Part
    ): Buku


    @Multipart
    @PUT("api/buku/{id}")
    suspend fun updateBuku(
        @Header("Authorization") token: String,
        @Path("id") bukuId: Int,
        @Part("nama_buku") namaBuku: RequestBody,
        @Part("penulis_buku") penulisBuku: RequestBody,
        @Part gambar: MultipartBody.Part?
    ): Buku


    @DELETE("api/buku/{id}")
    suspend fun deleteBuku(
        @Header("Authorization") token: String,
        @Path("id") bukuId: Int
    ): Response<Unit>
}

object BukuApi {
    val service: BukuApiService by lazy {
        retrofit.create(BukuApiService::class.java)
    }
}