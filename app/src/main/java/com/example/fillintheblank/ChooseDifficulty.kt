package com.example.fillintheblank

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

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
        var difficulty: Int
        if (view == easyButton)
            difficulty = 0
        else if (view == normalButton)
            difficulty = 1
        else difficulty = 2

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("난이도", difficulty)
        startActivity(intent)
    }
}