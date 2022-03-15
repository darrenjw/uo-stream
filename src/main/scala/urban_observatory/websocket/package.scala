package urban_observatory

import com.github.andyglow.websocket.WebsocketClient
import com.typesafe.scalalogging.LazyLogging
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import monix.execution.Scheduler.Implicits.global
import org.json4s.{DefaultFormats, MappingException}
import org.json4s.native.JsonMethods.parse
import statistics.StatisticalLocation
import statistics.kriging.GaussianProcess
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}
import urban_observatory.api.lookupGeolocation
import urban_observatory.websocket.Metrics.lookupGaussianProcess

import scala.concurrent.duration._

package object websocket extends LazyLogging {

  case class Feed(metric: String)

  case class Brokerage(id: String, broker: Broker)

  case class Broker(id: String)

  case class Entity(name: String)

  case class TimeSeries(unit: String = "", value: TimeSeriesValue = null)

  case class TimeSeriesValue(time: String, timeAccuracy: String, data: Double, `type`: String)

  case class Data(serverTime: Option[Int], feed: Option[Feed], brokerage: Option[Brokerage], entity: Option[Entity], timeseries: Option[TimeSeries])

  case class SensorReading(signal: String, recipients: Option[Int], data: Data)

  implicit val jsonDefaultFormats: DefaultFormats.type = DefaultFormats

  implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

  val webSocketUrl = "wss://api.newcastle.urbanobservatory.ac.uk/stream"

  def createUrbanObservatoryWebSocket: Observable[SensorReading] = {

    Observable.create[SensorReading](OverflowStrategy.Unbounded)({ downstream =>
      val webSocketClient = WebsocketClient[String](webSocketUrl) {
        case message =>
          try {
            val reading = parse(message).extract[SensorReading]
            // todo log at trace
            //println(reading)
            downstream.onNext(reading)
          } catch {
            case _: MappingException => println(s"Could not parse: $message")
          }
      }

      val webSocket = webSocketClient.open()


      Cancelable(() => webSocket.close)
    })

  }

  def createDataPointStream(code: String, description: String): Observable[GaussianProcess[StatisticalLocation]] = {
    val d0: GaussianProcess[StatisticalLocation] = lookupGaussianProcess(code)

    val webSocket = createUrbanObservatoryWebSocket
    val filteredStream = webSocket.filter(_.data.feed.getOrElse(Feed(metric = "unknown")).metric == description)

    val dataPointStream = filteredStream
      .map(sensorReading => {
        val (id, geolocation) = sensorReading.data.brokerage.map(brokerage => {
          val id = brokerage.id
          val rawId = id.split(":").head
          val geolocation: (Double, Double) = lookupGeolocation(rawId)
          (id, geolocation)
        }).getOrElse(("", (0.0, 0.0)))

        val (value, time) = sensorReading.data.timeseries.map(timeseries => {
          (timeseries.value.data, java.time.LocalDateTime.parse(timeseries.value.time).toEpochSecond(java.time.ZoneOffset.UTC).toInt)
        }).getOrElse((0.0, -1))

        val sensorName = sensorReading.data.entity.getOrElse(Entity(name = "unknown")).name


        val statisticalLocation = StatisticalLocation(id, sensorName, value, time, geolocation._1, geolocation._2)
        logger.debug(s"Sensor reading from Urban Observatory: $statisticalLocation")
        statisticalLocation
      })
      .throttleLast(10.second)
      .scan(d0)((d, o) => {
        d.updateMx(math.log10(o.value + 1.0), o, 300)
      })

    dataPointStream
  }

}
