package com.example.androidapprentice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    companion object {
        val SCORE_KEY = "score_key"
        val TIME_LEFT_KEY = "time_left_key"
    }

    private lateinit var tapMeButton: Button
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView

    private var score = 0

    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private val timeLimit = 5L // need configuration
    private val initialCountDown: Long = timeLimit * 1000
    private val countDownInterval: Long = 1000
    private var timeLeft = timeLimit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tapMeButton = findViewById(R.id.tap_me_button)
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)

        savedInstanceState?.let {
            score = it.getInt(SCORE_KEY, 0)
            timeLeft = it.getLong(TIME_LEFT_KEY, 0L)
            restoreGame()
        } ?: run {
            resetGame()
        }

        tapMeButton.setOnClickListener(View.OnClickListener {v ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            v.startAnimation(bounceAnimation)
            increamentScore()
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.about_item) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val title = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val msg = getString(R.string.about_message)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .create()
            .show()
    }

    private fun increamentScore() {
        if(!gameStarted) {
            startGame()
        }

        score += 1
        gameScoreTextView.text = getString(R.string.your_score, score)
    }

    private fun restoreGame() {
        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(timeLeft * 1000, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000) + 1
                timeLeftTextView.text = getString(R.string.time_left, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        gameStarted = false
    }

    private fun resetGame() {
        score = 0

        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, initialCountDown/1000)
        timeLeftTextView.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000) + 1
                timeLeftTextView.text = getString(R.string.time_left, timeLeft)
            }

            override fun onFinish() {
               endGame()
            }
        }

        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}