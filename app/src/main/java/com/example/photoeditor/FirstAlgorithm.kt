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

        val filename = EditImages.getLastImage().filename
        val uri = Uri.parse("$filesDir/$filename")
        binding.image.setImageURI(uri)


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



    //получаем изображение, поворачиваем и устанавливаем обратно в Image
    fun buttonReverse(view: View) {
        val rotatedBitmap = rotateBitmap(GeneralFunc.drawableToBitmap(binding.image.drawable))

        binding.image.setImageBitmap(rotatedBitmap)
    }

    fun save(view: View) {
        val bmp: Bitmap = GeneralFunc.drawableToBitmap(binding.image.drawable)
        EditImages.addImage(EditTags.turn)

        openFileOutput(EditImages.getLastImage().filename, MODE_PRIVATE).use {
            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                throw IOException("Couldn't save bitmap.")
            } else {
                Log.d("save", "succes")
            }
        }

        finish()
    }
    fun exit(view: View) {
        onBackPressed()
    }
}