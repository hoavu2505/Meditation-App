package com.example.meditation.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.meditation.adapter.CategoryAdapter
import com.example.meditation.model.Category
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class MeditationCategoryRepository {
    var categoryListLiveData = MutableLiveData<ArrayList<Category>?>()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun callData() {
        val categoryArrayList = arrayListOf<Category>()
        val docRef = db.collection("Meditation")
        docRef.addSnapshotListener { value, error ->
            if (error != null){
                Log.e("Firestore error", error.message.toString())
                categoryListLiveData.postValue(null)
                return@addSnapshotListener
            }

            for (doc in value?.documentChanges!!){
                if (doc.type == DocumentChange.Type.ADDED){
                    categoryArrayList.add(0, doc.document.toObject(Category::class.java))
                }
            }
            categoryListLiveData.postValue(categoryArrayList)
        }
    }
}