package com.tera.colorselectdialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

typealias OnToneChangeListener = (view: ToneView, tone: Float) -> Unit

class ToneView (context: Context, attrs: AttributeSet?
): View(context, attrs) {

    private var listener: OnToneChangeListener? = null

    private var mPaintStroke = Paint()
    private var mPaintFill = Paint()
    private var mViewWidth = VIEW_WIDTH
    private var mViewHeight = VIEW_HEIGHT
    private var mOffset = OFFSET.toFloat()
    private var mPosCursor = OFFSET.toFloat()

    init {
        mPaintFill.color = COLOR_FILL

        mPaintStroke.color = COLOR_STROKE
        mPaintStroke.isAntiAlias = true
        mPaintStroke.style = Paint.Style.STROKE
        mPaintStroke.strokeWidth = STROKE_WIDTH
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
        var tone = 360 - (mPosCursor - OFFSET) * 360f / mViewHeight
        if (tone > 357) tone = 360.0f
        if (tone < 2) tone = 0f
        listener?.invoke(this, tone)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawImage(canvas)
        drawCursor(canvas)
    }

    private fun drawImage(canvas: Canvas) {
        val x1 = OFFSET
        val y1 = OFFSET
        val x2 = x1 + mViewWidth
        val y2 = y1 + mViewHeight
        val drawable = ContextCompat.getDrawable(context, R.drawable.tone) as Drawable
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
    }

    fun setOnToneChangeListener(listener: OnToneChangeListener){
        this.listener = listener
    }

    var positionY: Float = mPosCursor
        set(value) {
            field = value
            mPosCursor = value - OFFSET
            invalidate()
        }

}