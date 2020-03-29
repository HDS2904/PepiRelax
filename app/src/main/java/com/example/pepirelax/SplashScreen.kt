package com.example.pepirelax

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.pepirelax.ui.MainActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            val p1: Pair<View, String> = Pair.create(logoOpening,"transitionImage")
            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,p1)
            startActivity(intent,options.toBundle())
            finish()
        }, 2000)

    }

}
