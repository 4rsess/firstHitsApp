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
import kotlinx.coroutines.*

class SecondAlgorithm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_secondalgorithm)

        val app = application as StorageUriImage
        val uri = app.selectedImageUri

        if (uri != null) {
            val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter2)
            imageView.setImageURI(uri)
        }

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter2)

        findViewById<ImageView>(R.id.negative).setOnClickListener {
            applyFilter(imageView, ::negativeFilter)
        }

        findViewById<ImageView>(R.id.red).setOnClickListener {
            applyFilter(imageView, ::redFilter)
        }

        findViewById<ImageView>(R.id.green).setOnClickListener {
            applyFilter(imageView, ::greenFilter)
        }

        findViewById<ImageView>(R.id.blue).setOnClickListener {
            applyFilter(imageView, ::blueFilter)
        }

        findViewById<ImageView>(R.id.black_and_white).setOnClickListener {
            applyFilter(imageView, ::blackAndWhiteFilter)
        }

        findViewById<TextView>(R.id.back).setOnClickListener {
            startActivity(Intent(this, InstrumentsActivity::class.java))
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
        return bitmap
    }

    private suspend fun negativeFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = 255 - Color.red(color)
            val g = 255 - Color.green(color)
            val b = 255 - Color.blue(color)
            pixels[i] = Color.rgb(r, g, b)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun redFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = Color.red(color)
            pixels[i] = Color.rgb(r, 0, 0)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun greenFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val g = Color.green(color)
            pixels[i] = Color.rgb(0, g, 0)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun blueFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val b = Color.blue(color)
            pixels[i] = Color.rgb(0, 0, b)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun blackAndWhiteFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val bw = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
            pixels[i] = Color.rgb(bw, bw, bw)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private fun applyFilter(imageView: ImageView, filterFunction: suspend (Bitmap) -> Bitmap) {
        val drawable = imageView.drawable
        if (drawable != null) {
            val sourceBitmap = drawableToBitmap(drawable)
            CoroutineScope(Dispatchers.Main).launch {
                val filteredBitmap = filterFunction(sourceBitmap)
                imageView.setImageBitmap(filteredBitmap)
            }
        }
    }
}
