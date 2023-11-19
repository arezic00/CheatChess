package com.example.cheatchess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val rectSide = 130f
    private val marginLeft = 20f
    private val marginTop = 200f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (row in 0..7)
            for (col in 0..7) {
                paint.color = if ((row + col) % 2 == 0) Color.LTGRAY else Color.GRAY
                canvas?.drawRect(
                    marginLeft + rectSide*col,
                    marginTop + rectSide*row,
                    marginLeft + rectSide*(col+1),
                    marginTop + rectSide*(row+1),
                    paint)
            }

    }
}
