package com.example.youtubedownloader.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.youtubedownloader.R
import com.example.youtubedownloader.database.VideoApplication
import com.example.youtubedownloader.databinding.FragmentDialogUrlBinding
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModelFactory
import java.lang.ClassCastException

class DialogUrl : DialogFragment() {
       private lateinit var binding:FragmentDialogUrlBinding
       private val viewModel:YoutubeDownloaderViewModel by activityViewModels{
           YoutubeDownloaderViewModelFactory(
               (activity?.application as VideoApplication).database.itemDao(),requireContext()
           )
       }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentDialogUrlBinding.inflate(inflater,container,false)
        dialog?.setTitle("Video Url")
        binding.submit.setOnClickListener{
            if(viewModel.isYoutubeUrl(binding.myUrl.text.toString())) {
                viewModel.insertUrl(binding.myUrl.text.toString())
                findNavController().navigate(R.id.action_dialogUrl_to_searchedVideos)
            }
            else
                Toast.makeText(context,"Enter valid url",Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setWidthPercent(100)
        super.onActivityCreated(savedInstanceState)
    }
    private fun DialogFragment.setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}