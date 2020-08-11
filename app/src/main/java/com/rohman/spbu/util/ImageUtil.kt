package com.rohman.spbu.util

import android.graphics.Bitmap
import android.graphics.Matrix


/**
 * convert grey image
 * @param img  bitmap
 * @return  data
 */
fun convertGreyImg(img: Bitmap): Bitmap? {
    val width = img.width
    val height = img.height
    val pixels = IntArray(width * height)
    img.getPixels(pixels, 0, width, 0, 0, width, height)


    //The arithmetic average of a grayscale image; a threshold
    var redSum = 0.0
    var greenSum = 0.0
    var blueSun = 0.0
    val total = width * height.toDouble()
    for (i in 0 until height) {
        for (j in 0 until width) {
            val grey = pixels[width * i + j]
            val red = grey and 0x00FF0000 shr 16
            val green = grey and 0x0000FF00 shr 8
            val blue = grey and 0x000000FF
            redSum += red.toDouble()
            greenSum += green.toDouble()
            blueSun += blue.toDouble()
        }
    }
    val m = (redSum / total).toInt()

    //Conversion monochrome diagram
    for (i in 0 until height) {
        for (j in 0 until width) {
            var grey = pixels[width * i + j]
            val alpha1 = 0xFF shl 24
            var red = grey and 0x00FF0000 shr 16
            var green = grey and 0x0000FF00 shr 8
            var blue = grey and 0x000000FF
            if (red >= m) {
                blue = 255
                green = blue
                red = green
            } else {
                blue = 0
                green = blue
                red = green
            }
            grey = alpha1 or (red shl 16) or (green shl 8) or blue
            pixels[width * i + j] = grey
        }
    }
    val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return mBitmap
}

/*
        use the Matrix compress the bitmap
	 *   */
fun resizeImage(bitmap: Bitmap, w: Int, ischecked: Boolean): Bitmap? {
    var resizedBitmap: Bitmap? = null
    val width = bitmap.width
    val height = bitmap.height
    if (width <= w) {
        return bitmap
    }
    resizedBitmap = if (!ischecked) {
        val newHeight = height * w / width
        val scaleWidth = w.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap.createBitmap(
            bitmap, 0, 0, width,
            height, matrix, true
        )
    } else {
        Bitmap.createBitmap(bitmap, 0, 0, w, height)
    }
    return resizedBitmap
}
