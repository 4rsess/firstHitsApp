package com.example.photoeditorv2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FifthAlgorithm : AppCompatActivity() {

    private val points = mutableListOf<Pair<Float, Float>>()
    private var isTransformed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_fifthalgorithm)

        val container = findViewById<FrameLayout>(R.id.drawingContainer)
        val drawingView = DrawingView(this)
        container.addView(drawingView)

        val backToHome = findViewById<TextView>(R.id.back)
        backToHome.setOnClickListener {
            finish()
        }

        val clearBtn = findViewById<TextView>(R.id.clearBtn)
        clearBtn.setOnClickListener {
            points.clear()
            isTransformed = false
            drawingView.invalidate()
        }

        val transformationBtn = findViewById<TextView>(R.id.transformationBtn)
        transformationBtn.setOnClickListener {
            isTransformed = !isTransformed
            drawingView.invalidate()
        }
    }

    inner class DrawingView(context: Context) : View(context) {
        private val linePaint = Paint().apply {
            color = Color.rgb(186, 184, 184)
            isAntiAlias = true
            strokeWidth = 17f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        private val pointPaint = Paint().apply {
            color = Color.rgb(86, 140, 52)
            isAntiAlias = true
            strokeWidth = 33f
            style = Paint.Style.FILL
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            if (!isTransformed) {
                drawLines(canvas)
            } else {
                drawSpline(canvas)
            }
            drawPoints(canvas)
        }

        private fun drawPoints(canvas: Canvas) {
            for (point in points) {
                canvas.drawCircle(point.first, point.second, 17.5f, pointPaint)
            }
        }

        private fun drawLines(canvas: Canvas) {
            if (points.size < 2) return
            val path = Path()
            path.moveTo(points[0].first, points[0].second)
            for (i in 1 until points.size) {
                val (x, y) = points[i]
                path.lineTo(x, y)
            }
            canvas.drawPath(path, linePaint)
        }

        private fun drawSpline(canvas: Canvas) {
            if (points.size < 2) return

            val path = Path()
            path.moveTo(points[0].first, points[0].second)


            for (i in 0 until points.size - 1) {
                //кординаты предыдущей точки или текущ точки, если это первая
                val x0 = if (i > 0) points[i - 1].first else points[i].first
                val y0 = if (i > 0) points[i - 1].second else points[i].second
                //координаты текущ точки
                val x1 = points[i].first
                val y1 = points[i].second
                //оординаты след точки
                val x2 = points[i + 1].first
                val y2 = points[i + 1].second
                //координаты через одну точку или след точки, если это была последней
                val x3 = if (i < points.size - 2) points[i + 2].first else points[i + 1].first
                val y3 = if (i < points.size - 2) points[i + 2].second else points[i + 1].second


                val cx = 3 * (x1 - x0) //t
                val bx = 3 * (x2 - x1) - cx //t^2
                val ax = x3 - x0 - cx - bx //t^3

                val cy = 3 * (y1 - y0) //t
                val by = 3 * (y2 - y1) - cy //t^2
                val ay = y3 - y0 - cy - by //t^3

                //разбиваем сегмент на 20 частей и вычисляем координаты для каждой части
                for (t in 0..20) {
                    val tNormalized = t / 20f //нормализуем t 0-1

                    //вычисляем координаты и добавляем в путь
                    val x =
                        ax * tNormalized * tNormalized * tNormalized + bx * tNormalized * tNormalized + cx * tNormalized + x0
                    val y =
                        ay * tNormalized * tNormalized * tNormalized + by * tNormalized * tNormalized + cy * tNormalized + y0
                    path.lineTo(x, y)
                }
            }

            //рисуем путь
            canvas.drawPath(path, linePaint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    points.add(x to y)
                }

                MotionEvent.ACTION_MOVE -> {
                    points.add(x to y)
                }

                MotionEvent.ACTION_UP -> {
                    points.add(x to y)
                }
            }
            invalidate()
            return true
        }
    }
}


