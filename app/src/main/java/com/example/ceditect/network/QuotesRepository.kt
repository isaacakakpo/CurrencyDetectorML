package com.example.ceditect.network

import com.example.ceditect.response.ExchangerateResponse
import retrofit2.Response

class QuotesRepository (private val api: MyApi){

    suspend fun fetchQuotes(): Response<ExchangerateResponse> {
            return api.getExchangeRates()

    }

}