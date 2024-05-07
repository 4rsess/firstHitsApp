package com.example.photoeditorv2

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import java.io.IOException

class FirstAlgorithm : AppCompatActivity(){

    private lateinit var originalBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_firstalgorithm)

        //копирование исходной картинки
        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {

            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter1)
            imageView.setImageURI(uri)
        }

        //копирование и сохранение оригинального изображения для кнопки сохранения
        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter1)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            originalBitmap = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val saveButton = findViewById<ImageView>(R.id.battonSaveFromFilter1)
        saveButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter1)
            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                saveImageToGallery(drawable.bitmap)
            }
        }

        val cancelBtn = findViewById<ImageView>(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            imageView.setImageBitmap(originalBitmap)
        }


        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            val intent = Intent(this, InstrumentsActivity::class.java)
            startActivity(intent)
        }


        //реализация поворота
        val angle = findViewById<EditText>(R.id.scaleAngle)

        val rotateButton = findViewById<TextView>(R.id.rotateBtn)
        rotateButton.setOnClickListener {
            val angleString = angle.text.toString()
            val angle = angleString.toFloatOrNull()

            angle?.let {
                rotateImage(angle)
            } ?: run {
                Toast.makeText(this, "Введите корректный угол", Toast.LENGTH_SHORT).show()
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


    private fun rotateImage(degrees: Float) {
        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter1)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val width = bitmap.width
            val height = bitmap.height

            val radians = Math.toRadians(degrees.toDouble())
            val sin = Math.sin(radians)
            val cos = Math.cos(radians)

            val newWidth = (width * Math.abs(cos) + height * Math.abs(sin)).toInt()
            val newHeight = (width * Math.abs(sin) + height * Math.abs(cos)).toInt()

            val rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.config)

            val cx = width / 2f
            val cy = height / 2f

            val newCx = newWidth / 2f
            val newCy = newHeight / 2f

            val pixels = IntArray(newWidth * newHeight)

            for (y in 0 until newHeight) {
                for (x in 0 until newWidth) {
                    val rotatedX = (cos * (x - newCx) + sin * (y - newCy) + cx).toInt()
                    val rotatedY = (-sin * (x - newCx) + cos * (y - newCy) + cy).toInt()

                    if (rotatedX in 0 until width && rotatedY in 0 until height) {
                        pixels[y * newWidth + x] = bitmap.getPixel(rotatedX, rotatedY)
                    }
                }
            }

            rotatedBitmap.setPixels(pixels, 0, newWidth, 0, 0, newWidth, newHeight)

            imageView.setImageBitmap(rotatedBitmap)
        }
    }

}