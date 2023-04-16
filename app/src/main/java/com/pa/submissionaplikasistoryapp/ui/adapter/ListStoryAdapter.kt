package com.pa.submissionaplikasistoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pa.submissionaplikasistoryapp.R
import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem

class ListStoryAdapter(private var listStories: List<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallBack(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setList(listStories: List<ListStoryItem>) {
        this.listStories = listStories
        notifyDataSetChanged()
    }

    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvAvatar: ImageView = view.findViewById(R.id.iv_item_photo)
        private val name: TextView = view.findViewById(R.id.name)

        fun bind(stories: ListStoryItem) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(stories.photoUrl)
                    .apply(RequestOptions().override(100, 100))
                    .into(tvAvatar)
                name.text = stories.name
                setOnClickListener {
                    onItemClickCallback?.onItemClick(stories)
                }
            }
        }

//        fun bind(stories: listSto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_story, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listStories.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStories[position])
    }

    interface OnItemClickCallback {
        fun onItemClick(data: ListStoryItem)
    }

}
