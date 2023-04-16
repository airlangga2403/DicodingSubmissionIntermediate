package com.pa.submissionaplikasistoryapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.pa.submissionaplikasistoryapp.R
import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem
import com.pa.submissionaplikasistoryapp.ui.DetailStoryActivity

class ListStoryAdapter : ListAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_story, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAvatar: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val name: TextView = itemView.findViewById(R.id.tv_item_name)
        private val createdAt: TextView = itemView.findViewById(R.id.tv_created_at)

        fun bind(item: ListStoryItem) {
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .transform(RoundedCorners(10))
                .apply(RequestOptions().override(200, 200))
                .into(tvAvatar)
            name.text = item.name
            createdAt.text = item.createdAt

            // Navigate to detail story page and pass the ID data when item is clicked
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("id", item.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}