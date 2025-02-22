package com.tera.colorselectdialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton

typealias OnClickListener = (color: Int) -> Unit

class PaletteDialog(context: Context?, color: Int) {

    private var mListener: OnClickListener? = null
    private var mDialog: AlertDialog? = null
    private var mColor = color
    private var mAlpha = 0
    private var mTone = 0f
    private var mSat = 0f
    private var mValue = 0f

    private var square: SquareView
    private var tone: ToneView
    private var alpha: AlphaView

    private var tvAlpha: TextView
    private var tvRed: TextView
    private var tvGreen: TextView
    private var tvBlue: TextView
    private var tvHex: TextView
    private var viewOld: View
    private var viewNew: View
    private var bnOk: MaterialButton
    private var bnCansel: MaterialButton
    private var handler = Handler(Looper.getMainLooper())

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_palette_layout, null)

        square = view.findViewById(R.id.square)
        tone = view.findViewById(R.id.tone)
        alpha = view.findViewById(R.id.alpha)

        tvAlpha = view.findViewById(R.id.tvAlpha)
        tvRed = view.findViewById(R.id.tvRed)
        tvGreen = view.findViewById(R.id.tvGreen)
        tvBlue = view.findViewById(R.id.tvBlue)
        tvHex = view.findViewById(R.id.tvHex)
        viewOld = view.findViewById(R.id.viewOld)
        viewNew = view.findViewById(R.id.viewNew)
        bnOk = view.findViewById(R.id.bnOk)
        bnCansel = view.findViewById(R.id.bnCansel)

        mDialog = AlertDialog.Builder(context).create()

        bnOk.setOnClickListener {
            mListener?.invoke(mColor)
            mDialog?.dismiss()
        }
        bnCansel.setOnClickListener {
            mDialog?.dismiss()
        }

        mDialog?.setCancelable(false)
        mDialog?.setView(view)
        mDialog?.show()

        initCursors()

        // Get parameter on open
        handler.postDelayed({
            setParamsStart()
            moveCursorsStart()
            setText()
        }, 100)
    }

    private fun initCursors() {
        square.setOnColorChangeListener { _, color ->
            mColor = getColorFromSquare(color)
            viewNew.setBackgroundColor(mColor)
            alpha.color = mColor
            setText()
        }
        tone.setOnToneChangeListener { _, tone ->
            mTone = tone
            mColor = getColorFromTone()
            square.tone = tone
            square.color = mColor
            viewNew.setBackgroundColor(mColor)
            alpha.color = mColor
            setText()
        }
        alpha.setOnAlphaChangeListener { _, alpha ->
            mAlpha = alpha
            mColor = getColorFromAlpha()
            viewNew.setBackgroundColor(mColor)
            setText()
        }
    }

    private fun getColorFromSquare(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return Color.HSVToColor(mAlpha, hsv)
    }

    private fun getColorFromTone(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(mColor, hsv)
        val sat = hsv[1]
        val value = hsv[2]
        hsv[0] = mTone
        hsv[1] = sat
        hsv[2] = value
        val color = Color.HSVToColor(mAlpha, hsv)
        return color
    }

    private fun getColorFromAlpha(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(mColor, hsv)
        return Color.HSVToColor(mAlpha, hsv)
    }

    private fun setParamsStart() {
        val hsv = FloatArray(3)
        Color.colorToHSV(mColor, hsv)
        mTone = hsv[0]
        mSat = hsv[1]
        mValue = hsv[2]
        mAlpha = Color.alpha(mColor)
        square.tone = mTone
        square.color = mColor
        alpha.color = mColor
        viewOld.setBackgroundColor(mColor)
        viewNew.setBackgroundColor(mColor)
    }

    private fun moveCursorsStart() {
        val w = square.width
        val h = square.height
        val ws = w - OFFSET * 2
        val x = mSat * ws
        square.positionX = x
        val hs = h - OFFSET * 2
        var y = (1f - mValue) * hs
        square.positionY = y

        y = h - (mTone * hs / 360f)
        tone.positionY = y
        y = h - (mAlpha * hs / 255f)
        alpha.positionY = y
    }

    private fun setText() {
        val red = Color.red(mColor)
        val green = Color.green(mColor)
        val blue = Color.blue(mColor)
        var str = red.toString()
        tvRed.text = str
        str = green.toString()
        tvGreen.text = str
        str = blue.toString()
        tvBlue.text = str

        val rate = (mAlpha * 100 / 255)
        str = rate.toString()
        tvAlpha.text = str
        var hexColor = Integer.toHexString(mColor)
        if (rate == 100)
            hexColor = hexColor.substring(2, hexColor.length)
        hexColor = "#${hexColor.uppercase()}"
        tvHex.text = hexColor
    }


    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    var buttonOkColor: Int = 0
        set(value) {
            field = value
            bnOk.setBackgroundColor(value)
        }

    var textOkColor: Int = 0
        set(value) {
            field = value
            bnOk.setTextColor(value)
        }

    var buttonCancelColor: Int = 0
        set(value) {
            field = value
            bnCansel.setBackgroundColor(value)
        }

    var textCancelColor: Int = 0
        set(value) {
            field = value
            bnCansel.setTextColor(value)
        }

}