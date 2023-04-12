package service

import com.byteowls.jopencage.JOpenCageGeocoder
import com.byteowls.jopencage.model.{JOpenCageLatLng, JOpenCageResponse, JOpenCageResult, JOpenCageStatus}
import model.{Hotel, Weather}
import org.junit.jupiter.api.Assertions.{assertFalse, assertTrue}
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import service.GeoService.hasCorrectCoordinates


class GeoServiceTest extends ParentTest {

  private val geoService: GeoService = new GeoService

  @Test
  def whenHotelWithoutCoordinates_thenMethodReturnsHotelWithCoordinates(): Unit = {
    // GIVEN
    val mockGeocoder = mock(classOf[JOpenCageGeocoder])
    val clientField = geoService.getClass.getDeclaredField("geocoder")
    clientField.setAccessible(true)
    clientField.set(geoService, mockGeocoder)

    val hotelWithEmptyCoordinates = Hotel("id", "Lakeview Hotel", "US", "Boston", "99 Saint Botolph St", null, null)


    // WHEN
    when(mockGeocoder.forward(any())).thenReturn(getSuccessMockResponse)
    val hotelWithCoordinates = geoService.getHotelWithUpdatedCoordinates(hotelWithEmptyCoordinates)

    // THEN
    assertTrue(hotelWithCoordinates.longitude != null)
    assertTrue(hotelWithCoordinates.latitude == 71.08088684082031.toString &&
      hotelWithCoordinates.longitude == 42.344932556152344.toString)
  }

  @Test
  def whenOpenCageApiCallFails_thenMethodReturnsNull(): Unit = {
    // GIVEN
    val mockGeocoder = mock(classOf[JOpenCageGeocoder])
    val clientField = geoService.getClass.getDeclaredField("geocoder")
    clientField.setAccessible(true)
    clientField.set(geoService, mockGeocoder)

    val hotelWithEmptyCoordinates = Hotel("id", "Lakeview Hotel", "US", "Boston", "99 Saint Botolph St", null, null)

    // WHEN
    when(mockGeocoder.forward(any())).thenReturn(getFailMockResponse)

    val nullResponse = geoService.getHotelWithUpdatedCoordinates(hotelWithEmptyCoordinates)

    // THEN
    assertTrue(nullResponse == null)
  }

  @Test
  def whenHotelCoordinatesAreNotCorrect_thenMethodReturnsFalse(): Unit = {
    // GIVEN
    val hotelWithIncorrectCoordinates = Hotel("id", "Lakeview Hotel", "US", "Boston", "99 Saint Botolph St", "-200", "200")

    // WHEN
    val result = hasCorrectCoordinates(hotelWithIncorrectCoordinates)

    // THEN
    assertFalse(result)
  }

  @Test
  def whenHotelCoordinatesAreCorrect_thenMethodReturnsTrue(): Unit = {
    // GIVEN
    val hotelWithCorrectCoordinates = Hotel("id", "Lakeview Hotel", "US", "Boston", "99 Saint Botolph St", "-20", "20")

    // WHEN
    val result = hasCorrectCoordinates(hotelWithCorrectCoordinates)

    // THEN
    assertTrue(result)
  }

  @Test
  def whenWeatherCoordinatesAreNotCorrect_thenMethodReturnsFalse(): Unit = {
    // GIVEN
    val weatherWithIncorrectCoordinates = Weather(-200, 200, 0, 10, "", "", "", "")

    // WHEN
    val result = hasCorrectCoordinates(weatherWithIncorrectCoordinates)

    // THEN
    assertFalse(result)
  }

  @Test
  def whenWeatherCoordinatesAreCorrect_thenMethodReturnsTrue(): Unit = {
    // GIVEN
    val weatherWithCorrectCoordinates = Weather(-20, 20, 0, 10, "", "", "", "")

    // WHEN
    val result = hasCorrectCoordinates(weatherWithCorrectCoordinates)

    // THEN
    assertTrue(result)
  }

  def getSuccessMockResponse: JOpenCageResponse = {
    val response: JOpenCageResponse = new JOpenCageResponse()

    val latLng: JOpenCageLatLng = new JOpenCageLatLng
    latLng.setLat(71.08088684082031)
    latLng.setLng(42.344932556152344)

    val result: JOpenCageResult = new JOpenCageResult
    val geoField = result.getClass.getDeclaredField("geometry")
    geoField.setAccessible(true)
    geoField.set(result, latLng)

    val list: java.util.List[JOpenCageResult] = new java.util.ArrayList[JOpenCageResult]
    list.add(result)

    val resultsField = response.getClass.getDeclaredField("results")
    resultsField.setAccessible(true)
    resultsField.set(response, list)

    val status: JOpenCageStatus = new JOpenCageStatus
    status.setCode(200)

    val statusField = response.getClass.getDeclaredField("status")
    statusField.setAccessible(true)
    statusField.set(response, status)

    response
  }

  def getFailMockResponse: JOpenCageResponse = {
    val response: JOpenCageResponse = new JOpenCageResponse()

    val status: JOpenCageStatus = new JOpenCageStatus
    status.setCode(400)

    val statusField = response.getClass.getDeclaredField("status")
    statusField.setAccessible(true)
    statusField.set(response, status)

    response
  }
}
