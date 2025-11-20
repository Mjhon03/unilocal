package co.edu.eam.unilocal.services

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ImageUploadService(private val context: Context) {

    companion object {
        private const val TAG = "ImgUpload"
        private const val CLOUD_NAME = "dflrtslxs"
        private const val API_KEY = "487582579142941"
        private const val UPLOAD_PRESET = "appMobil"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "▶ Start upload")
        
        try {
            // Step 1: Read file
            Log.d(TAG, "Reading file...")
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open file")

            val bytes = inputStream.readBytes()
            inputStream.close()
            Log.d(TAG, "✓ File read: ${bytes.size} bytes")
            
            if (bytes.isEmpty()) throw Exception("Empty file")

            // Step 2: Build request
            Log.d(TAG, "Building request...")
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", 
                    "img_${System.currentTimeMillis()}.jpg", 
                    bytes.toRequestBody("image/jpeg".toMediaType())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .addFormDataPart("api_key", API_KEY)
                .build()

            // Step 3: Send request
            Log.d(TAG, "Sending to Cloudinary...")
            val url = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            Log.d(TAG, "Response code: ${response.code}")
            
            // Step 4: Process response
            val responseBody = response.body?.string() 
                ?: throw Exception("Empty response")

            if (!response.isSuccessful) {
                Log.e(TAG, "HTTP Error: ${response.code} - $responseBody")
                throw Exception("HTTP ${response.code}")
            }

            // Step 5: Parse JSON
            Log.d(TAG, "Parsing JSON...")
            val json = JSONObject(responseBody)
            if (!json.has("secure_url")) {
                Log.e(TAG, "No URL in response: $responseBody")
                throw Exception("Invalid response")
            }

            val url_foto = json.getString("secure_url")
            Log.d(TAG, "✅ SUCCESS: $url_foto")
            return@withContext url_foto
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: ${e.message}", e)
            throw e
        }
    }
}
