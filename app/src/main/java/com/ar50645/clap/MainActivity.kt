package com.ar50645.clap

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf

class MainActivity : AppCompatActivity() {
    private var points = 0
    private var firstNum = 0
    private var secondNum = 0

    private var totalQuestion = 0

    private var imgClap: ImageView? = null
    private var textView1: TextView? = null
    private var response: EditText? = null
    private var soundPool: SoundPool? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgClap = findViewById(R.id.imgClap)
        textView1 = findViewById(R.id.textView1)
        response = findViewById(R.id.response_edit_text)
        initialize()
    }

    fun initialize() {
        assignNum()
        resetView()
        initializeSound()
    }

    fun initializeSound() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        soundPool!!.load(baseContext, R.raw.clap, 1)

    }

    fun playSound() {
        soundPool?.play(1, 1F, 1F, 0, 0, 1F)
    }

    fun assignNum() {
        firstNum = (0..10).shuffled().first()
        secondNum = (0..10).shuffled().last()
    }


    fun animate() {
        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.animate)
        imgClap?.startAnimation(anim)
    }

    fun calculateClick(view: View) {
        val response = response?.text.toString()
        if(response.toInt() == firstNum + secondNum) {
            correctResult()
        }
    }

    fun resetView() {
        response?.setText("")
        val toDisplay = firstNum.toString() + " + " + secondNum.toString() + " =  "
        textView1?.setText(toDisplay)
    }

    fun correctResult() {
        points++
        totalQuestion++
        playSound()
        animate()

        assignNum()
        resetView()
    }

    fun wrongAnswer() {
        resetView()
        totalQuestion++

    }

    override fun onStop() {
        super.onStop()

        val message = "your score " + points + " out of " + totalQuestion
        // Start the Worker if the timer is running
        val timerWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(
                workDataOf(
                    KEY_SCORE to message
                )
            ).build()

        WorkManager.getInstance(applicationContext).enqueue(timerWorkRequest)
    }
}