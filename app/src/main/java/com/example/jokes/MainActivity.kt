package com.example.jokes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = (application as App).viewModel

        val button = findViewById<Button>(R.id.get_joke_button)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val jokeTextView = findViewById<TextView>(R.id.joke_text_view)
        progressBar.visibility = View.INVISIBLE

        val checkBox = findViewById<CheckBox>(R.id.checkbox)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.chooseFavorites(isChecked)
        }

        val changeButton = findViewById<ImageButton>(R.id.change_button)
        changeButton.setOnClickListener {
            viewModel.changeJokeStatus()
        }

        button.setOnClickListener {
            button.isEnabled = false
            progressBar.visibility = View.VISIBLE
            viewModel.getJoke()
        }

        viewModel.init(object : DataCallback {
            override fun provideText(text: String) = runOnUiThread {
                button.isEnabled = true
                progressBar.visibility = View.INVISIBLE
                jokeTextView.text = text
            }

            override fun provideIconRes(id: Int) = runOnUiThread {
                changeButton.setImageResource(id)
            }
        })
    }

    override fun onDestroy() {
        viewModel.clear()
        super.onDestroy()
    }
}