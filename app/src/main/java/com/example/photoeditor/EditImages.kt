package com.example.photoeditor

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

abstract class EditImages {
    companion object{
        var imageObjects: ArrayList<EditImage> = arrayListOf()

        fun deleteAllImageObjects(){
            imageObjects.clear()
        }
        fun addImage() {
            imageObjects.add(EditImage(imageObjects.size))
        }

        fun addImage(whatEdit: String) {
            imageObjects.add(EditImage(imageObjects.size, whatEdit))
        }

        fun getLastImage(): EditImage {
            return imageObjects[imageObjects.size - 1]
        }
    }
}