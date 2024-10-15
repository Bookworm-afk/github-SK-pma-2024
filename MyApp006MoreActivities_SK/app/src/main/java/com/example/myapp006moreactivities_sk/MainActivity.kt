package com.example.myapp006moreactivities_sk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities_sk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Použití View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení TopBaru
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "To-Do List"

        // Kliknutí na tlačítko pro odeslání úkolu do druhé aktivity
        binding.buttonSend.setOnClickListener {
            val task = binding.editTextTask.text.toString()
            if (task.isNotEmpty()) {
                val intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
            }
        }
    }
}
