package com.example.photoeditorv2

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Canvas

class FirstAlgorithm : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_firstalgorithm)

        //копирование картинки
        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {

            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter1)
            imageView.setImageURI(uri)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            val intent = Intent(this, InstrumentsActivity::class.java)
            startActivity(intent)
        }

        val rotateButton = findViewById<ImageView>(R.id.bottomReverse)
        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter1)

        //получаем изображение, поворачиваем и устанавливаем обратно в ImageView
        rotateButton.setOnClickListener {

            val drawable = imageView.drawable
            val bitmap = drawableToBitmap(drawable)

            val rotatedBitmap = rotateBitmap(bitmap)

            imageView.setImageBitmap(rotatedBitmap)
        }

    }

    //конвертация drawable в bitmap
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
        return bitmap
    }

    private fun rotateBitmap(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val rotatedBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                rotatedBitmap.setPixel(height - y - 1, x, source.getPixel(x, y))
            }
        }
        return rotatedBitmap
    }
}