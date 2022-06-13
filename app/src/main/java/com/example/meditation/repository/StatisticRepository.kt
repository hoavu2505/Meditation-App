package com.example.meditation.repository

import androidx.lifecycle.MutableLiveData
import com.example.meditation.model.Statistic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StatisticRepository {
    var statisticLiveData = MutableLiveData<Statistic?>()
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    fun getData() {
        mAuth.currentUser?.let {
            val docRef = db.collection("User")
                .document(mAuth.currentUser!!.uid)
                .collection("UserStatistic")
                .document("Statistics")

            docRef.addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null && value.exists()){
                    val statistic = Statistic(
                        value.data!!["todayTimeMeditate"] as Long,
                        value.data!!["totalTimeMeditate"] as Long,
                        value.data!!["sessionsCompleted"] as Long
                    )

                    statisticLiveData.postValue(statistic)
                }
                else    statisticLiveData.postValue(null)
            }
        }

    }

    fun createData(statistic: Statistic){
        db.collection("User")
            .document(mAuth.currentUser!!.uid)
            .collection("UserStatistic")
            .document("Statistics")
            .set(statistic)

        statisticLiveData.postValue(statistic)
    }

    fun updateData(statistic: Statistic){
        val docRef = db.collection("User")
            .document(mAuth.currentUser!!.uid)
            .collection("UserStatistic")
            .document("Statistics")

        docRef.update("todayTimeMeditate", statistic.todayTimeMeditate)
        docRef.update("totalTimeMeditate", statistic.totalTimeMeditate)
        docRef.update("sessionsCompleted", statistic.sessionsCompleted)

//        statisticLiveData.postValue(statistic)
    }

    fun resetTodayTime(){
        val docRef = db.collection("User")
            .document(mAuth.currentUser!!.uid)
            .collection("UserStatistic")
            .document("Statistics")

        docRef.update("todayTimeMeditate", 0)
    }

}