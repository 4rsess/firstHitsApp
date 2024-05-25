package com.example.photoeditorv2

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_fourthalgorithm)


        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter4)
        //копирование картинки
        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {
            imageView.setImageURI(uri)
        }

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            finish()
        }


        findViewById<ImageView>(R.id.cancelBtn).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.battonSaveFromFilter4).setOnClickListener {
            val drawable = imageView.drawable
            saveImageToGallery(drawableToBitmap(drawable))

        }


    }

    override fun onResume() {
        super.onResume()
        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter4)

        runBlocking {
            launch(Dispatchers.Default) {
                val cascadeDir: File = getDir("cascade", MODE_PRIVATE);
                val cascFile = File(cascadeDir, "lbpcascade_frontalface_improved.xml");

                if (!OpenCVLoader.initLocal()) {
                    Log.e("opencv", "OpenCV initialization failed.")
                } else {
                    val _is = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved);
                    val fos = FileOutputStream(cascFile);
                    fos.write(_is.readBytes())
                    _is.close();
                    fos.close();

                    Log.d("opencv", "OpenCV initialization succeeded.")
                }
                val face_cascade = CascadeClassifier(cascFile.absolutePath)
                val faces_rect = MatOfRect()
                val bmp = drawableToBitmap(imageView.drawable)
                val mat = Mat()
                Utils.bitmapToMat(bmp, mat)
                face_cascade.detectMultiScale(mat, faces_rect)

                for (rect in faces_rect.toArray()) {
                    rectangle(
                        mat,
                        Point(rect.x.toDouble(), rect.y.toDouble()),
                        Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                        Scalar(1.0, 40.0, 100.0),
                        5
                    );
                }
                Utils.matToBitmap(mat, bmp)
                imageView.setImageBitmap(bmp)
            }
        }
    }


    fun drawableToBitmap(drawable: Drawable): Bitmap {
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
}