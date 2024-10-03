package com.example.ukol2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ukol2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.consoleOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.console1.id -> binding.consoleImageView.setImageResource(R.drawable.playstation)
                binding.console2.id -> binding.consoleImageView.setImageResource(R.drawable.xboxx)
                binding.console3.id -> binding.consoleImageView.setImageResource(R.drawable.nintendo)
            }
        }

        binding.orderButton.setOnClickListener {
            val selectedConsole = when (binding.consoleOptions.checkedRadioButtonId) {
                binding.console1.id -> "PlayStation 5"
                binding.console2.id -> "Xbox Series X"
                binding.console3.id -> "Nintendo Switch"
                else -> ""
            }

            val accessories = mutableListOf<String>()
            if (binding.accessory1.isChecked) accessories.add(binding.accessory1.text.toString())
            if (binding.accessory2.isChecked) accessories.add(binding.accessory2.text.toString())
            if (binding.accessory3.isChecked) accessories.add(binding.accessory3.text.toString())

            // Kontrola, zda je vybrána konzole nebo příslušenství
            if (selectedConsole.isEmpty() && accessories.isEmpty()) {
                Toast.makeText(this, "Musíte si objednat alespoň jednu konzoli nebo příslušenství", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Souhrnobjednavky::class.java).apply {
                    putExtra("console", selectedConsole)
                    putExtra("accessories", accessories.joinToString(", "))
                }
                startActivity(intent)

                // Vyčištění výběru
                binding.consoleOptions.clearCheck()
                binding.accessory1.isChecked = false
                binding.accessory2.isChecked = false
                binding.accessory3.isChecked = false
                binding.consoleImageView.setImageResource(0) // Odstranění obrázku konzole
            }
        }
    }
}