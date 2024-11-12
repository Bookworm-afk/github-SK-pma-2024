package com.example.myapp012ajednoduchamatematika

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var TimeTextView: TextView? = null
    var QuestionTextText: TextView? = null
    var ScoreTextView: TextView? = null
    var AlertTextView: TextView? = null
    var FinalScoreTextView: TextView? = null
    var btn0: Button? = null
    var btn1: Button? = null
    var btn2: Button? = null
    var btn3: Button? = null
    var countDownTimer: CountDownTimer? = null
    var random: Random = Random
    var a = 0
    var b = 0
    var indexOfCorrectAnswer = 0
    var answers = ArrayList<Int>()
    var points = 0
    var totalQuestions = 0
    var cals = ""
    private var showDialog: AlertDialog? = null // Přidání proměnné pro dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val calInt = intent.getStringExtra("cals")
        cals = calInt!!
        TimeTextView = findViewById(R.id.TimeTextView)
        QuestionTextText = findViewById(R.id.QuestionTextText)
        ScoreTextView = findViewById(R.id.ScoreTextView)
        AlertTextView = findViewById(R.id.AlertTextView)
        btn0 = findViewById(R.id.button0)
        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)

        startGame()
    }

    private fun startGame() {
        resetGame()
        startTimer()
    }

    fun NextQuestion(cal: String) {
        if (cal == "/") {
            // Nastaví `b` na náhodné číslo mezi 1 a 9, aby se vyhnul nule
            b = random.nextInt(1, 10)

            // Nastaví `a` jako násobek `b`, aby `a / b` bylo vždy celé číslo
            a = b * random.nextInt(1, 10)
        } else {
            // Pro jiné operace generuje `a` a `b` obvykle
            a = random.nextInt(10)
            b = random.nextInt(10)
        }

        // Nastaví text otázky
        QuestionTextText!!.text = "$a $cal $b"
        indexOfCorrectAnswer = random.nextInt(4)
        answers.clear()

        for (i in 0..3) {
            if (indexOfCorrectAnswer == i) {
                answers.add(when (cal) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> a / b // Dělení je bezpečné, protože `a` je násobkem `b`
                    else -> 0
                })
            } else {
                var wrongAnswer = random.nextInt(20)
                // Zajišťuje, že špatná odpověď není stejná jako správná odpověď
                while (wrongAnswer == a + b || wrongAnswer == a - b || wrongAnswer == a * b || (b != 0 && wrongAnswer == a / b)) {
                    wrongAnswer = random.nextInt(20)
                }
                answers.add(wrongAnswer)
            }
        }

        // Nastaví text pro odpovědní tlačítka
        btn0!!.text = "${answers[0]}"
        btn1!!.text = "${answers[1]}"
        btn2!!.text = "${answers[2]}"
        btn3!!.text = "${answers[3]}"
    }

    fun optionSelect(view: View?) {
        totalQuestions++
        if (indexOfCorrectAnswer.toString() == view!!.tag.toString()) {
            points++
            AlertTextView!!.text = "Correct"
        } else {
            AlertTextView!!.text = "Wrong"
        }
        ScoreTextView!!.text = "$points/$totalQuestions"
        NextQuestion(cals)
    }

    fun PlayAgain(view: View?) {
        // Zavírá dialog, pokud je otevřený
        showDialog?.dismiss()

        // Restartuje hru
        startGame()
    }

    private fun resetGame() {
        points = 0
        totalQuestions = 0
        ScoreTextView!!.text = "$points/$totalQuestions"
        AlertTextView!!.text = ""
        FinalScoreTextView?.text = ""
        countDownTimer?.cancel() // Zrušení předchozího časovače
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(20000, 500) {
            override fun onTick(p0: Long) {
                TimeTextView!!.text = (p0 / 1000).toString() + "s"
            }

            override fun onFinish() {
                TimeTextView!!.text = "Konec času"
                openDialog()
            }
        }.start()
    }

    private fun openDialog() {
        val inflate = LayoutInflater.from(this)
        val winDialog = inflate.inflate(R.layout.win_layout, null)
        FinalScoreTextView = winDialog.findViewById(R.id.FinalScoreTextView)
        val btnPlayAgain = winDialog.findViewById<Button>(R.id.buttonPlayAgain)
        val btnBack = winDialog.findViewById<Button>(R.id.buttonBack)
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setView(winDialog)
        FinalScoreTextView!!.text = "$points/$totalQuestions"

        // Přiřaďte vytvořený dialog k proměnné showDialog
        showDialog = dialog.create()

        btnPlayAgain.setOnClickListener {
            PlayAgain(it)
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Zobrazí dialog
        showDialog?.show()
    }
}
