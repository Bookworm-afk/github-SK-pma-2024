package com.example.vanocniapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EndGameDialogFragment(
    private val casovacSekundy: Int,
    private val callback: (String) -> Unit // Callback pro akci (nová hra nebo zavření)
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Konec hry")
            .setMessage("Dokončili jste hru za $casovacSekundy sekund.")
            .setPositiveButton("Nová hra") { _, _ ->
                callback("new_game") // Spustí novou hru
            }
            .setNegativeButton("Zavřít") { _, _ ->
                callback("exit") // Vrátí na úvodní obrazovku
            }
            .create()
    }
}