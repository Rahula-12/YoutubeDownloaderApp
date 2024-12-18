package com.example.youtubedownloader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController:NavController
    private lateinit var internetReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        throw Exception("Random Exception")
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        if(intent.type=="text/plain") {
            val videoUrl=intent.getStringExtra(Intent.EXTRA_TEXT)
//            Toast.makeText(this,"Got $videoUrl",Toast.LENGTH_SHORT).show()
            val bundle=Bundle()
            bundle.putString("videoUrl",videoUrl)
            navController.navigate(R.id.action_homeScreen_to_searchedVideos,bundle)
        }
        internetReceiver=object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action== ConnectivityManager.CONNECTIVITY_ACTION) {
                    lifecycleScope.launch {
                        while(!isNetworkAvailable(this@MainActivity)) {
                            internetDialog(this@MainActivity)
                            delay(10000)
                        }
                    }
                }
            }
        }
        registerReceiver(internetReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(internetReceiver)
    }
}

fun internetDialog(context: Context): AlertDialog {
    val internetAlertDialog= MaterialAlertDialogBuilder(context).
    setTitle("Internet Connectivity Alert").setCancelable(true).setMessage("Please check your internet connection.").setPositiveButton("OK"){
            _,_->
    }
    // phoneNumberInputBinding=DataBindingUtil.inflate(layoutInflater,R.layout.phone_number_input,null,true)
    internetAlertDialog.setCancelable(true)
    return internetAlertDialog.show()
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}