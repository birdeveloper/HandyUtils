package com.birdeveloper.handyutils.screenshot

import android.app.Activity
import com.birdeveloper.handyutils.screenshot.core.ScreenshotBuilder

object Screenshot {

    fun with(activity: Activity): ScreenshotBuilder =
        ScreenshotBuilder(activity)

}
