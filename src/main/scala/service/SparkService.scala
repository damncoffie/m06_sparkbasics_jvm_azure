package service

import config.AppConfig._
import model.{Hotel, Weather}
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}

/**
 * Service for loading datasets from Azure storage with Spark.
 */
object SparkService {

  /**
   * Spark session.
   */
  private val session: SparkSession = SparkSession
    .builder()
    .appName("homework1")
    .master("local[*]")
    .getOrCreate()

  /**
   * Loads hotels dataset and changes column names to custom.
   *
   * @return hotels dataset
   */
  def loadHotels(): Dataset[Hotel] = {
    session.read
      .option("header", "true")
      .csv(abfssPath + "hotels")
      .withColumnRenamed("Id", "id")
      .withColumnRenamed("Name", "name")
      .withColumnRenamed("Country", "country")
      .withColumnRenamed("City", "city")
      .withColumnRenamed("Address", "address")
      .withColumnRenamed("Latitude", "latitude")
      .withColumnRenamed("Longitude", "longitude")
      .as(Encoders.product[Hotel])
  }

  /**
   * Loads weather dataset and changes column names to custom.
   *
   * @return weather dataset
   */
  def loadWeather(): Dataset[Weather] = {
    session.read
      .option("header", "true")
      .parquet(abfssPath + "weather")
      .withColumnRenamed("lng", "longitude")
      .withColumnRenamed("lat", "latitude")
      .withColumnRenamed("avg_tmpr_f", "avgTempFahrenheit")
      .withColumnRenamed("avg_tmpr_c", "avgTempCelsius")
      .withColumnRenamed("wthr_date", "date")
      .withColumnRenamed("wthr_day", "day")
      .withColumnRenamed("wthr_year", "year")
      .withColumnRenamed("wthr_month", "month")
      .as(Encoders.product[Weather])
  }

  def getSession: SparkSession = {
    session
  }
  /**
   * Closes spark session.
   */
  def closeSession(): Unit = {
    session.close()
  }
}
