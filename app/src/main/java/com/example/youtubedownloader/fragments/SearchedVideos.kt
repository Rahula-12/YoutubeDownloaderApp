package com.example.youtubedownloader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.youtubedownloader.databinding.FragmentSearchedVideosBinding
import com.example.youtubedownloader.recyclerView.VideoUrlAdapter
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchedVideos : Fragment(), ItemClicked {
    private lateinit var binding:FragmentSearchedVideosBinding
    private val viewModel:YoutubeDownloaderViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSearchedVideosBinding.inflate(inflater,container,false)
        val adapter=VideoUrlAdapter(this)
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager= LinearLayoutManager(context)
        viewModel.urls.observe(viewLifecycleOwner){
            if(it.isEmpty())
                binding.message.visibility=View.VISIBLE
            else
                binding.message.visibility=View.INVISIBLE
            adapter.updateList(it)
        }
        viewModel.downloadCompleted.observe(viewLifecycleOwner) { downloadCompleted ->
            if (downloadCompleted) {
                congratulationsDialog()
            }
        }
        binding.addUrl.setOnClickListener{
            findNavController().navigate(com.example.youtubedownloader.R.id.action_searchedVideos_to_dialogUrl)
        }
        viewModel.resetAll()
        return binding.root
    }

    override fun onItemClicked(url:String) {
        viewModel.assignUrls(url)
        findNavController().navigate(com.example.youtubedownloader.R.id.action_searchedVideos_to_optionFragment)
    }
}
interface ItemClicked{
    fun onItemClicked(url:String)
}

private fun Fragment.congratulationsDialog() {
    SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
        .setTitleText("Congratulations")
        .setContentText("Video downloaded successfully.")
        .show()
}