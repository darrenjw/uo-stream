import urban_observatory.websocket.Metrics.metricsMap
import com.typesafe.scalalogging.LazyLogging
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.json4s.native.Serialization.write
import org.json4s.DefaultFormats
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import org.json4s.native.Serialization
import statistics.{Colour, StatisticalLocation, StatisticalLocations}
import statistics.kriging.GaussianProcess
import urban_observatory.websocket.createDataPointStream


object UrbanObservatoryServer extends App with LazyLogging {

  implicit val jsonDefaultFormats: DefaultFormats.type = DefaultFormats

  val app: Javalin = Javalin.create(cfg => cfg.addStaticFiles("public", Location.EXTERNAL))
  val PORT = 7000

  implicit val serialization: Serialization.type = org.json4s.native.Serialization


  def createMetricsListRoute = {
    app.get("/metrics", ctx => {
      ctx.result(write(metricsMap))
    })
  }

  def createRoutes(): Unit = {
    for ( metric <- metricsMap) {
      createRoute(app, metric)
    }
  }

  def createRoute(app: Javalin, metric: (String, String)): Javalin = {
    val (code, description) = metric
    val wsPath = f"/uo/$code".toLowerCase

    app.ws(wsPath, ws => {
      var cancelableFuture: CancelableFuture[Unit] = null
      ws.onConnect(ctx => {
        logger.info(s"Websocket client connected - metric: $code")

        val locations = createMatrix()
        val dataPointStream = createDataPointStream(code, description)

        cancelableFuture = dataPointStream.foreach(d => {
          val newLocations = updateMatrix(d, locations)
          val statisticalLocations = StatisticalLocations(locations = newLocations.toList)
          logger.info(s"Sending to websocket client - metric: $code")
          ctx.send(write(statisticalLocations))
        })
      })

      ws.onClose(_ => {
        cancelableFuture.cancel()
        logger.info(s"Websocket closed - metric: $code")
      })

    })
  }

  private def createMatrix(): Seq[StatisticalLocation] = {
    val gridSize = 250

    val rows = gridSize
    val cols = gridSize * 2
    val ct = java.time.Instant.now.getEpochSecond.toInt
    val locations = for {
      i <- 0 until rows
      j <- 0 until cols
    } yield StatisticalLocation(time = ct, lat = 54.92 + 0.0006 * i, lng = -1.76 + 0.0006 * j)

    locations
  }

  private def updateMatrix(d0: GaussianProcess[StatisticalLocation], statisticalLocations: Seq[StatisticalLocation]): Seq[StatisticalLocation] = {
    val ct = java.time.Instant.now.getEpochSecond.toInt

    val minCol = Colour(0, 255, 0, 1.0) // green
    val maxCol = Colour(255, 0, 0, 1.0)  // red

    val newLocations = statisticalLocations.map(statisticalLocation => {
      val slPlusTime = statisticalLocation.copy(time = ct)
      val (mean, variance) = d0.meanAndVar(slPlusTime)
      // signal to noise ratio not so useful for log-transformed data...
      // val signalNoiseRatio = mean / sqrt(variance)
      val alpha = -math.log10(variance) // to be rescaled later...
      val newSl = slPlusTime.copy(value = mean, colour = slPlusTime.colour.copy(alpha = alpha))
      newSl
    })

    val min = newLocations.minBy(_.value)
    val max = newLocations.maxBy(_.value)
    val minA = newLocations.minBy(_.colour.alpha)
    val maxA = newLocations.maxBy(_.colour.alpha)


    val colouredLocations = newLocations.map(st => {
      val point = (st.value - min.value) / (max.value - min.value)
      val alpha = (st.colour.alpha - minA.colour.alpha) / (maxA.colour.alpha - minA.colour.alpha)
      val colour = Colour(
        math.round(point * maxCol.red + (1 - point) * minCol.red).toInt,
        math.round(point * maxCol.green + (1 - point) * minCol.green).toInt,
        math.round(point * maxCol.blue + (1 - point) * minCol.blue).toInt,
        alpha
      )
      st.copy(colour = colour)
    })

    colouredLocations
  }

  createRoutes()
  createMetricsListRoute

  app.start(PORT)
}
