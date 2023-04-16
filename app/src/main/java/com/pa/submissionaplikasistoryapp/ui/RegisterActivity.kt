package com.pa.submissionaplikasistoryapp.ui

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
import androidx.lifecycle.Observer
import com.pa.submissionaplikasistoryapp.databinding.ActivityRegisterBinding
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private var name: String? = null
    private var email: String? = null
    private var password: String? = null

    //
    private val factory: RegisterViewModelFactory by lazy {
        RegisterViewModelFactory.getInstance(this.application)
    }
    private val viewModel: RegisterViewModel by viewModels {
        factory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Register"

        // Disable the button and set the text in MyButton to "Isi Dulu"
        val myButton = binding.registerButton
        myButton.isEnabled = false

        setupTextWatchers()
        binding.registerButton.setOnClickListener {
            name = binding.name.text.toString().trim()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            registUser(name!!, email!!, password!!)
        }

    }

    private fun registUser(name: String, email: String, password: String) {
        showProgressBar(true)
        viewModel.registerUser(name, email, password).observe(this, { response ->
            if (response.error) {
                Toast.makeText(
                    this@RegisterActivity,
                    "User Failed To Register ${response.message}",
                    Toast.LENGTH_SHORT
                ).show()
                showProgressBar(false)
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "User Registered Successfuly",
                    Toast.LENGTH_SHORT
                ).show()
                showProgressBar(false)
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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

    private fun setupTextWatchers() {
        val editText1 = binding.name
        val editText2 = binding.email
        val editText3 = binding.password
        val myButton = binding.registerButton

        editText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(editText1, editText2, editText3, myButton)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        editText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(editText1, editText2, editText3, myButton)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        editText3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(editText1, editText2, editText3, myButton)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

//    private fun setMyButtonEnable() {
//        val result = binding.password.text
//        binding.registerButton.isEnabled = result != null && result.toString().isNotEmpty()
//    }

    private fun setMyButtonEnable(
        editText1: EditText,
        editText2: EditText,
        editText3: EditText,
        myButton: Button
    ) {
        val editText1Text = editText1.text.toString().trim()
        val editText2Text = editText2.text.toString().trim()
        val editText3Text = editText3.text.toString().trim()

        myButton.isEnabled =
            editText1Text.isNotEmpty() && editText2Text.isNotEmpty() && editText3Text.isNotEmpty()
    }


}