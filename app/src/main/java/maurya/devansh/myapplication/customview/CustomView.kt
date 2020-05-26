package maurya.devansh.myapplication.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import maurya.devansh.myapplication.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Created by Devansh on 26/5/20
 */

/**
 * @constructor(context) used when creating views programmatically
 * @constructor(context, attrs) used when creating view from XML
 */
class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var mTranslateX = 0
    private var mTranslateY = 0
    private var mBackgroundCircleRadius = 0
    private var mBackgroundCircleColor = 0
    private var mBackgroundCircleWidth = 0f
    private var mBlobColor = 0
    private var mProgressColor = 0
    private val mCircleRect = RectF()
    private val circlePaint: Paint
    private val progressPaint: Paint
    private val blobPaint: Paint

    private var mProgressAngle = 0f
    private var mPreviousAngle = 0f

    companion object {
        private const val OFFSET = -90f
    }

    init {
        val density = resources.displayMetrics.density

        mBackgroundCircleColor = ContextCompat.getColor(context, R.color.colorBackgroundCircle)
        mBlobColor = ContextCompat.getColor(context, R.color.colorBlob)
        mProgressColor = ContextCompat.getColor(context, R.color.colorProgress)
        mBackgroundCircleWidth = (10 * density)

        // Creating here because onDraw is called frequently and object initialisation there can make the app slow
        circlePaint = Paint().apply {
            color = mBackgroundCircleColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = mBackgroundCircleWidth
        }

        progressPaint = Paint().apply {
            color = mProgressColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = mBackgroundCircleWidth
        }

        blobPaint = Paint().apply {
            color = mBlobColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val min = min(width, height)

        mTranslateX = (width * 0.5).toInt()
        mTranslateY = (height * 0.5).toInt()

        val diameter = min - paddingLeft
        mBackgroundCircleRadius = (diameter / 2)
        val top = height / 2 - (diameter / 2f)
        val left = width / 2 - (diameter / 2f)

        mCircleRect.set(left, top, left + diameter, top + diameter)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(mCircleRect, OFFSET, 360f, false, circlePaint)
        canvas.drawArc(mCircleRect, OFFSET, mProgressAngle, false, progressPaint)

        // Use polar equation of circle to get X and Y coordinates of blob marker
        val blobPosX = mTranslateX + mBackgroundCircleRadius * cos((mProgressAngle + OFFSET).toRadians())
        val blobPosY = mTranslateY + mBackgroundCircleRadius * sin((mProgressAngle + OFFSET).toRadians())
        canvas.drawCircle(blobPosX, blobPosY, 30f, blobPaint)
    }

    fun setProgress(value: Int) {
        // Restrict max progress to 100
        mPreviousAngle = mProgressAngle
        val progress = min(value, 100)
        val maxProgressAngle = 3.6f * progress

        val animator = ValueAnimator.ofFloat(mPreviousAngle, maxProgressAngle).apply {
            duration = 1000
            addUpdateListener {
                mProgressAngle = it.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }

    private fun Float.toRadians(): Float = (this / 180) * PI.toFloat()
}