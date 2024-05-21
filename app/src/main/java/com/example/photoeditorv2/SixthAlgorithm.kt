package com.example.photoeditorv2

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class SixthAlgorithm : AppCompatActivity() {
    private lateinit var originalBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_sixthalgorithm)

        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter6)
            imageView.setImageURI(uri)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            val intent = Intent(this, InstrumentsActivity::class.java)
            startActivity(intent)
        }

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter6)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            originalBitmap = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val saveButton = findViewById<ImageView>(R.id.saveBtn)
        saveButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter6)
            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                saveImageToGallery(drawable.bitmap)
            }
        }

    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val resolver = applicationContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { imageUri ->
            try {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                }

                Toast.makeText(this, "Изображение сохранено!", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }
}