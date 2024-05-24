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


class InstrumentsActivity : AppCompatActivity() {

    private lateinit var originalBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity_with_instrumets)

        //копирование картинки
        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {

            val imageView = findViewById<ImageView>(R.id.CopyImageInput)
            imageView.setImageURI(uri)
        }

        //копирование и сохранение оригинального изображения для кнопки сохранения
        val imageView = findViewById<ImageView>(R.id.CopyImageInput)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            originalBitmap = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val saveButton = findViewById<TextView>(R.id.battonSave)
        saveButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.CopyImageInput)
            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                saveImageToGallery(drawable.bitmap)
            }
        }

        val instrumentsText = findViewById<TextView>(R.id.battonInstruments)
        instrumentsText.setOnClickListener {
            val bottomSheetDialog = FilterList()
            bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
        }

        val styleBtn = findViewById<TextView>(R.id.battonStyle)
        styleBtn.setOnClickListener {
            val intent = Intent(this, SecondAlgorithm::class.java)
            startActivity(intent)
        }

        val backToHome = findViewById<TextView>(R.id.backToFirstPage)
        backToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

