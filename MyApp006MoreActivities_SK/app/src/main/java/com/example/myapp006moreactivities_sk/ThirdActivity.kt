package com.example.myapp006moreactivities_sk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities_sk.databinding.ThirdactivityBinding

class ThirdActivity : AppCompatActivity() {
    private lateinit var binding: ThirdactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Použití ViewBindingu
        binding = ThirdactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Top AppBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Přehled úkolu"

        // Získání dat z druhé aktivity
        val taskName = intent.getStringExtra("task_name")
        val taskDeadline = intent.getStringExtra("task_deadline")
        val taskCategory = intent.getStringExtra("task_category")

        // Nastavení dat do TextView
        binding.tvTaskSummary.text = "Úkol: $taskName\nTermín: $taskDeadline\nKategorie: $taskCategory"
    }
}