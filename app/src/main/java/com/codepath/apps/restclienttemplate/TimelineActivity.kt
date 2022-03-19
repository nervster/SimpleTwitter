package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException


class TimelineActivity : AppCompatActivity() {

    lateinit var client:TwitterClient
    private lateinit var rvTweets: RecyclerView
    lateinit var adapter: TimelineAdapter
    private val tweets = ArrayList<Tweet>()
    lateinit var swipeContainer: SwipeRefreshLayout
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    var tweet_max_id: Long? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
//        setSupportActionBar(findViewById(R.id.toolbar))


        rvTweets = findViewById(R.id.rvTimeline)

        adapter = TimelineAdapter(this, tweets)
        val linearLayoutManager = LinearLayoutManager(this)
        rvTweets.layoutManager = linearLayoutManager
        rvTweets.adapter = adapter
        swipeContainer = findViewById(R.id.swipeContainer)
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData()
            }
        }
        rvTweets.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener);

        swipeContainer.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            populateHomeTimeline()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        client = TwitterApplication.getRestClient(this)
        populateHomeTimeline()

    }

    //Inflates new menu layout
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handles clicks on menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.compose) {
            Toast.makeText(this, "Ready to compose tweet!", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            val tweet = data?.getParcelableExtra("tweet") as? Tweet

            //Update Timeline
            if (tweet != null) {
                tweets.add(0, tweet)
            }

            //Update Adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)


    }


    fun loadMoreData() {
        Log.i(TAG, "loadMoreData called")
        tweet_max_id?.let { max_id ->
            client.getNextPageOfTweets(object: JsonHttpResponseHandler(){
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.i(TAG, "GetMoreTweets: onFailure!" + statusCode)
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                    Log.i(TAG, "onSuccess!")

                    try{

                        val jsonArray = json?.jsonArray
                        val listOfNewTweets = jsonArray?.let { it1 -> Tweet.fromJsonArray(it1) }
                        if (listOfNewTweets != null) {
                            for (listOfNewTweet in listOfNewTweets) {
                                if (listOfNewTweet.id!! > tweet_max_id!!) {
                                    tweet_max_id = listOfNewTweet.id
                                }
                            }
                        }
//                    tweets.addAll(listOfNewTweets)
                        if (listOfNewTweets != null) {
                            adapter.addAll(listOfNewTweets)
                        }
                        adapter.notifyDataSetChanged()

                    } catch (e: JSONException) {
                        Log.e(TAG, "JSON Exception $e")
                    }
                }

            }, max_id)
        }

        // 1. Send an API request to retrieve appropriate paginated data
        // 2. Deserialize and construct new model objects from the API response
        // 3. Append the new data objects to the existing set of items inside the array of items
        // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }


    fun populateHomeTimeline() {

        client.getHomeTimeline(object : JsonHttpResponseHandler(){

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure!" + statusCode)
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess!")

                try{
                    adapter.clear()
                    val jsonArray = json.jsonArray
                    val listOfNewTweets = Tweet.fromJsonArray(jsonArray)
                    for (listOfNewTweet in listOfNewTweets) {
                        if (listOfNewTweet.id!! > tweet_max_id!!) {
                            tweet_max_id = listOfNewTweet.id
                        }
                    }
//                    tweets.addAll(listOfNewTweets)
                    adapter.addAll(listOfNewTweets)
                    adapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }


            }

        })
    }

    companion object {
        const val TAG = "TimelineActivity"
        val REQUEST_CODE = 20
    }
}