package com.example.photoeditor

import android.content.Context
import android.content.ContextWrapper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class EditImage {
    var id: Int
    var whatEdit: String = ""
    var filename: String = "temp"

    constructor(_id: Int) {
        id = _id
        filename = filename + id.toString() + ".jpg"
    }

    constructor(_id: Int, _whatEdit: String) {
        id = _id
        filename = filename + id.toString() + ".jpg"
        whatEdit = _whatEdit
    }

}

class EditImages() {

    var imagesObjects: ArrayList<EditImage> = arrayListOf()

    fun addImage() {
        imagesObjects.add(EditImage(imagesObjects.size))
    }

    fun addImage(whatEdit: String) {
        imagesObjects.add(EditImage(imagesObjects.size, whatEdit))
    }

    fun getLastImage(): EditImage {
        return imagesObjects[imagesObjects.size - 1]
    }


}