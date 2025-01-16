package com.example.vanocniapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.GridLayout
import com.example.vanocniapp.databinding.ActivityMainBinding
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // View Binding
    private lateinit var seznamKaret: List<karta>     // Seznam všech karet
    private var vybraneKarty = mutableListOf<karta>() // Karty, které hráč otočil
    private var casovacHandler = Handler(Looper.getMainLooper())
    private var casovacSekundy = 0
    private var hraBezi = false
    private var jeBlokovano = false // Blokování dalších kliknutí

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializace View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializace hry
        inicializovatHru()
    }

    private fun inicializovatHru() {
        // Vánoční obrázky (každý obrázek má dvojici)
        hraBezi = true
        casovacSekundy = 0 // Reset časovače
        jeBlokovano = false // Odblokování hry

        spustitCasovac()

        val obrazky = listOf(
            R.drawable.darek1,
            R.drawable.kalendar1,
            R.drawable.kapr1,
            R.drawable.kometa1,
            R.drawable.prase1,
            R.drawable.strom1,
            R.drawable.svicka1,
            R.drawable.zvon1
        )

        // Vytvoření seznamu karet (dvojice každého obrázku) a zamíchání
        seznamKaret = (obrazky + obrazky).shuffled().map { karta(it) }

        // Vyčištění GridLayout a přidání tlačítek pro každou kartu
        binding.gridLayout.removeAllViews()
        for ((index, karta) in seznamKaret.withIndex()) {
            val tlacitko = Button(this).apply {
                layoutParams = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                    width = 200
                    height = 200
                    marginEnd = 8
                    bottomMargin = 8
                }
                setBackgroundResource(R.drawable.card_back) // Zadní strana karty
            }

            tlacitko.setOnClickListener {
                otocitKartu(karta, tlacitko)
            }

            binding.gridLayout.addView(tlacitko)
        }
    }
    private fun spustitCasovac() {
        casovacHandler.postDelayed(object : Runnable {
            override fun run() {
                if (hraBezi) {
                    casovacSekundy++
                    binding.timerTextView.text = "Čas: $casovacSekundy s"
                    casovacHandler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }
    private fun otocitKartu(karta: karta, tlacitko: Button) {
        if (jeBlokovano || karta.jeOtocena || karta.jeSparovana) return

        karta.jeOtocena = true
        tlacitko.setBackgroundResource(karta.idObrazku) // Zobrazíme obrázek

        vybraneKarty.add(karta)

        if (vybraneKarty.size == 2) {
            jeBlokovano = true // Blokujeme další kliknutí
            kontrolovatSparovani()
        }
    }

    private fun kontrolovatSparovani() {
        val (prvni, druha) = vybraneKarty

        if (prvni.idObrazku == druha.idObrazku) {
            prvni.jeSparovana = true
            druha.jeSparovana = true

            if (seznamKaret.all { it.jeSparovana }) {
                hraBezi = false // Zastavení časovače
                zobrazitDialog()
            }
            jeBlokovano = false // Odblokujeme hru po úspěšném spárování
        } else {
            binding.gridLayout.postDelayed({
                prvni.jeOtocena = false
                druha.jeOtocena = false

                obnovitTlacitka()
                jeBlokovano = false // Odblokujeme hru po neúspěšném spárování
            }, 1000)
        }

        vybraneKarty.clear()
    }



        private fun zobrazitDialog() {
            val dialogFragment = EndGameDialogFragment(casovacSekundy) { action ->
                if (action == "new_game") {
                    inicializovatHru() // Spustí novou hru
                } else if (action == "exit") {
                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent) // Vrátí uživatele na úvodní obrazovku
                    finish()
                }
            }
            dialogFragment.show(supportFragmentManager, "EndGameDialog")
        }

    private fun obnovitTlacitka() {
        for ((index, karta) in seznamKaret.withIndex()) {
            val tlacitko = binding.gridLayout.getChildAt(index) as Button

            if (karta.jeSparovana) {
                tlacitko.setBackgroundResource(karta.idObrazku)
                tlacitko.isEnabled = false // Spárované karty nelze kliknout
            } else if (karta.jeOtocena) {
                tlacitko.setBackgroundResource(karta.idObrazku)
            } else {
                tlacitko.setBackgroundResource(R.drawable.card_back)
            }
        }
    }
}