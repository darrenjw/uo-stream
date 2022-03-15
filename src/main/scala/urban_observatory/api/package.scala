package urban_observatory

import org.json4s.native.JsonMethods.parse

import scala.io.Source
import scala.util.Try

import org.json4s.DefaultFormats
import org.json4s._

package object api {

  implicit val jsonDefaultFormats: DefaultFormats.type = DefaultFormats

  case class Sensor(`Sensor Name`: String, `Raw ID`: String, `Sensor Centroid Latitude`: Double, `Sensor Centroid Longitude`: Double)
  case class Sensors(sensors: List[Sensor])

  def loadSensorMap(): Map[String, Sensor] = {
    val sensorMap = parse(Source.fromResource("sensors.json").mkString)
      .extract[Sensors]
      .sensors.map(sensor => (sensor.`Raw ID`, sensor)).toMap
    sensorMap
  }

  def lookupGeolocation(rawId: String): (Double, Double) = {
    val sensorOption = Try(sensorMap(rawId)).toOption
    sensorOption.map(sensor => (sensor.`Sensor Centroid Latitude`, sensor.`Sensor Centroid Longitude`)).getOrElse((0.0, 0.0))
  }


  val sensorMap = loadSensorMap()
}
