package com.example.coroutinedemo

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.system.measureTimeMillis

//this demo is base on demo of elye "https://github.com/elye/demo_android_coroutines_race"
// in order to learning how coroutine works

class MainActivity : AppCompatActivity() {

    private var greenJob: Job? = null
    private var blueJob: Job? = null
    private var orangeJob: Job? = null
    private var purpleJob: Job? = null
    private var pinkJob: Job? = null
    private var yellowJob: Job? = null
    private var isEndDelay = false
    private var isEndSleep = false
    private var delayOrder = ""
    private var sleepOrder = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnThreadSleep.setOnClickListener {
            startThreadSleep()
        }

        btnCoroutineDelay.setOnClickListener {
            startCoroutineDelay()
        }
    }

    private fun startCoroutineDelay() {
        resetRunDelay()

        val timeDelay = measureTimeMillis {
            greenJob = GlobalScope.launch(Android) {
                startRunningDelay(progressBlue, "Blue")
            }

            blueJob = GlobalScope.launch(Android) {
                startRunningDelay(progressGreen, "Green")
            }

            orangeJob = GlobalScope.launch(Android) {
                startRunningDelay(progressOrange, "Orange")
            }
        }

        tv_delay.text = timeDelay.toString()
        Toast.makeText(this,delayOrder,Toast.LENGTH_SHORT).show()
    }

    // When delay coroutine return back resource for another process to execute
    // In this example when "delay(10)" execute, program will execute blue -> green -> orange -> blue to the end
    private suspend fun startRunningDelay(progress: ProgressBar, name: String) {
        progress.progress = 0
        while (progress.progress < 1000 && !isEndDelay) {
            delayOrder += "$name -> "
            delay(10)
            progress.progress += (0..10).random()
        }
        if (!isEndDelay) {
            isEndDelay = true
        }

    }

    private fun startThreadSleep() {
        resetRunSleep()

        val timeSleep = measureTimeMillis {
            purpleJob = GlobalScope.launch {
                startRunningSleep(progressPurple, "Purple")
            }

            pinkJob = GlobalScope.launch {
                startRunningSleep(progressPink, "Pink")
            }

            yellowJob = GlobalScope.launch {
                startRunningSleep(progressYellow, "Yellow")
            }
        }

        tv_sleep.text = timeSleep.toString()
        Toast.makeText(this,sleepOrder,Toast.LENGTH_SHORT).show()
    }

    // Using Thread sleep
    // Thread sleep block coroutines during time millis. It doesn't return resources back
    // So when using Thread.sleep() only one process executing at one time
    private fun startRunningSleep(progress: ProgressBar, name: String) {
        progress.progress = 0
        while (progress.progress < 1000 && !isEndSleep) {
            sleepOrder += "$name -> "
            Thread.sleep(10)
            progress.progress += (0..10).random()
        }
        if (!isEndSleep) {
            isEndSleep = true
        }
    }

    private fun ClosedRange<Int>.random() = Random.nextInt(endInclusive - start) + start

    private fun resetRunDelay() {
        delayOrder = ""
        isEndDelay = false
        greenJob?.cancel()
        blueJob?.cancel()
        orangeJob?.cancel()
    }

    private fun resetRunSleep(){
        sleepOrder = ""
        isEndSleep = false
        purpleJob?.cancel()
        pinkJob?.cancel()
        yellowJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetRunDelay()
        resetRunSleep()
    }
}
