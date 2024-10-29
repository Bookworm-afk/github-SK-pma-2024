package com.example.app_fragments

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    val title: String,
    val posterResource: Int,
    val description: String,
    val imdbUrl: String,    // URL na IMDB
    val trailerUrl: String  // URL na trailer
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(posterResource)
        parcel.writeString(description)
        parcel.writeString(imdbUrl)
        parcel.writeString(trailerUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            val title = parcel.readString() ?: ""
            val posterResource = parcel.readInt()
            val description = parcel.readString() ?: ""
            val imdbUrl = parcel.readString() ?: ""
            val trailerUrl = parcel.readString() ?: ""
            return Movie(title, posterResource, description, imdbUrl, trailerUrl)
        }

        override fun newArray(size: Int): Array<Movie?> = arrayOfNulls(size)
    }
}
