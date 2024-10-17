package com.example.toas_snackbar_cv5

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Odkazy na elementy z XML
        val todoInput = findViewById<EditText>(R.id.todo_input)
        val addButton = findViewById<Button>(R.id.add_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val todoListLayout = findViewById<LinearLayout>(R.id.todo_list_layout)
        val snackbarButton = findViewById<Button>(R.id.snackbar_button)
        val toastButton = findViewById<Button>(R.id.toast_button)

        // Nastavení akce pro tlačítko přidání úkolu
        addButton.setOnClickListener {
            val taskText = todoInput.text.toString()

            if (taskText.isNotEmpty()) {
                // Dynamické vytvoření nového LinearLayout pro úkol
                val taskLayout = LinearLayout(this)
                taskLayout.orientation = LinearLayout.HORIZONTAL

                // Dynamické vytvoření CheckBox pro nový úkol
                val taskCheckBox = CheckBox(this)

                // Dynamické vytvoření TextView pro nový úkol
                val taskTextView = TextView(this)
                taskTextView.text = taskText
                taskTextView.textSize = 18f
                taskTextView.setPadding(0, 16, 0, 16)

                // Přidání CheckBox a TextView do LinearLayout
                taskLayout.addView(taskCheckBox)
                taskLayout.addView(taskTextView)

                // Přidání nového LinearLayout do seznamu
                todoListLayout.addView(taskLayout)

                // Vyčištění textového pole
                todoInput.text.clear()

                // Zobrazení vlastního Toastu s ikonou
                showCustomToast("Úkol přidán: $taskText")

            } else {
                showCustomToast("Úkol nesmí být prázdný!")
            }
        }


        // Nastavení akce pro tlačítko odstranění vybraných úkolů
        deleteButton.setOnClickListener {
            // Procházení seznamu a odstranění zaškrtnutých úkolů z konce seznamu
            for (i in todoListLayout.childCount - 1 downTo 0) {
                val taskLayout = todoListLayout.getChildAt(i) as LinearLayout
                val taskCheckBox = taskLayout.getChildAt(0) as CheckBox

                if (taskCheckBox.isChecked) {
                    // Pokud je úkol zaškrtnut, odstraní se
                    todoListLayout.removeViewAt(i)
                }
            }

            // Zobrazení Snackbaru po odstranění úkolu
            Snackbar.make(findViewById(android.R.id.content), "Úkoly byly odstraněny", Snackbar.LENGTH_LONG)
                .setAction("Zpět") {
                    Toast.makeText(this, "Akce byla vrácena!", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        // Nastavení tlačítka pro Toast
        toastButton.setOnClickListener {
            showCustomToast("Ukaž toast")
        }

        // Nastavení tlačítka pro Snackbar
        snackbarButton.setOnClickListener {
            showSnackbar()
        }
    }
    // vlastní toast message jinak bych volal pouze toast.maketext()
    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout))

        // Nastavení textu pro Toast
        val toastText: TextView = layout.findViewById(R.id.toastText)
        toastText.text = message

        // Změna ikony
        val toastIcon: ImageView = layout.findViewById(R.id.toastIcon)
        toastIcon.setImageResource(R.drawable.toast) // Použijte vlastní ikonu zde

        // Vytvoření a zobrazení Toastu s vlastním layoutem
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

    private fun showSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Toto je Snackbar", Snackbar.LENGTH_LONG)
            .setAction("Zpět") {
            }
            .show()
    }
}

