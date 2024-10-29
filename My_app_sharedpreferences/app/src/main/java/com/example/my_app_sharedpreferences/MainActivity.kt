package com.example.my_app_sharedpreferences

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.my_app_sharedpreferences.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tasks = mutableListOf<Pair<String, String>>() // List obsahující dvojici úkolu a časového razítka
    private val trashedTasks = mutableListOf<Pair<String, String>>() // Koš pro odstraněné úkoly
    private val sharedPrefs by lazy { getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTasks()

        binding.addButton.setOnClickListener {
            val taskText = binding.taskInput.text.toString()
            if (taskText.isNotBlank()) {
                val timestamp = getCurrentTimestamp() // Získání aktuálního časového razítka
                addTask(taskText, timestamp)
                binding.taskInput.text.clear()
            }
        }

        // Přidání showTrashButton
        binding.showTrashButton.setOnClickListener { // Přidáme tlačítko pro zobrazení koše
            showTrashedTasks()
        }
    }

    private fun addTask(task: String, timestamp: String) {
        tasks.add(Pair(task, timestamp))
        saveTasks()
        displayTask(task, timestamp)
    }

    private fun displayTask(task: String, timestamp: String) {
        // TextView pro úkol
        val taskTextView = TextView(this).apply {
            text = task
            textSize = 18f // Velikost písma pro úkol
            setPadding(16, 16, 16, 0) // Padding pro úkol
        }

        // TextView pro časové razítko
        val timestampTextView = TextView(this).apply {
            text = "Čas: $timestamp"
            textSize = 14f // Menší velikost písma pro časové razítko
            setPadding(16, 0, 16, 16) // Padding pro časové razítko
        }

        // Tlačítko pro trvalé odstranění úkolu
        val deleteButton = Button(this).apply {
            text = "Smazat"
            setOnClickListener {
                tasks.remove(Pair(task, timestamp))
                saveTasks()
                trashedTasks.add(Pair(task, timestamp)) // Přidáme úkol do koše
                saveTrashedTasks()

                // Odstranění úkolu z rozhraní
                binding.taskContainer.removeView(taskTextView.parent as View)
            }
        }

        // Layout pro úkol a tlačítkem
        val taskLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL // Změna na vertikální orientaci
            addView(taskTextView) // Přidání textu úkolu
            addView(timestampTextView) // Přidání časového razítka
            addView(deleteButton) // Přidání tlačítka pro odstranění
        }

        binding.taskContainer.addView(taskLayout) // Přidání celého layoutu do kontejneru
    }

    private fun saveTasks() {
        val editor = sharedPrefs.edit()
        val taskSet = tasks.map { "${it.first}|${it.second}" }.toSet() // Uložíme úkoly s timestampem
        editor.putStringSet("tasks", taskSet)
        editor.apply()
    }

    private fun saveTrashedTasks() {
        val editor = sharedPrefs.edit()
        val trashedSet = trashedTasks.map { "${it.first}|${it.second}" }.toSet()
        editor.putStringSet("trashedTasks", trashedSet)
        editor.apply()
    }

    private fun loadTasks() {
        val savedTasks = sharedPrefs.getStringSet("tasks", setOf()) ?: setOf()
        tasks.clear()
        for (taskString in savedTasks) {
            val taskParts = taskString.split("|")
            if (taskParts.size == 2) {
                val task = taskParts[0]
                val timestamp = taskParts[1]
                tasks.add(Pair(task, timestamp))
                displayTask(task, timestamp)
            }
        }
    }

    private fun showTrashedTasks() {
        binding.taskContainer.removeAllViews()
        for (trashedTask in trashedTasks) {
            displayTrashedTask(trashedTask.first, trashedTask.second)
        }
    }

    private fun displayTrashedTask(task: String, timestamp: String) {
        val trashedTaskTextView = TextView(this).apply {
            text = "$task\nČas: $timestamp"
            textSize = 18f
            setPadding(16, 16, 16, 16)
        }

        val restoreButton = Button(this).apply {
            text = "Obnovit"
            setOnClickListener {
                trashedTasks.remove(Pair(task, timestamp))
                saveTrashedTasks()
                addTask(task, timestamp)
                binding.taskContainer.removeView(trashedTaskTextView.parent as View)
            }
        }

        val permanentDeleteButton = Button(this).apply {
            text = "Trvale smazat"
            setOnClickListener {
                trashedTasks.remove(Pair(task, timestamp))
                saveTrashedTasks()
                binding.taskContainer.removeView(trashedTaskTextView.parent as View)
            }
        }

        val trashedTaskLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(trashedTaskTextView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(restoreButton)
            addView(permanentDeleteButton)
        }

        binding.taskContainer.addView(trashedTaskLayout)
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
