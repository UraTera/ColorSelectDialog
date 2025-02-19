package com.tera.colorselectdialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

const val VIEW_WIDTH = 78
const val VIEW_HEIGHT = 582 // Size for real phone
const val OFFSET = 17

// Курсор
const val RADIUS = 10f
const val COLOR_FILL = -1776926
const val COLOR_STROKE = -7434610
const val STROKE_WIDTH = 4f


typealias OnColorChangeListener = (view: SquareView, color: Int) -> Unit

class SquareView(
    context: Context, attrs: AttributeSet?
) : View(context, attrs) {

    private var listener: OnColorChangeListener? = null
    private var mPaintRect = Paint()
    private var mPaintCircle = Paint()
    private var mShaderVert: Shader? = null
    private var mShaderHor: Shader? = null
    private var mViewWidth = VIEW_HEIGHT
    private var mViewHeight = VIEW_HEIGHT
    private val mColorHsv = floatArrayOf(0f, 1f, 1f)
    private var mColor = 0
    private var mOffset = OFFSET.toFloat()
    private var mRadius = mOffset - STROKE_WIDTH / 2
    private var mCircleX = OFFSET * 2f
    private var mCircleY = OFFSET * 2f
    private var mTone = 0f

    init {
        mPaintCircle.isAntiAlias = true
        mPaintCircle.style = Paint.Style.STROKE
        mPaintCircle.strokeWidth = 4f
        setShader()
    }

    private fun setShader() {
        val x0 = mOffset
        val x1 = x0 + mViewWidth
        val y0 = mOffset
        val y1 = y0 + mViewHeight

        mShaderVert = LinearGradient(
            x0, y0, x0, y1, -1, -16777216, TileMode.CLAMP
        )

        val color = Color.HSVToColor(mColorHsv)
        mShaderHor = LinearGradient(
            x0, y0, x1, y0, -1, color, TileMode.CLAMP
        )
        val shader = ComposeShader(mShaderVert!!, mShaderHor!!, PorterDuff.Mode.MULTIPLY)
        mPaintRect.setShader(shader)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,  MotionEvent.ACTION_MOVE -> {
                changeValue(event.x, event.y)
            }
        }
        return true
    }

    private fun changeValue(eventX: Float, eventY: Float) {
        var x = eventX
        var y = eventY
        if (x < mOffset) x = mOffset
        if (y < mOffset) y = mOffset
        if (x > mViewWidth + mOffset) x = mViewWidth + mOffset
        if (y > mViewHeight + mOffset) y = mViewHeight + mOffset

        mCircleX = x
        mCircleY = y
        mColor = getColor(x, y)
        setCursorColor()
        invalidate()
        listener?.invoke(this, mColor)
    }

    private fun getColor(x: Float, y: Float): Int {
        val sat = (x - OFFSET) * 1f / mViewWidth
        val value = 1f - (y - OFFSET) * 1f / mViewHeight
        mColorHsv[0] = mTone
        mColorHsv[1] = sat
        mColorHsv[2] = value
        return Color.HSVToColor(mColorHsv)
    }

    private fun setCursorColor() {
        val luminance = Color.luminance(mColor)
        if (luminance > 0.15f)
            mPaintCircle.color = Color.BLACK
        else
            mPaintCircle.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRect(canvas)
        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaintCircle)
    }

    private fun drawRect(canvas: Canvas) {
        val x1 = mOffset
        val y1 = mOffset
        val x2 = x1 + mViewWidth
        val y2 = y1 + mViewHeight
        canvas.drawRect(x1, y1, x2, y2, mPaintRect)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wC = mViewWidth + mOffset * 2
        val hC = mViewHeight + mOffset * 2

        setMeasuredDimension(
            resolveSize(wC.toInt(), widthMeasureSpec),
            resolveSize(hC.toInt(), heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = (w - mOffset * 2).toInt()
        mViewHeight = (h - mOffset * 2).toInt()
    }

    fun setOnColorChangeListener(listener: OnColorChangeListener) {
        this.listener = listener
    }

    var color: Int
        get() = mColor
        set(value) {
            mColor = value
            setCursorColor()
        }

    var tone: Float = 1f
        set(value) {
            field = value
            mColorHsv[0] = value
            mColorHsv[1] = 1f
            mColorHsv[2] = 1f
            mTone = value
            setShader()
            invalidate()
        }

    var positionX: Float = mCircleX
        set(value) {
            field = value
            mCircleX = value + OFFSET
            invalidate()
        }

    var positionY: Float = mCircleY
        set(value) {
            field = value
            mCircleY = value + OFFSET
            invalidate()
        }
}
