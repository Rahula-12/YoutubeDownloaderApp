package com.example.youtubedownloader.fragments
import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.databinding.FragmentOptionBinding
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

private const val STORAGE_REQUEST_CODE = 1
@AndroidEntryPoint
class OptionFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding

    private var downloadId:Long?=null
    // ViewModel initialization using activityViewModels delegate and custom ViewModelFactory
    private val viewModel:YoutubeDownloaderViewModel by activityViewModels()
    private lateinit var downloadNotificationReceiver:BroadcastReceiver
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOptionBinding.inflate(inflater, container, false)

        // Observing thumbnail changes from ViewModel
        viewModel.thumbnail.observe(viewLifecycleOwner) { thumbnailUrl ->
            if (thumbnailUrl.isNotBlank()) {
                context?.let { Glide.with(it)
                    .load(thumbnailUrl)
                    .fitCenter()
                    .placeholder(R.drawable.loading_spinner)
                    .into(binding.thumbnail) }
            } else {
                context?.let { Glide.with(it)
                    .load(R.drawable.loading_spinner)
                    .fitCenter()
                    .into(binding.thumbnail) }
            }
        }

        // Observing title changes from ViewModel
        viewModel.title.observe(viewLifecycleOwner) { title ->
            binding.title.text = title
        }

        downloadNotificationReceiver=object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id =intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                Log.d("DownloadId",id.toString())
                if(id!=null && id==downloadId){
                    congratulationsDialog()
                }
            }
        }

        // OnClickListener for deleteUrl button
        binding.deleteUrl.setOnClickListener {
            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete URL")
                .setContentText("Do you want to delete this URL?")
                .setConfirmText("Yes")
                .setConfirmClickListener {
                    viewModel.deleteUrl(requireArguments().getString("url")!!)
                    it.setTitleText("Deleted!")
                        .setContentText("URL has been deleted!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    findNavController().navigate(R.id.action_optionFragment_to_searchedVideos)
                }.setCancelClickListener {
                    it.dismiss()
                }
                .show()
        }

        // OnClickListener for downloadVideo button
        binding.downloadVideo.setOnClickListener {
            if (context?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                // Show dialog to select video size
                try {
                    downloadId=makeDownloadRequest(viewModel.getVideo().url)
                }
                catch (e:Exception) {
                    Toast.makeText(requireContext(),"Video not available for this url. Sorry for inconvenience.",Toast.LENGTH_SHORT).show()
                }
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show rationale for storage permission
                showStoragePermissionRationale()
            } else {
                // Request storage permission
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
            }
        }
        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Storage permission granted, handle accordingly
                } else {
                    // Storage permission denied, show alert
                    showStoragePermissionDeniedAlert()
                }
                return
            }
        }
    }

    // Function to display congratulations dialog
    private fun congratulationsDialog() {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Congratulations")
            .setContentText("Video downloaded successfully.")
            .show()
        this@OptionFragment.findNavController().navigate(R.id.action_optionFragment_to_searchedVideos)
    }

    // Function to show rationale for storage permission
    private fun showStoragePermissionRationale() {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Storage Permission")
            .setContentText("Without Storage Permission, you will not be able to download video.")
            .setConfirmText("OK")
            .setConfirmClickListener {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
                it.dismiss()
            }
            .setCancelText("NO THANKS")
            .setCancelClickListener {
                it.dismiss()
            }
            .show()
    }

    // Function to show alert for denied storage permission
    private fun showStoragePermissionDeniedAlert() {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Storage Permission")
            .setContentText("Storage Permission required to download video.")
            .setConfirmText("OK")
            .show()
    }

    private fun makeDownloadRequest(url:String):Long {
        val directory = "/YoutubeVideos/"
        val directoryFolder =
            File("${Environment.getExternalStorageDirectory()}/Download/${directory}")
        if (!directoryFolder.exists()) {
            directoryFolder.mkdirs()
        }
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("YoutubeVideo_" + System.currentTimeMillis().toString() + ".mp4")
            .setDescription("Downloading Video")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                directory + "YoutubeVideo_" + System.currentTimeMillis().toString() + ".mp4"
            )
        val dm: DownloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(
                File(
                    Environment.DIRECTORY_DOWNLOADS + "/" + directory + "YoutubeVideo_" + System.currentTimeMillis()
                        .toString() + ".mp4"
                ).absolutePath
            ),
            null
        ){
            _,_->
        }
         return dm.enqueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(downloadNotificationReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(downloadNotificationReceiver)
    }

}
