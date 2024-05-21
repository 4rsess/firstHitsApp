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




class MainEditActivity : AppCompatActivity() {
    private lateinit var firstImageUri: Uri

    private lateinit var binding: ActivityMainEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainEditBinding.inflate(layoutInflater)

        setContentView(binding.root)

        EditImages.deleteAllImageObjects()

        firstImageUri = intent.extras!!.get("uri") as Uri
        EditImages.addImage()

        Log.d("getImage", "${EditImages.getLastImage().filename}")

        val bmp = MediaStore.Images.Media.getBitmap(contentResolver, firstImageUri as Uri?)
        binding.image.setImageBitmap(bmp)

        openFileOutput(EditImages.getLastImage().filename, MODE_PRIVATE).use { stream ->
            try {
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                } else {
                    Log.d("get", "succes")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }



    }

    override fun onResume() {
        super.onResume()

        val filename = EditImages.getLastImage().filename
        val uri = Uri.parse("$filesDir/$filename")

        //binding.image.setImageURI(uri)
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
        val bmp = GeneralFunc.drawableToBitmap(binding.image.drawable)

        if (firstImageUri.toString().contains("Camera")) {
            Log.d("camera", "camera")

            var filename: String =""
            for (ind in firstImageUri.toString().length - 1 downTo 0) {
                if (firstImageUri.toString()[ind] == '/') {
                    filename = firstImageUri.toString().substring(ind + 1)
                    break
                }
            }


            FileOutputStream(File(Pathes.externalCamera,filename)).use {
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                    throw IOException("Couldn't save bitmap.")
                } else {
                    Log.d("get", "succes")
                }
            }
        } else {
            val path = GeneralFunc.getRealPathFromURI(this, firstImageUri)

            FileOutputStream(path).use {
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                    throw IOException("Couldn't save bitmap.")
                } else {
                    Log.d("get", "succes")
                }
            }
        }

    }


    @SuppressLint("SimpleDateFormat")
    fun saveAsCopy() {
        val bmp = GeneralFunc.drawableToBitmap(binding.image.drawable)


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





}