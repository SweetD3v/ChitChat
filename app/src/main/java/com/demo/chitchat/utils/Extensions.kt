package com.demo.chitchat.utils

import android.app.Activity
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.CamcorderProfile
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.demo.chitchat.R
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.roundToInt

fun Context.isOnline(): Boolean {
    val cm = getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
    val net = cm.activeNetwork ?: return false
    val actNw = cm.getNetworkCapabilities(net) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}

fun View.adjustInsets(activity: Activity) {
    ViewCompat.setOnApplyWindowInsetsListener(
        activity.window.decorView
    ) { _, insets ->
        val statusbarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        Log.e("TAG", "adjustInsets: ${statusbarHeight}")
        (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusbarHeight
        insets
    }
}

fun View.onWindowInsets(action: (View, WindowInsetsCompat) -> Unit) {
    ViewCompat.requestApplyInsets(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        action(v, insets)
        insets
    }
}

fun dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), Resources.getSystem().displayMetrics
    ).roundToInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).roundToInt()
}

fun isTablet(context: Context): Boolean {
    return ((context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
}

fun getMaxSupportedWidth(context: Context): Int {
    val recordingInfo: RecordingInfo = getRecordingInfo(context)
    return recordingInfo.width
}

fun getMaxSupportedHeight(context: Context): Int {
    val recordingInfo: RecordingInfo = getRecordingInfo(context)
    return recordingInfo.height
}

fun getRecordingInfo(context: Context): RecordingInfo {
    val displayMetrics = DisplayMetrics()
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getRealMetrics(displayMetrics)
    val displayWidth = displayMetrics.widthPixels
    val displayHeight = displayMetrics.heightPixels
    val displayDensity = displayMetrics.densityDpi
    val configuration = context.resources.configuration
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
    val cameraWidth = camcorderProfile?.videoFrameWidth ?: -1
    val cameraHeight = camcorderProfile?.videoFrameHeight ?: -1
    val cameraFrameRate = camcorderProfile?.videoFrameRate ?: 60
    return calculateRecordingInfo(
        displayWidth,
        displayHeight,
        displayDensity,
        isLandscape,
        cameraWidth,
        cameraHeight,
        cameraFrameRate,
        100
    )
}

class RecordingInfo(val width: Int, val height: Int, val frameRate: Int, val density: Int)

fun calculateRecordingInfo(
    displayWidth: Int,
    displayHeight: Int,
    displayDensity: Int,
    isLandscapeDevice: Boolean,
    cameraWidth: Int,
    cameraHeight: Int,
    cameraFrameRate: Int,
    sizePercentage: Int
): RecordingInfo {
    var displayWidth = displayWidth
    var displayHeight = displayHeight
    displayWidth = displayWidth * sizePercentage / 100
    displayHeight = displayHeight * sizePercentage / 100
    if (cameraWidth == -1 && cameraHeight == -1) {
        return RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity)
    }
    var frameWidth = if (isLandscapeDevice) cameraWidth else cameraHeight
    var frameHeight = if (isLandscapeDevice) cameraHeight else cameraWidth
    if (frameWidth >= displayWidth && frameHeight >= displayHeight) {
        return RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity)
    }
    if (isLandscapeDevice) {
        frameWidth = displayWidth * frameHeight / displayHeight
    } else {
        frameHeight = displayHeight * frameWidth / displayWidth
    }
    return RecordingInfo(frameWidth, frameHeight, cameraFrameRate, displayDensity)
}

fun Window.fitSystemWindows() {
    WindowCompat.setDecorFitsSystemWindows(this, false)
}

var View.topMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = value }
    }

var View.topPadding: Int
    get() = paddingTop
    set(value) {
        updateLayoutParams { setPaddingRelative(paddingStart, value, paddingEnd, paddingBottom) }
    }

var View.bottomMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = value }
    }

var View.endMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { marginEnd = value }
    }

var View.startMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginStart
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { marginStart = value }
    }

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) invisible() else visible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) visible() else gone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun Context.shareMediaUri(
    uriList: ArrayList<Uri>
) {
    if (uriList.isEmpty()) return
    var fileURI: Uri
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = uriList.let {
            fileURI = it[0]
            contentResolver.getType(fileURI)
        }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(
            Intent.EXTRA_SUBJECT, "Sharing file from the ${getString(R.string.app_name)}"
        )
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
    }
    startActivity(
        Intent.createChooser(
            shareIntent, getString(R.string.share_media)
        )
    )
}

val Context.audioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

fun Uri?.isTypeVideo(ctx: Context): Boolean {
    var isVideo = false
    this?.let {
        if (ctx.contentResolver.getType(it).toString().contains("video")) isVideo = true
    } ?: let {
        isVideo = false
    }
    return isVideo
}

