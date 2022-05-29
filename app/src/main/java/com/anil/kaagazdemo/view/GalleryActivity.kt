package com.anil.kaagazdemo.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Camera
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.anil.kaagazdemo.adapters.GalleryAdapter
import com.anil.kaagazdemo.adapters.SliderAdapter
import com.anil.kaagazdemo.model.AlbumEntity
import com.anil.kaagazdemo.model.ImageEntity
import com.anil.kaagazdemo.databinding.ActivityGalleryBinding
import com.anil.kaagazdemo.databinding.PhotosBgBinding
import com.anil.kaagazdemo.interfaces.AlbumListner
import com.anil.kaagazdemo.data.database.DatabaseHandler
import com.anil.kaagazdemo.viewmodel.CameraViewModel
import com.anil.kaagazdemo.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity(), AlbumListner {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var bindingPhotosBgBinding: PhotosBgBinding
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var adapter: GalleryAdapter
    private var albumListData: MutableList<AlbumEntity> = mutableListOf()
    private lateinit var mainViewModel: GalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModels()
        initListeners()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        getSavedAlbumsFromDB()
    }

    /** Init methods */

    private fun setViewModels(){
        mainViewModel = ViewModelProvider(this@GalleryActivity)[GalleryViewModel::class.java]
    }

    private fun initListeners() {
        binding.cameraFab.setOnClickListener {
            startActivity(Intent(this@GalleryActivity, CameraActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        adapter = GalleryAdapter(albumListData, this)
        binding.recyclerview.layoutManager = GridLayoutManager(binding.root.context, 3)
        binding.recyclerview.adapter = adapter
    }

    /** get saved images*/
    private fun getSavedAlbumsFromDB() {
        lifecycleScope.launch {
            mainViewModel.readAlbums.observe(this@GalleryActivity) { albumList ->
                if (albumList.isNotEmpty()) {
                    binding.recyclerview.visibility = View.VISIBLE
                    binding.tvNoAlbum.visibility = View.GONE
                    adapter.updateList(albumList)
                } else {
                    binding.recyclerview.visibility = View.GONE
                    binding.tvNoAlbum.visibility = View.VISIBLE
                }
            }
        }
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