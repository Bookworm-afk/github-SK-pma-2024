package com.example.app_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MovieDetailFragment : Fragment() {

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getParcelable(ARG_MOVIE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_detail, container, false)

        view.findViewById<ImageView>(R.id.moviePoster).setImageResource(movie.posterResource)
        view.findViewById<TextView>(R.id.movieTitle).text = movie.title
        view.findViewById<TextView>(R.id.movieDescription).text = movie.description

        // Tlačítko pro návrat zpět na seznam
        view.findViewById<Button>(R.id.backButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        private const val ARG_MOVIE = "movie"

        fun newInstance(movie: Movie): MovieDetailFragment {
            val fragment = MovieDetailFragment()
            val args = Bundle().apply {
                putParcelable(ARG_MOVIE, movie)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
