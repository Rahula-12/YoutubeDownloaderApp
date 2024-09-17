package com.example.youtubedownloader

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController:NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
//                val navHostFragment = supportFragmentManager
//                    .findFragmentById(R.id.navHostFragment) as NavHostFragment
                try {
                    navController.navigate(R.id.action_homeScreen_to_searchedVideos)
                }
                catch (_:Exception) {

                }

            }

        }.start()
    }

    override fun onStart() {
        super.onStart()
        if(intent.type=="text/plain") {
            val videoUrl=intent.getStringExtra(Intent.EXTRA_TEXT)
//            Toast.makeText(this,"Got $videoUrl",Toast.LENGTH_SHORT).show()
            val bundle=Bundle()
            bundle.putString("videoUrl",videoUrl)
            navController.navigate(R.id.action_homeScreen_to_searchedVideos,bundle)
        }
    }

    override fun onPause() {
        super.onPause()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)


        graph.startDestination = R.id.searchedVideos
        navController.graph = graph
    }
}