package com.example.photoeditor

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.photoeditor.databinding.MainPageForFourthAlgorithmBinding
import org.opencv.android.Utils;
import org.opencv.core.Mat
import java.io.IOException

class FourthAlgorithm : AppCompatActivity() {
    private lateinit var binding:MainPageForFourthAlgorithmBinding

    companion object {
        // Used to load the 'photoeditor' library on application startup.
        init {
            System.loadLibrary("photoeditor")
        }
    }
    external fun findFaces(matAddr:Long)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageForFourthAlgorithmBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val filename = EditImages.getLastImage().filename
        val uri = Uri.parse("$filesDir/$filename")
        binding.image.setImageURI(uri)


    }

    override fun onResume()  {
        super.onResume()
        runBlocking {
            launch(Dispatchers.Default){
                val bmp = GeneralFunc.drawableToBitmap(binding.image.drawable)
                val mat = Mat()
                Utils.bitmapToMat(bmp,mat)
                findFaces(mat.nativeObjAddr)
                Utils.matToBitmap(mat,bmp)
                binding.image.setImageBitmap(bmp)
            }
        }
    }


    fun save(view: View) {
        val bmp: Bitmap = GeneralFunc.drawableToBitmap(binding.image.drawable)
        EditImages.addImage(EditTags.findFaces)

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