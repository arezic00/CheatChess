package com.example.cheatchess

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.Integer.min

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val darkColor = Color.parseColor("#703B24")
    private val lightColor = Color.parseColor("#f8e7bb")
    private var squareSide = 130f
    private var marginLeft = 20f
    private var marginTop = 200f
    private val scaleFactor = .95f
    private var fromRow: Int = -1
    private var fromCol: Int = -1
    private var movingPieceX = -1f
    private var movingPieceY = -1f
    private val bitmaps = mapOf<Int,Bitmap>(
        Pair(R.drawable.black_rook,BitmapFactory.decodeResource(resources, R.drawable.black_rook)),
        Pair(R.drawable.black_knight,BitmapFactory.decodeResource(resources, R.drawable.black_knight)),
        Pair(R.drawable.black_bishop,BitmapFactory.decodeResource(resources, R.drawable.black_bishop)),
        Pair(R.drawable.black_queen,BitmapFactory.decodeResource(resources, R.drawable.black_queen)),
        Pair(R.drawable.black_king,BitmapFactory.decodeResource(resources, R.drawable.black_king)),
        Pair(R.drawable.black_pawn,BitmapFactory.decodeResource(resources, R.drawable.black_pawn)),
        Pair(R.drawable.white_rook,BitmapFactory.decodeResource(resources, R.drawable.white_rook)),
        Pair(R.drawable.white_knight,BitmapFactory.decodeResource(resources, R.drawable.white_knight)),
        Pair(R.drawable.white_bishop,BitmapFactory.decodeResource(resources, R.drawable.white_bishop)),
        Pair(R.drawable.white_queen,BitmapFactory.decodeResource(resources, R.drawable.white_queen)),
        Pair(R.drawable.white_king,BitmapFactory.decodeResource(resources, R.drawable.white_king)),
        Pair(R.drawable.white_pawn,BitmapFactory.decodeResource(resources, R.drawable.white_pawn)),
    )

    var chessDelegate: ChessDelegate? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        scaleToScreenSize(canvas)
        drawBoard(canvas)
        drawPieces(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return  false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                fromCol = ((event.x - marginLeft) / squareSide).toInt()
                fromRow = ((event.y - marginTop) / squareSide).toInt()
                Log.d("MainActivity", "Down :($fromRow,$fromCol)")
                chessDelegate?.pieceAt(fromRow,fromCol) ?: return false
            }
            MotionEvent.ACTION_MOVE -> {
                movingPieceX = event.x
                movingPieceY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val col = ((event.x - marginLeft) / squareSide).toInt()
                val row = ((event.y - marginTop) / squareSide).toInt()
                Log.d("MainActivity", "Up :($row,$col)")
                chessDelegate?.movePiece(fromRow,fromCol,row,col)
            }
        }
        return true
    }

    private fun drawBoard(canvas: Canvas) {
        for (row in 0..7)
            for (col in 0..7) {
                drawSquareAt(canvas, row, col)
            }
    }

    private fun drawPieceAt(canvas: Canvas, row: Int, col: Int, resId: Int) {
        canvas.drawBitmap(bitmaps[resId]!!,null, RectF(marginLeft + col * squareSide,marginTop + row * squareSide,marginLeft + (col + 1) * squareSide,marginTop + (row + 1) * squareSide),paint)
    }

    private fun drawMovingPiece(canvas: Canvas, y: Float, x: Float, resId: Int) {
        canvas.drawBitmap(bitmaps[resId]!!,null, RectF(x - squareSide/2,y - squareSide/2,x + squareSide/2,y + squareSide/2),paint)
    }

    private fun drawPieces(canvas: Canvas) {
        for (row in 0..7)
            for (col in 0..7)
                if (fromRow != row || fromCol != col)
                    chessDelegate?.pieceAt(row,col)?.let { drawPieceAt(canvas, row, col, it.resID) }

        chessDelegate?.pieceAt(fromRow,fromCol)?.let {
            drawMovingPiece(canvas, movingPieceY, movingPieceX, it.resID)
        }
    }

    private fun drawSquareAt(canvas: Canvas, row: Int, col: Int) {
        paint.color = if ((row + col) % 2 == 0) lightColor else darkColor
        canvas.drawRect(
            marginLeft + squareSide*col,
            marginTop + squareSide*row,
            marginLeft + squareSide*(col+1),
            marginTop + squareSide*(row+1),
            paint)
    }

    private fun scaleToScreenSize(canvas: Canvas) {
            val boardSide = min(canvas.width, canvas.height) * scaleFactor
            squareSide = boardSide / 8f
            marginLeft = (canvas.width - boardSide) / 2f
            marginTop = (canvas.height - boardSide) / 2f
    }
}