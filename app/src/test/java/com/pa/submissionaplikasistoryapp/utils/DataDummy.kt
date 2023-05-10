package com.pa.submissionaplikasistoryapp.utils

import com.pa.submissionaplikasistoryapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt + $i",
                "name $i",
                "description $i",
                0.0,
                "story-$i",
                0.0
            )
            items.add(story)
        }
        return items
    }
}