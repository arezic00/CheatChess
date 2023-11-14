package com.example.cheatchess

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: StockfishApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://stockfish.online")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StockfishApi::class.java)
    }
}