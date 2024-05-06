package com.example.photoeditorv2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var MainImageInput: ImageView
    private lateinit var buttonNext: TextView
    private lateinit var imageChoose: TextView
    private lateinit var imageChoose2: View
    private var uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        MainImageInput = findViewById(R.id.MainImageInput)
        buttonNext = findViewById(R.id.buttonNext)
        imageChoose = findViewById(R.id.imageButtonView)
        imageChoose2 = findViewById(R.id.allScreen)


        buttonNext.setOnClickListener{
            try {
                if (uri != null) {
                    val app = application as StorageUriImage
                    app.selectedImageUri = uri
                    val intent = Intent(this, InstrumentsActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Выберите изображение", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }

        imageChoose.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }

        imageChoose2.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
                uri = data.data
                MainImageInput.setImageURI(uri)
            } else {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }
}

