package com.example.cheatchess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        lifecycleScope.launch {
            val positionFEN = "r2q1rk1/ppp2ppp/3bbn2/3p4/8/1B1P4/PPP2PPP/RNB1QRK1 w - - 5 11"
            val depth = "10"
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

    override fun pieceAt(row: Int, col: Int) = chessModel.pieceAt(row, col)

    override fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        chessModel.movePiece(fromRow, fromCol, toRow, toCol)
        binding.chessView.invalidate()
    }
}