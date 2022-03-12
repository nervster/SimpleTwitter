package com.codepath.apps.restclienttemplate

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codepath.apps.restclienttemplate.models.Tweet

class TimelineAdapter(private val context: Context, private val tweets: ArrayList<Tweet>)
    : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineAdapter.ViewHolder {
        val tweetView = android.view.LayoutInflater.from(parent.context).inflate(R.layout.timeline_view, parent, false)
        return ViewHolder(tweetView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TimelineAdapter.ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder position: $position")
        val tweet = tweets[position]
        holder.bind(tweet)
    }

    override fun getItemCount() = tweets.size

    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(tweetView: View):RecyclerView.ViewHolder(tweetView) {

        private val name =tweetView.findViewById<TextView>(R.id.tvName) as TextView
        private val username =tweetView.findViewById<TextView>(R.id.tvUsername) as TextView
        private val body = tweetView.findViewById<TextView>(R.id.tvTimelineBody)
        private val createdAt = tweetView.findViewById<TextView>(R.id.tvTime)
        private val image = tweetView.findViewById<ImageView>(R.id.ivAvatar)
        private val likeButton = tweetView.findViewById<ImageButton>(R.id.ibLike)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(tweet: Tweet) {


            name.text = tweet.user?.name
            username.text = "@" + tweet.user?.screenName
            body.text = tweet.body
            Glide.with(context)
                .load(tweet.user?.publicImageURL)
                .centerCrop() // scale image to fill the entire ImageView
                .transform(RoundedCorners(70))
                .into(image)




            // convert and create createdAt time
            createdAt.text = TimeFormatter.getTimeDifference(tweet.createdAt)
        }

    }

    companion object {
        const val TAG = "TimelineAdapter"
    }

}