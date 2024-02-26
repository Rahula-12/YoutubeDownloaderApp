package com.example.youtubedownloader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubedownloader.database.VideoApplication
import com.example.youtubedownloader.databinding.FragmentSearchedVideosBinding
import com.example.youtubedownloader.recyclerView.VideoUrlAdapter
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModelFactory


class SearchedVideos : Fragment(), ItemClicked {
    private lateinit var binding:FragmentSearchedVideosBinding
    private val viewModel:YoutubeDownloaderViewModel by activityViewModels{
        YoutubeDownloaderViewModelFactory(
            (activity?.application as VideoApplication).database.itemDao(),requireContext()
        )
    }
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