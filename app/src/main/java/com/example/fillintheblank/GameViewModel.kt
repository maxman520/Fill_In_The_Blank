package com.example.fillintheblank

import android.content.Intent
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import java.util.Random

class GameViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    // 난이도 LiveData
    private val _difficulty = MutableLiveData<Int>(0)
    val difficulty: LiveData<Int> get() = _difficulty
    fun setDifficulty(value: Int) {
        _difficulty.value = value
    }

    // 문제 및 정답 LiveData
    private val _problem = MutableLiveData<String>()
    val problem: LiveData<String> get() = _problem

    private val _currentAnswer = MutableLiveData<Int>(0)
    val currentAnswer: LiveData<Int> get() = _currentAnswer

    // 점수 LiveData
    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> get() = _score

    // 시간 LiveData
    private val _time = MutableLiveData<Long>(0)
    val time: LiveData<Long> get() = _time
    fun setTime(value: Long) {
        _time.value = value
    }

    // 게임 오버 LiveData
    private val _gameOverCheck = MutableLiveData<Boolean>(false)
    val gameOverCheck: LiveData<Boolean> get() = _gameOverCheck
    fun setGameOverCheck(value: Boolean) {
        _gameOverCheck.value = value
    }

    init {
        // 게임 초기화
        initGame()
    }
    fun initGame() {
        val difficultyValue = difficulty.value ?: 0
        generateNewProblem(difficultyValue)
        setTime((4 + (difficultyValue * 3)) * 1000L)
    }

    // 정답 확인 및 게임 로직
    fun checkAnswer(activity: MainActivity, userAnswer: String, answerEditText: EditText) {
        if (userAnswer.isEmpty()) {
            answerEditText.error = "답을 입력하세요."
            return
        }
        try {
            cancelTimer()//  정확한 시간 계산을 위해 정답 체크 전에 타이머를 종료

            if (userAnswer.toInt() != _currentAnswer.value) { // 오답이라면
                Toast.makeText(
                    activity,
                    "틀렸습니다. 정답은 ${_currentAnswer.value} 입니다.",
                    Toast.LENGTH_LONG
                ).show()
                setGameOverCheck(true)
            } else { // 정답이라면
                _score.value = _score.value!! + ((_difficulty.value!!+1) * 100)
                countDownTimer?.start() // 타이머 재시작
            }
            generateNewProblem(_difficulty.value)
            answerEditText.text.clear()
        } catch (e: NumberFormatException) {
            answerEditText.error = "올바른 숫자를 입력하세요."
        }
    }

    fun startTimer(initialTime: Long, activity: MainActivity) {
        countDownTimer = object : CountDownTimer(initialTime, 1) {
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished // 시간 갱신
            }

            override fun onFinish() {
                Toast.makeText(
                    activity,
                    "제한시간을 초과했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                setGameOverCheck(true) // 게임 오버
            }
        }.start()
    }

    fun cancelTimer() {
        countDownTimer?.cancel()
    }


    // 다음 문제 생성 로직
    private fun generateNewProblem(difficulty: Int?) {
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
        val newProblem = ArrayList<Any>()

        // 곱하기 연산을 한 번만 등장하도록 설정
        var hasMultiplication = false
        var i: Int = 0
        while (i < numCount*2-1) {
            // 숫자 추가
            if (i % 2 == 0) {
                newProblem.add(numbers[i/2])

            } else { // 연산 기호 추가
                var j = random.nextInt(operators.size)
                if (j == 3 && !hasMultiplication) {
                    newProblem.add(operators[j])
                    hasMultiplication = true
                }
                else {
                    j = random.nextInt(operators.size-1)
                    newProblem.add(operators[j])
                }
            }
            i++
        }
        // 문제의 답 계산
        _currentAnswer.value = calculateAnswer(numCount, newProblem)

        // 문제 문자열 생성
        val problemString = newProblem.joinToString(" ")
        _problem.value = "$problemString = ___"
    }

    // 식에 대한 정답 계산
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
}
