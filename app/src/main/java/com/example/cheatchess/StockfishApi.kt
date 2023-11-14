package com.example.cheatchess

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface StockfishApi {
    @GET("/api/stockfish.php")
    suspend fun getStockfishEvaluation(@QueryMap options: Map<String,String>) : Response<StockfishEvaluation>
}