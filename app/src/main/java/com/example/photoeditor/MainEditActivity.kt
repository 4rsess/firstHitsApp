package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityMainEditBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


var imageObjects = EditImages()

class MainEditActivity : AppCompatActivity() {
    private lateinit var firstImage: Uri

    private lateinit var binding: ActivityMainEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainEditBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firstImage = intent.extras!!.get("uri") as Uri
        Log.d("uri", firstImage.toString())
        Log.d("uri", firstImage.path.toString())
        imageObjects.addImage()

        Log.d("log", "${imageObjects.getLastImage()}")

        val bmp = MediaStore.Images.Media.getBitmap(contentResolver, firstImage as Uri?)
        Log.d("log", "${bmp}")

        openFileOutput(imageObjects.getLastImage().filename, MODE_PRIVATE).use { stream ->
            try {
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                } else {
                    Log.d("dk", "succes")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    override fun onResume() {
        super.onResume()

        val filename = imageObjects.getLastImage().filename
        val uri = Uri.parse("$filesDir/$filename")

        binding.image1.setImageURI(uri)
    }

    fun buttonSave(view: View) = showPictureDialog()

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Save as copy", "Save as original")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> saveAsCopy()
                1 -> saveAsOriginal()
            }
        }
        pictureDialog.show()
    }

    fun saveAsOriginal() {
        val bmp = drawableToBitmap(binding.image1.drawable)

        val path = getRealPathFromURI_API19(this,firstImage)
        Log.d("path",path)
        FileOutputStream(path).use {
            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                throw IOException("Couldn't save bitmap.")
            } else {
                Log.d("dk", "succes")
            }
        }
    }



    @SuppressLint("SimpleDateFormat")
    fun saveAsCopy() {
        val bmp = drawableToBitmap(binding.image1.drawable)


        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val resultFilename = "image_$timeStamp.jpg"

        val resultFile = File(Pathes.externalResultEdit + File.separator + resultFilename)

        FileOutputStream(resultFile).use {
            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                throw IOException("Couldn't save bitmap.")
            } else {
                Log.d("dk", "succes")
            }
        }

    }

    fun buttonInstruments(view: View) {
        val bottomSheetDialog = FilterList()
        bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
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

    fun getRealPathFromURI_API19(context: Context, uri: Uri?): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor: Cursor? = context.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )
        val columnIndex = cursor?.getColumnIndex(column[0])
        if (cursor?.moveToFirst() == true) {
            filePath = cursor.getString(columnIndex!!)
        }
        cursor?.close()
        return filePath
    }

}