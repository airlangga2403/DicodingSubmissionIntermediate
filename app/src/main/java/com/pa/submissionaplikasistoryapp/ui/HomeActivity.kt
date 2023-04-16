package com.pa.submissionaplikasistoryapp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pa.submissionaplikasistoryapp.R
import com.pa.submissionaplikasistoryapp.data.remote.pref.UserTokenPref
import com.pa.submissionaplikasistoryapp.ui.adapter.ListStoryAdapter
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val factory: RegisterViewModelFactory by lazy {
        RegisterViewModelFactory.getInstance(this.application)
    }
    private val viewModel: RegisterViewModel by viewModels {
        factory
    }
    private lateinit var adapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        Log.d(this@HomeActivity::class.java.simpleName, UserTokenPref.getToken().toString())

        recyclerView = findViewById(R.id.rv_story_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListStoryAdapter(emptyList())
        recyclerView.adapter = adapter

        val token = UserTokenPref.getToken().toString()
        viewModel.getAllStories(token).observe(this, { listStory ->
            adapter.setList(listStory)
        })
    }
}