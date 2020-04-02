package com.birdeveloper.handyutils.screenshot.core

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.birdeveloper.handyutils.getAsBitmap
import com.birdeveloper.handyutils.getAsImageFile
import com.birdeveloper.handyutils.screenshot.Default
import com.birdeveloper.handyutils.screenshot.properties.Flip
import com.birdeveloper.handyutils.screenshot.properties.Quality
import com.birdeveloper.handyutils.screenshot.properties.Rotate
import java.io.File


/**
 * ScreenshotBuilder is the builder class for Screenshot builder.
 */
class ScreenshotBuilder constructor(private val activity: Activity) {

    private var quality = Default.QUALITY_VALUE
    private var flip = Default.FLIP_VALUE
    private var rotate = Default.ROTATION_VALUE
    private var share = false
    private var outputView = activity.window.decorView.rootView

    fun setView(view: View): ScreenshotBuilder = apply {
        this.outputView = view
    }

    fun setQuality(quality: Quality): ScreenshotBuilder = apply {
        this.quality = quality
    }

    fun setFlip(flip: Flip): ScreenshotBuilder = apply {
        this.flip = flip
    }

    fun setRotation(rotate: Rotate): ScreenshotBuilder = apply {
        this.rotate = rotate
    }

    fun shareScreenshot(share: Boolean): ScreenshotBuilder = apply {
        this.share = share
    }

    fun getAsBitmap(): Bitmap {
        return outputView.getAsBitmap( rotate.rotationDegree, quality, flip)
    }

    fun getAsImageFile(path: File): Uri {

        return getAsImageFile(activity, outputView, rotate.rotationDegree, quality, flip, path)
    }

    fun getAsImageFile(): Uri {
        return getAsImageFile(activity, outputView, rotate.rotationDegree, quality, flip)

    }
}