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

    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var valueAnimator = ValueAnimator()
    private var downloadButtonStatus = "Download"
    private var widthSize = 0f
    private var heightSize = 0f
    private var progress: Float = 0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> {
                custom_button.isEnabled = false
            }
            ButtonState.Loading -> {
                downloadButtonStatus = "We are loading"
                valueAnimator= ValueAnimator.ofFloat(0f, 1f).apply { addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    repeatCount = ValueAnimator.INFINITE
                    duration = 2000
                    repeatMode = ValueAnimator.REVERSE
                    start()
                }
                custom_button.isEnabled = false
            }
            ButtonState.Completed -> {
                progress = 0f
                downloadButtonStatus = "Download"
                valueAnimator.cancel()
                invalidate()
                custom_button.isEnabled = true
            }
        }
        invalidate()
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
        widthSize = (width.toFloat() * 2)
        heightSize = height.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val loadingButtonWidth = measuredWidth.toFloat()
        val loadingButtonHeight = measuredHeight.toFloat()

        //To assign the paint color to be buttonBackgroundColor.
        paint.color = buttonBackgroundColor
        //Draw the rectangle loading button.
        canvas.drawRect(loadingButtonWidth, loadingButtonHeight, 0f, 0f, paint)

        //Reassign teh paint color to and then draw the (LoadingButton) text.
        paint.color = buttonTextColor
        canvas.drawText(downloadButtonStatus, pivotX, pivotY, paint)

        //Check if the (LoadingButton) in the loading state.
        if (buttonState == ButtonState.Loading) {
            paint.color = resources.getColor(R.color.colorPrimaryDark)
            var arcProgress = progress * measuredWidth
            canvas.drawRect(0f, 0f, arcProgress, loadingButtonHeight,  paint)

            paint.color = buttonTextColor
            canvas.drawText(downloadButtonStatus, pivotX, pivotY, paint)

            //Set the diameter of the arc
            // and then evaluate its size.
            val arcDiameter = 40f
            val arcSizeOfRect = measuredHeight.toFloat()  - arcDiameter - (paddingBottom.toFloat()+ paddingTop.toFloat())

            paint.color = resources.getColor(R.color.colorAccent)
            arcProgress = progress * 360f

            // Translate the canvas to allow us draw on a new specific area.
            canvas.translate(pivotX + arcSizeOfRect*2, 1f)
            canvas.drawArc(10f, 10f, arcSizeOfRect, arcSizeOfRect, 0f, arcProgress, true, paint)
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
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

    fun setLoadingButtonStatus(status: ButtonState) {
        buttonState = status
    }

}