package com.k_antonov.jokes.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.k_antonov.jokes.App
import com.k_antonov.jokes.R

class MainActivity : AppCompatActivity() {

    private lateinit var jokeViewModel: JokeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jokeViewModel = (application as App).jokeViewModel

        val button = findViewById<Button>(R.id.get_joke_button)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val jokeTextView = findViewById<TextView>(R.id.joke_text_view)
        progressBar.visibility = View.INVISIBLE

        val checkBox = findViewById<CheckBox>(R.id.checkbox)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            jokeViewModel.chooseFavorites(isChecked)
        }

        val changeButton = findViewById<ImageButton>(R.id.change_button)
        changeButton.setOnClickListener {
            jokeViewModel.changeJokeStatus()
        }

        button.setOnClickListener {
            button.isEnabled = false
            progressBar.visibility = View.VISIBLE
            jokeViewModel.getJoke()
        }

        jokeViewModel.init(object : DataCallback {
            override fun provideText(text: String) {
                button.isEnabled = true
                progressBar.visibility = View.INVISIBLE
                jokeTextView.text = text
            }

            override fun provideIconRes(id: Int) {
                changeButton.setImageResource(id)
            }
        })
    }

    override fun onDestroy() {
        jokeViewModel.clear()
        super.onDestroy()
    }
}