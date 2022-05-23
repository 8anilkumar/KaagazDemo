package com.anil.kaagazdemo.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.databinding.ImageRowBinding
import com.anil.kaagazdemo.interfaces.PhotoUpdateInterface
import com.bumptech.glide.Glide

internal class ImageAdapter( private var imageUriList: MutableList<Uri> = mutableListOf(),
    val listener: PhotoUpdateInterface) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    lateinit var binding: ImageRowBinding

    internal inner class MyViewHolder(private val binding: ImageRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(uri: Uri, position: Int) {
            Glide.with(binding.imageView.context).load(uri).into(binding.imageView)
            binding.imgCancel.setOnClickListener {
                imageUriList.removeAt(position)
                if (imageUriList.size > 0) {
                    listener.shouldDisplaySaveButton(true)
                } else {
                    listener.shouldDisplaySaveButton(false)
                }
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newImageUtilList: List<Uri>) {
        imageUriList = newImageUtilList as MutableList<Uri>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ImageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageUri = imageUriList[position]
        holder.binding(imageUri, position)
    }

    override fun getItemCount(): Int {
        return imageUriList.size ?:0
    }
}



