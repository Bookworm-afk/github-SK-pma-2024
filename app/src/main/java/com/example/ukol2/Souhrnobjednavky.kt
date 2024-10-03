package com.example.ukol2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ukol2.databinding.ActivitySouhrnobjednavkyBinding // Importujte správný binding

class Souhrnobjednavky : AppCompatActivity() {

    private lateinit var binding: ActivitySouhrnobjednavkyBinding // Použijte správný binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializujte správný binding
        binding = ActivitySouhrnobjednavkyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Získejte data z intentu
        val selectedConsole = intent.getStringExtra("console")
        val selectedAccessories = intent.getStringExtra("accessories")

        // Zobrazte souhrn objednávky
        binding.orderSummaryText.text = "Konzole: $selectedConsole\nPříslušenství: $selectedAccessories"
    }
}