package com.example.myapp_galerie_fotka

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import java.io.IOException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var rotateButton: Button
    private lateinit var grayscaleButton: Button
    private lateinit var selectButton: Button
    private lateinit var saveButton: Button
    private var selectedImageUri: Uri? = null
    private var originalBitmap: Bitmap? = null
    private var currentRotationAngle: Float = 0f // Uchovává aktuální úhel rotace

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        rotateButton = findViewById(R.id.rotateButton)
        grayscaleButton = findViewById(R.id.grayscaleButton)
        selectButton = findViewById(R.id.selectButton)
        saveButton = findViewById(R.id.saveButton)

        // Zkontroluj, jestli má aplikace povolení pro čtení/ukládání obrázků
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }

        // Nastavení funkce pro výběr obrázku
        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Nastavení funkce pro otočení obrázku o 90 stupňů
        rotateButton.setOnClickListener {
            originalBitmap?.let { bitmap ->
                // Aktualizujeme aktuální úhel rotace o 90 stupňů
                currentRotationAngle += 90f
                val rotatedBitmap = rotateImage(bitmap, currentRotationAngle) // Otočení o aktuální úhel
                imageView.setImageBitmap(rotatedBitmap)
            }
        }

        // Nastavení funkce pro aplikování černobílého filtru
        grayscaleButton.setOnClickListener {
            originalBitmap?.let { bitmap ->
                val grayscaleBitmap = applyGrayscaleFilter(bitmap) // Aplikace černobílého filtru
                imageView.setImageBitmap(grayscaleBitmap)
                originalBitmap = grayscaleBitmap // Uložíme filtr na původní bitmapu pro pozdější použití
            }
        }

        // Nastavení funkce pro uložení obrázku
        saveButton.setOnClickListener {
            originalBitmap?.let { bitmap ->
                saveImageToGallery(bitmap) // Uložíme aktuální obrázek
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                try {
                    originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    imageView.setImageBitmap(originalBitmap) // Zobrazí vybraný obrázek
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Funkce pro otočení obrázku
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle) // Otočení o zadaný úhel
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // Funkce pro aplikování černobílého filtru
    private fun applyGrayscaleFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = source.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val gray = (0.3 * red + 0.59 * green + 0.11 * blue).toInt()
                val newPixel = Color.rgb(gray, gray, gray)
                grayscaleBitmap.setPixel(x, y, newPixel)
            }
        }
        return grayscaleBitmap
    }

    // Funkce pro uložení obrázku zpět do galerie
    private fun saveImageToGallery(bitmap: Bitmap) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "modified_image")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "modified_image.jpg")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourAppName") // Cesta pro uložení obrázku do galerie

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            try {
                val outputStream: OutputStream? = contentResolver.openOutputStream(it)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val PICK_IMAGE = 1
    }
}
