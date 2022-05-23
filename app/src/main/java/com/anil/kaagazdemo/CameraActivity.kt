package com.anil.kaagazdemo

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.anil.kaagazdemo.adapters.ImageAdapter
import com.anil.kaagazdemo.database.AlbumEntity
import com.anil.kaagazdemo.database.ImageEntity
import com.anil.kaagazdemo.database.ImageListEntity
import com.anil.kaagazdemo.databinding.ActivityMainBinding
import com.anil.kaagazdemo.databinding.AlbumbNameBinding
import com.anil.kaagazdemo.interfaces.PhotoUpdateInterface
import com.anil.kaagazdemo.utils.DatabaseHandler
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), PhotoUpdateInterface {

    companion object {
        val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var albumbNameBinding: AlbumbNameBinding
    private lateinit var adapter: ImageAdapter
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private var imageUriList: MutableList<Uri> = mutableListOf()
    private lateinit var photoUpdateInterface: PhotoUpdateInterface

    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(
                    binding.root,
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //photoUpdateInterface = PhotoUpdateInterface
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

        initRecyclerView()
        initListener()
        setUpDB()

    }

    private fun setUpDB() {
        databaseHandler =
            Room.databaseBuilder(this@CameraActivity, DatabaseHandler::class.java, "IMAGE_TABLE")
                .allowMainThreadQueries().build()
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

//        binding.galleryBtn.setOnClickListener {
//            val intent = Intent(this, GalleryActivity::class.java)
//            startActivity(intent)
//        }

        binding.saveBtn.setOnClickListener {

            val alertDialog = AlertDialog.Builder(binding.root.context).create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            albumbNameBinding = AlbumbNameBinding.inflate(layoutInflater)

            albumbNameBinding.btnSubmit.setOnClickListener {
                val albumbName: String = albumbNameBinding.edAlbumbName.text.trim().toString()
                if (albumbName.isNotBlank() && albumbName.isNotEmpty()) {
                    val imageEntityList: MutableList<ImageEntity> = mutableListOf()
                    imageUriList.forEach {
                        val imageEntity = ImageEntity(it.toString(), Date().toString())
                        imageEntityList.add(imageEntity)
                    }
                    val albumEntity = AlbumEntity(ImageListEntity(imageEntityList), albumbName)
                    databaseHandler.imageInterface()?.addImageInAlbum(albumEntity)

                    imageUriList.clear()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@CameraActivity,"Album successfully Saved",Toast.LENGTH_SHORT)
                    alertDialog.dismiss()
                    this.finish()
                } else{
                    Toast.makeText(binding.root.context,"Please Enter Album name!",Toast.LENGTH_SHORT).show()
                }
            }

            alertDialog.setView(albumbNameBinding.root)
            alertDialog.show()
        }
    }

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
                Log.d(TAG, "Use case binding failed")
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
                        Log.i(TAG, "The image has been saved in ${file.toUri()}")
                        outputFileResults.savedUri?.let { uri -> updateRecyclerView(uri) }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, "Error taking photo:$exception")
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

    private fun initRecyclerView() {
        adapter = ImageAdapter(listener = this@CameraActivity)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
        binding.recyclerview.adapter = adapter
    }

    fun updateRecyclerView(imageUri: Uri) {
        imageUriList.add(imageUri)
        adapter.updateList(imageUriList)

        runOnUiThread {
            if(imageUriList.isNotEmpty()){
                binding.saveBtn.visibility = View.VISIBLE
            }else{
                binding.saveBtn.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun shouldDisplaySaveButton(isShow: Boolean) {
        if(isShow){
            binding.saveBtn.visibility = View.VISIBLE
        }else{
            binding.saveBtn.visibility = View.GONE
        }
    }

}