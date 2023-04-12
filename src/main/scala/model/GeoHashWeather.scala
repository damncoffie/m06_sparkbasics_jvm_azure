package model

/**
 * A class representing weather.
 * Has additional field for geohash.
 *
 * @param longitude longitude
 * @param latitude latitude
 * @param avgTempFahrenheit average fahrenheit temperature
 * @param avgTempCelsius average celsius temperature
 * @param date weather date
 * @param day weather day
 * @param year weather year
 * @param month weather month
 * @param geohash geohash based on longitude and latitude
 */
case class GeoHashWeather(longitude: Double,
                          latitude: Double,
                          avgTempFahrenheit: Double,
                          avgTempCelsius: Double,
                          date: String,
                          day: String,
                          year: String,
                          month: String,
                          geohash: String)
