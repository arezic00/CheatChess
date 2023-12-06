package com.example.cheatchess

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.cheatchess.Constants.CHAR_CODE_OFFSET
import com.example.cheatchess.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import kotlin.math.abs

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
                    val color: Int
                    var turnString = "Turn: "
                    if (isWhiteTurn) {
                        turnString += "W"
                        color = Color.WHITE
                    }
                    else {
                        turnString += "B"
                        color = Color.BLACK
                    }
                    setTextColor(color)
                    text = turnString
                }
            }
        }
        binding.btnAnalyze.setOnClickListener {
            analyzePosition()

        }

        setBestMoveBtnOnClickListener()

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navItemBest -> setupBestMoveMode()
                R.id.navItemEval -> setupEvalMode()
            }
            true
        }
    }

    override fun pieceAt(row: Int, col: Int) = chessModel.pieceAt(row, col)

    override fun movePiece(movingPiece: ChessPiece, toRow: Int, toCol: Int) {
        chessModel.movePiece(movingPiece, toRow, toCol)
        binding.chessView.invalidate()
    }

    private fun analyzePosition() {
        lifecycleScope.launch {
            val response = getStockfishResponse("eval")
            if (response.isSuccessful)
                response.body()?.let {
                    binding.tvEval.text = it.data
                    updateEvalBar(extractEval(it.data))
                }
        }
    }

    private fun extractEval(responseData: String): Float {
        return responseData.substring(18, responseData.length - 13).toFloat()
    }

    private fun extractBestMove(responseData: String): String {
        return responseData.substring(9, responseData.length - 12)
    }

    private fun existsLegalMove(responseData: String): Boolean {
        return !(responseData.contains("none"))
    }

    private fun bestMoveToBoardIndexes(bestMove: String): Pair<Pair<Int,Int>,Pair<Int,Int>> {
        val colStart = bestMove[0].code - CHAR_CODE_OFFSET
        val rowStart = abs(bestMove[1].digitToInt() - 8)
        val colEnd = bestMove[2].code - CHAR_CODE_OFFSET
        val rowEnd = abs(bestMove[3].digitToInt() - 8)

        return Pair(Pair(rowStart,colStart), Pair(rowEnd,colEnd))
    }

    private fun updateEvalBar(progress: Float) {
        val progressInt = ((progress + 10) * 5 ).toInt()
        binding.pbEvalBar.progress = progressInt
    }

    private fun setupBestMoveMode() {
        binding.pbEvalBar.visibility = View.GONE
        binding.tvEval.visibility = View.GONE
        binding.btnAnalyze.visibility = View.GONE
        binding.tvBestmove.visibility = View.VISIBLE
        binding.btnBestmove.visibility = View.VISIBLE
    }

    private fun setupEvalMode() {
        binding.tvBestmove.visibility = View.GONE
        binding.btnBestmove.visibility = View.GONE
        binding.pbEvalBar.visibility = View.VISIBLE
        binding.tvEval.visibility = View.VISIBLE
        binding.btnAnalyze.visibility = View.VISIBLE
    }

    private fun setBestMoveBtnOnClickListener() {
        binding.btnBestmove.setOnClickListener {
            lifecycleScope.launch {
                val response = getStockfishResponse("bestmove")
                if (response.isSuccessful)
                    response.body()?.let {
                        var move = "none"
                        if (existsLegalMove(it.data))
                            move = extractBestMove(it.data)
                        binding.tvBestmove.text = getString(R.string.bestmove,move)
                    }
            }
        }
    }

    private suspend fun getStockfishResponse(mode: String): Response<StockfishEvaluation> {
        val positionFEN = chessModel.positionToFEN()
        val depth = "13"
        val options = mapOf(Pair("fen", positionFEN), Pair("depth", depth), Pair("mode", mode))

        return try {
            RetrofitInstance.api.getStockfishEvaluation(options)
        } catch (e: IOException) {
            Log.d("MainActivity", "IOException")
            return Response.success(404, StockfishEvaluation("errorString",false))
        } catch (e: HttpException) {
            Log.d("MainActivity", "HttpException")
            return Response.success(404, StockfishEvaluation("errorString",false))
        }
    }
}