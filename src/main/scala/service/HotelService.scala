package service

import ch.hsr.geohash.GeoHash
import model.{GeoHashHotel, Hotel}
import org.apache.commons.lang3.StringUtils.isNotBlank
import org.apache.spark.sql.Dataset
import service.GeoService.hasCorrectCoordinates

/**
 * Service for dealing with Hotel data.
 *
 */
class HotelService extends Serializable {

  /**
   * Calculates hotel' coordinates based on address.
   */
  private val geoService: GeoService = new GeoService with Serializable

  /**
   * Loads hotel data from Azure storage.
   *
   * @return hotel dataset
   */
  def loadHotels(): Dataset[Hotel] = {
    SparkService.loadHotels()
  }

  /**
   * Iterates through hotel dataset.
   * Performs filtering, coordinates update and maps dataset to GeoHashWeather dataset.
   *
   * @param iterator hotel dataset iterator
   * @return iterator for geoHashHotel dataset
   */
  def processHotels(iterator: Iterator[Hotel]): Iterator[GeoHashHotel] = {
    iterator
      .filter(hasEnoughData)
      .map(checkCoordinates)
      .filter(_ != null)
      .map(getHotelWithGeoHash)
  }

  /**
   * Checks if particular hotel instance has valid coordinates or address for their calculation.
   *
   * @param hotel hotel instance
   * @return true if has, false otherwise
   */
  private def hasEnoughData(hotel: Hotel): Boolean = {
    hasCorrectCoordinates(hotel) ||
      (isNotBlank(hotel.address) && isNotBlank(hotel.city) &&
        hotel.address != "NA" && hotel.city != "NA")
  }

  /**
   * Sets hotel coordinates if they are incomplete.
   *
   * @param hotel instance
   * @return original instance if it has valid coordinates, updated otherwise
   */
  private def checkCoordinates(hotel: Hotel): Hotel = {
    if (!hasCorrectCoordinates(hotel)) geoService.getHotelWithUpdatedCoordinates(hotel) else hotel
  }

  /**
   * Calculates geohash for Hotel instance
   *
   * @param hotel instance with correct coordinates
   * @return geoHashHotel instance with set geohash
   */
  private def getHotelWithGeoHash(hotel: Hotel): GeoHashHotel = {
    val geohash = GeoHash.withCharacterPrecision(hotel.latitude.toDouble, hotel.longitude.toDouble, 4).toString
    GeoHashHotel(hotel.id, hotel.name, hotel.country, hotel.city, hotel.address, hotel.latitude, hotel.longitude, geohash)
  }
}
