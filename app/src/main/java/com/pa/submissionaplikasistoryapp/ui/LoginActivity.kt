package com.pa.submissionaplikasistoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pa.submissionaplikasistoryapp.data.remote.pref.UserTokenPref
import com.pa.submissionaplikasistoryapp.databinding.ActivityLoginBinding
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var email: String? = null
    private var password: String? = null

    private val factory: RegisterViewModelFactory by lazy {
        RegisterViewModelFactory.getInstance(this.application)
    }
    private val viewModel: RegisterViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Disable the button and set the text in MyButton to "Isi Dulu"
        val myButton = binding.loginButton
        myButton.isEnabled = false

        setupTextWatchers()

        binding.loginButton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            login(email!!, password!!)
        }

        playAnimation()


    }

    private fun login(email: String, password: String) {
        showProgressBar(true)
        viewModel.loginUser(email, password).observe(this) { response ->

            if (response.error) {
                Toast.makeText(this, "User Failed Login", Toast.LENGTH_SHORT).show()
                showProgressBar(false)
            } else {
                showProgressBar(false)
                UserTokenPref.setLoggedIn(true)
                UserTokenPref.setToken(response.loginResult.token)
                Toast.makeText(
                    this,
                    "User ${response.loginResult.name} Successfully Login",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        }
    }

    private fun showProgressBar(loading: Boolean) {
        if (loading) {
            binding.progressbar.visibility = View.VISIBLE
        } else {
            binding.progressbar.visibility = View.GONE
        }
    }

    private fun setupTextWatchers() {
        val editText1 = binding.email
        val editText2 = binding.password
        val button = binding.loginButton

        editText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(editText1, editText2, button)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        editText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(editText1, editText2, button)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun setMyButtonEnable(
        editText1: EditText,
        editText2: EditText,
        myButton: Button
    ) {
        val editText1Text = editText1.text.toString().trim()
        val editText2Text = editText2.text.toString().trim()
        myButton.isEnabled =
            editText1Text.isNotEmpty() && editText2Text.isNotEmpty()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)

        val emailText = ObjectAnimator.ofFloat(binding.emailText, View.ALPHA, 1f).setDuration(500)
        val passwordText =
            ObjectAnimator.ofFloat(binding.passwordtext, View.ALPHA, 1f).setDuration(500)

        val btn = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        val together1 = AnimatorSet().apply {
            playTogether(email, emailText)
        }
        val together2 = AnimatorSet().apply {
            playTogether(password, passwordText)
        }

        AnimatorSet().apply {
            playSequentially(together1, together2, btn)
            startDelay = 500
        }.start()
    }

}