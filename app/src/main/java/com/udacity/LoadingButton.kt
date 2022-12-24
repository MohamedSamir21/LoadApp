package com.udacity

import android.animation.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progress: Float = 0f
    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var valueAnimator = ValueAnimator()
    private var downloadButtonStatus = "Download"

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {
                this.downloadButtonStatus = "We are loading"
                valueAnimator= ValueAnimator.ofFloat(0f, 300f).apply {
                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }

                    repeatCount = ValueAnimator.INFINITE
                    duration = 100000
                    repeatMode = ValueAnimator.REVERSE
                    start()
                }
                custom_button.isEnabled = false
            }
            ButtonState.Completed -> {
                this.progress = 0f
                this.downloadButtonStatus = "Completed"
                valueAnimator.cancel()
                invalidate()
                custom_button.isEnabled = false
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 65.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }



    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor,0)
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        widthSize = (width * 2)
        heightSize = height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = buttonBackgroundColor
        val loadingButtonWidth = measuredWidth.toFloat()
        val loadingButtonHeight = measuredHeight.toFloat()
        canvas.drawRect(loadingButtonWidth, loadingButtonHeight, 0f, 0f, paint)
        paint.color = buttonTextColor
        canvas?.drawText(downloadButtonStatus, pivotX, pivotY, paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = resources.getColor(R.color.colorPrimaryDark)
            var progressVal = progress++ * measuredWidth.toFloat()
            canvas.drawRoundRect(0f, 0f, progressVal, loadingButtonHeight, 10f, 10f, paint)

            paint.color = buttonTextColor
            canvas.drawText(downloadButtonStatus, pivotX, pivotY, paint)
            val arcDiameter = 100 * 2
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            paint.color = resources.getColor(R.color.colorAccent)
            progressVal = progress * 360f

            canvas.drawArc(paddingStart + arcDiameter.toFloat(),
                paddingTop.toFloat() + arcDiameter,
                arcRectSize,
                arcRectSize,
                0f,
                progressVal,
                true,
                paint)
        }
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

    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

}