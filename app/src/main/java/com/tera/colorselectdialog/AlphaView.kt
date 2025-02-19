package com.tera.colorselectdialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat


typealias OnAlphaChangeListener = (view: AlphaView, alpha: Int) -> Unit

class AlphaView (context: Context, attrs: AttributeSet?
): View(context, attrs) {

    companion object{
        const val ALPHA = 255
    }

    private var listener: OnAlphaChangeListener? = null

    private var mPaintStroke = Paint()
    private var mPaintFill = Paint()
    private var mPaintShader = Paint()
    private var mViewWidth = VIEW_WIDTH
    private var mViewHeight = VIEW_HEIGHT
    private var mPosCursor = OFFSET.toFloat()
    private var mOffset = OFFSET.toFloat()
    private var mShaderVert: Shader? = null
    private var mColor1 = Color.argb(ALPHA, 255, 0, 0)
    private var mColor2 = Color.argb(0, 0, 0, 0)
    init {
        mPaintFill.color = COLOR_FILL

        mPaintStroke.color = COLOR_STROKE
        mPaintStroke.isAntiAlias = true
        mPaintStroke.style = Paint.Style.STROKE
        mPaintStroke.strokeWidth = STROKE_WIDTH
        setShader()
    }

    private fun setShader() {
        val x0 = OFFSET.toFloat()
        val y0 = OFFSET.toFloat()
        val y1 = y0 + mViewHeight

        mShaderVert = LinearGradient(
            x0, y0, x0, y1, mColor1, mColor2, TileMode.CLAMP)
        mPaintShader.setShader(mShaderVert)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                changeValue(event.y)
            }
        }
        return true
    }

    private fun changeValue(eventY: Float) {
        var y = eventY
        if (y < mOffset) y = mOffset
        if (y > mViewHeight + mOffset) y = mViewHeight + mOffset
        mPosCursor = y
        invalidate()
        val alpha = 255 - ((mPosCursor - OFFSET) * 255f / mViewHeight).toInt()
        listener?.invoke(this, alpha)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawImage(canvas)
        drawGrad(canvas)
        drawCursor(canvas)
    }

    private fun drawGrad(canvas: Canvas) {
        val x1 = OFFSET.toFloat()
        val y1 = OFFSET.toFloat()
        val x2 = x1 + mViewWidth
        val y2 = y1 + mViewHeight
        canvas.drawRect(x1, y1, x2, y2, mPaintShader)
    }

    private fun drawImage(canvas: Canvas) {
        val x1 = OFFSET
        val y1 = OFFSET
        val x2 = x1 + mViewWidth
        val y2 = y1 + mViewHeight
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_cells_12) as Drawable
        drawable.setBounds(x1, y1, x2, y2)
        drawable.draw(canvas)
    }

    private fun drawCursor(canvas: Canvas) {
        val xC = OFFSET
        val yC = mPosCursor
        val x1 = xC - RADIUS
        val y1 = yC - RADIUS
        val x2 = xC + mViewWidth.toFloat() + RADIUS
        val y2 = yC + RADIUS
        val r = RADIUS
        canvas.drawRoundRect(x1, y1, x2, y2, r, r, mPaintFill)
        canvas.drawRoundRect(x1, y1, x2, y2, r, r, mPaintStroke)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wC = mViewWidth + OFFSET * 2
        val hC = mViewHeight + OFFSET * 2

        setMeasuredDimension(
            resolveSize(wC, widthMeasureSpec),
            resolveSize(hC, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w - OFFSET * 2
        mViewHeight = h - OFFSET * 2
        setShader()
    }

    fun setOnAlphaChangeListener(listener: OnAlphaChangeListener){
        this.listener = listener
    }

    var positionY: Float = mPosCursor
        set(value) {
            field = value
            mPosCursor = value - OFFSET
            invalidate()
        }

    var color: Int = 0
        set(value) {
            field = value
            mColor1 = getColorA(value)
            setShader()
            invalidate()
        }

    private fun getColorA(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(ALPHA, red, green, blue)
    }


}