package com.example.domaciukol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Odkazy na elementy z XML
        val todoInput = findViewById<EditText>(R.id.todo_input)
        val addButton = findViewById<Button>(R.id.add_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val todoListLayout = findViewById<LinearLayout>(R.id.todo_list_layout)

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
            } else {
                // Zobrazí chybovou hlášku, pokud je textové pole prázdné
                Toast.makeText(this, "Úkol nesmí být prázdný!", Toast.LENGTH_SHORT).show()
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
        }
    }
}