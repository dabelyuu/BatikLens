package com.capstone.batiklen.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.capstone.batiklen.R
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier

class ImageClassifierHelper(
    val thresholds : Float = 0.3f,
    val maxResults : Int = 1,
    val modelName : String = "model_resnet_with_metadata.tflite",
    val runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    val classifierListener : ClassifierListener?
) {

    private var imageClassifier : ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier(){
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(thresholds)
            .setMaxResults(maxResults)
            .setRunningMode(runningMode)

        if (runningMode == RunningMode.LIVE_STREAM){
            optionsBuilder.setResultListener{result, image ->
                val finishTimeMs = SystemClock.uptimeMillis()
                val inferenceTime = finishTimeMs - result.timestampMs()
                classifierListener?.onResults(
                    result.classificationResult().classifications(),
                    inferenceTime
                )
            }.setErrorListener { error ->
                classifierListener?.onError(error.message.toString())
            }
        }

        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelName)
        optionsBuilder.setBaseOptions(baseOptions.build())

        try {
            imageClassifier = ImageClassifier.createFromOptions(
                context,
                optionsBuilder.build()
            )
        }catch (e: IllegalStateException){
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.d("FailedClassifier","${e.message}")
        }
    }

    fun classifyImage(image: ImageProxy){
        if (imageClassifier == null){
            setupImageClassifier()
        }

        val mpImage = BitmapImageBuilder(toBitmap(image)).build()

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(image.imageInfo.rotationDegrees)
            .build()

        val inferenceTime = SystemClock.uptimeMillis()
        imageClassifier?.classifyAsync(mpImage, imageProcessingOptions, inferenceTime)
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer =Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }


    interface ClassifierListener{
        fun onError(error: String)
        fun onResults(
            results:List<Classifications>?,
            inferenceTime: Long
        )
    }
}