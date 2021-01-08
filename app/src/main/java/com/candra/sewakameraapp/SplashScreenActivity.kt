package com.candra.sewakameraapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.candra.sewakameraapp.intro.IntroActivity
import com.candra.sewakameraapp.sign.SignInActivity
import com.candra.sewakameraapp.utils.Preferences

class SplashScreenActivity : AppCompatActivity() {

    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preferences = Preferences(this)
        var handler = Handler()
        handler.postDelayed({

            if (preferences.getValues("intro").equals("sudah")) {

                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            } else {

                var intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }
}