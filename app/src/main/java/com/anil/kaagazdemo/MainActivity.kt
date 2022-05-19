package com.anil.kaagazdemo

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.anil.kaagazdemo.databinding.ActivityMainBinding
import com.anil.kaagazdemo.view.GalleryActivity
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private var fileUri: File? = null
    private lateinit var imgCaptureExecutor: ExecutorService
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

    companion object {
        val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

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

        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
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
                        handleCropResult(file.toUri())
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



    private fun handleCropResult(resultUri: Uri) {
        if (resultUri != null && resultUri != Uri.EMPTY) {
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(binding.root.context.getContentResolver(), resultUri)
                if (bitmap != null) {
                    Log.d("BitmapSize", "BitmapBefore: " + bitmap.width + ":" + bitmap.height)
                    bitmap = decodeBitmap(bitmap)
                    Log.d("BitmapSize", "BitmapAfter: " + bitmap.width + ":" + bitmap.height)
                    if (bitmap != null) {
                        settingViews(bitmap)
                    } else {
                        Toast.makeText(binding.root.context, "Can noot retrive croped image" , Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(binding.root.context, "Can noot retrive croped image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun decodeBitmap(bm: Bitmap): Bitmap? {
        val actuallyUsableBitmap: Bitmap
        val maxSize = 720
        var outWidth = 0
        var outHeight = 0
        val inWidth = bm.width
        val inHeight = bm.height
        if (inWidth > maxSize || inHeight > maxSize) {
            if (inWidth > inHeight) {
                outWidth = maxSize
                outHeight = inHeight * maxSize / inWidth
            } else {
                outHeight = maxSize
                outWidth = inWidth * maxSize / inHeight
            }
        } else if (inWidth <= maxSize && inHeight <= maxSize) {
            outWidth = inWidth
            outHeight = inHeight
        }
        actuallyUsableBitmap = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false)
        return actuallyUsableBitmap
    }

    fun settingViews(bitmap: Bitmap?) {
        val frameLayout = FrameLayout(binding.root.context)
        val params1 = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        params1.weight = 1f
        frameLayout.layoutParams = params1
        val image = ImageView(binding.root.context)
        val params = FrameLayout.LayoutParams(
            200,
            200
        )
        params.setMargins(
            10,10,10,10
        )

        params.gravity = Gravity.CENTER
        image.setImageBitmap(bitmap)
        image.setScaleType(ImageView.ScaleType.CENTER_CROP)
        image.setLayoutParams(params)
        val cross = ImageView(binding.root.context)
        val params2 = FrameLayout.LayoutParams(
            80,
            80
        )
        params2.setMargins(0,0,0,20)
        params2.gravity = Gravity.TOP or Gravity.RIGHT
        cross.setImageResource(R.drawable.ic_delete)
        cross.setScaleType(ImageView.ScaleType.CENTER_CROP)
        cross.setLayoutParams(params2)
        frameLayout.addView(image)
        frameLayout.addView(cross)
        binding.imageLayout.addView(frameLayout)

        //		((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, ((ScrollView) findViewById(R.id.scrollview)).getHeight());
//        (getView().findViewById(R.id.hori_scroll) as HorizontalScrollView).post {
//            (getView().findViewById(
//                R.id.hori_scroll
//            ) as HorizontalScrollView).fullScroll(HorizontalScrollView.FOCUS_RIGHT)
//        }

       cross.setOnClickListener {
           binding.imageLayout.removeView(frameLayout)
           binding.imageLayout.invalidate()
           fileUri = null
        }
    }

}