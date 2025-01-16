package com.example.vanocniapp

data class karta(
    val idObrazku: Int,          // ID obrázku (např. R.drawable.darek1)
    var jeOtocena: Boolean = false,  // Je karta otočená lícem nahoru?
    var jeSparovana: Boolean = false // Je karta již spárovaná?
)