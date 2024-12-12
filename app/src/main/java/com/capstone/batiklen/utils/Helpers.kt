package com.capstone.batiklen.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.capstone.batiklen.BuildConfig
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val fileNameFormat ="yyyyMMdd_HHmmss"
private const val MaximalSize = 1000000
private val timeStamp: String = SimpleDateFormat(fileNameFormat, Locale.US).format(Date())

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
    //content://com.dicoding.picodiploma.mycamera.fileprovider/my_images/MyCamera/20230825_133659.jpg
}

fun createCustomTempFile(context: Context, prefix: String = "image"): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp+prefix, ".jpg", filesDir)
}

fun File.reduceFileImage(): File{
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do{
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    }while(streamLength > 1000000)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun UriToFile(uri: Uri, context: Context): File{
    val file = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(uri) as InputStream
    val outputStream = FileOutputStream(file)
    val buffer = ByteArray(1024)
    var length: Int
    while(inputStream.read(buffer).also { length = it } > 0)outputStream.write(buffer,0, length)
    outputStream.close()
    inputStream.close()
    return file
}

suspend fun String.translateLanguage(context: Context, languageCode: String): String{
    return withContext(Dispatchers.IO){
        try {
            Log.d("checkLanguage", this@translateLanguage)
            Log.d("checkLanguage", languageCode)
            val (sourceLanguage, targetLanguage) = when(languageCode){
                "in" -> TranslateLanguage.ENGLISH to TranslateLanguage.INDONESIAN
                "en" -> TranslateLanguage.INDONESIAN to TranslateLanguage.ENGLISH
                else -> TranslateLanguage.INDONESIAN to TranslateLanguage.ENGLISH
            }

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            val translate = Translation.getClient(options)
            val conditions = DownloadConditions.Builder().build()

            translate.downloadModelIfNeeded(conditions).await()

            val translateText = translate.translate(this@translateLanguage).await()

            translate.close()

            translateText
        }catch (e: Exception){
            e.printStackTrace()
            this@translateLanguage
        }
    }
}