package com.example.meditation.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.meditation.model.Content
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class SleepContentRepository {
    var contentListLiveData = MutableLiveData<ArrayList<Content>?>()
    var headingContentLiveData = MutableLiveData<Content?>()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun callData(){
        val contentArrayList = ArrayList<Content>()

        val docRef = db.collection("Sleep")
        docRef.addSnapshotListener { value, error ->
            if (error != null){
                Log.e("Firestore error", error.message.toString())
                contentListLiveData.postValue(null)
                return@addSnapshotListener
            }

            for (doc in value?.documentChanges!!){
                if (doc.type == DocumentChange.Type.ADDED){
                    contentArrayList.add(doc.document.toObject(Content::class.java))
                }
            }
            contentListLiveData.postValue(contentArrayList)
        }
    }

    fun headingContentCallData(){
        val docRef = db.collection("Heading Content")
            .whereEqualTo("mode", "Sleep")

        docRef.addSnapshotListener { value, error ->
            if (error != null){
                headingContentLiveData.postValue(null)
                return@addSnapshotListener
            }

            for (doc in value?.documentChanges!!){
                if (doc.type == DocumentChange.Type.ADDED){
                    headingContentLiveData.postValue(doc.document.toObject(Content::class.java))
                }
            }

        }
    }
}