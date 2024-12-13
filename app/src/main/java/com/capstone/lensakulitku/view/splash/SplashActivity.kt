package com.capstone.lensakulitku.view.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.ActivitySplashBinding
import com.capstone.lensakulitku.view.main.MainActivity
import com.capstone.lensakulitku.view.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animasi fade-in untuk logo
        playLogoAnimation()

        // Cek token untuk menentukan navigasi
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("LensaKulitkuPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("TOKEN", null)

            val intent = if (token != null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }

            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 3000)
    }

    private fun playLogoAnimation() {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1500
            fillAfter = true
        }
        binding.logoImage.startAnimation(fadeIn)
    }
}