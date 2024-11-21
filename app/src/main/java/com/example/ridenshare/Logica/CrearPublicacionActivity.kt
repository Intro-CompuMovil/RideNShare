package com.example.ridenshare.Logica

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.ridenshare.Data.Data.Companion.MAX_IMAGE_DIMENSION
import com.example.ridenshare.Data.Data.Companion.MY_PERMISSION_REQUEST_CAMERA
import com.example.ridenshare.Data.Data.Companion.MY_PERMISSION_REQUEST_IMAGES
import com.example.ridenshare.Data.Data.Companion.PERMISSION_REQUEST_CODE
import com.example.ridenshare.R
import com.example.ridenshare.databinding.ActivityCrearPublicacionBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import com.example.ridenshare.Data.Publicacion
import com.example.ridenshare.Interfaz.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import java.util.concurrent.TimeUnit

class CrearPublicacionActivity : AppCompatActivity() {
    lateinit var binding: ActivityCrearPublicacionBinding
    private var currentPhotoPath: String = ""
    private var publicPhotoUrl: String = ""
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearPublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.buttonGallery.setOnClickListener{
            openGallery()
        }

        binding.buttonCamera.setOnClickListener{
            openCamera()
        }

        binding.buttonRegistrar.setOnClickListener{
            uploadPhoto(currentPhotoPath)
            createPublicacion()
            startActivity(Intent(this, FeedActivity::class.java))
        }
    }

    fun createPublicacion() {
        // Call the createPublicacion method from the Retrofit service
        var publicacion = Publicacion()

        val currentTimeMillis = System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(currentTimeMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) % 60
        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        publicacion.hora = timeFormatted
        publicacion.imagen = publicPhotoUrl
        publicacion.titulo = binding.editTitulo.text.toString()
        publicacion.descripcion = binding.editDescripcion.text.toString()
        publicacion.fecha = binding.editFecha.text.toString()
        publicacion.userName = auth.currentUser!!.email.toString()

        val call = RetrofitInstance.apiPublicacion.createPublicacion(publicacion)

        // Enqueue the request asynchronously
        call.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val result = response.body()
                    if (result != null) {
                        Log.d("Publicacion", "Publicacion created successfully with ID: $result")
                    }
                } else {
                    // Handle failure response
                    Log.e("Publicacion", "Failed to create Publicacion: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                // Handle error
                Log.e("Publicacion", "Error: ${t.message}")
            }
        })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, MY_PERMISSION_REQUEST_IMAGES)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            takePicture()
        }
    }

    fun uploadPhoto(currentPhotoPath: String?) {
        currentPhotoPath?.let {
            val file = File(it)

            // Ensure the file exists before proceeding
            if (file.exists()) {
                // Log the file name
                Log.d("Upload", file.name)

                // Create new file name with transformations
                val newFileName = file.name
                    .toLowerCase()
                    .replace("\\s+".toRegex(), "_")
                    .replace("[^a-z0-9._-]".toRegex(), "")
                    .replace("_+".toRegex(), "_")

                // Create a request body for the file
                val requestBody = RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    file
                )

                // Create a MultipartBody.Part using the file
                val multipartBody = MultipartBody.Part.createFormData("file", newFileName, requestBody)

                // Call Retrofit service to upload the photo
                val call = RetrofitInstance.apiPhoto.uploadPhoto(multipartBody, "images")

                publicPhotoUrl = "https://storage.googleapis.com/web-service-image-bucket/images/" + newFileName

                // Execute the request asynchronously
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            // Handle success
                            Log.d("Upload", "Photo uploaded successfully!")
                            // Reset file input or handle UI updates here if necessary
                        } else {
                            // Handle failure
                            Log.e("Upload", "Upload failed: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // Handle error
                        Log.e("Upload", "Upload failed: ${t.message}")
                    }
                })
            } else {
                Log.e("Upload", "File does not exist at path: $it")
            }
        }
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, MY_PERMISSION_REQUEST_CAMERA)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePicture()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MY_PERMISSION_REQUEST_IMAGES -> {
                    data?.data?.let { uri ->
                        loadImage(uri)
                    }
                }
                MY_PERMISSION_REQUEST_CAMERA -> {
                    File(currentPhotoPath).let { file ->
                        loadImage(Uri.fromFile(file))
                        galleryAddPic()
                    }
                }
            }
        }
    }

    private fun loadImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val exif = ExifInterface(inputStream!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)

        options.inJustDecodeBounds = false
        options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION)

        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
        val rotatedBitmap = bitmap?.let { rotateBitmap(it, orientation) }

        binding.fotoPlaceholder.setImageBitmap(rotatedBitmap)
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
}