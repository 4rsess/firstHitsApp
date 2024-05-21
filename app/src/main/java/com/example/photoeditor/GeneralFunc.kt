package com.example.photoeditor

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

abstract class GeneralFunc {
    companion object{
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

        fun getRealPathFromURI(context: Context, uri: Uri?): String {
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
}