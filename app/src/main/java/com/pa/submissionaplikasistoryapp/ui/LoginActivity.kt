
package com.pa.submissionaplikasistoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

        binding.loginButton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            login(email!!, password!!)
        }

        setupTextWatchers()
    }

    private fun login(email: String, password: String){
        showProgressBar(true)
        viewModel.loginUser(email, password).observe(this, { response ->
            showProgressBar(false)
            if (response.error) {
                Toast.makeText(this, "User Failed Login", Toast.LENGTH_SHORT).show()
            } else {
//                SET SHARED PREF TO TRUE SESSION
                UserTokenPref.setLoggedIn(true)

                Toast.makeText(this, "User ${response.loginResult.name} Successfully Login}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
            }

        })

    }

    private fun showProgressBar(loading: Boolean) {
        if (loading) {
            binding.progressbar.visibility = View.VISIBLE
        } else {
            binding.progressbar.visibility = View.GONE
        }
    }

    private fun setupTextWatchers(){
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

}