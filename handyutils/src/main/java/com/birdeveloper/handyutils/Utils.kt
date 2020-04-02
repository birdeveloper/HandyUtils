package com.birdeveloper.handyutils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Patterns
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.birdeveloper.handyutils.screenshot.properties.Flip
import com.birdeveloper.handyutils.screenshot.properties.Quality
import com.birdeveloper.handyutils.screenshot.properties.Rotate
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import java.io.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun Bitmap.flip(flip: Flip): Bitmap {
    val cx = width / 2f
    val cy = height / 2f
    return when (flip) {
        Flip.HORIZONTALLY -> flip(-1f, 1f, cx, cy)
        Flip.VERTICALLY -> flip(1f, -1f, cx, cy)
        Flip.NOTHING -> this
    }
}
fun Bitmap.flip(x: Float, y: Float, cx: Float, cy: Float): Bitmap {
    val matrix = Matrix().apply { postScale(x, y, cx, cy) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * @rotate is an extension function which rotates the bitmap
 */
 fun Bitmap.rotate(rotate: Rotate): Bitmap = rotate(rotate.rotationDegree)

 fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
fun View.getAsBitmap(
    degrees: Float,
    quality: Quality,
    flip: Flip
): Bitmap {

    val stream = ByteArrayOutputStream()
    val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable = background
    if (bgDrawable != null) bgDrawable.draw(canvas)
    else canvas.drawColor(Color.WHITE)
    draw(canvas)
    returnedBitmap.run {
        compress(Bitmap.CompressFormat.JPEG, quality.quality, stream as OutputStream?)
    }
    val byteArray = stream.toByteArray()
    val bitmapAfterFlip = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).flip(flip)
    return bitmapAfterFlip.rotate(degrees)

}

fun getAsImageFile(
    activity: Activity,
    view: View,
    degrees: Float,
    quality: Quality,
    flip: Flip,
    path: File
): Uri {
    val pathOfFile = File(path, "images")
    pathOfFile.mkdirs()
    val file: File = File("$pathOfFile/${System.currentTimeMillis()}_image.png")
    try {
        val stream = FileOutputStream(file)
        view.getAsBitmap(degrees, quality, flip).compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        stream.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Uri.parse(file.absolutePath)

}

fun getAsImageFile(
    activity: Activity,
    view: View,
    degrees: Float,
    quality: Quality,
    flip: Flip
): Uri {
    val cachePath = File(activity.cacheDir, "images")
    cachePath.mkdirs()
    try {
        val stream = FileOutputStream("$cachePath/image.png")
        view.getAsBitmap(degrees, quality, flip).compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Uri.parse(cachePath.absolutePath)

}

fun ImageView.load(url: String?) {
    if (!url.isNullOrBlank()) customGlide(context).load(url).into(this)
}

fun ImageView.load(url: Uri?) {
    if (url != null) customGlide(context).load(url).into(this)
}

fun ImageView.load(file: File?) {
    if (file != null) customGlide(context).load(file).into(this)
}

fun customGlide(context: Context) =
    Glide.with(context)
        .applyDefaultRequestOptions(RequestOptions.timeoutOf(5 * 60 * 1000))

fun Activity.recreate(animate: Boolean = true) {
    if (animate) {
        val restartIntent = Intent(this, this::class.java)

        val extras = intent.extras
        if (extras != null) {
            restartIntent.putExtras(extras)
        }
        ActivityCompat.startActivity(
            this, restartIntent,
            ActivityOptionsCompat
                .makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle()
        )
        finish()
    } else {
        recreate()
    }
}

@SuppressLint("SimpleDateFormat")
fun getUserAge(birthdate: String?): String {
    val dateSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val daySimpleDateFormat = SimpleDateFormat("dd")
    val monthSimpleDateFormat = SimpleDateFormat("MM")
    val yearSimpleDateFormat = SimpleDateFormat("yyyy")
    val date = dateSimpleDateFormat.parse(birthdate!!)

    date?.let {
        val age = getAge(
            yearSimpleDateFormat.format(date).toInt(),
            monthSimpleDateFormat.format(date).toInt(),
            daySimpleDateFormat.format(date).toInt()
        )
        return age
    }
    return ""
}
fun getAge(year: Int, month: Int, day: Int): String {
    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()

    dob.set(year, month, day)

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    val ageInt = age

    return ageInt.toString()
}

class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)

    }
}

fun View.safeClickListener2(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.alpha = 0f
                v.invalidate()
            }
            MotionEvent.ACTION_UP -> {
                v.alpha = 1f
                v.invalidate()


            }
        }
        false
    }
    setOnClickListener(safeClickListener)
}
fun View.safeClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}
@Suppress("DEPRECATION")
fun getLanguageCode(context: Context): String {
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        context.resources.configuration.locale
    }
    return locale.language
}

@Suppress("DEPRECATION")
fun getCountryCode(context: Context): String {
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        context.resources.configuration.locale
    }
    return locale.country
}

fun loadJSONFromAsset(activity: Activity, fileName: String): String {
    var json: String? = null
    try {
        val `is` = activity.assets.open("$fileName.json")
        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        json = String(buffer, Charsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
        return json.toString()
    }

    return json
}

fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this) }
        top?.run { topMargin = dpToPx(this) }
        right?.run { rightMargin = dpToPx(this) }
        bottom?.run { bottomMargin = dpToPx(this) }
    }
}
inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}
fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun CharSequence?.isValidText(): Boolean {
    return this != null && this.length != 0 && this.toString() != "" && this.toString() != " "
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun String?.isValidEmail(): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String?.isValidPhoneNumber(): Boolean {
    return if (this!!.length == 10) {
        true
    }else Patterns.PHONE.matcher(this).matches()
}

fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)

fun hideKeyboard(activity: Activity) {
    val imm =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
fun openKeyboard(activity: Activity, editText: EditText?) {
    val imm =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Long?.strBalance(): String{
    val n: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    return n.format(this!! / 100.0)
}
fun getCurrentlyLocale(): String {
    return Currency.getInstance(Locale.getDefault()).symbol
}
fun toastyInitialize(tintIcon: Boolean, typeface: Typeface?, sizeInSp: Int,allowQueue: Boolean){
    Toasty.Config.getInstance()
        .tintIcon(tintIcon) // optional (apply textColor also to the icon)
        .setToastTypeface(typeface!!) // optional
        .setTextSize(sizeInSp) // optional
        .allowQueue(allowQueue) // optional (prevents several Toastys from queuing)
        .apply(); // required
}
fun toastyReset(){
    Toasty.Config.reset();
}
fun Context.error(@NonNull message:CharSequence){
    Toasty.error(this, message, Toast.LENGTH_SHORT, true).show()
}
fun Context.success(@NonNull message:CharSequence){
    Toasty.success(this, message, Toast.LENGTH_SHORT, true).show()
}
fun Context.info(@NonNull message:CharSequence){
    Toasty.info(this, message, Toast.LENGTH_SHORT, true).show()
}
fun Context.warning(@NonNull message:CharSequence){
    Toasty.warning(this, message, Toast.LENGTH_SHORT, true).show()
}
fun Context.normal(@NonNull message:CharSequence, icon: Drawable?){
    if (icon != null){
        Toasty.normal(this, message,icon).show()
    }else{
        Toasty.normal(this, message).show()
    }
}
fun Context.customToasty(@NonNull message:CharSequence, @DrawableRes iconRes:Int , @ColorRes tintColorRes:Int , duration:Int, withIcon:Boolean , shouldTint:Boolean){
    Toasty.custom(this, message, iconRes, tintColorRes, duration, withIcon, shouldTint).show()
}