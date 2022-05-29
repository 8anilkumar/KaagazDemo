package com.anil.kaagazdemo.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anil.kaagazdemo.R
import com.anil.kaagazdemo.adapters.ImageAdapter
import com.anil.kaagazdemo.model.AlbumEntity
import com.anil.kaagazdemo.model.ImageEntity
import com.anil.kaagazdemo.databinding.ActivityMainBinding
import com.anil.kaagazdemo.databinding.AlbumbNameBinding
import com.anil.kaagazdemo.interfaces.PhotoUpdateInterface
import com.anil.kaagazdemo.viewmodel.CameraViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraActivity : AppCompatActivity(), PhotoUpdateInterface {

    companion object {
        const val tag = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var albumNameBinding: AlbumbNameBinding
    private lateinit var adapter: ImageAdapter
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private var imageUriList: MutableList<ImageEntity> = mutableListOf()
    private lateinit var cameraViewModel: CameraViewModel


    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.cam_permission_text),
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        initCamera()
        initRecyclerView()
        initListener()
    }

    /** Init methods */

    private fun initCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)
    }

    private fun setViewModel(){
        cameraViewModel = ViewModelProvider(this@CameraActivity)[CameraViewModel::class.java]
    }

    private fun initListener() {
        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }

        binding.switchBtn.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
        binding.saveBtn.setOnClickListener {
        showAlbumNameDialog()
        }
    }

    /** Dialog to ask user album's name */
    private fun showAlbumNameDialog(){
        val alertDialog = AlertDialog.Builder(binding.root.context).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        albumNameBinding = AlbumbNameBinding.inflate(layoutInflater)
        albumNameBinding.btnSubmit.setOnClickListener {
            val albumName = albumNameBinding.edAlbumbName.text.trim().toString()
            if (albumName.isNotEmpty()) {
                val albumEntity = AlbumEntity(imageUriList, albumName)
                cameraViewModel.insertAlbum(albumEntity)
                adapter.notifyDataSetChanged()
                toast("Album successfully Saved")
                alertDialog?.dismiss()
                finish()
            } else {
               toast("Please Enter Album name!")
            }
        }
        alertDialog?.apply {
            setView(albumNameBinding.root)
            show()
        }
    }

    /** Methods to operate camera */
    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d(tag, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        imageCapture?.let {
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i(tag, "The image has been saved in ${file.toUri()}")

                        outputFileResults.savedUri?.let { uri -> updateRecyclerView(uri) }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(tag, "Error taking photo:$exception")
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

    /** methods to handle clicked image's recyclerview */
    private fun initRecyclerView() {
        adapter = ImageAdapter(listener = this@CameraActivity)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
        binding.recyclerview.adapter = adapter
    }

    fun updateRecyclerView(imageUri: Uri) {
        val imageEntity = ImageEntity(imageUri.toString(),Date().toString())
        imageUriList.add(imageEntity)
        adapter.updateList(imageUriList)

        runOnUiThread {
            if (imageUriList.isNotEmpty()) {
                binding.saveBtn.visibility = View.VISIBLE
            } else {
                binding.saveBtn.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun shouldDisplaySaveButton(isShow: Boolean) {
        if (isShow) {
            binding.saveBtn.visibility = View.VISIBLE
        } else {
            binding.saveBtn.visibility = View.GONE
        }
    }

  private  fun toast(msg: String){
      Toast.makeText(
          this@CameraActivity,
          msg,
          Toast.LENGTH_SHORT
      )
    }
}