package com.example.fillintheblank

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider
import com.example.fillintheblank.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        val difficultyIntent = intent
        viewModel.setDifficulty(difficultyIntent.getIntExtra("난이도", 0))

        // 화면이 처음 생성될 때만 ViewModel 초기화
        if (savedInstanceState == null)
            viewModel.initGame()

        viewModel.cancelTimer() // 기존의 countDownTimer를 제거
        viewModel.startTimer(viewModel.time.value ?: 0, this) // 새로운 타이머 시작

        observeViewModel() // Observe 모음

        binding.checkButton.setOnClickListener {
            checkAnswer()
        }
    }

    // Observe 모음
    private fun observeViewModel() {
        with(viewModel) {
            problem.observe(this@MainActivity, Observer { newProblem ->
                binding.problem = newProblem
            })

            score.observe(this@MainActivity, Observer { newScore ->
                binding.score = "점수 : $newScore"
            })

            time.observe(this@MainActivity, Observer { newTime ->
                updateTimerText(newTime)
            })

            gameOverCheck.observe(this@MainActivity, Observer { gameOverCheck ->
                if (gameOverCheck) gameOver()
            })
        }
    }

    // Timer 텍스트 업데이트
    private fun updateTimerText(remainingTime: Long) {
        val seconds = remainingTime / 1000
        val milliseconds = remainingTime % 1000 / 10
        binding.time = "남은 시간: %02d.%02d".format(seconds, milliseconds)
    }

    // 뒤로가기 버튼을 눌렀을 때 timer가 종료되게 설정
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.cancelTimer()
    }
    // Activity가 Destroy 될 때 timer가 종료되게 설정
    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelTimer()
    }

    // viewModel의 checkAnswer호출
    private fun checkAnswer() {
        val userAnswer = binding.answerEditText.text.toString()
        viewModel.checkAnswer(this, userAnswer, binding.answerEditText)
    }
    // 게임 오버시 결과 화면으로 이동, MainActivity 종료
    private fun gameOver() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("점수", viewModel.score.value)
        startActivity(intent)
        finish()
    }
}