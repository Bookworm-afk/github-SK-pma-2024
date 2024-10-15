package com.example.myapp006moreactivities_sk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.myapp006moreactivities_sk.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Použití View Binding
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení TopBaru
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Zadaný úkol"

        // Získání úkolu z MainActivity
        val task = intent.getStringExtra("task")
        binding.textViewTask.text = "Zadaný úkol: $task"

        // Kliknutí na tlačítko pro spuštění třetí aktivity
        binding.buttonNext.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("taskSummary", "Úkol: $task byl úspěšně přidán do seznamu.")
            startActivity(intent)
        }
    }
}
