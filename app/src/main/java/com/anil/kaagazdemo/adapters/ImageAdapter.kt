package com.anil.kaagazdemo.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.R
import com.bumptech.glide.Glide

internal class ImageAdapter(private var imageUriList:MutableList<Uri> = mutableListOf()) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageView)
        var imgCancel: ImageView = view.findViewById(R.id.imgCancel)
    }

    fun updateList(newImageUtilList: List<Uri>) {
         imageUriList = newImageUtilList as MutableList<Uri>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image = imageUriList.get(position)
        Glide.with(holder.itemView.context).load(image).into(holder.imageView)
        holder.imgCancel.setOnClickListener {
            imageUriList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imageUriList.size ?:0
    }
}



