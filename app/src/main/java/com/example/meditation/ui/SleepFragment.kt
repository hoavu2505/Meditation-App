package com.example.meditation.ui

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.adapter.SleepContentAdapter
import com.example.meditation.databinding.FragmentSleepBinding
import com.example.meditation.model.Content
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.example.meditation.viewmodel.SleepContentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SleepFragment : Fragment(), SleepContentAdapter.OnItemClickListerner, LifecycleOwner {

    private lateinit var binding : FragmentSleepBinding
    private lateinit var contentRecyclerView : RecyclerView
    private lateinit var adapter: SleepContentAdapter
    private lateinit var contentViewModel: SleepContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentViewModel = ViewModelProvider(this)[SleepContentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        contentViewModel.callData()
        contentViewModel.headingContentCallData()

        binding = FragmentSleepBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.dark_background, context)
        Theme.setStatusBarLightText(requireActivity().window ,true)

        contentRecyclerView = binding.rcvSleepContent
        contentRecyclerView.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

        contentViewModel.contentListLiveData.observe(viewLifecycleOwner, Observer { contentArrayList ->
            if (contentArrayList != null ){
                adapter = SleepContentAdapter(contentArrayList, this)
                contentRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                contentViewModel.contentListLiveData.postValue(null)
            }
        })

        contentViewModel.headingContentLiveData.observe(viewLifecycleOwner, Observer { content ->
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


        return view
    }

    override fun onClickItem(content: Content, position: Int) {
        val action = SleepFragmentDirections.actionSleepFragmentToContentDetailDarkFragment(content)
        findNavController().navigate(action)
    }

    private fun headingContentClickItem(content: Content){
        val action = SleepFragmentDirections.actionSleepFragmentToContentDetailDarkFragment(content)
        findNavController().navigate(action)
    }

    private fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_sleepFragment_to_splashScreenFragment)
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