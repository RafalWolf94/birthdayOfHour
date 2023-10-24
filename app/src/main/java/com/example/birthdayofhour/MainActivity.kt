package com.example.birthdayofhour

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import domain.BirthdayTimerService
import domain.BirthdayTimerService.Companion.countDownStartValue
import domain.BirthdayTimerService.Companion.oneMinute
import domain.BirthdayTimerService.Companion.stringEmpty
import domain.MirrorHourMeanings

class MainActivity : AppCompatActivity() {
    private var notificationManager: NotificationManager? = null
    private val notificationRequestCode = 101
    private val notificationType = Manifest.permission.POST_NOTIFICATIONS;

    private lateinit var animation: Animation
    private lateinit var timeLabel: TextView
    private lateinit var countDownLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureAnimation()
        configureTimerLabels()

        validateAndRequestPermissionForNotification()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        timer.start();
    }

    private val timer = object : CountDownTimer(20000, 1000) {
        @SuppressLint("NewApi")
        override fun onTick(millisUntilFinished: Long) {
            val (hour, minute, seconds) = BirthdayTimerService.getCurrentTime()

            if (hour == minute) {
                displayMessage(hour)
                return
            }
            if (hour == (minute + oneMinute) && seconds <= countDownStartValue) {
                runCountDown(seconds)
                return;
            }

            timeLabel.text = BirthdayTimerService.getTimeLeft(hour, minute, seconds)
        }

        override fun onFinish() {
            start()
        }
    }

    private fun runCountDown(second: Int) {
        timeLabel.text = stringEmpty
        countDownLabel.text = second.toString();
        countDownLabel.startAnimation(animation)
    }

    @SuppressLint("NewApi")
    private fun displayMessage(hour: Int) {
        timeLabel.text = MirrorHourMeanings[hour]
        countDownLabel.text = stringEmpty
        sendNotification(hour.toString())
    }

    private fun configureAnimation() {
        animation = AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_out)
        animation.duration = 999
    }

    private fun configureTimerLabels() {
        timeLabel = findViewById(R.id.timer_label)
        countDownLabel = findViewById(R.id.countdown_label)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            notificationRequestCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Notification permission required", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(hour: String) {

        val notificationID = 101
        val channelID = "com.ebookfrenzy.notifydemo.news"

        val notification = Notification.Builder(this, channelID)
            .setContentTitle("Urodziny Godziny!")
            .setContentText("${hour}:${hour}")
            .setSmallIcon(android.R.drawable.btn_star_big_on)
            .build()

        notificationManager?.notify(notificationID, notification)
    }

    private fun validateAndRequestPermissionForNotification() {
        val permission = ContextCompat.checkSelfPermission(this, notificationType)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(notificationType), notificationRequestCode)
        }
    }
}