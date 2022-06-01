package com.example.meditation.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.meditation.model.Content
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class HomeContentRepository {
    var cardContent1 = MutableLiveData<Content?>()
    var cardContent2 = MutableLiveData<Content?>()
    var cardDailyContent = MutableLiveData<Content?>()
    var contentListLiveData = MutableLiveData<ArrayList<Content>?>()

    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun cardContent1CallData(){
        val docRef = db.collection("Meditation")
            .document("o2JiWmcCljD11UCL9ynW")
            .collection("Content")
            .whereEqualTo("type", "Course")

        docRef.addSnapshotListener { value, error ->
            if (error != null){
                cardContent1.postValue(null)
                return@addSnapshotListener
            }

            for (doc in value?.documentChanges!!){
                if (doc.type == DocumentChange.Type.ADDED){
                    cardContent1.postValue(doc.document.toObject(Content::class.java))
                }
            }

        }
    }

    fun cardContent2CallData(){
        val docRef = db.collection("Focus")
            .document("Vfr23K0s4E2k6Wx18P48")
            .collection("Content")
            .whereEqualTo("type", "Sound")

        docRef.addSnapshotListener { value, error ->
            if (error != null){
                cardContent2.postValue(null)
                return@addSnapshotListener
            }

            for (doc in value?.documentChanges!!){
                if (doc.type == DocumentChange.Type.ADDED){
                    cardContent2.postValue(doc.document.toObject(Content::class.java))
                }
            }

        }
    }

    fun cardDailyContentCallData(){
        val docRef = db.collection("Meditation")
            .document("o2JiWmcCljD11UCL9ynW")
            .collection("Content")
            .whereEqualTo("name", "Breathe")

        docRef.addSnapshotListener { value, error ->
            if (error != null){
                cardDailyContent.postValue(null)
                return@addSnapshotListener
            }

            for (doc in value?.documentChanges!!){
                if (doc.type == DocumentChange.Type.ADDED){
                    cardDailyContent.postValue(doc.document.toObject(Content::class.java))
                }
            }

        }
    }

    fun callData(){
        var contentArrayList = ArrayList<Content>()
        val meditateCategoryID = ArrayList<String>()
        val focusCategoryID = ArrayList<String>()

        val docRefSleep = db.collection("Sleep")
        docRefSleep.addSnapshotListener { value, error ->
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
        }

        //Get id from meditation category
        val docRefMeditation = db.collection("Meditation")
        docRefMeditation.get().addOnSuccessListener { result ->
            if (result != null){
                for (doc in result){
                    meditateCategoryID.add(doc.data["id"] as String)
                }
                Log.d("Meditation Category ID: ", "$meditateCategoryID")
            }

            //Get data from id of Meditation
            for (id in meditateCategoryID){
                val docRef = db.collection("Meditation")
                    .document(id).collection("Content")
                docRef.addSnapshotListener { value, error ->
                    if (error != null){
                        return@addSnapshotListener
                    }

                    for (doc in value?.documentChanges!!){
                        if (doc.type == DocumentChange.Type.ADDED){
                            contentArrayList.add(doc.document.toObject(Content::class.java))
                        }
                    }

                }

            }
        }

        //Get id from focus category
        val docRefFocus = db.collection("Focus")
        docRefFocus.get().addOnSuccessListener { result ->
            if (result != null){
                for (doc in result){
                    focusCategoryID.add(doc.data["id"] as String)
                }
            }

            //Get data from id of Focus
            for (id in focusCategoryID){
                val docRef = db.collection("Focus")
                    .document(id).collection("Content")
                docRef.addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener

                    for (doc in value?.documentChanges!!){
                        if (doc.type == DocumentChange.Type.ADDED){
                            contentArrayList.add(doc.document.toObject(Content::class.java))
                        }
                    }

                    contentArrayList.shuffle()
                    contentArrayList = contentArrayList.slice(0..4) as ArrayList<Content>
                    contentListLiveData.postValue(contentArrayList)
                }
            }
        }

    }
}