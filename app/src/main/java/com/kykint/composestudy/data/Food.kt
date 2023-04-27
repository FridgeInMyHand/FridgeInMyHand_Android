package com.kykint.composestudy.data

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import coil.request.ImageRequest
import org.jsoup.Jsoup
import java.net.URLEncoder

data class Food(
    /**
     * Food name
     */
    var name: String,

    /**
     * UNIX Timestamp
     */
    var bestBefore: Long? = null,
) {
    @WorkerThread
    fun getImageModel(context: Context): ImageRequest? {
        val imageUrl = getNaverImageUrl(name)
        return if (imageUrl != null) {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build()
        } else {
            null
        }
    }

    /**
     * Get food image url from naver dictionary
     */
    companion object {
        @WorkerThread
        private fun getNaverImageUrl(foodName: String): String? {
            try {
                //#content > div:nth-child(2) > ul > li:nth-child(2) > div.thumb_area > div.thumb.id1721752 > a > img
                val url = "https://terms.naver.com/search.naver?query=" +
                        URLEncoder.encode(foodName, "UTF-8") +
                        "&dicType=15"
                val jsoup = Jsoup.connect(url)
                val doc = jsoup.get()
                val imageUrl = doc
                    .select("#content")
                    .select("div.thumb_area")
                    .select("a:nth-child(1)")
                    .select("img")
                    .attr("data-src")
                return imageUrl
            } catch (e: Throwable) {
                Log.e(Food::class.java.simpleName, "Error while fetching image url\n$e")
            }
            return null
        }
    }
}