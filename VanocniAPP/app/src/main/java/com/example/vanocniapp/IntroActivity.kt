package com.example.vanocniapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vanocniapp.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding pro úvodní obrazovku
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení tlačítka "Hrát", které přejde na MainActivity
        binding.buttonPlay.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Zavře IntroActivity, aby se uživatel nemohl vrátit zpět tlačítkem zpět
        }
    }
}
