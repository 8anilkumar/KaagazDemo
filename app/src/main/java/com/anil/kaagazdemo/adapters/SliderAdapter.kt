package com.anil.kaagazdemo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.database.ImageEntity
import com.anil.kaagazdemo.databinding.ListItemImgBinding
import com.bumptech.glide.Glide

class SliderAdapter(private val fileArray: List<ImageEntity>) : RecyclerView.Adapter<SliderAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ListItemImgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            val uriString = "file://${imagePath}"
            Glide.with(binding.root).load(uriString).into(binding.localImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ListItemImgBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fileArray[position].imgPath?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }
}