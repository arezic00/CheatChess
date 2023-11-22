package com.example.cheatchess

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.cheatchess.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class MainActivity : AppCompatActivity(), ChessDelegate {
    private lateinit var binding: ActivityMainBinding
    private val chessModel = ChessModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chessView.chessDelegate = this

        binding.btnClear.setOnClickListener {
            chessModel.emptyBoard()
            binding.chessView.invalidate()
        }

        binding.btnReset.setOnClickListener {
            chessModel.setStartingPosition()
            binding.chessView.invalidate()
        }

        binding.btnTurn.setOnClickListener {
            chessModel.apply {
                changeTurn()
                (it as Button).apply {
                    setBackgroundColor(currentTextColor)
                    val color = if (isWhiteTurn) Color.BLACK else Color.WHITE
                    setTextColor(color)
                }
            }
        }
        binding.btnAnalyze.setOnClickListener {
            analyzePosition()
        }
/* ACCESSING THE API

        */
    }

    override fun pieceAt(row: Int, col: Int) = chessModel.pieceAt(row, col)

    override fun movePiece(movingPiece: ChessPiece, toRow: Int, toCol: Int) {
        chessModel.movePiece(movingPiece, toRow, toCol)
        binding.chessView.invalidate()
    }

    private fun analyzePosition() {
        Log.d("MainActivity", chessModel.positionToFEN())
        lifecycleScope.launch {
            val positionFEN = chessModel.positionToFEN()
            val depth = "13"
            val mode = "eval"
            val options = mapOf(Pair("fen", positionFEN), Pair("depth", depth), Pair("mode", mode))

            val response = try {
                RetrofitInstance.api.getStockfishEvaluation(options)
            } catch (e: IOException) {
                Log.d("MainActivity", "IOException")
                return@launch
            } catch (e: HttpException) {
                Log.d("MainActivity", "HttpException")
                return@launch
            }
            if (response.isSuccessful && response.body() != null)
                Log.d("MainActivity", response.body().toString())
            else Log.d("MainActivity", "response not successful")

        }
    }
}