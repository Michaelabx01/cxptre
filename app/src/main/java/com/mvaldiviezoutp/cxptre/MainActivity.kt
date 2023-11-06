package com.mvaldiviezoutp.cxptre

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 101
    private val GALLERY_REQUEST_CODE = 102
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 103

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
        if (result != null) {
            val imageView: ImageView = findViewById(R.id.img)
            imageView.setImageBitmap(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openCameraButton: Button = findViewById(R.id.open_camera)
        val openGalleryButton: Button = findViewById(R.id.open_gallery)
        val saveToGalleryButton: Button = findViewById(R.id.save_to_gallery)

        openCameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture.launch(null)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            }
        }

        openGalleryButton.setOnClickListener {
            openGallery()
        }

        saveToGalleryButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveImageToGalleryAndDatabase()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun saveImageToGalleryAndDatabase() {
        val imageView: ImageView = findViewById(R.id.img)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val savedImageUri = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                "MyImage",
                "Image saved from my app"
            )
            if (savedImageUri != null) {
                // Aquí debes registrar la información de la imagen en tu base de datos.
                // Por ejemplo, puedes guardar el URI de la imagen en la base de datos.
                val imageUriString = savedImageUri.toString()
                // Luego, puedes usar tu base de datos para guardar la información adicional, como metadatos, títulos, descripciones, etc.
                // Por ejemplo:
                val imageTitle = "Image Title"
                val imageDescription = "Image Description"
                // Aquí debes insertar los datos en tu base de datos (reemplaza con tu código real).
                // db.insertImage(imageUriString, imageTitle, imageDescription)

                Toast.makeText(this, "Imagen registrada correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGalleryAndDatabase()
            } else {
                Toast.makeText(this, "Permission denied. Cannot save the image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            val imageView: ImageView = findViewById(R.id.img)
            imageView.setImageURI(selectedImage)
        }
    }
}
