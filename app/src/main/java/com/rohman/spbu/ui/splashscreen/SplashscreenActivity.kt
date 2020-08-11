package com.rohman.spbu.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rohman.spbu.R
import com.rohman.spbu.persistence.prefs.LoginPrefInterface
import com.rohman.spbu.persistence.prefs.LoginPrefs
import com.rohman.spbu.ui.home.MainActivity
import com.rohman.spbu.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splashscreen.*

class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val loginPrefInterface: LoginPrefInterface =
            LoginPrefs()

        Glide.with(this@SplashscreenActivity).load(R.drawable.logo)
            .into(image_logo)

        val firstLogin = loginPrefInterface.getFirstLogin(this)
        var destinationActivity: Class<*> = MainActivity::class.java

        if (firstLogin) {
            loginPrefInterface.setFirstLogin(this@SplashscreenActivity)
            destinationActivity = LoginActivity::class.java
        }

        val secondsDelayed = 2L
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashscreenActivity, destinationActivity))
            finish()
        }, secondsDelayed * 1000)
    }
}