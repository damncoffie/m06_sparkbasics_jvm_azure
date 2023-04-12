package service

import ch.hsr.geohash.GeoHash
import model.{GeoHashWeather, Weather}
import org.apache.spark.sql.Dataset
import service.GeoService.hasCorrectCoordinates

/**
 * Service for dealing with Weather data
 */
class WeatherService extends Serializable {

  /**
   * Loads weather data from Azure storage.
   *
   * @return weather dataset
   */
  def loadWeather(): Dataset[Weather] = {
    SparkService.loadWeather()
  }

  /**
   * Iterates through weather dataset.
   * Performs filtering and maps dataset to GeoHashWeather dataset.
   *
   * @param iterator weather dataset iterator
   * @return iterator for geoHashWeather dataset
   */
  def processWeather(iterator: Iterator[Weather]): Iterator[GeoHashWeather] = {
    iterator
      .filter(hasCorrectCoordinates)
      .map(getWeatherWithGeoHash)
  }

  /**
   * Calculates geohash for Weather instance.
   *
   * @param weather instance with correct coordinates
   * @return geoHashWeather instance with set geohash
   */
  def getWeatherWithGeoHash(weather: Weather): GeoHashWeather = {
    val geohash = GeoHash.withCharacterPrecision(weather.latitude, weather.longitude, 4).toString
    GeoHashWeather(weather.longitude, weather.latitude, weather.avgTempFahrenheit, weather.avgTempCelsius, weather.date, weather.day, weather.year, weather.month, geohash)
  }
}
