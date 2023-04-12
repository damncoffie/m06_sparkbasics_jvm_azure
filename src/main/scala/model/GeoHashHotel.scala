package model

/**
 * A class representing a Hotel.
 * Has additional field for geohash.
 *
 * @param id id
 * @param name name
 * @param country country
 * @param city city
 * @param address address
 * @param latitude latitude
 * @param longitude longitude
 * @param geohash geohash based on longitude and latitude
 */
case class GeoHashHotel(id: String,
                        name: String,
                        country: String,
                        city: String,
                        address: String,
                        latitude: String,
                        longitude: String,
                        geohash: String)
