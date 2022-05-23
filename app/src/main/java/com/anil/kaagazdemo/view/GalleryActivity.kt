package com.anil.kaagazdemo.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.anil.kaagazdemo.CameraActivity
import com.anil.kaagazdemo.adapters.GalleryAdapter
import com.anil.kaagazdemo.adapters.SliderAdapter
import com.anil.kaagazdemo.database.AlbumEntity
import com.anil.kaagazdemo.database.ImageEntity
import com.anil.kaagazdemo.databinding.ActivityGalleryBinding
import com.anil.kaagazdemo.databinding.PhotosBgBinding
import com.anil.kaagazdemo.interfaces.AlbumListner
import com.anil.kaagazdemo.interfaces.PhotoUpdateInterface
import com.anil.kaagazdemo.utils.DatabaseHandler


class GalleryActivity : AppCompatActivity() , AlbumListner{

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var bindingPhotosBgBinding: PhotosBgBinding
    private lateinit var databaseHandler: DatabaseHandler
    private var albumList: List<AlbumEntity>? = emptyList()
    private lateinit var adapter: GalleryAdapter
    private var albumListData:MutableList<AlbumEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        setUpDB()
        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        retrieveImageList()
    }

    private fun retrieveImageList() {
        albumList = databaseHandler.imageInterface()?.getAlbum()
        albumList?.let {
            if(it.isNotEmpty()){
                binding.recyclerview.visibility = View.VISIBLE
                binding.tvNoAlbum.visibility = View.GONE
                adapter.updateList(it)
            }else{
                binding.recyclerview.visibility = View.GONE
                binding.tvNoAlbum.visibility = View.VISIBLE
            }
        }
    }

    private fun initListeners(){
        binding.cameraFab.setOnClickListener{
            startActivity(Intent(this@GalleryActivity, CameraActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        adapter = GalleryAdapter(albumListData,this)
        binding.recyclerview.layoutManager = GridLayoutManager(binding.root.context, 3)
        binding.recyclerview.adapter = adapter
    }

    private fun setUpDB() {
        databaseHandler = Room.databaseBuilder(this@GalleryActivity, DatabaseHandler::class.java, "IMAGE_TABLE")
            .allowMainThreadQueries().build()
    }

    override fun albumListener(mutableList: List<ImageEntity>) {
        val alertDialog = AlertDialog.Builder(binding.root.context).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingPhotosBgBinding = PhotosBgBinding.inflate(layoutInflater)
        bindingPhotosBgBinding.imgCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        val adapter = SliderAdapter(mutableList)
        bindingPhotosBgBinding.viewPager.adapter = adapter
        alertDialog.setView(bindingPhotosBgBinding.root)
        alertDialog.show()
    }

}