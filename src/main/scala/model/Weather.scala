package model

/**
 * A class representing weather
 *
 * @param longitude longitude
 * @param latitude latitude
 * @param avgTempFahrenheit average fahrenheit temperature
 * @param avgTempCelsius average celsius temperature
 * @param date weather date
 * @param day weather day
 * @param year weather year
 * @param month weather month
 */
case class Weather(longitude: Double,
                   latitude: Double,
                   avgTempFahrenheit: Double,
                   avgTempCelsius: Double,
                   date: String,
                   day: String,
                   year: String,
                   month: String)
