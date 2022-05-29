package com.anil.kaagazdemo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.model.AlbumEntity
import com.anil.kaagazdemo.model.ImageEntity
import com.anil.kaagazdemo.databinding.AlbumbRowBinding
import com.anil.kaagazdemo.interfaces.AlbumListner
import com.bumptech.glide.Glide

class GalleryAdapter(
    private var albumList: MutableList<AlbumEntity> = mutableListOf(),
    private var albumblistner: AlbumListner
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: AlbumbRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var albumLayout: LinearLayout = binding.albumbLayout
        fun bind(imageData: ImageEntity, albumName: String) {
            val uriString = "${imageData.imgPath}"
            Glide.with(binding.root).load(uriString).into(binding.imageView)
            binding.txtFileName.text = albumName
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
        val album = albumList[position]
        if (album.imageListEntity.isNotEmpty()) {
            holder.bind(album.imageListEntity.first(), album.albumName)
        }
        holder.albumLayout.setOnClickListener {
            albumblistner.albumListener(albumList[position].imageListEntity)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }
}

