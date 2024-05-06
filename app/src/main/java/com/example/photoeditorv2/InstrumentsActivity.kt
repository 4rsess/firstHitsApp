package com.example.photoeditorv2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class InstrumentsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity_with_instrumets)

        //копирование картинки
        val app = application as StorageUriImage
        val uri = app.selectedImageUri
        if (uri != null) {

            val imageView = findViewById<ImageView>(R.id.CopyImageInput)
            imageView.setImageURI(uri)
        }

        val instrumentsText = findViewById<TextView>(R.id.battonInstruments)
        instrumentsText.setOnClickListener {
            val bottomSheetDialog = FilterList()
            bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
        }

        val backToHome = findViewById<TextView>(R.id.backToFirstPage)
        backToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}

