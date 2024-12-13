package com.capstone.lensakulitku.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.ActivitySignupBinding
import com.capstone.lensakulitku.view.login.LoginActivity
import data.model.RegisterRequest
import data.model.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(applicationContext)
        signupViewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        playAnimation()
        setLoginTextColor()
        setupActions()
        observeViewModel()
    }

    private fun playAnimation() {
        val titleAnimator = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).setDuration(500)
        val nameInputAnimator = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val emailInputAnimator = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordInputAnimator = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val confirmPasswordAnimator = ObjectAnimator.ofFloat(binding.confirmPasswordEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val signupButtonAnimator = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 0f, 1f).setDuration(500)
        val orTextAnimator = ObjectAnimator.ofFloat(binding.orText, View.ALPHA, 0f, 1f).setDuration(500)
        val googleSignupButtonAnimator = ObjectAnimator.ofFloat(binding.googleSignupButton, View.ALPHA, 0f, 1f).setDuration(500)
        val loginTextAnimator = ObjectAnimator.ofFloat(binding.loginTextView, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                titleAnimator,
                nameInputAnimator,
                emailInputAnimator,
                passwordInputAnimator,
                confirmPasswordAnimator,
                signupButtonAnimator,
                orTextAnimator,
                googleSignupButtonAnimator,
                loginTextAnimator
            )
            startDelay = 300
            start()
        }
    }

    private fun setLoginTextColor() {
        val fullText = getString(R.string.already_have_account)
        val loginStart = fullText.indexOf(getString(R.string.login_label))
        val spannable = SpannableString(fullText).apply {
            setSpan(
                ForegroundColorSpan(getColor(R.color.blue)),
                loginStart,
                fullText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.loginTextView.text = spannable
    }

    private fun setupActions() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                val request = RegisterRequest(
                    fullname = name.trim(),
                    email = email.trim(),
                    password = password,
                    confirmpw = confirmPassword
                )
                signupViewModel.registerUser(request)
            }
        }

        binding.loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            binding.nameEditTextLayout.error = getString(R.string.error_empty_name)
            return false
        } else {
            binding.nameEditTextLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.error_empty_email)
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditTextLayout.error = getString(R.string.error_invalid_email)
            return false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.error_empty_password)
            return false
        } else if (password.length < 6) {
            binding.passwordEditTextLayout.error = getString(R.string.error_short_password)
            return false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordEditTextLayout.error = getString(R.string.error_empty_confirm_password)
            return false
        } else if (confirmPassword != password) {
            binding.confirmPasswordEditTextLayout.error = getString(R.string.error_password_mismatch)
            return false
        } else {
            binding.confirmPasswordEditTextLayout.error = null
        }

        return true
    }

    private fun observeViewModel() {
        signupViewModel.registerResult.observe(this) { result ->
            if (result.msg == getString(R.string.registration_completed)) {
                showSuccessDialog(binding.emailEditText.text.toString())
            } else {
                showErrorDialog(result.msg)
            }
        }

        signupViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showErrorDialog(message: String) {
        android.app.AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error_title))
            setMessage(message)
            setPositiveButton(getString(R.string.ok), null)
            create()
            show()
        }
    }

    private fun showSuccessDialog(email: String) {
        android.app.AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.signup_success_title))
            setMessage(getString(R.string.signup_success_message, email))
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
}
