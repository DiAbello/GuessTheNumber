package ru.potapov.guessnumber

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class PlayGame : AppCompatActivity() {
    lateinit var etNum: EditText;

    lateinit var tvWelcome: TextView;
    lateinit var tvStatus: TextView;
    lateinit var tvAttemptsText: TextView;
    lateinit var tvAttemptsValue: TextView;
    lateinit var tvTimeRemainText: TextView;
    lateinit var tvTimeRemainValue: TextView;
    lateinit var tvDifficulty: TextView;
    lateinit var tvRandNum: TextView;

    lateinit var linAttempts: LinearLayout;
    lateinit var linTime: LinearLayout;

    lateinit var btnRandom: Button;
    lateinit var btnGuess: Button;
    lateinit var btnEnd: Button;
    lateinit var strName: String;

    lateinit var timer: CountDownTimer;

    var intSeconds: Int = 30;
    var randomNum: Int = 0;
    var attempts: Int = 10;
    var diff: String = "";
    var isGameEnded = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        tvWelcome = findViewById(R.id.welcomeText)

        randomNum = Random.nextInt(1,100);
        tvRandNum = findViewById(R.id.tvRandNum);

        tvRandNum.text = randomNum.toString();

        tvWelcome.text = "Здравствуйте, " + intent.extras?.getString("username") + "!"

//        intSeconds = intent.extras?.getInt("seconds")!!
        btnEnd = findViewById(R.id.buttonEnd)
        diff = intent.extras?.getString("difficulty")!!;
        tvDifficulty = findViewById(R.id.tvDiffValue)

        linAttempts = findViewById(R.id.lin_attemptsRemain)
        linTime = findViewById(R.id.lin_timeRemain)

        when(diff) {
            "easy" -> {
                tvDifficulty.text = getString(R.string.diff_easy)
                linAttempts.visibility = View.GONE;
                linTime.visibility = View.GONE;
            }
            "middle" -> {
                tvDifficulty.text = getString(R.string.diff_middle)
                btnEnd.visibility = View.GONE
                linAttempts.visibility = View.VISIBLE
                tvAttemptsValue = findViewById(R.id.attemptsRemainValue)
                tvAttemptsValue.text = attempts.toString()
                linTime.visibility = View.GONE;
            }
            "hard" -> {
                tvDifficulty.text = getString(R.string.diff_hard)
                btnEnd.visibility = View.GONE
                linAttempts.visibility = View.VISIBLE
                tvAttemptsValue = findViewById(R.id.attemptsRemainValue)
                tvAttemptsValue.text = attempts.toString()

                linTime.visibility = View.VISIBLE
                tvTimeRemainValue = findViewById(R.id.timeRemainValue)
                tvTimeRemainValue.text = intSeconds.toString()

                timer = object: CountDownTimer((intSeconds*1000).toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (!isGameEnded) {
                            intSeconds -= 1;
                            tvTimeRemainValue.text = intSeconds.toString();
                        } else this.cancel()
                    }

                    override fun onFinish() { if(!isGameEnded) { isGameEnded=true; goToMain("К сожалению вы проиграли!\n Время вышло!"); } }

                }
                timer.start()
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("attempts", attempts.toString())
        outState.putString("number", randomNum.toString())
        outState.putString("timeremain", intSeconds.toString())
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        attempts = savedInstanceState.getString("attempts")?.toInt() ?: 10;
        tvAttemptsValue.setText(attempts.toString());
        randomNum = savedInstanceState.getString("number")?.toInt() ?: Random.nextInt(1,100);
        tvRandNum.text = randomNum.toString();
        intSeconds = savedInstanceState.getString("timeremain")?.toInt() ?: 30;
        timer.cancel();

        timer = object: CountDownTimer((intSeconds*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isGameEnded) {
                    intSeconds -= 1;
                    tvTimeRemainValue.text = intSeconds.toString();
                } else this.cancel()
            }

            override fun onFinish() { if(!isGameEnded) { isGameEnded=true; goToMain("К сожалению вы проиграли!\n Время вышло!"); } }

        }
        timer.start()
    }

    fun tryGuess(view: View) {
        etNum = findViewById(R.id.etNumber)
        val triedNumber: Int? = etNum.text.toString().toIntOrNull()
        if(etNum.text.toString().isEmpty()) {
            showToast("Поле не может быть пустым")
        } else if (triedNumber == null){
            showToast("Данные не являются числом!")
        } else if (!isInRange(triedNumber, 0, 100)){
            showToast("Число должно быть от 0 до 100!")
        } else {
            tvStatus = findViewById(R.id.textStatus)
            btnRandom = findViewById(R.id.btnRandom)
            tvAttemptsText = findViewById(R.id.attemptsRemainText)
            when {
                triedNumber == randomNum -> {
                    goToMain("Вы угадали!\n Загадано было число - ${randomNum.toString()}")
                    //                btnRandom.visibility = View.VISIBLE
//                btnGuess.visibility = View.GONE
                }
                triedNumber > randomNum -> {
                    tvStatus.text = "Не угадали... ${etNum.text.toString()} больше X"
                }
                triedNumber < randomNum -> {
                    tvStatus.text = "Не угадали... ${etNum.text.toString()} меньше X"
                }
            }
            if(diff=="middle" || diff=="hard") {
                attempts--;
                tvAttemptsValue.text = attempts.toString()
                if (attempts == 0) {
                    isGameEnded = true;
                    goToMain("К сожалению вы проиграли!\n Попытки кончились!")
                }
            }
        }
    }
    fun endTheGame(view: View) {
        goToMain("Вы закончили игру!")
    }
    fun goToMain(reas: String) {
        val intent = Intent(this@PlayGame, MainMenu::class.java)
        intent.putExtra("gameResult", reas)
        startActivity(intent)
    }

    fun isInRange(num: Int, min: Int, max: Int): Boolean {
        return (min..max).contains(num)
    }
    fun showToast(str: String) {
        return Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show();
    }
}