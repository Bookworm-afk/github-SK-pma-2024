package com.example.myapp006moreactivities_sk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities_sk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Použití ViewBindingu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Top AppBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Zadat úkol"

        // Akce po kliknutí na tlačítko "Další"
        binding.btnNext.setOnClickListener {
            val taskName = binding.etTaskName.text.toString()
            val taskDeadline = binding.etTaskDeadline.text.toString()

            // Přechod do druhé aktivity s přenesením dat
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("task_name", taskName)
            intent.putExtra("task_deadline", taskDeadline)
            startActivity(intent)
        }
    }
}
