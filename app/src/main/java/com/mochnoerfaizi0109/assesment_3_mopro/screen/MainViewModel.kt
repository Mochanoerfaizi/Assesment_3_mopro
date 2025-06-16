package com.mochnoerfaizi0109.assesment_3_mopro.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mochnoerfaizi0109.assesment_3_mopro.model.Buku // Ganti ke Buku
import com.mochnoerfaizi0109.assesment_3_mopro.network.AuthResponse
import com.mochnoerfaizi0109.assesment_3_mopro.network.BukuApi // Ganti ke BukuApi
import com.mochnoerfaizi0109.assesment_3_mopro.network.GoogleLoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

enum class ApiStatus { LOADING, SUCCESS, FAILED }

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Buku>()) // Ganti ke Buku
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loginWithGoogle(googleIdToken: String, onLoginSuccess: (AuthResponse) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = GoogleLoginRequest(token = googleIdToken)
                val response = BukuApi.service.loginWithGoogle(request) // Ganti ke BukuApi
                onLoginSuccess(response)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Google Login Failure: ${e.message}")
                errorMessage.value = "Google Login Error: ${e.message}"
            }
        }
    }

    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = BukuApi.service.getBuku() // Ganti ke getBuku
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("MainViewModel", "Retrieve Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(authToken: String, namaBuku: String, penulisBuku: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $authToken"
                val namaBukuBody = namaBuku.toRequestBody("text/plain".toMediaTypeOrNull())
                val penulisBukuBody = penulisBuku.toRequestBody("text/plain".toMediaTypeOrNull())
                val gambarPart = bitmap.toMultipartBody()

                BukuApi.service.addBuku(bearerToken, namaBukuBody, penulisBukuBody, gambarPart) // Ganti ke addBuku
                retrieveData()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Save Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateData(authToken: String, bukuToUpdate: Buku, newName: String, newPenulis: String, newBitmap: Bitmap?) {
        // Log ini untuk memastikan ID yang benar selalu terkirim
        Log.d("MainViewModel_DEBUG", "Attempting to update book with ID: ${bukuToUpdate.id}")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $authToken"
                val namaBukuBody = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                val penulisBukuBody = newPenulis.toRequestBody("text/plain".toMediaTypeOrNull())
                val gambarPart = newBitmap?.toMultipartBody()

                // Memanggil API. Jika model Buku.kt sudah benar, ini tidak akan error.
                BukuApi.service.updateBuku(bearerToken, bukuToUpdate.id, namaBukuBody, penulisBukuBody, gambarPart)

                // Karena tidak ada error, fungsi ini akan dieksekusi untuk me-refresh UI
                retrieveData()

            } catch (e: Exception) {
                // PERBAIKAN LOGGING: 'e' ditambahkan untuk melihat seluruh detail error
                Log.e("MainViewModel", "Update Failure", e)
                errorMessage.value = "Error updating: ${e.message}"
            }
        }
    }

    fun deleteBuku(authToken: String, bukuId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $authToken"
                val response = BukuApi.service.deleteBuku(bearerToken, bukuId) // Ganti ke deleteBuku
                if (response.isSuccessful) {
                    retrieveData()
                } else {
                    throw Exception("Failed to delete. Code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Delete Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData("gambar", "image.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }

    fun getBukuById(id: Int): Buku? { // Ganti ke getBukuById
        return data.value.find { it.id == id }
    }
}