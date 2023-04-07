package com.example.coroutineofsuspendsfunctions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    private fun startUpdate() {
        resetRun()

        greenJob = launch(Android) {
            startRunning(progressBarGreen)
        }

        redJob = launch(Android) {
            startRunning(progressBarRed)
        }

        blueJob =launch(Android) {
            startRunning(progressBarBlue)
        }
    }

    private suspend fun startRunning(
        progressBar: RoundCornerProgressBar) {
        progressBar.progress = 0f
        while (progressBar.progress < 1000 && !raceEnd) {
            delay(10)
            progressBar.progress += (1..10).random()
        }
        if (!raceEnd) {
            raceEnd = true
            Toast.makeText(this, "${progressBar.tooltipText} won!",
                Toast.LENGTH_SHORT).show()
        }
    }
}