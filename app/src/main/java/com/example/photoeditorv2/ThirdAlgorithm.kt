package com.example.photoeditorv2

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class ThirdAlgorithm : AppCompatActivity() {

    private lateinit var originalBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_thirdalgorithm)

        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter3)
            imageView.setImageURI(uri)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            finish()
        }

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter3)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            originalBitmap = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val saveButton = findViewById<ImageView>(R.id.saveBtn)
        saveButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter3)
            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                saveImageToGallery(drawable.bitmap)
            }
        }

        val scaleFactorInput = findViewById<EditText>(R.id.scaleFactor)
        val scaleFactorBtn = findViewById<TextView>(R.id.scaleBtn)
        scaleFactorBtn.setOnClickListener {
            val scaleFactor = scaleFactorInput.text.toString().toFloatOrNull()
            if (scaleFactor != null) {
                scaleAndCropImage(scaleFactor)
            } else {
                Toast.makeText(this, "Введите корректный коэффициент", Toast.LENGTH_SHORT).show()
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

    private fun scaleAndCropImage(scaleFactor: Float) {
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        //если изображение уменьшается, вмешиваемся в ориг размеры
        //если изображение увеличивается, обрезаем для соответствия ориг размеров
        val croppedBitmap = if (scaleFactor >= 1) {
            //границы для увеличенного изображения
            val left = (scaledBitmap.width - originalWidth) / 2
            val top = (scaledBitmap.height - originalHeight) / 2
            Bitmap.createBitmap(scaledBitmap, left, top, originalWidth, originalHeight)
        } else {
            //границы для уменьшенного изображения
            val paddedBitmap =
                Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(paddedBitmap)
            val left = (originalWidth - newWidth) / 2
            val top = (originalHeight - newHeight) / 2
            canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)
            paddedBitmap
        }

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter3)
        imageView.setImageBitmap(croppedBitmap)
    }

}