package com.example.meditation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.adapter.HomeContentAdapter
import com.example.meditation.constant.Constant
import com.example.meditation.databinding.FragmentHomeBinding
import com.example.meditation.model.Content
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.example.meditation.util.CheckingInternet
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.example.meditation.viewmodel.HomeContentViewModel
import com.example.meditation.viewmodel.UserViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), LifecycleOwner, HomeContentAdapter.OnItemClickListerner {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var adapter: HomeContentAdapter
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeContentViewModel: HomeContentViewModel
    private lateinit var db : FirebaseFirestore

    private val checkingInternet by lazy { CheckingInternet(requireActivity().application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }

        returnTransition = MaterialElevationScale(true).apply {
            duration = 50L
        }

        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        homeContentViewModel = ViewModelProvider(this)[HomeContentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        showNavBar()

        homeContentViewModel.cardContent1CallData()
        homeContentViewModel.cardContent2CallData()
        homeContentViewModel.cardDailyContentCallData()
        homeContentViewModel.callData()

        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        val notConnected = requireActivity().findViewById<RelativeLayout>(R.id.ly_not_connected)

        checkingInternet.isConnected(requireActivity().application)

        checkingInternet.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.root.visibility = View.VISIBLE
//                binding.lyNotConnected.visibility = View.GONE
                notConnected.visibility = View.GONE
                updateUI()
            }
            else{
//                Toast.makeText(requireContext(), "Không có kết nối Internet.", Toast.LENGTH_SHORT).show()
                binding.root.visibility = View.INVISIBLE
//                binding.lyNotConnected.visibility = View.VISIBLE
                notConnected.visibility = View.VISIBLE
            }

            Log.d("network: ", "$it")
        })

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun updateUI(){
        firebaseAuthViewModel.signOutMutableLiveData.observe(viewLifecycleOwner, Observer { logout ->
            if (logout){
                findNavController().navigate(R.id.action_homeFragment_to_homeLoginFragment)
                hideNavBar()
            } else{
                firebaseAuthViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
                    if (it != null){
                        userViewModel.getDataUser()
                    }
                })
            }
        })

        userViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                Glide.with(requireContext()).load(it.avatar)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.circleAvatar)

                binding.tvAccountName.text = it.name

                Log.d("avatar", "${it.avatar}")
            }
        })

        homeContentViewModel.cardContent1.observe(viewLifecycleOwner, Observer { content ->
            if (content != null){
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(requireContext()).load(content.img)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.imgHome1)

                    binding.tvName1.text = content.name
                    binding.tvType1.text = content.type!!.uppercase()
                    binding.tvDuration1.text = content.duration.toString() + " PHÚT"
                }

                binding.btnStart1.setOnClickListener { homeContentClickItem(content) }
            }
        })

        homeContentViewModel.cardContent2.observe(viewLifecycleOwner, Observer { content ->
            if (content != null){
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(requireContext()).load(content.img)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.imgHome2)

                    binding.tvName2.text = content.name
                    binding.tvType2.text = content.type!!.uppercase()
                    binding.tvDuration2.text = content.duration.toString() + " PHÚT"
                }

                binding.btnStart2.setOnClickListener { homeContentClickItem(content) }
            }
        })

        homeContentViewModel.cardDailyContent.observe(viewLifecycleOwner, Observer { content ->
            if (content != null){
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(requireContext()).load(content.img)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.imgDaily)

                    binding.tvNameDaily.text = content.name
                    binding.tvModeDaily.text = content.mode!!.uppercase()
                    binding.tvDurationDaily.text = content.duration.toString() + " PHÚT"
                }

                binding.cardDaily.setOnClickListener { homeContentClickItem(content) }
            }
        })

        db = FirebaseFirestore.getInstance()
        compareTime()

        contentRecyclerView = binding.rcvSuggestHome
        contentRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)

        homeContentViewModel.contentListLiveData.observe(viewLifecycleOwner, Observer { contentArrayList ->
            if (contentArrayList != null){
                adapter = HomeContentAdapter(contentArrayList, this)
                contentRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })

        binding.circleAvatar.setOnClickListener {
            materialMotion()
            findNavController().navigate(R.id.action_homeFragment_to_accountInfoFragment)
            hideNavBar()
        }

    }

    private fun homeContentClickItem(content: Content) {
        when (content.type){
            "Sound" ->{
                val action = HomeFragmentDirections.actionHomeFragmentToContentDetailLightFragment(content.id!!, content)
                findNavController().navigate(action)
            }
            "Course" ->{
                val action = HomeFragmentDirections.actionHomeFragmentToCourseDetailFragment(content)
                findNavController().navigate(action)
            }
        }

        hideNavBar()
    }


    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    @SuppressLint("SimpleDateFormat")
    private fun compareTime(){
        val date : Date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm")
        val currentTime = dateFormat.format(date)
        val dateAfterFormat : Date = dateFormat.parse(currentTime) as Date


        when{
            dateAfterFormat.after(dateFormat.parse(Constant.SwitchTime.MIDNIGHT_2))
                    && dateAfterFormat.before(dateFormat.parse(Constant.SwitchTime.MORNING)) -> {
                binding.tvHello.text = resources.getStringArray(R.array.time)[0]
            }
            dateAfterFormat.after(dateFormat.parse(Constant.SwitchTime.MORNING))
                    && dateAfterFormat.before(dateFormat.parse(Constant.SwitchTime.AFTERNOON))-> {
                binding.tvHello.text = resources.getStringArray(R.array.time)[1]
            }
            dateAfterFormat.after(dateFormat.parse(Constant.SwitchTime.AFTERNOON))
                    && dateAfterFormat.before(dateFormat.parse(Constant.SwitchTime.EVENING))-> {
                binding.tvHello.text = resources.getStringArray(R.array.time)[2]
            }
            dateAfterFormat.after(dateFormat.parse(Constant.SwitchTime.EVENING))
                    && dateAfterFormat.before(dateFormat.parse(Constant.SwitchTime.MIDNIGHT)) -> {
                binding.tvHello.text = resources.getStringArray(R.array.time)[3]
            }

        }

    }


    private fun onBackPressed(){
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onClickItem(content: Content, position: Int) {
        if (content.mode == "Sleep"){
            val action = HomeFragmentDirections.actionHomeFragmentToContentDetailDarkFragment(content)
            findNavController().navigate(action)
            hideNavBar()
        }else{
            materialMotion()
            when(content.type){
                "Sound" -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToContentDetailLightFragment(content.id!!, content)
                    findNavController().navigate(action)
                }
                "Course" -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToCourseDetailFragment(content)
                    findNavController().navigate(action)
                }
                "Video" -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToPlayContentVideoFragment(content)
                    findNavController().navigate(action)
                }
            }
            hideNavBar()
        }
    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

}