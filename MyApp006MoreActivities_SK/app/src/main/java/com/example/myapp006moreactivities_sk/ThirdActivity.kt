package com.example.myapp006moreactivities_sk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities_sk.databinding.ThirdactivityBinding

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ThirdactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Použití View Binding
        binding = ThirdactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení TopBaru
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Potvrzení úkolu"

        // Získání souhrnu úkolu z SecondActivity
        val taskSummary = intent.getStringExtra("taskSummary")
        binding.textViewSummary.text = taskSummary
    }
}
