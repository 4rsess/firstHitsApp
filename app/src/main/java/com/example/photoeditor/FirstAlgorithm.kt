package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.example.photoeditor.databinding.MainPageForFirstAlgorithmBinding
import java.io.IOException
import java.net.URI

class FirstAlgorithm : AppCompatActivity() {
    private lateinit var binding: MainPageForFirstAlgorithmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainPageForFirstAlgorithmBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val filename = imageObjects.getLastImage().filename
        val uri = Uri.parse("$filesDir/$filename")

        Log.d("log", uri.toString())
        binding.image.setImageURI(uri)


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

    fun save(view: View) {
        val bmp: Bitmap = drawableToBitmap(binding.image.drawable)
        imageObjects.addImage("turn")
        Log.d("log", "${imageObjects.imagesObjects.size} ${imageObjects.imagesObjects[0].filename} ${imageObjects.imagesObjects[1].filename}")

        openFileOutput(imageObjects.getLastImage().filename, MODE_PRIVATE).use {
            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                throw IOException("Couldn't save bitmap.")
            } else {
                Log.d("dk", "succes")
            }
        }

        finish()
    }

    //получаем изображение, поворачиваем и устанавливаем обратно в Image
    fun buttonReverse(view: View) {
        val drawable = binding.image.drawable
        val bitmap = drawableToBitmap(drawable)

        val rotatedBitmap = rotateBitmap(bitmap)

        binding.image.setImageBitmap(rotatedBitmap)
    }

    fun exit(view: View) {
        onBackPressed()
    }
}