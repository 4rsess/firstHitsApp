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
import android.graphics.Color
import kotlinx.coroutines.*

class SeventhAlgorithm : AppCompatActivity() {

    private lateinit var originalBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_seventhalgorithm)

        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter7)
            imageView.setImageURI(uri)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            val intent = Intent(this, InstrumentsActivity::class.java)
            startActivity(intent)
        }

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter7)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            originalBitmap = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        findViewById<TextView>(R.id.recognizeBtn).setOnClickListener {
            applyFilter(imageView, ::unsharpMaskingFilter)
        }

        val saveButton = findViewById<ImageView>(R.id.saveBtn)
        saveButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter7)
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

    private suspend fun unsharpMaskingFilter(
        source: Bitmap,
        blurRadius: Int = 2,
        intensity: Double = 1.5,
        threshold: Int = 10
    ): Bitmap = withContext(Dispatchers.Default) {
        val blurredBitmap = blurFilter(source, blurRadius)

        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val blurredPixels = IntArray(width * height)
        blurredBitmap.getPixels(blurredPixels, 0, width, 0, 0, width, height)

        val newPixels = IntArray(pixels.size)

        for (i in pixels.indices) {
            val sourceColor = pixels[i]
            val blurredColor = blurredPixels[i]

            val sourceRed = Color.red(sourceColor)
            val sourceGreen = Color.green(sourceColor)
            val sourceBlue = Color.blue(sourceColor)

            val blurredRed = Color.red(blurredColor)
            val blurredGreen = Color.green(blurredColor)
            val blurredBlue = Color.blue(blurredColor)

            val diffRed = sourceRed - blurredRed
            val diffGreen = sourceGreen - blurredGreen
            val diffBlue = sourceBlue - blurredBlue

            val newRed = if (Math.abs(diffRed) >= threshold) {
                (sourceRed + intensity * diffRed).toInt().coerceIn(0, 255)
            } else {
                sourceRed
            }

            val newGreen = if (Math.abs(diffGreen) >= threshold) {
                (sourceGreen + intensity * diffGreen).toInt().coerceIn(0, 255)
            } else {
                sourceGreen
            }

            val newBlue = if (Math.abs(diffBlue) >= threshold) {
                (sourceBlue + intensity * diffBlue).toInt().coerceIn(0, 255)
            } else {
                sourceBlue
            }

            newPixels[i] = Color.rgb(newRed, newGreen, newBlue)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(newPixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun blurFilter(source: Bitmap, radius: Int): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val newPixels = IntArray(pixels.size)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var sumRed = 0
                var sumGreen = 0
                var sumBlue = 0
                var pixelCount = 0

                for (j in -radius..radius) {
                    for (i in -radius..radius) {
                        val offsetX = x + i
                        val offsetY = y + j

                        if (offsetX in 0 until width && offsetY in 0 until height) {
                            val color = pixels[offsetY * width + offsetX]
                            sumRed += Color.red(color)
                            sumGreen += Color.green(color)
                            sumBlue += Color.blue(color)
                            pixelCount++
                        }
                    }
                }

                val avgRed = sumRed / pixelCount
                val avgGreen = sumGreen / pixelCount
                val avgBlue = sumBlue / pixelCount

                newPixels[y * width + x] = Color.rgb(avgRed, avgGreen, avgBlue)
            }
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(newPixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private fun applyFilter(imageView: ImageView, filterFunction: suspend (Bitmap) -> Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            val filteredBitmap = filterFunction(originalBitmap)
            imageView.setImageBitmap(filteredBitmap)
        }
    }
}