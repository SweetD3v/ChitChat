package com.ai.photogallery.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

val Context.baseDirFaces: String
    get() = this.getExternalFilesDir("Clusterface")?.absolutePath ?: ""

fun Context.getFacesEncodingsPath(): String {
    return "$baseDirFaces/encodings.enc"
}

fun Context.getFacesCroppedPath(): String {
    return "$baseDirFaces/Crops"
}

fun Context.getFacesResultPath(): String {
    return "$baseDirFaces/Results"
}

fun saveImage(context: Context, bitmap: Bitmap, origFileName: String, faceIdx: String): String? {
    Log.d("finding faces", "Saving crop..")
    var savedImagePath: String? = null

    // Create the new file in the external storage
    val imageFileName = origFileName + "_" + faceIdx + ".jpg"
    val cropsDir = File(context.getFacesCroppedPath())
    var success = true
    if (!cropsDir.exists()) {
        success = cropsDir.mkdirs()
    }

    // Save the new Bitmap
    if (success) {
        val imageFile = File(cropsDir, imageFileName)
        savedImagePath = imageFile.absolutePath
        try {
            val fOut: OutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.d("finding faces", "Failed to write to output stream!")
        }
        Log.d("finding faces", "Saved crop to $savedImagePath")
        //galleryAddPic(savedImagePath);
    } else {
        Log.d("finding faces", "Failed to save image!")
    }
    return savedImagePath
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ViewGroup.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.getFilenameFromUri(uri: Uri): String {
    return if (uri.scheme == "file") {
        File(uri.toString()).name
    } else {
        getFilenameFromContentUri(uri) ?: uri.lastPathSegment ?: ""
    }
}

fun Context.getFilenameFromContentUri(uri: Uri): String? {
    val projection = arrayOf(
        OpenableColumns.DISPLAY_NAME
    )

    try {
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(OpenableColumns.DISPLAY_NAME)
            }
        }
    } catch (e: Exception) {
    }
    return null
}

fun Cursor.getStringValue(key: String) = getString(getColumnIndexOrThrow(key))

fun File.getVideoResolution(): Array<String?> {
    val metaRetriever = MediaMetadataRetriever()
    metaRetriever.setDataSource(this.path)
    val height: String? =
        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val width: String? =
        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)

    return arrayOf(width, height)
}

fun File.deleteWithChildren() {
    this.deleteRecursively()
}
