package service

import model.{GeoHashWeather, Weather}
import org.apache.spark.sql.{Encoders, SparkSession}
import org.junit.jupiter.api.Assertions.{assertEquals, assertTrue}
import org.junit.jupiter.api.Test

class WeatherServiceTest extends ParentTest {

  @Test
  def whenDatasetWithIncompleteCoordinates_thenWeatherServiceFiltersAndReturnsDatasetWithGeohash(): Unit = {
    // GIVEN
    val spark = SparkSession.builder().appName("test").master("local[*]").getOrCreate()
    val expectedFilteredWeatherCount = 2

    import spark.implicits._
    val testDataset = Seq(
      Weather(-71.08089, 42.344933, 0, 0, "", "", "", ""),
      Weather(-72.08089, 43.344933, 0, 0, "", "", "", ""),
      Weather(-200, 200, 0, 0, "", "", "", ""),
    ).toDF("longitude", "latitude", "avgTempFahrenheit", "avgTempCelsius", "date", "day", "year", "month").as[Weather]

    val weatherService: WeatherService = new WeatherService

    // WHEN
    val result = testDataset.mapPartitions(weatherService.processWeather)(Encoders.product[GeoHashWeather])

    // THEN
    assertEquals(expectedFilteredWeatherCount, result.count())
    result.foreach(h => assertTrue(h.geohash != null))

    spark.stop()
  }

  @Test
  def whenDatasetWithCompleteCoordinates_thenWeatherServiceReturnsDatasetWithGeohash(): Unit = {
    // GIVEN
    val spark = SparkSession.builder().appName("test").master("local[*]").getOrCreate()

    import spark.implicits._
    val testDataset = Seq(
      Weather(-71.08089, 42.344933, 0, 0, "", "", "", ""),
      Weather(-72.08089, 43.344933, 0, 0, "", "", "", ""),
      Weather(-73.08089, 44.344933, 0, 0, "", "", "", ""),
    ).toDF("longitude", "latitude", "avgTempFahrenheit", "avgTempCelsius", "date", "day", "year", "month").as[Weather]

    val weatherService: WeatherService = new WeatherService

    // WHEN
    val result = testDataset.mapPartitions(weatherService.processWeather)(Encoders.product[GeoHashWeather])

    // THEN
    assertEquals(testDataset.count(), result.count())
    result.foreach(h => assertTrue(h.geohash != null))

    spark.stop()
  }
}