fun getFileContentUri(context: Context, filePath: String): Uri? {
    val projection = arrayOf(
        BaseColumns._ID, MediaStore.Files.FileColumns.DATA
    )
    val selection = "${MediaStore.Files.FileColumns.DATA}=? "

    context.contentResolver.query(
        MediaStore.Files.getContentUri("external"), projection, selection, arrayOf(filePath), null
    ).use { cursor ->
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            cursor.close()
            ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
        } else {
            null
        }
    }
}

fun File.renameFileName(ctx: Context, toFilePath: String): Boolean {
    val from = File(this.absolutePath)
    val to = File(toFilePath)
    val renamed = from.renameTo(to)
    MediaScannerConnection.scanFile(
        ctx, arrayOf(this.absolutePath, toFilePath), null, null
    )
    return renamed
}

fun String?.getFileNameWithoutExt(): String {
    return try {
        this?.let { name ->
            name.substring(0, name.lastIndexOf("."))
        } ?: ""
    } catch (e: Exception) {
        this ?: ""
    }
}

fun Int.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

fun Int.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()
    return "${
        DecimalFormat("#,##0.#").format(
            this / Math.pow(
                1024.0, digitGroups.toDouble()
            )
        )
    } ${units[digitGroups]}"
}


fun Context.getVersionName() = packageManager.getPackageInfo(packageName, 0).versionName
fun Context.getVersionCode() = packageManager.getPackageInfo(packageName, 0).versionCode

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

const val ROUNDED_CORNERS_NONE = 1

const val TYPE_IMAGES = 1
const val TYPE_VIDEOS = 2
const val TYPE_GIFS = 4
const val TYPE_RAWS = 8
const val TYPE_SVGS = 16
const val TYPE_PORTRAITS = 32

val photoExtensions: Array<String>
    get() = arrayOf(
        ".jpg", ".png", ".jpeg", ".bmp", ".webp", ".heic", ".heif", ".apng", ".avif"
    )
val videoExtensions: Array<String>
    get() = arrayOf(
        ".mp4", ".mkv", ".webm", ".avi", ".3gp", ".mov", ".m4v", ".3gpp"
    )
val audioExtensions: Array<String>
    get() = arrayOf(
        ".mp3", ".wav", ".wma", ".ogg", ".m4a", ".opus", ".flac", ".aac"
    )
val rawExtensions: Array<String>
    get() = arrayOf(
        ".dng", ".orf", ".nef", ".arw", ".rw2", ".cr2", ".cr3"
    )

fun String.isMediaFile() =
    isImageFast() || isVideoFast() || isGif() || isRawFast() || isSvg() || isPortrait()

fun String.isWebP() = endsWith(".webp", true)

fun String.isGif() = endsWith(".gif", true)

fun String.isSvg() = endsWith(".svg", true)

fun String.isPortrait() = getFilenameFromPath().contains(
    "portrait", true
) && File(this).parentFile?.name?.startsWith("img_", true) == true

fun String.getFilenameFromPath() = substring(lastIndexOf("/") + 1)

fun String.getFilenameExtension() = substring(lastIndexOf(".") + 1)

// fast extension checks, not guaranteed to be accurate
fun String.isVideoFast() = videoExtensions.any { endsWith(it, true) }

fun String.isImageFast() = photoExtensions.any { endsWith(it, true) }
fun String.isAudioFast() = audioExtensions.any { endsWith(it, true) }
fun String.isRawFast() = rawExtensions.any { endsWith(it, true) }

fun String.isPng() = endsWith(".png", true)

fun String.isApng() = endsWith(".apng", true)

fun String.isJpg() = endsWith(".jpg", true) or endsWith(".jpeg", true)

fun Long.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${
        DecimalFormat("#,##0.#").format(
            this / Math.pow(
                1024.0, digitGroups.toDouble()
            )
        )
    } ${units[digitGroups]}"
}


fun Long.convertToDDMMMMYYYY(): String {
    var dateStr = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH).format(this)
    if (dateStr.startsWith("0"))
        dateStr = dateStr.drop(1)
    return dateStr
}

fun String.revertDDMMMYYYY(): Long {
    val date = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH).parse(this)
    return date?.time ?: System.currentTimeMillis()
}

fun Long.convertToDDMMMYYYY(): String {
    var dateStr = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH).format(this)
    if (dateStr.startsWith("0"))
        dateStr = dateStr.drop(1)
    return dateStr
}

