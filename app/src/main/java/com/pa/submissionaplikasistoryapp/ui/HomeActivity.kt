package com.pa.submissionaplikasistoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pa.submissionaplikasistoryapp.R
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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.addstory)
        menuItem.title = menuItem.titleCondensed
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addstory -> {
                val intent = Intent(this@HomeActivity, AddStoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                // call the logoutUser function in the viewModel
                viewModel.logoutUser()
                Toast.makeText(this@HomeActivity, "User SucessFully Logout", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onResume() {
        super.onResume()
        showProgressBar(true)
        viewModel.getAllStories(0, 20).observe(this) { response ->
            response.listStory?.let { listFollowing ->
                setUserListStory(listFollowing)
            }
            showProgressBar(false)
        }
    }


    private fun setUserListStory(listFollowing: List<ListStoryItem>) {
        val adapter = ListStoryAdapter()
        adapter.submitList(listFollowing)
        binding.rvStoryList.adapter = adapter
        showProgressBar(false)

    }

    private fun showProgressBar(loading: Boolean) {
        if (loading) {
            binding.progressbar.visibility = View.VISIBLE
        } else {
            binding.progressbar.visibility = View.GONE
        }
    }
}