package com.example.youtubedownloader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubedownloader.R
import com.example.youtubedownloader.databinding.FragmentSearchedVideosBinding
import com.example.youtubedownloader.recyclerView.VideoUrlAdapter
import com.example.youtubedownloader.viewmodel.YoutubeDownloaderViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchedVideos : Fragment() {
    private lateinit var binding:FragmentSearchedVideosBinding
    private val viewModel:YoutubeDownloaderViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSearchedVideosBinding.inflate(inflater,container,false)
        val adapter=VideoUrlAdapter(onItemClicked={url->
            viewModel.assignUrls(url)
            val bundle=Bundle()
            bundle.putString("url",url)
            findNavController().navigate(R.id.action_searchedVideos_to_optionFragment,bundle)
        })
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager= LinearLayoutManager(context)
        viewModel.urls.observe(viewLifecycleOwner){
            if(it.isEmpty())
                binding.message.visibility=View.VISIBLE
            else
                binding.message.visibility=View.INVISIBLE
            adapter.submitList(it)
        }
        binding.addUrl.setOnClickListener{
            findNavController().navigate(R.id.action_searchedVideos_to_dialogUrl)
        }
        viewModel.resetAll()
        return binding.root
    }
}