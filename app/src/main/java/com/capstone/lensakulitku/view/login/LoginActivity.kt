package com.capstone.lensakulitku.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.ActivityLoginBinding
import com.capstone.lensakulitku.view.main.MainActivity
import com.capstone.lensakulitku.view.signup.SignupActivity
import data.model.LoginRequest
import data.model.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("LensaKulitkuPrefs", Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("TOKEN", null)
        if (token != null) {
            navigateToMainActivity()
            return
        }

        val factory = ViewModelFactory(applicationContext)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        setSignupTextColor()
        playAnimation()
        setupActions()
        observeViewModel()
        loadRememberedCredentials()
    }

    private fun playAnimation() {
        val titleAnimator = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).setDuration(500)
        val emailInputAnimator = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordInputAnimator = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val rememberMeAnimator = ObjectAnimator.ofFloat(binding.rememberMeCheckbox, View.ALPHA, 0f, 1f).setDuration(500)
        val loginButtonAnimator = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 0f, 1f).setDuration(500)
        val orTextAnimator = ObjectAnimator.ofFloat(binding.orText, View.ALPHA, 0f, 1f).setDuration(500)
        val googleLoginButtonAnimator = ObjectAnimator.ofFloat(binding.googleLoginButton, View.ALPHA, 0f, 1f).setDuration(500)
        val signupTextAnimator = ObjectAnimator.ofFloat(binding.signupTextView, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                titleAnimator,
                emailInputAnimator,
                passwordInputAnimator,
                rememberMeAnimator,
                loginButtonAnimator,
                orTextAnimator,
                googleLoginButtonAnimator,
                signupTextAnimator
            )
            startDelay = 300
            start()
        }
    }

    private fun setSignupTextColor() {
        val fullText = "Don't Have An Account? Sign Up"
        val spannable = SpannableString(fullText)
        val signUpStart = fullText.indexOf("Sign Up")
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.blue)),
            signUpStart,
            fullText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.signupTextView.text = spannable
    }

    private fun setupActions() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                val request = LoginRequest(email = email, password = password)
                loginViewModel.loginUser(request)
            }
        }

        binding.signupTextView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditTextLayout.error = "Invalid email address"
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordEditTextLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(this) { result ->
            if (result != null) {
                saveToken(result.token)
                saveUserId(result.userId)
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Login failed. Token or User ID is null.", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.errorMessage.observe(this) { error ->
            error?.let { showErrorDialog(it) }
        }
    }


    private fun saveToken(token: String) {
        sharedPreferences.edit().apply {
            putString("TOKEN", token)
            apply()
        }
    }

    private fun saveUserId(userId: String) {
        sharedPreferences.edit().apply {
            putString("USER_ID", userId)
            apply()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadRememberedCredentials() {
        val rememberedEmail = sharedPreferences.getString("REMEMBERED_EMAIL", "")
        val rememberedPassword = sharedPreferences.getString("REMEMBERED_PASSWORD", "")
        val isRememberMeChecked = sharedPreferences.getBoolean("REMEMBER_ME", false)

        if (isRememberMeChecked) {
            binding.emailEditText.setText(rememberedEmail)
            binding.passwordEditText.setText(rememberedPassword)
            binding.rememberMeCheckbox.isChecked = true
        }
    }

    private fun saveCredentialsIfNeeded(email: String, password: String) {
        if (binding.rememberMeCheckbox.isChecked) {
            sharedPreferences.edit().apply {
                putString("REMEMBERED_EMAIL", email)
                putString("REMEMBERED_PASSWORD", password)
                putBoolean("REMEMBER_ME", true)
                apply()
            }
        } else {
            sharedPreferences.edit().apply {
                remove("REMEMBERED_EMAIL")
                remove("REMEMBERED_PASSWORD")
                putBoolean("REMEMBER_ME", false)
                apply()
            }
        }
    }

    private fun showErrorDialog(message: String) {
        android.app.AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

}