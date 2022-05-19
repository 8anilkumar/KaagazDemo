package com.anil.kaagazdemo.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.ImageEntity
import com.anil.kaagazdemo.R
import com.anil.kaagazdemo.databinding.AlbumbRowBinding
import com.anil.kaagazdemo.databinding.ListItemImgBinding
import com.bumptech.glide.Glide
import java.io.File

class GalleryAdapter(private var imageList:MutableList<ImageEntity> = mutableListOf()) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: AlbumbRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Glide.with(binding.root).load(image.toUri()).into(binding.imageView)
        }

        fun bindName(name: String) {
            binding.txtFileName.text = name
        }

    }

    fun updateList(newImageUtilList: List<ImageEntity>) {
        imageList = newImageUtilList as MutableList<ImageEntity>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(AlbumbRowBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        imageList[position].imgPath?.let { holder.bind(it) }
        imageList[position].timeStamp?.let { holder.bindName(it) }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

