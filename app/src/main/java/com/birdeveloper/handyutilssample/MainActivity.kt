package com.birdeveloper.handyutilssample

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log.d
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.birdeveloper.handyutils.*
import com.birdeveloper.handyutils.screenshot.Screenshot
import com.birdeveloper.handyutils.screenshot.properties.Flip
import com.birdeveloper.handyutils.screenshot.properties.Quality
import com.birdeveloper.handyutils.screenshot.properties.Rotate
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var rootView : ConstraintLayout
    lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        rootView = findViewById(R.id.rootView)
        //screenshot nasıl alınır
        //bitmap = Screenshot.with(this).setView(rootView).setQuality(Quality.HIGH).setFlip(Flip.HORIZONTALLY).shareScreenshot(true).getAsBitmap()
        Handler().postDelayed({
            //imageView.setImageBitmap(bitmap)
        },2000)
        /*resimi çevirmek için
        * Flip.HORIZONTALLY
        * Flip.VERTICALLY
        * Flip.NOTHING
        * */
        //bitmap.flip(Flip.HORIZONTALLY)
        //or
        //bitmap.flip(90f,90f,45f,45f)
        /*
        resmi döndürmek için
        DEGREE_0,
        DEGREE_90,
        DEGREE_180,
        DEGREE_270
         */
        //bitmap.rotate(Rotate.DEGREE_90)
        //or
        //bitmap.rotate(90f)

        /*
        view'i bitmap a çevir
         */
        //window.decorView.rootView.getAsBitmap(0f,Quality.HIGH,Flip.NOTHING)
        //log cat kullanmak için
        if (BuildConfig.DEBUG) {
            initializeLog(true)
        }
        d { "Sample Log d"}
        i { "Sample Log i"}
        w { "Sample Log w"}
        e { "Sample Log e"}
        wtf { "Sample Log wtf"}

        //activity refresh
        //this.recreate()

        //get user age
        d { "getUserAge: " +getUserAge("1998-05-22")}

        //get language Code
        d { "getLanguageCode: "+ getLanguageCode(this)}

        //get Country Code
        d { "getCountryCode: " + getCountryCode(this)}

        //get load Json From Asset
        d { "loadJSONFromAsset: "+ loadJSONFromAsset(this,"file")}

        //bir view'e marginler vermek için kullanılacak kod
        //rootView.margin(15f,15f,15f,15f)

        //is valid text
        d { "isValidText: " + "".isValidText() + " ----> " +"isValidText: " + "Görkem".isValidText()}

        //is emailValid text
        d { "isEmailValid: " + "birdeveloper.com@gmail.com".isValidEmail() }

        //is phoneNumber text
        d { "isPhoneNumber: " + "+905539890976".isValidPhoneNumber()}

        //open keyboard
        //openKeyboard(this,null)
        //close keyboard
        //hideKeyboard(this)

        //balance(long) to format and locale currently symbol
        d { "totalBalance: "+ (9042322).toLong().strBalance() + " <---> " +getCurrentlyLocale()}

        //toast message show
        rootView.safeClickListener {
            this.customToasty("Sample Toasty Message",R.drawable.ic_favorite_black_24dp,R.color.customToastyBG,5500,true,true)
        }

    }
}
