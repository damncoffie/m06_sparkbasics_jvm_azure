package service

import com.byteowls.jopencage.JOpenCageGeocoder
import com.byteowls.jopencage.model.{JOpenCageForwardRequest, JOpenCageResponse}
import config.AppConfig.openCageApiKey
import model.{Hotel, Weather}
import org.apache.commons.lang3.StringUtils.isNotBlank
import org.slf4j.LoggerFactory
import org.sparkproject.jetty.http.HttpStatus

import scala.language.postfixOps
import scala.util.Try

/**
 * Service for OpenCage API call.
 */
class GeoService {

  /**
   * OpenCage geocoder.
   */
  private val geocoder = new JOpenCageGeocoder(openCageApiKey)
  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * Makes a call to OpenCage API, copies data to new Hotel instance and put there returned coordinates.
   *
   * @param hotel instance
   * @return instance with coordinates or null in case of OpenCage API call failure
   */
  def getHotelWithUpdatedCoordinates(hotel: Hotel): Hotel = {
    val request = new JOpenCageForwardRequest(getRequestString(hotel))
    val response = geocoder.forward(request)

    if (isSuccessResponse(response)) {
      Hotel(hotel.id, hotel.name, hotel.country, hotel.city, hotel.address,
        response.getFirstPosition.getLat.toString, response.getFirstPosition.getLng.toString)
    } else {
      logger.error(s"OpenCage API call failure. Hotel id: $hotel.id. Error message: $response.getStatus.getMessage")
      null
    }
  }

  private def getRequestString(hotel: Hotel): String = {
    val stringsList = scala.collection.mutable.ListBuffer[String]()

    if (isNotBlank(hotel.name)) stringsList += hotel.name
    if (isNotBlank(hotel.address)) stringsList += hotel.address
    if (isNotBlank(hotel.city)) stringsList += hotel.city
    if (isNotBlank(hotel.country)) stringsList += hotel.country

    stringsList.mkString(", ")
  }

  private def isSuccessResponse(response: JOpenCageResponse): Boolean = {
    response != null && response.getStatus.getCode == HttpStatus.OK_200 && response.getFirstPosition != null &&
      response.getFirstPosition.getLat != null && response.getFirstPosition.getLng != null
  }
}

/**
 * Set of util methods for coordinates.
 */
object GeoService {
  /**
   * Checks if hotel instance has valid coordinates.
   *
   * @param hotel instance
   * @return true if has, false otherwise
   */
  def hasCorrectCoordinates(hotel: Hotel): Boolean = {
    isNotBlank(hotel.latitude) && isNotBlank(hotel.longitude) &&
      isValidLongitude(hotel.longitude) && isValidLatitude(hotel.latitude)
  }

  /**
   * Checks if weather instance has valid coordinates.
   *
   * @param weather instance
   * @return true if has, false otherwise
   */
  def hasCorrectCoordinates(weather: Weather): Boolean = {
    isDoubleDefined(weather.latitude) && isDoubleDefined(weather.longitude) &&
      isValidLongitude(weather.longitude) && isValidLatitude(weather.latitude)
  }

  /**
   * Checks if Double longitude value is valid.
   *
   * @param double longitude value
   * @return true if valid, false otherwise
   */
  private def isValidLongitude(double: Double): Boolean = {
    double >= -180 && double <= 180
  }

  /**
   * Checks if Double latitude value is valid.
   *
   * @param double latitude value
   * @return true if valid, false otherwise
   */
  private def isValidLatitude(double: Double): Boolean = {
    double >= -90 && double <= 90
  }

  /**
   * Checks if String longitude value is valid.
   *
   * @param str longitude value
   * @return true if valid, false otherwise
   */
  private def isValidLongitude(str: String): Boolean = {
    Try(str.toDouble).toOption match {
      case Some(value) if isValidLongitude(value) => true
      case _ => false
    }
  }

  /**
   * Checks if String latitude value is valid.
   *
   * @param str latitude value
   * @return true if valid, false otherwise
   */
  private def isValidLatitude(str: String): Boolean = {
    Try(str.toDouble).toOption match {
      case Some(value) if isValidLatitude(value) => true
      case _ => false
    }
  }

  /**
   * Checks if Double value is correct.
   *
   * @param value double
   * @return true if correct, false otherwise
   */
  private def isDoubleDefined(value: Double): Boolean = {
    val susDouble: Option[Double] = Some(value)

    susDouble match {
      case Some(d) => true
      case None => false
    }
  }
}
