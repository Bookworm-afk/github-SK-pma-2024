package com.example.my_app_datastore

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.my_app_datastore.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "ToDoPrefs")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tasks = mutableListOf<Pair<String, String>>() // List obsahující dvojici úkolu a časového razítka
    private val trashedTasks = mutableListOf<Pair<String, String>>() // Koš pro odstraněné úkoly
    private val taskKey = stringSetPreferencesKey("tasks")
    private val trashedKey = stringSetPreferencesKey("trashedTasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTasks()

        binding.addButton.setOnClickListener {
            val taskText = binding.taskInput.text.toString()
            if (taskText.isNotBlank()) {
                val timestamp = getCurrentTimestamp() // Získání aktuálního časového razítka
                addNewTask(taskText, timestamp) // Použijeme specifickou funkci pro nový úkol
                binding.taskInput.text.clear()
            }
        }

        // Přidání showTrashButton
        binding.showTrashButton.setOnClickListener {
            showTrashedTasks()
        }
    }

    private fun addNewTask(task: String, timestamp: String) {
        // Funkce pro přidání pouze nového úkolu
        tasks.add(Pair(task, timestamp))
        saveTasks()
        displayTask(task, timestamp)
    }

    private fun displayTask(task: String, timestamp: String) {
        // TextView pro úkol
        val taskTextView = TextView(this).apply {
            text = task
            textSize = 18f
            setPadding(16, 16, 16, 0)
        }

        // TextView pro časové razítko
        val timestampTextView = TextView(this).apply {
            text = "Čas: $timestamp"
            textSize = 14f
            setPadding(16, 0, 16, 16)
        }

        // Tlačítko pro trvalé odstranění úkolu
        val deleteButton = Button(this).apply {
            text = "Smazat"
            setOnClickListener {
                tasks.remove(Pair(task, timestamp))
                saveTasks()
                trashedTasks.add(Pair(task, timestamp))
                saveTrashedTasks()
                binding.taskContainer.removeView(taskTextView.parent as View)
            }
        }

        // Layout pro úkol a tlačítkem
        val taskLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(taskTextView)
            addView(timestampTextView)
            addView(deleteButton)
        }

        binding.taskContainer.addView(taskLayout)
    }

    private fun saveTasks() {
        // Uložíme seznam úkolů do DataStore
        val taskSet = tasks.map { "${it.first}|${it.second}" }.toSet()
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[taskKey] = taskSet
            }
        }
    }

    private fun saveTrashedTasks() {
        val trashedSet = trashedTasks.map { "${it.first}|${it.second}" }.toSet()
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[trashedKey] = trashedSet
            }
        }
    }

    private fun loadTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect { preferences ->
                val savedTasks = preferences[taskKey] ?: emptySet()
                tasks.clear()
                runOnUiThread {
                    binding.taskContainer.removeAllViews() // Smažeme staré položky, aby se neopakovaly
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
                addNewTask(task, timestamp)
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
