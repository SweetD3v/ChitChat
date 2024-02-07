package com.demo.chitchat.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable


fun Drawable.applyColorFilter(color: Int) = mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)

fun Drawable.convertToBitmap(): Bitmap {
    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }

    val canvas = Canvas(bitmap!!)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun colorDistance(c1: String, c2: String): Double {
    val red1: Int = Color.red(Color.parseColor(c1))
    val red2: Int = Color.red(Color.parseColor(c2))
    val rmean = red1 + red2 shr 1
    val r = red1 - red2
    val g: Int = Color.green(Color.parseColor(c1)) - Color.green(Color.parseColor(c2))
    val b: Int = Color.blue(Color.parseColor(c1)) - Color.blue(Color.parseColor(c2))
    return Math.sqrt((((512 + rmean) * r * r shr 8) + 4 * g * g + ((767 - rmean) * b * b shr 8)).toDouble())
}