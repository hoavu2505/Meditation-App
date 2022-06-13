package com.example.meditation.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.constant.Constant
import com.example.meditation.databinding.FragmentAccountInfoBinding
import com.example.meditation.model.Statistic
import com.example.meditation.viewmodel.StatisticViewModel
import com.example.meditation.viewmodel.UserViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.jar.Manifest

class AccountInfoFragment : Fragment(), LifecycleOwner {
    private lateinit var binding : FragmentAccountInfoBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var docRef : DocumentReference

    private lateinit var userViewModel: UserViewModel
    private lateinit var statisticViewModel: StatisticViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }

        returnTransition = MaterialElevationScale(true).apply {
            duration = 50L
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        statisticViewModel = ViewModelProvider(this)[StatisticViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()

//        userViewModel.getDataUser()

        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReferenceFromUrl("gs://meditation-app-ec6d8.appspot.com/")
//        docRef = FirebaseFirestore.getInstance().collection("User").document(mAuth.uid!!)

        userViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer { user ->
            if (user != null){
                GlobalScope.launch(Dispatchers.Main) {
                    Log.d("avatarInfo", "${user.avatar}")

                    binding.lyProgress.visibility = View.INVISIBLE

                    Glide.with(requireContext()).load(user.avatar)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.circleAvatar)

                    binding.tvName.text = user.name

                }

                docRef = FirebaseFirestore.getInstance().collection("User").document(user.id)

                when(user.social_network){
                    "none" -> binding.circleAvatar.setOnClickListener { onClickRequestPermission() }
                    else -> binding.circleAvatar.setOnClickListener {
                        Toast.makeText(requireContext(),
                            "Tài khoản đăng ký bằng ${user.social_network} không thể thay đổi avatar ở đây.",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })

        statisticViewModel.getData()
        statisticViewModel.statisticLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                if (it.totalTimeMeditate != null)   {
                    val time = (it.totalTimeMeditate!! / 1000) / 60
                    binding.tvTotalTimeMeditate.text = time.toString()
                }
                if (it.sessionsCompleted != null)   binding.tvSession.text = it.sessionsCompleted.toString()

                when{
                    it.todayTimeMeditate != null -> {
                        val time = (it.todayTimeMeditate!! / 1000) / 60
                        binding.tvTodayTimeMeditate.text = time.toString()
                    }
                    it.totalTimeMeditate != null -> {
                        val time = (it.totalTimeMeditate!! / 1000) / 60
                        binding.tvTotalTimeMeditate.text = time.toString()
                    }
                    it.sessionsCompleted != null -> {
                        binding.tvSession.text = it.sessionsCompleted.toString()
                    }

                }
            }
            else{
                statisticViewModel.createData(Statistic(0,0, 0))
//                binding.tvTotalTimeMeditate.text = "0"
//                binding.tvSession.text = "0"
            }
        })

        binding = FragmentAccountInfoBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgSetting.setOnClickListener {
            findNavController().navigate(R.id.action_accountInfoFragment_to_settingAccountFragment)
            materialMotion()
        }

        binding.imgClose.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){  isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            }   else {
                Toast.makeText(requireContext(), getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }

    private fun onClickRequestPermission(){
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImage()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                Toast.makeText(requireContext(), getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            else ->{
                requestPermissionLauncher.launch(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constant.KeySwitch.REQUEST_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.KeySwitch.REQUEST_IMAGE_CODE && data != null){

            GlobalScope.launch (Dispatchers.IO) {
                val uri : Uri = data.data!!
                val bitmap : Bitmap = MediaStore
                    .Images.Media.getBitmap(requireActivity().contentResolver, uri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
                val imageData = byteArrayOutputStream.toByteArray()

                val id = UUID.randomUUID().toString()

                val filePath = "User/${mAuth.uid}/avatar-$id.jpg"
                val pathRef = storageRef.child(filePath)


//                val taskUpload = launch {
//                    pathRef.putBytes(imageData)
//                    delay(3000)
//                }
//                taskUpload.join()

                val taskUpload = async {
                    pathRef.putBytes(imageData)
                    Log.d("taskUpload1", "Done")
                    delay(3000)
                }

                withContext(Dispatchers.Main){
                    binding.lyProgress.visibility = View.VISIBLE
                }
                taskUpload.await()

                Log.d("taskUpload2", "Done")

                pathRef.downloadUrl.addOnSuccessListener {
                    docRef.update("avatar", it)
                }.addOnFailureListener {
                    Log.d("error", it.toString())
                }

            }

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