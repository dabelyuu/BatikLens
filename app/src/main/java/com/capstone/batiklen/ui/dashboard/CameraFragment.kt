package com.capstone.batiklen.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.batiklen.data.Result
import com.capstone.batiklen.databinding.FragmentCameraBinding
import com.capstone.batiklen.ui.BatikViewModel
import com.capstone.batiklen.ui.result.ResultActivity
import com.capstone.batiklen.utils.ImageClassifierHelper
import com.capstone.batiklen.utils.UriToFile
import com.capstone.batiklen.utils.ViewModelFactory
import com.capstone.batiklen.utils.createCustomTempFile
import com.capstone.batiklen.utils.dataStore
import com.capstone.batiklen.utils.reduceFileImage
import com.google.mediapipe.tasks.components.containers.Classifications
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.NumberFormat

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val batikViewModel by viewModels<BatikViewModel> {
        ViewModelFactory.getInstance(requireContext(), context?.dataStore!!)
    }


    private var currentImageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val cameraViewModel =
//            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchCamera.setOnClickListener{
            cameraSelector =
                if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }

        binding.takePicture.setOnClickListener{takePicture()}

        binding.pickGallery.setOnClickListener { pickFromGalley() }
    }

    private fun pickFromGalley() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri: Uri? ->
        if (uri != null){
            currentImageUri = uri

            val uCropOption = UCrop.Options()
            val source = currentImageUri!!
            val destinationFile = createCustomTempFile(requireContext(), "cropped")
            val destinationUri = Uri.fromFile(destinationFile)

            val uCrop = UCrop.of(source, destinationUri)
                .withOptions(uCropOption)
                .withMaxResultSize(1000,1000)

            registerUCrop.launch(uCrop.getIntent(requireContext()))
        }else{
            Log.d("photo Picker", "No Media Selected")
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val registerUCrop = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val data = UCrop.getOutput(result.data!!)

            Log.d("checkUcrop", "$data")

            predictImage(data!!)
        }else if(result.resultCode == UCrop.RESULT_ERROR){
            val uCropErr = UCrop.getError(result.data!!)
            Log.d("UcropErr", "$uCropErr")
        }
    }

    private fun predictImage(uri: Uri) {
        Log.d("checkImageUrl", uri.toString())

        val file  = UriToFile(uri, requireContext()).reduceFileImage()
        Log.d("checkUp", file.path)

        val postImageFile = file.asRequestBody("image/jpg".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            file.name,
            postImageFile
        )

        batikViewModel.predictImage(multipartBody).observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBarLogin.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    binding.progressBarLogin.visibility = View.GONE
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Error predict data")
                        setMessage(result.error.toString())
                        setPositiveButton("Tutup"){dialog,_ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                }
                is Result.Success -> {
                    binding.progressBarLogin.visibility = View.GONE
                    val prediction = result.data

                    val predictionData = prediction.first

                    val metadata = prediction.second

                    val imageUrl = prediction.third

                    Log.d("checkValue", predictionData.toString())

                    val intent = Intent(context, ResultActivity::class.java)
                    intent.putExtra("PREDICTION_DATA", predictionData)
                    intent.putExtra("METADATA", metadata)
                    intent.putExtra("IMAGE_URLS", imageUrl)
                    startActivity(intent)
                }
            }
        }

    }


    private fun takePicture() {
        val imageCapture = imageCapture ?: return

        val pictureFile = createCustomTempFile(requireContext())

        val outputPicture = ImageCapture.OutputFileOptions.Builder(pictureFile).build()

        imageCapture.takePicture(outputPicture, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uCropOption = UCrop.Options()
                    val source = Uri.fromFile(pictureFile)
                    val destinationFile = createCustomTempFile(requireContext(), "cropped")
                    val destinationUri = Uri.fromFile(destinationFile)

                    val uCrop = UCrop.of(source, destinationUri)
                        .withOptions(uCropOption)
                        .withMaxResultSize(500,500)

                    registerUCrop.launch(uCrop.getIntent(requireContext()))
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d("errorTakePicture", "message: $exception")
                }

            })
    }

    private fun startCamera() {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener{
                override fun onError(error: String) {
                   requireActivity().runOnUiThread {
                       Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                   }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    requireActivity().runOnUiThread {
                        results?.let { it ->
                            if (view != null && _binding != null) {
                                results?.let { it ->
                                    if (it.isNotEmpty() && it[0].categories().isNotEmpty()) {
                                        val sortedCategories =
                                            it[0].categories().sortedByDescending { it?.score() }
                                        val displayResult =
                                            sortedCategories.joinToString("\n") {
                                                "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                                    .format(it.score()).trim()
                                            }
                                        binding.result.text = displayResult
                                    } else {
                                        binding.result.text = ""
                                    }
                                }
                            }
//                            if (it.isNotEmpty() && it[0].categories().isNotEmpty()){
//                                println(it)
//                                val sortedCategories =
//                                    it[0].categories().sortedByDescending { it?.score() }
//                                val displayResult =
//                                    sortedCategories.joinToString("\n") {
//                                        "${it.categoryName()} " + NumberFormat.getPercentInstance()
//                                            .format(it.score()).trim()
//                                    }
//                                binding.result.text = displayResult
//                            }else{
//                                binding.result.text = ""
//                            }
                        }
                    }
                }
            }
        )


        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { image ->
                imageClassifierHelper.classifyImage(image)
            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            }catch (e: Exception){
                Toast.makeText(requireActivity(), "Gagal memunculkan Kamera", Toast.LENGTH_SHORT).show()
                Log.d("cameraActivity", "$e")
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }


}