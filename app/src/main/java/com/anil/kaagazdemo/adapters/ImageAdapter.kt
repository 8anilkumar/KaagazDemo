package com.anil.kaagazdemo.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.databinding.ImageRowBinding
import com.anil.kaagazdemo.interfaces.PhotoUpdateInterface
import com.anil.kaagazdemo.model.ImageEntity
import com.bumptech.glide.Glide

internal class ImageAdapter( private var imageUriList: MutableList<ImageEntity> = mutableListOf(),
    val listener: PhotoUpdateInterface) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    lateinit var binding: ImageRowBinding

    internal inner class MyViewHolder(private val binding: ImageRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(uri: Uri, position: Int) {
            Glide.with(binding.imageView.context).load(uri).into(binding.imageView)
            binding.imgCancel.setOnClickListener {
                imageUriList.removeAt(position)
                listener.shouldDisplaySaveButton(imageUriList.size > 0)
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newImageUtilList: List<ImageEntity>) {
        imageUriList = newImageUtilList as MutableList<ImageEntity>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ImageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageUri = imageUriList[position]
        holder.binding(Uri.parse(imageUri.imgPath), position)
    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }
}



