package com.example.meditation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.example.meditation.R
import com.example.meditation.adapter.ContentAdapter
import com.example.meditation.adapter.HomeContentAdapter
import com.example.meditation.databinding.FragmentCategoryDetailBinding
import com.example.meditation.model.Content
import com.example.meditation.theme.NavBar
import com.example.meditation.viewmodel.FocusContentViewModel
import com.example.meditation.viewmodel.MeditationContentViewModel
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class CategoryDetailFragment : Fragment(), ContentAdapter.OnItemClickListener, LifecycleOwner {

    private lateinit var binding : FragmentCategoryDetailBinding
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var adapter: ContentAdapter
    private lateinit var meditationContentViewModel: MeditationContentViewModel
    private lateinit var focusContentViewModel: FocusContentViewModel
    private val args: CategoryDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }

        returnTransition = MaterialElevationScale(true).apply {
            duration = 50L
        }

        meditationContentViewModel = ViewModelProvider(this)[MeditationContentViewModel::class.java]
        focusContentViewModel = ViewModelProvider(this)[FocusContentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val id = args.id
        val categoryName = args.name
        val type = args.type

        meditationContentViewModel.callData(id)
        focusContentViewModel.callData(id)

        binding = FragmentCategoryDetailBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.tvCategoryName.text = categoryName

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
            showNavBar()
        }

        contentRecyclerView = binding.rcvContent
        contentRecyclerView.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

        when(type){
            "Meditation" -> meditationData()
            "Focus" -> focusData()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun focusData() {
        focusContentViewModel.contentListLiveData.observe(viewLifecycleOwner, Observer { contentArrayList ->
            if (contentArrayList != null){
                adapter = ContentAdapter(contentArrayList, this)
                contentRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                meditationContentViewModel.contentListLiveData.postValue(null)
            }
        })
    }

    private fun meditationData() {
        meditationContentViewModel.contentListLiveData.observe(viewLifecycleOwner, Observer { contentArrayList ->
            if (contentArrayList != null){
                adapter = ContentAdapter(contentArrayList, this)
                contentRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                meditationContentViewModel.contentListLiveData.postValue(null)
            }
        })
    }


    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    override fun onClickItem(view: View, content: Content, position: Int) {
        Log.d("onClick", "${content.name}")

        when(content.type){
            "Sound" -> {
                materialMotion()
                val action = CategoryDetailFragmentDirections.actionCategoryDetailFragmentToContentDetailLightFragment(content.id!!, content)
                findNavController().navigate(action)
            }
            "Course" -> {
                materialMotion()
                val action = CategoryDetailFragmentDirections.actionCategoryDetailFragmentToCourseDetailFragment(content)
                findNavController().navigate(action)
            }
            "Video" -> {
                materialMotion()
                val action = CategoryDetailFragmentDirections.actionCategoryDetailFragmentToPlayContentVideoFragment(content)
                findNavController().navigate(action)
            }
        }

    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300L
        }
    }

}