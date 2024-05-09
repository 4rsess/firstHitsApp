package com.example.photoeditor

import android.os.Environment
import java.io.File

object Pathes {
    val externalResultEdit = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ File.separator +"PhotoEditor"
    val externalCamera = Environment.getExternalStorageDirectory().toString()+"/DCIM/Camera"
}