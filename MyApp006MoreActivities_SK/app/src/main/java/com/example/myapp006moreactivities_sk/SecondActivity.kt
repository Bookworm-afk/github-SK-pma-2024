package com.example.myapp006moreactivities_sk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities_sk.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Použití ViewBindingu
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Top AppBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Vyberte kategorii"

        // Získání dat z první aktivity
        val taskName = intent.getStringExtra("task_name")
        val taskDeadline = intent.getStringExtra("task_deadline")

        // Akce po kliknutí na tlačítko "Odeslat"
        binding.btnSubmit.setOnClickListener {
            val selectedCategory = when {
                binding.rbPersonal.isChecked -> "Osobní"
                binding.rbWork.isChecked -> "Pracovní"
                binding.rbSchool.isChecked -> "Školní"
                else -> "Neznámá"
            }

            // Přechod do třetí aktivity s přenesením dat
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("task_name", taskName)
            intent.putExtra("task_deadline", taskDeadline)
            intent.putExtra("task_category", selectedCategory)
            startActivity(intent)
        }
    }
}
