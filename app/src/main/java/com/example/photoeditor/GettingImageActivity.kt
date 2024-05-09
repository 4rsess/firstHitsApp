package com.example.photoeditor


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.photoeditor.databinding.ActivityImageGettingBinding
import java.io.File


class GettingImageActivity : AppCompatActivity() {

    lateinit var binding: ActivityImageGettingBinding


    fun regPermissionStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                var intent: Intent? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                }
                startActivityForResult(intent!!, 1)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val registrRequestPermission = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {
                    if (it == false) {
                        finish()
                    }
                }
                registrRequestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityImageGettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regPermissionStorage()

        File(Pathes.externalResultEdit).mkdirs()//создаём папку для сохранения результов
        File(Pathes.externalCamera).mkdirs()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            !Environment.isExternalStorageManager()
        ) {
            finish()
        }
    }

    var selectedImageUri: Any? = null

    var launchGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data
                selectedImageUri = data?.data

                val intent = Intent(this, MainEditActivity::class.java)

                intent.putExtra("uri", selectedImageUri as Uri?)

                startActivity(intent)
                finish()
            }
        }

    var launchCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // selectedImageUri - уже установлен на файл, в который сложили фотку, вотэтому в result.data - ничего нету..... вроде

                val intent = Intent(this, MainEditActivity::class.java)

                intent.putExtra("uri", selectedImageUri as Uri?)

                startActivity(intent)
                finish()
            }
        }


    private fun cameraChoose() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        val file = File(Pathes.externalCamera, "test.jpg")

        selectedImageUri =
            FileProvider.getUriForFile( //сам не шарю что за штука, но без неё камера не робит
                this,                           //да и вообще всё связанное с External/внешней памятью
                applicationContext.packageName + ".provider",
                file
            )

        i.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri as Uri?)

        launchCamera.launch(i)
    }

    private fun galleryChoose() {
        val i = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        launchGallery.launch(i)
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> galleryChoose()
                1 -> cameraChoose()
            }
        }
        pictureDialog.show()
    }

    fun buttonGetImage(view: View) = showPictureDialog()


}
