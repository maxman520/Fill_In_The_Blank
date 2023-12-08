package com.example.fillintheblank

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    private lateinit var scoreTextView: TextView
    private lateinit var confirmButton: Button
    private var result: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        scoreTextView = findViewById(R.id.scoreText)
        confirmButton = findViewById(R.id.confirmButton)

        val resultIntent = intent
        result = resultIntent.getIntExtra("점수", 0)

        scoreTextView.text = "최종 점수: $result"

        confirmButton.setOnClickListener {
            clickBtn(confirmButton)
        }

    }
    private fun clickBtn(view: View) {
        val intent = Intent(this, ChooseDifficulty::class.java)
        startActivity(intent)
        finish()
    }
}