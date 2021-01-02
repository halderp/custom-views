package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progressWidth = 0
    private var angle = 0

    private var buttonBackgroundColor = 0
    private var textColor = 0

    private var rectangleAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when (new) {
            ButtonState.Loading -> {
                rectangleAnimator = ValueAnimator.ofInt(0, widthSize).apply {
                    duration = 3000
                    addUpdateListener { valueAnimator ->
                        progressWidth = animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        valueAnimator.repeatMode = ValueAnimator.REVERSE
                        invalidate()
                    }
                    start()
                }
                circleAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration = 1000
                    addUpdateListener { valueAnimator ->
                        angle = valueAnimator.animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                rectangleAnimator.end()
                progressWidth = 0
                circleAnimator.end()
                angle = 0
                invalidate()
            }
        }
    }

    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                buttonBackgroundColor = getColor(
                    R.styleable.LoadingButton_buttonBackgroundColor,
                    context.getColor(R.color.colorPrimary)
                )
                textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            } finally {
                recycle()
            }
        }
    }

    // Used for the styling of the text...
    private var robotoFont = "roboto"
    private val textRect = Rect()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        color = textColor
        typeface = Typeface.create(robotoFont, Typeface.NORMAL)
    }

    // Used for the styling of the arc...
    val cornerRadius = 10.0f
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
    }

    private val paint = Paint()


    override fun onDraw(canvas: Canvas) {

        // draw fixed rectangle

        paint.color = buttonBackgroundColor
        canvas.drawRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(), paint
        )

        //draw dynamic rectangle
        paint.color = getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(
            0f,
            0f,
            widthSize.toFloat() * progressWidth / 100,
            heightSize.toFloat(), paint
        )


        //draw the text
        val buttonLabel = when (buttonState) {
            ButtonState.Clicked -> "Clicked"
            ButtonState.Loading -> "We are loading"
            ButtonState.Completed -> "Download"
        }
        textPaint.getTextBounds(buttonLabel, 0, buttonLabel.length, textRect)
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - textRect.centerY()
        canvas.drawText(buttonLabel, centerX, centerY, textPaint)


        //draw the circular progress
        val arcDiameter = cornerRadius * 2
        canvas.drawArc(
            (widthSize - 100f),
            paddingTop.toFloat() + arcDiameter,
            (widthSize - 50f),
            paddingTop.toFloat() + arcDiameter + 75f,
            0F,
            angle.toFloat(),
            true,
            arcPaint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }

    // Used to provide a way to change the button state from the main activity
    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

}