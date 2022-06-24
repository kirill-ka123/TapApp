package com.example.tap

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.tap.util.Constants.Companion.ARC_THICKNESS
import com.example.tap.util.Constants.Companion.DISTANCE_BETWEEN_ARCS
import com.example.tap.util.Constants.Companion.DISTANCE_BETWEEN_BIG_AND_SMALL_OVALS
import com.example.tap.util.Constants.Companion.MARGIN_FROM_EDGE_RELATIVE_XY
import com.example.tap.util.Constants.Companion.RATIO_OF_RADIUS_BIG_OVAL
import com.example.tap.util.Constants.Companion.RATIO_OF_RADIUS_SMALL_OVAL
import com.example.tap.util.Constants.Companion.SIZE_OF_SMALL_OVAL_RELATIVE_Y
import com.example.tap.util.Constants.Companion.TIME_FOR_INTERACTION

class CustomProgressBar(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val paintRedArc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.primary_pb_color, null)
        style = Paint.Style.STROKE
        strokeWidth = ARC_THICKNESS
        strokeCap = Paint.Cap.BUTT
    }
    private val paintWhiteArc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.primary_pb_background_color, null)
        style = Paint.Style.STROKE
        strokeWidth = ARC_THICKNESS
        strokeCap = Paint.Cap.BUTT
    }
    private val paintCircleWhite = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.secondary_pb_background_color, null)
        style = Paint.Style.FILL
    }
    private val paintCircleRed = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.secondary_pb_color, null)
        style = Paint.Style.FILL
    }
    private lateinit var rectForArc: RectF
    private lateinit var rectForBigCircle: RectF
    private lateinit var rectForSmallCircle: RectF

    private var indeterminateSweepWhite = 40F
    private var indeterminateSweepRed = 0F
    private var startAngle = 180F
    private var maxProgress = TIME_FOR_INTERACTION
    private var path = Path()

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        // Отступы прямоугольников от краев экрана
        val xIndent = xNew * MARGIN_FROM_EDGE_RELATIVE_XY
        val yIndent = yNew * MARGIN_FROM_EDGE_RELATIVE_XY

        // Прямоугольники внутри которых будут рисоваться овалы
        rectForArc =
            RectF(
                xIndent - DISTANCE_BETWEEN_ARCS,
                yIndent,
                xIndent + (yNew - 2 * yIndent) / RATIO_OF_RADIUS_BIG_OVAL,
                yNew - yIndent
            )

        rectForBigCircle =
            RectF(
                xIndent,
                yIndent,
                xIndent + (yNew - 2 * yIndent) / RATIO_OF_RADIUS_BIG_OVAL,
                yNew - yIndent
            )
        rectForSmallCircle =
            RectF(
                xIndent + DISTANCE_BETWEEN_BIG_AND_SMALL_OVALS,
                yIndent + yNew * SIZE_OF_SMALL_OVAL_RELATIVE_Y,
                xIndent + (yNew - 2 * yIndent) / RATIO_OF_RADIUS_SMALL_OVAL,
                yNew - yIndent - yNew * SIZE_OF_SMALL_OVAL_RELATIVE_Y
            )


        path = Path()
        path.addOval(rectForSmallCircle, Path.Direction.CW)
        path.fillType = Path.FillType.INVERSE_EVEN_ODD

        invalidate()
    }

    fun setMaxProgress(value: Int) {
        maxProgress = value
        invalidate()
    }

    fun setProgress(value: Int) {
        indeterminateSweepRed = (value.toFloat() / maxProgress.toFloat()) * (40F)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.clipPath(path)
        canvas.drawArc(
            rectForBigCircle,
            startAngle - indeterminateSweepWhite,
            indeterminateSweepWhite * 2,
            true,
            paintCircleWhite
        )
        canvas.drawArc(
            rectForBigCircle,
            startAngle - indeterminateSweepRed,
            indeterminateSweepRed * 2,
            true,
            paintCircleRed
        )

        canvas.drawArc(
            rectForArc,
            startAngle - indeterminateSweepWhite,
            indeterminateSweepWhite * 2,
            false,
            paintWhiteArc
        )
        canvas.drawArc(
            rectForArc,
            startAngle - indeterminateSweepRed,
            indeterminateSweepRed * 2,
            false,
            paintRedArc
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}