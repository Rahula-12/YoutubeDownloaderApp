package com.example.youtubedownloader.fragments
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.databinding.FragmentOptionBinding
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel


class OptionFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding
    private val STORAGE_REQUEST_CODE = 1
    // ViewModel initialization using activityViewModels delegate and custom ViewModelFactory
    private lateinit var viewModel: YoutubeDownloaderViewModel

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

        // Observing download completion from ViewModel
        viewModel.downloadCompleted.observe(viewLifecycleOwner) { downloadCompleted ->
            if (downloadCompleted) {
                congratulationsDialog()
            }
        }

        // OnClickListener for deleteUrl button
        binding.deleteUrl.setOnClickListener {
            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete URL")
                .setContentText("Do you want to delete this URL?")
                .setConfirmText("Yes")
                .setConfirmClickListener {
                    viewModel.deleteUrl()
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
                showSizeSelectionDialog()
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
    private fun Fragment.congratulationsDialog() {
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Congratulations")
            .setContentText("Video has been downloaded.")
            .show()
    }

    // Function to show dialog for selecting video size
    private fun showSizeSelectionDialog() {
        val builder = AlertDialog.Builder(context)
        val sizes: Array<String> = arrayOf("640 X 360", "1280 X 720")
        builder.setTitle("Select size")
        builder.setSingleChoiceItems(sizes, 0) { dialog, pos ->
            viewModel.outPutVideos(pos)
            dialog.dismiss()
        }
        builder.show()
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
}
