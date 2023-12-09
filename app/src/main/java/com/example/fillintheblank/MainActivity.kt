package com.example.fillintheblank

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider
import com.example.fillintheblank.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var problemTextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        problemTextView = binding.problemTextView
        answerEditText = binding.answerEditText

        val difficultyIntent = intent
        viewModel.setDifficulty(difficultyIntent.getIntExtra("난이도", 0))
        if (savedInstanceState == null) {
            // 화면이 처음 생성될 때만 ViewModel 초기화
            viewModel.initGame()
        }
        viewModel.cancelTimer() // 기존의 countDownTimer를 제거
        viewModel.startTimer(viewModel.time.value ?: 0, this) // 새로운 타이머 시작

        viewModel.problem.observe(this, Observer<String> { newProblem ->
            binding.problem = newProblem
        })
        viewModel.currentAnswer.observe(this, Observer<Int> { newAnswer ->
            // 필요한 경우 정답에 대한 처리 작성
        })
        viewModel.score.observe(this, Observer<Int> { newScore ->
            binding.score = "점수 : ${newScore.toString()}"
        })
        viewModel.time.observe(this, Observer<Long> { newTime ->
            updateTimerText(newTime)
        })
        viewModel.gameOverCheck.observe(this, Observer<Boolean> { gameOverCheck ->
            if (gameOverCheck) gameOver()
        })

        binding.checkButton.setOnClickListener {
            checkAnswer()
        }
    }

    private fun updateTimerText(remainingTime: Long) {
        val seconds = remainingTime / 1000
        val milliseconds = remainingTime % 1000 / 10
        binding.time = "남은 시간: ${String.format("%02d.%02d", seconds, milliseconds)}"
    }

    // 뒤로가기 버튼을 눌렀을 때 timer가 종료되게 설정
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.cancelTimer() // 타이머를 종료
    }
    override fun onDestroy() {
        super.onDestroy()

        // 액티비티가 소멸될 때 타이머 종료
        viewModel.cancelTimer()
    }

    private fun checkAnswer() {
        val userAnswer = answerEditText.text.toString()
        viewModel.checkAnswer(this, userAnswer, answerEditText)
    }
    private fun gameOver() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("점수", viewModel.score.value)
        startActivity(intent)
        finish()
    }
}