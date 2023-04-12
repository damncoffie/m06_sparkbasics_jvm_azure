package service

import model.{GeoHashHotel, Hotel}
import org.apache.spark.sql.{Encoders, SparkSession}
import org.junit.jupiter.api.Assertions.{assertEquals, assertTrue}
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, when}

class HotelServiceTest extends ParentTest {

  @Test
  def whenDatasetWithIncompleteData_thenHotelServiceFiltersAndReturnsDatasetWithGeohash(): Unit = {
    // GIVEN
    val spark = SparkSession.builder().appName("test").master("local[*]").getOrCreate()
    val expectedFilteredHotelsCount = 2

    import spark.implicits._
    val testDataset = Seq(
      Hotel("", "", "", "", "", "-71.08089", "42.344933"),
      Hotel("", "", "", "Boston", "99 Saint Botolph St", "", ""),
      Hotel("", "", "", "", "", "", ""),
      Hotel("", "", "", "", "", "-200", "200"),
      Hotel("", "", "", "NA", "NA", "", ""),
    ).toDF("Id", "Name", "Country", "City", "Address", "Latitude", "Longitude").as[Hotel]

    val hotelService: HotelService = new HotelService
    val mockGeoService = mock(classOf[GeoService], Mockito.withSettings().serializable())
    val geoServiceField = hotelService.getClass.getDeclaredField("geoService")
    geoServiceField.setAccessible(true)
    geoServiceField.set(hotelService, mockGeoService)

    // WHEN
    when(mockGeoService.getHotelWithUpdatedCoordinates(any())).thenReturn(Hotel("", "", "", "", "", "20", "-20"))

    val result = testDataset.mapPartitions(hotelService.processHotels)(Encoders.product[GeoHashHotel])

    // THEN
    assertEquals(expectedFilteredHotelsCount, result.count())
    result.foreach(h => assertTrue(h.geohash != null))

    spark.stop()
  }

  @Test
  def whenDatasetWithCompleteData_thenHotelServiceReturnsDatasetWithGeohash(): Unit = {
    // GIVEN
    val spark = SparkSession.builder().appName("test").master("local[*]").getOrCreate()

    import spark.implicits._
    val testDataset = Seq(
      Hotel("", "", "", "", "", "-71.08089", "42.344933"),
      Hotel("", "", "", "", "", "-71.08089", "42.344933"),
      Hotel("", "", "", "", "", "-71.08089", "42.344933"),
      Hotel("", "", "", "", "", "-71.08089", "42.344933"),
      Hotel("", "", "", "", "", "-71.08089", "42.344933"),
    ).toDF("Id", "Name", "Country", "City", "Address", "Latitude", "Longitude").as[Hotel]

    val hotelService: HotelService = new HotelService
    val mockGeoService = mock(classOf[GeoService], Mockito.withSettings().serializable())
    val geoServiceField = hotelService.getClass.getDeclaredField("geoService")
    geoServiceField.setAccessible(true)
    geoServiceField.set(hotelService, mockGeoService)

    // WHEN
    val result = testDataset.mapPartitions(hotelService.processHotels)(Encoders.product[GeoHashHotel])

    // THEN
    assertEquals(testDataset.count(), result.count())
    result.foreach(h => assertTrue(h.geohash != null))

    spark.stop()
  }
}
