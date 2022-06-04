package com.example.meditation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.example.meditation.viewmodel.HomeContentViewModel
import com.example.meditation.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), LifecycleOwner, HomeContentAdapter.OnItemClickListerner {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var contentArrayList: ArrayList<Content>
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeContentViewModel: HomeContentViewModel
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        homeContentViewModel = ViewModelProvider(this)[HomeContentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeContentViewModel.cardContent1CallData()
        homeContentViewModel.cardContent2CallData()
        homeContentViewModel.cardDailyContentCallData()
        homeContentViewModel.callData()

        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        firebaseAuthViewModel.signOutMutableLiveData.observe(viewLifecycleOwner, Observer { logout ->
            if (logout){
                findNavController().navigate(R.id.action_homeFragment_to_homeLoginFragment)
                hideNavBar()
            } else{
                firebaseAuthViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
                    getDisplayName(it)
                    userViewModel.getDataUser()
                })
            }
        })

        showNavBar()

        userViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                Glide.with(requireContext()).load(it.avatar)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.circleAvatar)
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

        homeContentViewModel.contentListLiveData.observe(viewLifecycleOwner, Observer { contentArrayList ->
            if (contentArrayList != null){
                var adapter = HomeContentAdapter(contentArrayList, this)
                contentRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })

        db = FirebaseFirestore.getInstance()
        compareTime()

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)

        contentRecyclerView = binding.rcvSuggestHome
        contentRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)

        binding.circleAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_accountInfoFragment)
            hideNavBar()
        }

//        onBackPressed()

        return view
    }

    private fun homeContentClickItem(content: Content) {
        val action = HomeFragmentDirections.actionHomeFragmentToContentDetailLightFragment(content.id!!, content)
        findNavController().navigate(action)
        hideNavBar()
    }


    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun getDisplayName(currentUser : FirebaseUser){
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val docRef = db.collection("User")
            .document(currentUser.uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null){
                Log.d("TAG", "DocumentSnapshot data: ${document.data!!["name"]}")
                binding.tvAccountName.text = document.data!!["name"].toString()
            }else{
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with", exception)
            }
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

}