package com.mechanitis.demo.stockservice

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

@SpringBootApplication
class StockServiceApplication

fun main(args: Array<String>) {
    runApplication<StockServiceApplication>(*args)
}

@RestController
class RestController(private val stockService: StockService) {

	
	@GetMapping("/stocks/{symbol}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])//sse (server sent events) response type!
    fun prices(@PathVariable symbol: String) = stockService.streamOfPrices(symbol)


	@Service
	class StockService{
		//server per fare tornare ad ogni simbolo  sempre l'ultimo prezzo e poi i sucessivi
		private val pricesForStock = ConcurrentHashMap<String, Flux<StockPrice>>()
		private val log = LogFactory.getLog(javaClass);

		 fun streamOfPrices(symbol: String): Flux<StockPrice> {
			 return pricesForStock.computeIfAbsent(symbol) {
				 Flux.interval(Duration.ofSeconds(1)).map { StockPrice(symbol, randomStockPrice(), LocalDateTime.now()) }
						 .doOnSubscribe { log.info("New subscription for symbol $symbol.") }
						 .share()
			 }
		 }

		private fun randomStockPrice(): Double {
			return ThreadLocalRandom.current().nextDouble(100.00);
		}
	}

	//	@Controller ??
//	class StockPricesRSocketController(private val stockService: StockService) {
//
//		@MessageMapping("stockPrices")??
//		fun prices(symbol: String) = stockService.streamOfPrices(symbol)
//	}


}
