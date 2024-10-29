package com.example.app_fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Odkaz na tlačítko na úvodní obrazovce
        val startButton: Button = findViewById(R.id.startButton)

        // Nastavíme tlačítku posluchač, který přepne na seznam filmů
        startButton.setOnClickListener {
            // Skryjeme úvodní obrazovku
            findViewById<View>(R.id.introScreen).visibility = View.GONE

            // Zobrazíme MovieListFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MovieListFragment())
                .commit()
        }
    }
}
