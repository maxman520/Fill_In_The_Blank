package com.example.fillintheblank

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class ChooseDifficulty : AppCompatActivity() {
    private lateinit var easyButton: Button
    private lateinit var normalButton: Button
    private lateinit var hardButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_difficulty)

        easyButton = findViewById<Button>(R.id.easyButton)
        normalButton = findViewById<Button>(R.id.normalButton)
        hardButton = findViewById<Button>(R.id.hardButton)

        easyButton.setOnClickListener {
            clickBtn(easyButton)
        }
        normalButton.setOnClickListener {
            clickBtn(normalButton)
        }
        hardButton.setOnClickListener {
            clickBtn(hardButton)
        }
    }

    private fun clickBtn(view: View) {
        var difficulty: Int = when {
            view == easyButton -> 0
            view == normalButton -> 1
            else -> 2
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("난이도", difficulty)
        startActivity(intent)
    }
}