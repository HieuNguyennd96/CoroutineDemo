package com.example.coroutinedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var greenJob: Job? = null
    private var blueJob: Job? = null
    private var orangeJob: Job? = null
    private var isEnd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener {
            startCoroutine()
        }
    }

    private fun startCoroutine() {
        resetRun()

        greenJob =  GlobalScope.launch(Android)  {
            startRunning(progressBlue,"Blue")
        }

        blueJob = GlobalScope.launch(Android)  {
            startRunning(progressGreen,"Green")
        }

        orangeJob = GlobalScope.launch(Android) {
            startRunning(progressOrange,"Orange")
        }

    }
    // When delay coroutine return back resource for another process to execute
    // In this example when "delay(10)" execute,program will run blue -> green -> orange -> blue to the end
    private suspend fun startRunning(progress: ProgressBar, name:String) {
        progress.progress = 0
        while (progress.progress < 1000 && !isEnd){
            val bar = name
            delay(10)
            progress.progress += (0..10).random()
        }
        if(!isEnd){
            isEnd = true
        }
    }

    private fun ClosedRange<Int>.random() = Random.nextInt(endInclusive - start) + start

    private fun resetRun() {
        isEnd = false
        greenJob?.cancel()
        blueJob?.cancel()
        orangeJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetRun()
    }
}
