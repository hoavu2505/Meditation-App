package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.example.meditation.viewmodel.FocusCategoryViewModel
import com.example.meditation.viewmodel.FocusContentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FocusFragment : Fragment(), CategoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFocusBinding
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var focusCategoryViewModel: FocusCategoryViewModel
    private lateinit var focusContentViewModel: FocusContentViewModel

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

        focusCategoryViewModel.callData()
        focusContentViewModel.headingContentCallData()

        binding = FragmentFocusBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)

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

//        onBackPressed()

        return view
    }

    private fun headingContentClickItem(content: Content) {
        val action = FocusFragmentDirections.actionFocusFragmentToContentDetailLightFragment(content.id!!, content)
        findNavController().navigate(action)
        hideNavBar()
    }

    override fun onClickItem(category: Category, position: Int) {
        val type : String = "Focus"
        val action = FocusFragmentDirections.actionFocusFragmentToCategoryDetailFragment(category.id!!, category.name!!, type)
        findNavController().navigate(action)
        hideNavBar()
    }

    private fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_focusFragment_to_splashScreenFragment)
                hideNavBar()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

}