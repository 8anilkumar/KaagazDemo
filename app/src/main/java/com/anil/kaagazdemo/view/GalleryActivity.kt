package com.anil.kaagazdemo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.anil.kaagazdemo.ImageEntity
import com.anil.kaagazdemo.adapters.GalleryAdapter
import com.anil.kaagazdemo.databinding.ActivityGalleryBinding
import com.anil.kaagazdemo.utils.DatabaseHandler


class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var databaseHandler: DatabaseHandler
    private var imageList: List<ImageEntity>? = emptyList()
    private lateinit var adapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpDB()
        initRecyclerView()
        retriveImageList()

    }

    private fun retriveImageList() {
        imageList = databaseHandler.imageInterface()?.getAlbumb()
        imageList?.let { adapter.updateList(it) }

    }

    private fun initRecyclerView() {
        adapter = GalleryAdapter()
        binding.recyclerview.layoutManager = GridLayoutManager(binding.root.context, 2)
        binding.recyclerview.adapter = adapter
    }

    private fun setUpDB() {
        databaseHandler = Room.databaseBuilder(this@GalleryActivity, DatabaseHandler::class.java, "IMAGE_TABLE")
            .allowMainThreadQueries().build()
    }

}