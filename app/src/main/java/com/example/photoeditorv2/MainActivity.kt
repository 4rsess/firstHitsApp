package com.example.photoeditorv2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainImageInput: ImageView
    private lateinit var buttonNext: TextView
    private lateinit var imageChooseOpen: TextView
    private lateinit var imageChooseFromGallery: ImageView
    private lateinit var imageChooseFromCamera: ImageView
    private lateinit var icWaitPhoto: ImageView
    private lateinit var infoText: TextView
    private var uri: Uri? = null

    private val cameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            mainImageInput.setImageURI(uri)
            hideViews()
        } else {
            Toast.makeText(this, "Ошибка, изображение не выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) { resultUri ->
        resultUri?.let {
            uri = it
            mainImageInput.setImageURI(uri)
            hideViews()
        } ?: run {
            Toast.makeText(this, "Ошибка, изображение не выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }


        mainImageInput = findViewById(R.id.MainImageInput)
        buttonNext = findViewById(R.id.buttonNext)
        imageChooseOpen = findViewById(R.id.imageButtonView)
        imageChooseFromGallery = findViewById(R.id.tapGallery)
        imageChooseFromCamera = findViewById(R.id.tapCamera)
        icWaitPhoto = findViewById(R.id.waitPhoto)
        infoText = findViewById(R.id.infoText)

        checkPermissions()

        buttonNext.setOnClickListener {
            try {
                if (uri != null) {
                    val app = application as StorageUriImage
                    app.selectedImageUri = uri
                    val intent = Intent(this, InstrumentsActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Выберите изображение", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }

        imageChooseOpen.setOnClickListener {
            galleryImage.launch("image/*")
        }

        imageChooseFromGallery.setOnClickListener {
            galleryImage.launch("image/*")
        }

        imageChooseFromCamera.setOnClickListener {
            uri = createImageUriForCamera()
            cameraImage.launch(uri)
        }
    }
    private fun createImageUriForCamera(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        return FileProvider.getUriForFile(
            this,
            "com.example.photoeditorv2.fileprovider",
            imageFile
        )
    }
    private fun hideViews() {
        imageChooseFromGallery.visibility = View.GONE
        imageChooseFromCamera.visibility = View.GONE
        icWaitPhoto.visibility = View.GONE
        infoText.visibility = View.GONE
    }
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Разрешения предоставлены", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Необходимо разрешение для работы с камерой", Toast.LENGTH_SHORT).show()
            }
        }
    }
}