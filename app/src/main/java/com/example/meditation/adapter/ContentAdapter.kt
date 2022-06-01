package com.example.meditation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.model.Content

class ContentAdapter (private var contentList: ArrayList<Content>, var onClick: OnItemClickListener) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    interface OnItemClickListener{
        fun onClickItem(content: Content, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_content, parent, false)
        return ContentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.init(contentList[position], onClick, holder.imgContent.context)
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    class ContentViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtName : TextView = itemView.findViewById(R.id.tv_name)
        val txtType : TextView = itemView.findViewById(R.id.tv_type)
        val txtDuration : TextView = itemView.findViewById(R.id.tv_duration)
        val imgContent : ImageView = itemView.findViewById(R.id.img_content)

        fun init(content: Content, action: OnItemClickListener, context: Context){
            txtName.text = content.name
            txtType.text = content.type!!.uppercase()
            txtDuration.text = content.duration.toString() + " PHÚT"
            Glide.with(context).load(content.img).into(imgContent)

            itemView.setOnClickListener {
                action.onClickItem(content, adapterPosition)
            }
        }
    }

}