package com.example.app_fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.FragmentTransaction

class MovieListFragment : ListFragment() {

    private lateinit var movies: List<Movie>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        movies = listOf(
            Movie("Coco", R.drawable.coco, "Dospívající hudebník cestuje do Říše mrtvých.",
                "https://www.imdb.com/title/tt2380307/", "https://www.youtube.com/watch?v=Rvr68u6k5sI"
            ),
            Movie("Shrek 2", R.drawable.shrek, "Shrek se ožení a musí čelit rodině své nevěsty.",
                "https://www.imdb.com/title/tt0298148/", "https://www.youtube.com/watch?v=V6X5ti4YlG8"
            ),
            Movie("Spider-Man: Into the Spider-Verse", R.drawable.spiderverse,
                "Mladý Miles Morales se stane Spider-Manem v multivesmíru.",
                "https://www.imdb.com/title/tt4633694/", "https://www.youtube.com/watch?v=g4Hbz2jLxvQ"
            )
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val movieTitles = movies.map { it.title }
        listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, movieTitles)
    }

    override fun onListItemClick(l: ListView, v: android.view.View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        val selectedMovie = movies[position]
        val fragment = MovieDetailFragment.newInstance(selectedMovie)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
