package com.example.photoeditorv2

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.math.abs

class SixthAlgorithm : AppCompatActivity() {
    private lateinit var originalBitmap: Bitmap
    private lateinit var brush: Brush
    private lateinit var imageView: ImageView
    private var lastX = 0f
    private var lastY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_sixthalgorithm)

        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        imageView = findViewById(R.id.CopyImageInputFilter6)
        if (uri != null) {
            imageView.setImageURI(uri)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            finish()
        }

        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            originalBitmap = drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val saveButton = findViewById<ImageView>(R.id.saveBtn)
        saveButton.setOnClickListener {
            saveImageToGallery((imageView.drawable as BitmapDrawable).bitmap)
        }

        val sizeSeekBar = findViewById<SeekBar>(R.id.sizeBrushSeekBar)
        val ratioSeekBar = findViewById<SeekBar>(R.id.ratioSeekBar)
        brush = Brush(size = sizeSeekBar.progress, ratio = ratioSeekBar.progress / 100f)

        imageView.setOnTouchListener { v, event ->
            val (x, y) = getBitmapPosition(event.x, event.y)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                    applyRetouch(x, y)
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    if (abs(dx) >= 1 || abs(dy) >= 1) {
                        applyRetouch(x, y)
                        lastX = event.x
                        lastY = event.y
                    }
                }
            }
            true
        }

        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                brush = brush.copy(size = progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        ratioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                brush = brush.copy(ratio = progress / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    private fun getBitmapPosition(x: Float, y: Float): Pair<Int, Int> {
        val imageMatrix = imageView.imageMatrix
        val drawable = imageView.drawable
        if (drawable == null) {
            return Pair(0, 0)
        }
        val values = FloatArray(9)
        imageMatrix.getValues(values)
        val scaleX = values[Matrix.MSCALE_X]
        val scaleY = values[Matrix.MSCALE_Y]
        val transX = values[Matrix.MTRANS_X]
        val transY = values[Matrix.MTRANS_Y]

        val origW = drawable.intrinsicWidth
        val origH = drawable.intrinsicHeight

        val finalX = ((x - transX) / scaleX).toInt()
        val finalY = ((y - transY) / scaleY).toInt()

        return Pair(finalX.coerceIn(0, origW - 1), finalY.coerceIn(0, origH - 1))
    }

    private fun applyRetouch(x: Int, y: Int) {
        val radius = brush.size / 2
        val startX = (x - radius).coerceAtLeast(0)
        val startY = (y - radius).coerceAtLeast(0)
        val endX = (x + radius).coerceAtMost(originalBitmap.width - 1)
        val endY = (y + radius).coerceAtMost(originalBitmap.height - 1)

        val centerX = x
        val centerY = y

        val pixels = IntArray((endX - startX + 1) * (endY - startY + 1))
        originalBitmap.getPixels(
            pixels,
            0,
            endX - startX + 1,
            startX,
            startY,
            endX - startX + 1,
            endY - startY + 1
        )

        val avgColor = calculateAverageColor(pixels)

        for (i in startX..endX) {
            for (j in startY..endY) {
                val distance =
                    kotlin.math.sqrt(((i - centerX) * (i - centerX) + (j - centerY) * (j - centerY)).toDouble())
                if (distance <= radius) {
                    if (i in 0 until originalBitmap.width && j in 0 until originalBitmap.height) {
                        val pixel = originalBitmap.getPixel(i, j)
                        val newColor = blendColors(avgColor, pixel, brush.ratio)
                        originalBitmap.setPixel(i, j, newColor)
                    }
                }
            }
        }

        imageView.setImageBitmap(originalBitmap)
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val r = (Color.red(color1) * (1 - ratio) + Color.red(color2) * ratio).toInt()
        val g = (Color.green(color1) * (1 - ratio) + Color.green(color2) * ratio).toInt()
        val b = (Color.blue(color1) * (1 - ratio) + Color.blue(color2) * ratio).toInt()
        val a = (Color.alpha(color1) * (1 - ratio) + Color.alpha(color2) * ratio).toInt()
        return Color.argb(a, r, g, b)
    }

    private fun calculateAverageColor(pixels: IntArray): Int {
        var R = 0
        var G = 0
        var B = 0
        var count = 0

        for (pixel in pixels) {
            R += Color.red(pixel)
            G += Color.green(pixel)
            B += Color.blue(pixel)
            count++
        }

        val avgR = R / count
        val avgG = G / count
        val avgB = B / count

        return Color.rgb(avgR, avgG, avgB)
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

    data class Brush(val size: Int, val ratio: Float)
}

