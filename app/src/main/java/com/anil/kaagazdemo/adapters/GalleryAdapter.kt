package com.anil.kaagazdemo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.database.AlbumEntity
import com.anil.kaagazdemo.database.ImageEntity
import com.anil.kaagazdemo.databinding.AlbumbRowBinding
import com.anil.kaagazdemo.interfaces.AlbumListner
import com.bumptech.glide.Glide

class GalleryAdapter(private var albumList: MutableList<AlbumEntity> = mutableListOf(),
    private var albumblistner: AlbumListner) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: AlbumbRowBinding) : RecyclerView.ViewHolder(binding.root) {
        var albumbLayout: LinearLayout =  binding.albumbLayout
        fun bind(imageData: ImageEntity) {
            val uriString = "file://${imageData.imgPath}"
            Glide.with(binding.root).load(uriString).into(binding.imageView)
        }
        fun bindName(albumbName: String) {
            binding.txtFileName.text = albumbName
        }
    }

    fun updateList(newImageUtilList: List<AlbumEntity>) {
        albumList = newImageUtilList as MutableList<AlbumEntity>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(AlbumbRowBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        albumList[position].let {
            if(it.imageListEntity.imageList.isNotEmpty()){
                holder.bind(it.imageListEntity.imageList.first())
            }
        }

        albumList[position].albumName.let {
            holder.bindName(it)
        }

        holder.albumbLayout.setOnClickListener {
            albumblistner.albumListener(albumList[position].imageListEntity.imageList)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }
}

