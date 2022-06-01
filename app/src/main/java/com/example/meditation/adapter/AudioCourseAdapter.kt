package com.example.meditation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.CourseDetailFragment
import com.example.meditation.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AudioCourseAdapter(private var audioCourseList: ArrayList<String>, var onClick: OnItemClickListener) : RecyclerView.Adapter<AudioCourseAdapter.AudioViewHolder>() {

    interface OnItemClickListener{
        fun onClickItem(audioCourse: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_audio_item, parent, false)
        return AudioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.initialize(audioCourseList[position], onClick, position)
    }

    override fun getItemCount(): Int {
        return audioCourseList.size
    }

    class AudioViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val btnPlay : FloatingActionButton = itemView.findViewById(R.id.btn_play)
        val txtName : TextView = itemView.findViewById(R.id.tv_name)


        @SuppressLint("SetTextI18n")
        fun initialize(audioCourse: String, action: OnItemClickListener, position: Int) {
            txtName.text = (position + 1).toString()

            btnPlay.setOnClickListener {
                action.onClickItem(audioCourse, adapterPosition)
            }
        }
    }

}