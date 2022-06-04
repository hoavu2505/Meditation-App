package com.example.meditation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.adapter.CategoryAdapter
import com.example.meditation.databinding.FragmentMeditationBinding
import com.example.meditation.model.Category
import com.example.meditation.model.Content
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.example.meditation.viewmodel.MeditationCategoryViewModel
import com.example.meditation.viewmodel.MeditationContentViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MeditationFragment : Fragment(), CategoryAdapter.OnItemClickListener, LifecycleOwner {

    private lateinit var binding : FragmentMeditationBinding
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var meditationRecyclerViewModel: MeditationCategoryViewModel
    private lateinit var meditationContentViewModel: MeditationContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        meditationRecyclerViewModel = ViewModelProvider(this)[MeditationCategoryViewModel::class.java]
        meditationContentViewModel = ViewModelProvider(this)[MeditationContentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        showNavBar()

        meditationRecyclerViewModel.callData()
        meditationContentViewModel.headingContentCallData()

        binding = FragmentMeditationBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)

        categoryRecyclerView = binding.rcvCategory

        categoryRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        meditationRecyclerViewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                adapter = CategoryAdapter(it, this)
                categoryRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                meditationRecyclerViewModel.categoryListLiveData.postValue(null)
            }
        })

        meditationContentViewModel.headingContentLiveData.observe(viewLifecycleOwner, Observer { content ->
            if (content != null){
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(requireContext()).load(content.img)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.imgHeading)

                    binding.tvHeading.text = content.name
                }

                binding.cardHeading.setOnClickListener { headingContentClickItem(content) }
            }
        })

//        onBackPressed()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun headingContentClickItem(content: Content) {
        when(content.type){
            "Course" -> {
                val action = MeditationFragmentDirections.actionMeditationFragmentToCourseDetailFragment(content)
                findNavController().navigate(action)
                materialMotion()
            }
            "Sound" -> {
                val action = MeditationFragmentDirections.actionMeditationFragmentToContentDetailLightFragment(content.id!!, content)
                findNavController().navigate(action)
                materialMotion()
            }
        }
        hideNavBar()
    }

    override fun onClickItem(itemView : View, category: Category, position: Int) {
        val type : String = "Meditation"
        materialMotion()
        val action = MeditationFragmentDirections.actionMeditationFragmentToCategoryDetailFragment(category.id!!, category.name!!, type)
        findNavController().navigate(action)
        Log.e("category", "${category.id}")
        hideNavBar()
    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

}