package com.mechanitis.demo.stockservice

import java.time.LocalDateTime

data class StockPrice (val symbol: String,
                       val price: Double,
                       val time: LocalDateTime);
