package com.example.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.MainPageForFourthAlgorithmBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils;
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc.rectangle
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FourthAlgorithm : AppCompatActivity() {
    private lateinit var binding:MainPageForFourthAlgorithmBinding
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
                val cascadeDir: File = getDir( "cascade", Context.MODE_PRIVATE);
                val cascFile = File(cascadeDir, "lbpcascade_frontalface_improved.xml");

                if(!OpenCVLoader.initLocal()){
                    Log.e("opencv", "OpenCV initialization failed.")
                } else {
                    val _is = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved);
                    val fos =  FileOutputStream(cascFile);
                    fos.write(_is.readBytes())
                    _is.close();
                    fos.close();

                    Log.d("opencv", "OpenCV initialization succeeded.")
                }
                val face_cascade = CascadeClassifier(cascFile.absolutePath)
                val faces_rect = MatOfRect()
                val bmp = GeneralFunc.drawableToBitmap(binding.image.drawable)
                val mat = Mat()
                Utils.bitmapToMat(bmp,mat)
                face_cascade.detectMultiScale(mat,faces_rect)

                for(rect in faces_rect.toArray()){
                    rectangle(mat,
                        Point(rect.x.toDouble(), rect.y.toDouble()),
                        Point((rect.x+rect.width).toDouble(), (rect.y+rect.height).toDouble()),
                        Scalar(255.0,0.0,255.0)
                    );
                }
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