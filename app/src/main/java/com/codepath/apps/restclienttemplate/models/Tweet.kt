package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class Tweet(var body: String = "",
            var createdAt: String = "",
            var user: User? = null,
            var id: Long? = null,
            var like: Boolean? = null ): Parcelable {


    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {

            var tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.id = jsonObject.getLong("id")
            tweet.like = jsonObject.getBoolean("favorited")
            return tweet

        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = mutableListOf<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))

            }
            return tweets
        }
    }
}