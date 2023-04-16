package com.pa.submissionaplikasistoryapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.pa.submissionaplikasistoryapp.databinding.ActivityHomeBinding
import com.pa.submissionaplikasistoryapp.ui.adapter.ListStoryAdapter
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModelFactory

class HomeActivity : AppCompatActivity() {

    private val factory: RegisterViewModelFactory by lazy {
        RegisterViewModelFactory.getInstance(this.application)
    }
    private val viewModel: RegisterViewModel by viewModels {
        factory
    }
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStoryList.layoutManager = LinearLayoutManager(this)

//        viewModel.getAllStories(1, 10).observe(this) { response ->
//            response.listStory?.let { listFollowing ->
//                setUserListStory(listFollowing)
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllStories(1, 10).observe(this) { response ->
            response.listStory?.let { listFollowing ->
                setUserListStory(listFollowing)
            }
        }
    }


    private fun setUserListStory(listFollowing: List<ListStoryItem>) {
        val adapter = ListStoryAdapter()
        adapter.submitList(listFollowing)
        binding.rvStoryList.adapter = adapter

    }
}