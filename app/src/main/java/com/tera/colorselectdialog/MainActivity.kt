package com.tera.colorselectdialog

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.tera.colorselectdialog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val ALPHA_VALUE = 8
        const val COLOR = "color"
        const val ALPHA = "alpha"
        const val RED = "red"
        const val GREEN = "green"
        const val BLUE = "blue"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var sp: SharedPreferences

    private var mColor = 0
    private var mAlpha = 0
    private var mRed = 0
    private var mGreen = 0
    private var mBlue = 0

    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        mColor = sp.getInt(COLOR, 0)
        mAlpha = sp.getInt(ALPHA, 255)
        mRed = sp.getInt(RED, 255)
        mGreen = sp.getInt(GREEN, 0)
        mBlue = sp.getInt(BLUE, 0)

        setSlider()
        setTextColor()
        initSlider()

        handler.postDelayed({
//            setParams()
        }, 100)
    }

    private fun setSlider() = with(binding) {
        slAlpha.value = mAlpha.toFloat()
        slRed.value = mRed.toFloat()
        slGreen.value = mGreen.toFloat()
        slBlue.value = mBlue.toFloat()
    }

    private fun setTextColor() = with(binding){
        mColor = Color.argb(mAlpha, mRed, mGreen, mBlue)
        var str = mColor.toString()
        tvColor.text = str
        vColor.setBackgroundColor(mColor)

        str = mRed.toString()
        tvRed.text = str
        str = mGreen.toString()
        tvGreen.text = str
        str = mBlue.toString()
        tvBlue.text = str

        val rate = (mAlpha * 100 / 255)
        str = rate.toString()
        tvAlpha.text = str

        var hexColor = Integer.toHexString(mColor)
        if (mAlpha == 255)
            hexColor = hexColor.substring(2, hexColor.length)
        hexColor = "#${hexColor.uppercase()}"
        tvHex.text = hexColor

        setLineColor()
    }

    private fun initSlider() = with(binding) {
        slAlpha.addOnChangeListener { _, value, _ ->
            mAlpha = value.toInt()
            setTextColor()
        }
        slRed.addOnChangeListener { _, value, _ ->
            mRed = value.toInt()
            setTextColor()
        }
        slGreen.addOnChangeListener { _, value, _ ->
            mGreen = value.toInt()
            setTextColor()
        }
        slBlue.addOnChangeListener { _, value, _ ->
            mBlue = value.toInt()
            setTextColor()
        }
        bnOpen.setOnClickListener {
            openDialog()
        }
    }

    private fun colorFromDialog() {
        mAlpha = Color.alpha(mColor)
        mRed = Color.red(mColor)
        mGreen = Color.green(mColor)
        mBlue = Color.blue(mColor)
    }

    private fun openDialog() {
        PaletteDialog(this, mColor)
            .setOnClickListener {
                mColor = it
                colorFromDialog()
                setSlider()
            }
    }
    private fun openDialog2() {
        val dialog = PaletteDialog(this, mColor)
        dialog.buttonOkColor = -2490390
        dialog.textOkColor = -16546298
        dialog.buttonCanselColor = -6426
        dialog.textCanselColor = -3012857
        dialog.setOnClickListener {
            mColor = it
            colorFromDialog()
            setSlider()
        }
    }

    private fun setLineColor() = with(binding) {
        var alpha = 255 - (255 - mRed)
        var color = Color.argb(alpha, 255, 0, 0)
        vRed.setBackgroundColor(color)

        alpha = 255 - (255 - mGreen)
        color = Color.argb(alpha, 0, 255, 0)
        vGreen.setBackgroundColor(color)

        alpha = 255 - (255 - mBlue)
        color = Color.argb(alpha, 0, 0, 255)
        vBlue.setBackgroundColor(color)

        val back = Color.argb(ALPHA_VALUE, mRed, mGreen, mBlue)
        main.setBackgroundColor(back)
    }

    override fun onStop() {
        super.onStop()
        val editor = sp.edit()
        editor.putInt(COLOR, mColor)
        editor.putInt(ALPHA, mAlpha)
        editor.putInt(RED, mRed)
        editor.putInt(GREEN, mGreen)
        editor.putInt(BLUE, mBlue)
        editor.apply()
    }
}