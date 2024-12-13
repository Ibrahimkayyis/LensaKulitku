package com.capstone.lensakulitku.view.tracking

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentTrackingCameraXBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TrackingCameraXFragment : Fragment() {

    private var _binding: FragmentTrackingCameraXBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private var baselineImageUri: Uri? = null
    private var baselineSeverity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingCameraXBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve baselineImageUri and baselineSeverity from arguments
        arguments?.let {
            baselineImageUri = it.getParcelable("baselineImageUri")
            baselineSeverity = it.getString("baselineSeverity")
        }

        Log.d("TrackingCameraXFragment", "Received baselineImageUri: $baselineImageUri")
        Log.d("TrackingCameraXFragment", "Received baselineSeverity: $baselineSeverity")

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
        }

        binding.btnCapture.setOnClickListener { takePhoto() }
        binding.btnGallery.setOnClickListener { pickFromGallery() }
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(requireActivity().externalCacheDir, "tracking_photo_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    cropPhoto(Uri.fromFile(photoFile))
                }
            })
    }

    private fun cropPhoto(uri: Uri) {
        val destination = File(requireActivity().externalCacheDir, "cropped_tracking_photo.jpg")
        UCrop.of(uri, Uri.fromFile(destination))
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(224, 224)
            .start(requireContext(), this)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { cropPhoto(it) }
        }
    }

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("TrackingCameraX", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val resultUri = UCrop.getOutput(it)
                resultUri?.let { uri ->
                    processAndNavigateToTrackingCheckPhoto(uri)
                }
            }
        }
    }

    private fun processAndNavigateToTrackingCheckPhoto(uri: Uri) {
        Log.d("TrackingCameraXFragment", "Processed image URI: $uri")

        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val processedImageFile = saveBitmap(resizedBitmap)

        val bundle = Bundle().apply {
            putParcelable("imageUri", Uri.fromFile(processedImageFile))
            baselineImageUri?.let { putParcelable("baselineImageUri", it) }
            baselineSeverity?.let { putString("baselineSeverity", it) }
        }

        Log.d("TrackingCameraXFragment", "Navigating to CheckPhoto with imageUri: ${bundle.getParcelable<Uri>("imageUri")}, baselineImageUri: $baselineImageUri, and baselineSeverity: $baselineSeverity")
        findNavController().navigate(
            R.id.action_trackingCameraXFragment_to_trackingCheckPhotoFragment,
            bundle
        )
    }

    private fun saveBitmap(bitmap: Bitmap): File {
        val file = File(requireActivity().externalCacheDir, "processed_tracking_image.jpg")
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }
}
