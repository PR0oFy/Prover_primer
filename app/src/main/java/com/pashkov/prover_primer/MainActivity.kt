package com.pashkov.prover_primer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pashkov.prover_primer.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var right = 0 // Переменная для хранения количества правильных ответов
    private var lose = 0 // Переменная для хранения количества неправильных ответов
    private var all = 0 // Переменная для хранения общего количества заданий
    private var startTime: Long = 0L // Переменная для хранения времени начала выполнения задания
    private val timeIntervals = mutableListOf<Long>() // Список для хранения временных интервалов выполнения заданий

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Восстановление данных при повороте экрана или пересоздании активити
        savedInstanceState?.let {
            right = it.getInt("right", 0)
            lose = it.getInt("lose", 0)
            all = it.getInt("all", 0)
            val savedIntervals = it.getLongArray("timeIntervals")
            savedIntervals?.let { intervals ->
                timeIntervals.addAll(intervals.toList())
            }
            restoreUIState(it)
        }

        // Если активити создается впервые (не в результате поворота экрана), отключаем кнопки "loseButton" и "reightButton"
        if (savedInstanceState == null) {
            binding.loseButton.isEnabled = false
            binding.reightButton.isEnabled = false
        }

        // Обработчик нажатия на кнопку "startButton"
        binding.startButton.setOnClickListener {
            button()
            generatePrimer()
        }

        // Обработчик нажатия на кнопку "loseButton"
        binding.loseButton.setOnClickListener {
            checkPrimer(false)
        }

        // Обработчик нажатия на кнопку "reightButton"
        binding.reightButton.setOnClickListener {
            checkPrimer(true)
        }
    }

    // Метод для управления кнопками
    private fun button() {
        binding.loseButton.isEnabled = true
        binding.reightButton.isEnabled = true
        binding.startButton.visibility = View.GONE
    }

    // Метод для генерации примера
    private fun generatePrimer() {
        val numberOne = Random.nextInt(10, 100)
        val numberTwo = Random.nextInt(10, 100)
        val operators = arrayOf('+', '-', '*', '/')

        val operator = operators.random()

        binding.nullNull1.text = numberOne.toString()
        binding.nullNull2.text = numberTwo.toString()
        binding.znak.text = operator.toString()

        val isCorrect = Random.nextBoolean()

        val correctResult = when (operator) {
            '+' -> numberOne + numberTwo
            '-' -> numberOne - numberTwo
            '*' -> numberOne * numberTwo
            '/' -> {
                val result = numberOne.toDouble() / numberTwo.toDouble()
                String.format("%.2f", result).replace(',', '.').toDouble()
            }
            else -> throw IllegalArgumentException("Unknown operator")
        }
        if (isCorrect) {
            binding.vvvod.text = correctResult.toString()
        } else {
            var wrongResult: Number
            do {
                val randomNumberString = String.format("%.2f", Random.nextDouble(0.1, 10.0)).replace(',', '.')
                wrongResult = when (operator) {
                    '/' -> randomNumberString.toDouble()
                    else -> Random.nextInt(5, 200)
                }
            } while (wrongResult == correctResult)
            binding.vvvod.text = wrongResult.toString()
        }
    }

    // Метод для проверки примера
    private fun checkPrimer(check: Boolean) {
        val numberOne = binding.nullNull1.text.toString().toDouble()
        val numberTwo = binding.nullNull2.text.toString().toDouble()
        var pollResult = binding.vvvod.text.toString().toDouble()

        val result = when (binding.znak.text.toString()) {
            "+" -> numberOne + numberTwo
            "-" -> numberOne - numberTwo
            "*" -> numberOne * numberTwo
            "/" -> {
                val stringResult = String.format("%.2f", numberOne / numberTwo)
                stringResult.replace(',', '.').toDouble()
            }
            else -> throw IllegalArgumentException("Unknown operator")
        }

        // Проверка правильности ответа
        if (check && result == pollResult) {
            right++
        }
        else if(!check && result != pollResult){
            right++
        }
        else {
            lose++
        }
        all++

        generatePrimer()
        time()
        voodoo()
    }

    // Метод для подсчета времени выполнения задания
    private fun time() {
        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime
        startTime = System.currentTimeMillis()
        val seconds = elapsedTime / 1000 % 10

        timeIntervals.add(seconds)

        val maxTime = timeIntervals.maxOrNull() ?: 0
        val minTime = timeIntervals.minOrNull() ?: 0

        binding.maxNull.text = maxTime.toString()
        binding.minNull.text = minTime.toString()

        val averageTime = timeIntervals.average()
        binding.credeeNull.text = String.format("%.2f", averageTime)
    }

    // Метод для обновления интерфейса
    private fun voodoo() {
        binding.itogoNull.text = all.toString()
        binding.rightNull.text = right.toString()
        binding.loseNull.text = lose.toString()

        val present = String.format("%.2f%%", (right.toDouble() / all.toDouble()) * 100)
        binding.prosenttext.text = present
    }

    // Метод для сохранения состояния при повороте экрана или пересоздании активити
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("right", right)
        outState.putInt("lose", lose)
        outState.putInt("all", all)
        outState.putLongArray("timeIntervals", timeIntervals.toLongArray())

        outState.putBoolean("startButtonEnabled", binding.startButton.isEnabled)
        outState.putInt("startButtonVisibility", binding.startButton.visibility)

        // Сохранение состояния кнопок
        outState.putBoolean("reightButtonEnabled", binding.reightButton.isEnabled)
        outState.putBoolean("loseButtonEnabled", binding.loseButton.isEnabled)

        // Сохранение значений minNull, maxNull, credeeNull, nullNull1, znak, nullNull2, vvvod
        outState.putString("minNull", binding.minNull.text.toString())
        outState.putString("maxNull", binding.maxNull.text.toString())
        outState.putString("credeeNull", binding.credeeNull.text.toString())
        outState.putString("nullNull1", binding.nullNull1.text.toString())
        outState.putString("znak", binding.znak.text.toString())
        outState.putString("nullNull2", binding.nullNull2.text.toString())
        outState.putString("vvvod", binding.vvvod.text.toString())
    }

    // Метод для восстановления состояния при повороте экрана или пересоздании активити
    private fun restoreUIState(savedInstanceState: Bundle) {
        binding.itogoNull.text = all.toString()
        binding.rightNull.text = right.toString()
        binding.loseNull.text = lose.toString()

        binding.startButton.isEnabled = savedInstanceState.getBoolean("startButtonEnabled", false)
        binding.startButton.visibility = savedInstanceState.getInt("startButtonVisibility", View.VISIBLE)

        binding.reightButton.isEnabled = savedInstanceState.getBoolean("reightButtonEnabled", false)
        binding.loseButton.isEnabled = savedInstanceState.getBoolean("loseButtonEnabled", false)

        val present = String.format("%.2f%%", (right.toDouble() / all.toDouble()) * 100)
        binding.prosenttext.text = present

        // Восстановление значений minNull, maxNull, credeeNull, nullNull1, znak, nullNull2, vvvod
        binding.minNull.text = savedInstanceState.getString("minNull", "0")
        binding.maxNull.text = savedInstanceState.getString("maxNull", "0")
        binding.credeeNull.text = savedInstanceState.getString("credeeNull", "0")
        binding.nullNull1.text = savedInstanceState.getString("nullNull1", "")
        binding.znak.text = savedInstanceState.getString("znak", "")
        binding.nullNull2.text = savedInstanceState.getString("nullNull2", "")
        binding.vvvod.text = savedInstanceState.getString("vvvod", "")
    }
}
