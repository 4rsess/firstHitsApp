package com.example.photoeditorv2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import android.graphics.Color


class SecondAlgorithm : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_secondalgorithm)

        //копирование картинки
        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {

            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter2)
            imageView.setImageURI(uri)
        }

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter2)

        val negativeButton = findViewById<ImageView>(R.id.negative)
        negativeButton.setOnClickListener {
            val drawable = imageView.drawable
            val bitmap = drawableToBitmap(drawable)

            val negativedBitmap = negativeBitmap(bitmap)

            imageView.setImageBitmap(negativedBitmap)
        }

        val redButton = findViewById<ImageView>(R.id.red)
        redButton.setOnClickListener {
            val drawable = imageView.drawable
            val bitmap = drawableToBitmap(drawable)

            val rededBitmap = redBitmap(bitmap)

            imageView.setImageBitmap(rededBitmap)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            val intent = Intent(this, InstrumentsActivity::class.java)
            startActivity(intent)
        }
    }

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

    private fun negativeBitmap(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val negativedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                negativedBitmap.setPixel(x, y, Color.rgb(255 - source.getPixel(x, y).red, 255 - source.getPixel(x, y).green, 255 - source.getPixel(x, y).blue))
            }
        }
        return negativedBitmap
    }

    private fun redBitmap(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val rededBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                rededBitmap.setPixel(x, y, Color.rgb(source.getPixel(x, y).red, 0, 0))
            }
        }
        return rededBitmap
    }
}