package com.example.linearlayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var placeEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var deleteButton: Button
    private lateinit var outputTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializace EditText a Buttonů
        nameEditText = findViewById(R.id.Name)
        surnameEditText = findViewById(R.id.SurName)
        placeEditText = findViewById(R.id.etPlace)
        ageEditText = findViewById(R.id.etAge)
        sendButton = findViewById(R.id.btnSend)
        deleteButton = findViewById(R.id.btnDelete)
        outputTextView = findViewById(R.id.outputText)

        // Nastavení onClickListener pro tlačítko Odeslat
        sendButton.setOnClickListener {
            handleSendButton()
        }

        // Nastavení onClickListener pro tlačítko Vymazat
        deleteButton.setOnClickListener {
            handleDeleteButton()
        }
    }

    // Funkce pro zpracování kliknutí na tlačítko Odeslat
    private fun handleSendButton() {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val place = placeEditText.text.toString()
        val age = ageEditText.text.toString()

        // Vytvoření výstupního textu
        val outputText = "Jméno: $name\nPříjmení: $surname\nBydliště: $place\nVěk: $age"

        // Zobrazení výstupního textu v TextView
        outputTextView.text = outputText
    }

    // Funkce pro zpracování kliknutí na tlačítko Vymazat
    private fun handleDeleteButton() {
        // Vymazání textu ve všech EditText
        nameEditText.text.clear()
        surnameEditText.text.clear()
        placeEditText.text.clear()
        ageEditText.text.clear()

        // Vymazání výstupního textu
        outputTextView.text = ""
    }
}