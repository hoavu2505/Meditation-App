package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.adapter.CategoryAdapter
import com.example.meditation.databinding.FragmentFocusBinding
import com.example.meditation.model.Category
import com.example.meditation.model.Content
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.example.meditation.util.CheckingInternet
import com.example.meditation.viewmodel.FocusCategoryViewModel
import com.example.meditation.viewmodel.FocusContentViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FocusFragment : Fragment(), CategoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFocusBinding
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var focusCategoryViewModel: FocusCategoryViewModel
    private lateinit var focusContentViewModel: FocusContentViewModel

    private lateinit var checkingInternet: CheckingInternet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        focusCategoryViewModel = ViewModelProvider(this)[FocusCategoryViewModel::class.java]
        focusContentViewModel = ViewModelProvider(this)[FocusContentViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        showNavBar()

        checkingInternet = CheckingInternet(requireActivity().application)

        focusCategoryViewModel.callData()
        focusContentViewModel.headingContentCallData()

        binding = FragmentFocusBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)

        val notConnected = requireActivity().findViewById<RelativeLayout>(R.id.ly_not_connected)

        checkingInternet.isConnected(requireActivity().application)

        checkingInternet.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.root.visibility = View.VISIBLE
                notConnected.visibility = View.GONE
                updateUI()
            }else{
                binding.root.visibility = View.INVISIBLE
                notConnected.visibility = View.VISIBLE
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun updateUI(){
        categoryRecyclerView = binding.rcvCategory
        categoryRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        focusCategoryViewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                adapter = CategoryAdapter(it, this)
                categoryRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })

        focusContentViewModel.headingContentLiveData.observe(viewLifecycleOwner, Observer { content ->
            if (content != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(requireContext()).load(content.img)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.imgHeading)
                }

                binding.cardHeading.setOnClickListener { headingContentClickItem(content) }
            }
        })
    }

    private fun headingContentClickItem(content: Content) {
        val action = FocusFragmentDirections.actionFocusFragmentToContentDetailLightFragment(content.id!!, content)
        materialMotion()
        findNavController().navigate(action)
        hideNavBar()
    }

    override fun onClickItem(itemView: View, category: Category, position: Int) {
        val type : String = "Focus"
        materialMotion()
        val action = FocusFragmentDirections.actionFocusFragmentToCategoryDetailFragment(category.id!!, category.name!!, type)
        findNavController().navigate(action)
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