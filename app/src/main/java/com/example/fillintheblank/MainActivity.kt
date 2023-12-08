package com.example.fillintheblank

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.fillintheblank.databinding.ActivityMainBinding
import java.util.Random
import android.os.CountDownTimer


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var problemTextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var checkButton: Button
    private lateinit var countDownTimer: CountDownTimer
    private var time = 0L
    private var difficulty: Int = 0
    private var currentAnswer: Int = 0
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val difficultyIntent = intent
        difficulty = difficultyIntent.getIntExtra("난이도", 0)
        time = (4 + (difficulty * 3)) * 1000L

        problemTextView = binding.problemTextView
        answerEditText = binding.answerEditText
        checkButton = binding.checkButton
        binding.score = "점수 : ${score.toString()}"

        generateNewProblem(difficulty)

        countDownTimer = object : CountDownTimer(time, 1) {
            override fun onTick(millisUntilFinished: Long) {
                time = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                Toast.makeText(
                    this@MainActivity,
                    "제한시간을 초과했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                gameOver()
            }
        }.start()

        checkButton.setOnClickListener {
            checkAnswer()
        }
    }
    private fun updateTimerText() {
        val seconds = time / 1000
        val milliseconds = time % 1000 / 10
        binding.time = "남은 시간: ${String.format("%02d.%02d", seconds, milliseconds)}"
    }

    // 뒤로가기 버튼을 눌렀을 때 timer가 종료되게 설정
    override fun onBackPressed() {
        super.onBackPressed()
        countDownTimer.cancel() // 타이머를 종료
    }

    private fun generateNewProblem(difficulty: Int) {
        val random = Random()

        // 난이도에 따라 숫자 개수 설정
        val numCount = when (difficulty) {
            0 -> 2
            1 -> 3
            2 -> 4
            else -> throw IllegalArgumentException("잘못된 difficulty 값")
        }

        // 문제에 사용할 숫자 및 연산 기호 생성
        val numbers = List(numCount) { random.nextInt(10) + 1 }
        val operators = listOf("+", "-", "/", "*")
        val problem = ArrayList<Any>()

        // 곱하기 연산을 한 번만 등장하도록 설정
        var hasMultiplication = false
        var i: Int = 0
        while (i < numCount*2-1) {
            // 숫자 추가
            if (i % 2 == 0) {
                problem.add(numbers[i/2])

            } else { // 연산 기호 추가
                var j = random.nextInt(operators.size)
                if (j == 3 && !hasMultiplication) {
                    problem.add(operators[j])
                    hasMultiplication = true
                }
                else {
                    j = random.nextInt(operators.size-1)
                    problem.add(operators[j])
                }
            }
            i++
        }
        // 문제의 답 계산
        currentAnswer = calculateAnswer(numCount, problem)

        // 문제 문자열 생성
        val problemString = problem.joinToString(" ")
        binding.problem = "$problemString = ___"
    }
    private fun calculateAnswer(numCount: Int, problem: ArrayList<Any>): Int {
        val priorityOperators = listOf("*", "/")
        val result = mutableListOf<Any>()

        // 초기 값 설정
        var currentOperator: String? = null
        var currentNumber: Int = 0

        for (element in problem) {
            when (element) {
                is Int -> {
                    if (currentOperator == null) {
                        currentNumber = element
                    } else {
                        // 연산자에 따라 계산 수행
                        when (currentOperator) {
                            "*" -> currentNumber *= element
                            "/" -> currentNumber /= element
                        }
                        currentOperator = null
                    }
                }
                is String -> {
                    // 현재 연산자 업데이트
                    if (priorityOperators.contains(element)) {
                        currentOperator = element
                    } else {
                        // 낮은 우선순위 연산자일 경우 결과에 추가
                        result.add(currentNumber)
                        result.add(element)
                        currentNumber = 0
                    }
                }
            }
        }

        // 마지막 숫자와 연산자 처리
        result.add(currentNumber)

        // 최종 결과 계산
        var finalResult = result[0] as Int
        for (i in 1 until result.size step 2) {
            when (result[i] as String) {
                "+" -> finalResult += result[i + 1] as Int
                "-" -> finalResult -= result[i + 1] as Int
            }
        }

        return finalResult
    }


    private fun checkAnswer() {
        val userAnswer = answerEditText.text.toString()
        if (userAnswer.isEmpty()) {
            answerEditText.error = "답을 입력하세요."
            return
        }
        try {
            countDownTimer.cancel() //  정확한 시간 계산을 위해 정답 체크 전에 타이머를 종료

            if (userAnswer.toInt() != currentAnswer) { // 오답이라면
                Toast.makeText(
                    this@MainActivity,
                    "틀렸습니다. 정답은 $currentAnswer 입니다.",
                    Toast.LENGTH_LONG
                ).show()
                gameOver()
            } else { // 정답이라면
                score += (difficulty+1) * 100
                binding.score = "점수 : ${score.toString()}"
                //time = (4 + (difficulty * 3)) * 1000L
                countDownTimer.start() // 타이머 재시작
            }
            generateNewProblem(difficulty)
            answerEditText.text.clear()
        } catch (e: NumberFormatException) {
            answerEditText.error = "올바른 숫자를 입력하세요."
        }
    }
    private fun gameOver() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("점수", score)
        startActivity(intent)
        finish()
    }
}