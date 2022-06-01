package com.example.meditation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.meditation.R
import com.example.meditation.model.Category

class CategoryAdapter(private var categoryList: ArrayList<Category>, var onClick: OnItemClickListener) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface OnItemClickListener{
        fun onClickItem(category: Category, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_staggered, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
//        val currentItem = categoryList[position]
//        holder.imgCategory.setImageResource(currentItem.image)
//        holder.txtCategoryName.text = currentItem.category_name

        holder.initialize(categoryList[position], onClick, holder.imgCategory.context)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtCategoryName : TextView = itemView.findViewById(R.id.tv_category_name)
        val imgCategory : ImageView = itemView.findViewById(R.id.img_category)

        fun initialize(category: Category, action: OnItemClickListener, context: Context){
//            imgCategory.setImageResource(category.img!!)
            Glide.with(context).load(category.img)
                .apply(RequestOptions().override(500, 800))
                .placeholder(R.drawable.ic_placeholder)
                .into(imgCategory)

            txtCategoryName.text = category.name

            itemView.setOnClickListener {
                action.onClickItem(category, adapterPosition)
            }
        }
    }

}