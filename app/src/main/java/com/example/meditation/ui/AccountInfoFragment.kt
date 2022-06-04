package com.example.meditation.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.constant.Constant
import com.example.meditation.databinding.FragmentAccountInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AccountInfoFragment : Fragment() {
    private lateinit var binding : FragmentAccountInfoBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var docRef : DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReferenceFromUrl("gs://meditation-app-ec6d8.appspot.com/")
        docRef = FirebaseFirestore.getInstance().collection("User").document(mAuth.uid!!)

        currentUser!!.let {
            getDisplayName(currentUser)
        }

        GlobalScope.launch(Dispatchers.Main) {
            downloadImg(storageRef, requireContext())
        }


        binding = FragmentAccountInfoBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgSetting.setOnClickListener { findNavController().navigate(R.id.action_accountInfoFragment_to_settingAccountFragment) }

        binding.circleAvatar.setOnClickListener { pickImage() }

        binding.imgClose.setOnClickListener { findNavController().navigate(R.id.action_accountInfoFragment_to_homeFragment) }

        return view
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constant.KeySwitch.REQUEST_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.KeySwitch.REQUEST_IMAGE_CODE && data != null){

            val uri : Uri = data.data!!
            val bitmap : Bitmap = MediaStore
                .Images.Media.getBitmap(requireActivity().contentResolver, uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageData = byteArrayOutputStream.toByteArray()

            val filePath = "User/${mAuth.uid}/avatar.jpg"
            val pathRef = storageRef.child(filePath)

            pathRef.putBytes(imageData)
            pathRef.downloadUrl.addOnSuccessListener {
//                Glide.with(this).load(it).into(binding.circleAvatar)
                binding.circleAvatar.setImageBitmap(bitmap)
                docRef.update("avatar", it)
            }.addOnFailureListener {
                Log.d("error", it.toString())
            }

        }
    }

    private suspend fun downloadImg(storageReference: StorageReference, context: Context){
        withContext(Dispatchers.IO){
            storageReference.child("User/${mAuth.uid}/avatar.jpg").downloadUrl.addOnSuccessListener {

                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(context).load(it)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.circleAvatar)
                }

            }.addOnFailureListener { e ->
                Log.d("error download image", e.toString())

                storageReference.child("default_avatar.png").downloadUrl.addOnSuccessListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        Glide.with(context).load(it)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(binding.circleAvatar)
                    }
                }.addOnFailureListener {
                    Log.d("error download default image", it.toString())
                }

            }
        }
    }


    private fun getDisplayName(currentUser : FirebaseUser){
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val docRef = db.collection("User")
            .document(currentUser.uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null){
                Log.d("TAG", "DocumentSnapshot data: ${document.data!!["name"]}")
                binding.tvName.text = document.data!!["name"].toString()
            }else{
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with", exception)
            }
    }

}