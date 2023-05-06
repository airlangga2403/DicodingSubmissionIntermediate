package com.pa.submissionaplikasistoryapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.pa.submissionaplikasistoryapp.databinding.ActivityDetailStoryBinding
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.pa.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val factory: RegisterViewModelFactory by lazy {
        RegisterViewModelFactory.getInstance(this.application)
    }
    private val viewModel: RegisterViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val idUser = intent.getStringExtra(ID)

        if (idUser != null) {
            implementView(idUser)
        }


    }

    private fun implementView(idUser: String) {
        viewModel.getDetailStory(idUser).observe(this) { response ->
//            Implement ImageView
            Glide.with(this)
                .load(response.story.photoUrl)
                .transform(RoundedCorners(5))
                .apply(RequestOptions().override(200, 200))
                .into(binding.tvDetailImage)
            binding?.apply {
                titleDetailName.text = response.story.name
                descriptionTitleDetail.text = response.story.description
                createdAtDetail.text = response.story.createdAt
            }

        }
    }

    companion object {
        const val ID = "id"
    }
}