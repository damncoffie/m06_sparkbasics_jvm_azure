import config.AppConfig.outputPath
import model.{GeoHashHotel, GeoHashWeather, Hotel}
import org.apache.spark.sql.{Dataset, Encoders, SaveMode}
import org.slf4j.LoggerFactory
import service.{HotelService, SparkService, WeatherService}

/**
 * Application entry point.
 */
object App {

  private val hotelService: HotelService = new HotelService
  private val weatherService: WeatherService = new WeatherService
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val loadedHotels: Dataset[Hotel] = hotelService.loadHotels()
    val geoHashHotels: Dataset[GeoHashHotel] = loadedHotels
      .mapPartitions(hotelService.processHotels)(Encoders.product[GeoHashHotel])

    val geoHashWeather: Dataset[GeoHashWeather] = weatherService.loadWeather()
      .mapPartitions(weatherService.processWeather)(Encoders.product[GeoHashWeather])

    geoHashHotels.join(geoHashWeather.select("geohash", "avgTempFahrenheit", "avgTempCelsius", "date", "day", "year", "month"),
      Seq("geohash"), "left")
      .distinct
      .write.mode(SaveMode.Overwrite)
      .parquet(outputPath)

    SparkService.closeSession()
    logger.info("Spark job completed")
  }
}
