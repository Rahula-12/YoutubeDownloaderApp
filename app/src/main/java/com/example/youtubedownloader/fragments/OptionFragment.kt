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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.youtubedownloader.R
import com.example.youtubedownloader.database.VideoApplication
import com.example.youtubedownloader.databinding.FragmentOptionBinding
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModelFactory


class OptionFragment : Fragment() {
   private lateinit var binding:FragmentOptionBinding
   private val STORAGE_REQUEST_CODE=1
   private val viewModel:YoutubeDownloaderViewModel by activityViewModels{
       YoutubeDownloaderViewModelFactory(
           (activity?.application as VideoApplication).database.itemDao(),requireContext()
       )
   }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentOptionBinding.inflate(inflater,container,false)
        viewModel.thumbnail.observe(viewLifecycleOwner){
            if(it!="")
            {
                context?.let { it1 -> Glide.with(it1).
                load(it).fitCenter().placeholder(R.drawable.loading_spinner)
                    .into(binding.thumbnail) }
            }
            else
                context?.let { it1 -> Glide.with(it1).
                load(R.drawable.loading_spinner).fitCenter()
                    .into(binding.thumbnail) }
        }
        viewModel.title.observe(viewLifecycleOwner){
            binding.title.text=it
        }
        viewModel.downloadCompleted.observe(viewLifecycleOwner)
        {
            if(it) {
                congratulationsDialog("Downloading Completed")
            }
        }
        binding.deleteUrl.setOnClickListener{
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
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    findNavController().navigate(R.id.action_optionFragment_to_searchedVideos)
                }.setCancelClickListener {
                    it.dismiss()
                }
                .show()
        }
        binding.downloadVideo.setOnClickListener {
            if (context?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                val builder = AlertDialog.Builder(context)
                val sizes: Array<String> = arrayOf("640 X 360", "1280 X 720")
                builder.setTitle("Select size")
                builder.setSingleChoiceItems(sizes, 0) { dialog, pos ->
                    viewModel.outPutVideos(pos)
                    dialog.dismiss()
                }
                builder.show()
            }
            else if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Storage Permission")
                    .setContentText("Without Storage Permission, you will not be able to download video.")
                    .setConfirmText("OK").setConfirmClickListener {
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),STORAGE_REQUEST_CODE)
                        it.dismiss()
                    }.setCancelText("NO THANKS").
                    setCancelClickListener {
                        it.dismiss()
                    }
                    .show()
            }
            else{
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),STORAGE_REQUEST_CODE)
            }
        }
        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            STORAGE_REQUEST_CODE->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Storage Permission")
                        .setContentText("Storage Permission required to download video.")
                        .setConfirmText("OK")
                        .show()
                }
                return
            }
            else->{

            }
        }
    }

    private fun Fragment.congratulationsDialog(msg:String){
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Congratulations")
            .setContentText("Video has been downloaded.")
            .show()
    }
}