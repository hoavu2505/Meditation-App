package com.example.meditation.ui

import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
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
import com.example.meditation.util.CheckingInternet
import com.example.meditation.viewmodel.SleepContentViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SleepFragment : Fragment(), SleepContentAdapter.OnItemClickListerner, LifecycleOwner {

    private lateinit var binding : FragmentSleepBinding
    private lateinit var contentRecyclerView : RecyclerView
    private lateinit var adapter: SleepContentAdapter
    private lateinit var contentViewModel: SleepContentViewModel

    private val checkingInternet by lazy { CheckingInternet(requireActivity().application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentViewModel = ViewModelProvider(this)[SleepContentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        showNavBar()

        contentViewModel.callData()
        contentViewModel.headingContentCallData()

        binding = FragmentSleepBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        val notConnected = requireActivity().findViewById<RelativeLayout>(R.id.ly_not_connected)

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

        Theme.changeColorStatusBar(requireActivity().window, R.color.dark_background, context)
        Theme.setStatusBarLightText(requireActivity().window ,true)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun updateUI(){
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
    }

    override fun onClickItem(itemView: View, content: Content, position: Int) {
        materialMotion()
        val action = SleepFragmentDirections.actionSleepFragmentToContentDetailDarkFragment(content)
        findNavController().navigate(action)
    }

    private fun headingContentClickItem(content: Content){
        materialMotion()
        val action = SleepFragmentDirections.actionSleepFragmentToContentDetailDarkFragment(content)
        findNavController().navigate(action)
    }

    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

    private fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

}