package com.example.fillintheblank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var problemTextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var checkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        problemTextView = findViewById(R.id.problemTextView)
        answerEditText = findViewById(R.id.answerEditText)
        checkButton = findViewById(R.id.checkButton)

        generateNewProblem()

        checkButton.setOnClickListener {
            checkAnswer()
        }
    }

    private var currentAnswer: Int = 0

    private fun generateNewProblem() {
        val num1 = Random().nextInt(10) + 1
        val num2 = Random().nextInt(10) + 1
        currentAnswer = num1 + num2
        val problem = "$num1 + $num2 = ___"
        problemTextView.text = problem
    }

    private fun checkAnswer() {
        val userAnswer = answerEditText.text.toString()
        if (userAnswer.isEmpty()) {
            answerEditText.error = "답을 입력하세요."
            return
        }

        try {
            val parsedAnswer = userAnswer.toInt()
            if (parsedAnswer == currentAnswer) {
                problemTextView.text = "정답입니다!"
            } else {
                problemTextView.text = "틀렸습니다. 정답은 $currentAnswer 입니다."
            }
            generateNewProblem()
            answerEditText.text.clear()
        } catch (e: NumberFormatException) {
            answerEditText.error = "올바른 숫자를 입력하세요."
        }
    }
}