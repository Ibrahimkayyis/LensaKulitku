package com.capstone.lensakulitku.view.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.ActivityOnboardingBinding
import com.capstone.lensakulitku.view.signup.SignupActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.ic_onboarding1,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
            ),
            OnboardingItem(
                R.drawable.ic_onboarding2,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
            ),
            OnboardingItem(
                R.drawable.ic_onboarding3,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
            )
        )

        adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        setupPageIndicator(onboardingItems.size)
        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePageIndicator(position)
                binding.btnNext.text = if (position == onboardingItems.size - 1) {
                    getString(R.string.get_started)
                } else {
                    getString(R.string.next)
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < onboardingItems.size - 1) {
                binding.viewPager.currentItem = current + 1
            } else {
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            }
        }

        binding.skipText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        // Mulai animasi pop-up
        playPopUpAnimations()
    }

    private fun setupPageIndicator(count: Int) {
        repeat(count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(16, 16).apply {
                    marginEnd = 8
                }
                setBackgroundResource(R.drawable.dot_inactive)
            }
            binding.pageIndicator.addView(dot)
        }
        updatePageIndicator(0)
    }

    private fun updatePageIndicator(position: Int) {
        for (i in 0 until binding.pageIndicator.childCount) {
            val dot = binding.pageIndicator.getChildAt(i)
            dot.setBackgroundResource(
                if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }

    private fun playPopUpAnimations() {
        // Animasi untuk elemen-elemen halaman onboarding
        val skipTextAnimator = ObjectAnimator.ofFloat(binding.skipText, View.ALPHA, 0f, 1f).setDuration(500)
        val viewPagerAnimator = ObjectAnimator.ofFloat(binding.viewPager, View.ALPHA, 0f, 1f).setDuration(500)
        val pageIndicatorAnimator = ObjectAnimator.ofFloat(binding.pageIndicator, View.ALPHA, 0f, 1f).setDuration(500)
        val buttonAnimator = ObjectAnimator.ofFloat(binding.btnNext, View.ALPHA, 0f, 1f).setDuration(500)

        // Gabungkan animasi secara sequential
        AnimatorSet().apply {
            playSequentially(skipTextAnimator, viewPagerAnimator, pageIndicatorAnimator, buttonAnimator)
            start()
        }
    }
}