fun Array<Long>.convertToDDMMMYYYYSeparate(ctx: Context): String {
    var dateStrFirst = SimpleDateFormat(
        "dd", Locale.ENGLISH
    ).format(this.first())
    if (dateStrFirst.startsWith("0"))
        dateStrFirst = dateStrFirst.drop(1)
    if (this.first().isToday()) dateStrFirst = ctx.getString(R.string.today)
    if (this.first().isYesterday()) dateStrFirst = ctx.getString(R.string.yesterday)

    var dateStrLast = SimpleDateFormat(
        "dd MMM, yyyy", Locale.ENGLISH
    ).format(this.last())
    if (dateStrLast.startsWith("0"))
        dateStrLast = dateStrLast.drop(1)
    if (this.last().isToday()) dateStrLast = ctx.getString(R.string.today)
    if (this.last().isYesterday()) dateStrLast = ctx.getString(R.string.yesterday)

    return "$dateStrFirst - $dateStrLast"
}

fun Long.convertToDDMMM(): String {
    var dateStr = SimpleDateFormat("dd MMM", Locale.ENGLISH).format(this)
    if (dateStr.startsWith("0"))
        dateStr = dateStr.drop(1)
    return dateStr
}

fun Long.convertToHHMMA(): String {
    return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(this)
}

fun Long.checkDaysGap(date2: Long): Long {
    date2 - this
    val endDate = Date(date2)
    val startDate = Date(this)
    val different: Long = endDate.time - startDate.time

    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    val elapsedDays = different / daysInMilli
    return elapsedDays
}

fun Long.isYesterday1(): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.timeInMillis = this
    val day1 = cal1.get(Calendar.DAY_OF_MONTH)
    val month1 = cal1.get(Calendar.MONTH)
    val year1 = cal1.get(Calendar.YEAR)

    val cal2 = Calendar.getInstance()
    cal2.timeInMillis = System.currentTimeMillis()
    val day2 = cal2.get(Calendar.DAY_OF_MONTH)
    val month2 = cal2.get(Calendar.MONTH)
    val year2 = cal2.get(Calendar.YEAR)

    Log.e("TAG", "isToday1: $year1 - $year1")

    if (month1 == month2 && year1 == year2) {
        if (day1 != day2) {
            if (day2 - day1 == 1) return true
        }
    }

    return false
}

fun Long.isYesterday(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
}

fun Long.isToday(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ArrayList<Uri>.shareMediaUri(
    context: Context
) {
    if (isEmpty()) return
    var fileURI: Uri
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = this@shareMediaUri.let {
            fileURI = it[0]
            context.contentResolver.getType(fileURI)
        }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(
            Intent.EXTRA_SUBJECT, "Sharing file from the ${context.getString(R.string.app_name)}"
        )
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, this@shareMediaUri)
    }
    context.startActivity(
        Intent.createChooser(
            shareIntent, context.getString(R.string.share_media)
        )
    )
}

fun String.pathToBitmap(): Bitmap {
    val op = BitmapFactory.Options()
    op.inPreferredConfig = Bitmap.Config.ARGB_8888
    return BitmapFactory.decodeFile(this, op) ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
}

fun Bitmap.scaleBitmapTo(maxWidth: Int, maxHeight: Int): Bitmap {
    var width = this.width
    var height = this.height
    if (width > height) {
        // landscape
        val ratio = width.toFloat() / maxWidth
        width = maxWidth
        height = (height / ratio).toInt()
    } else if (height > width) {
        // portrait
        val ratio = height.toFloat() / maxHeight
        height = maxHeight
        width = (width / ratio).toInt()
    } else {
        // square
        height = maxHeight
        width = maxWidth
    }
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun Bitmap.bitmapToRgba(): ByteArray {
    require(this.config == Bitmap.Config.ARGB_8888) { "Bitmap must be in ARGB_8888 format" }
    val pixels = IntArray(this.width * this.height)
    val bytes = ByteArray(pixels.size * 4)
    this.getPixels(pixels, 0, this.width, 0, 0, this.width, this.height)
    var i = 0
    for (pixel in pixels) {
        // Get components assuming is ARGB
        val A = pixel shr 24 and 0xff
        val R = pixel shr 16 and 0xff
        val G = pixel shr 8 and 0xff
        val B = pixel and 0xff
        bytes[i++] = R.toByte()
        bytes[i++] = G.toByte()
        bytes[i++] = B.toByte()
        bytes[i++] = A.toByte()
    }
    return bytes
}

fun <T> MutableList<T>.prepend(element: T) {
    add(0, element)
}

fun <T> MutableList<T>.prependAll(elements: List<T>) {
    addAll(0, elements)
}

@OptIn(InternalCoroutinesApi::class)
fun Job.ensureActive(): Unit {
    if (!isActive) {
        throw getCancellationException()
    }
}