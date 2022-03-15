package urban_observatory.websocket

import statistics.kriging.GaussianProcess
import statistics.{StatisticalFunctions, StatisticalLocation}

object Metrics {

  case class Metric(code: String, description: String, gaussianProcess: GaussianProcess[StatisticalLocation])

  val fullMetricsMap = Map(
    "co" -> Metric(
      code = "co",
      description = "CO",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
    "no2" -> Metric(
      code = "no2",
      description = "NO2",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.05051, lengthScale = 0.005, timeLengthScale = 3.841 * 60 * 60, varianceOfOverallMean = 1.5 * 1.5))),
    "no" -> Metric(
      code = "no",
      description = "NO",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 30 * 30, lengthScale = 0.01, timeLengthScale = 10 * 60, varianceOfOverallMean = 50 * 50))),
    "03" -> Metric(
      code = "03",
      description = "03",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
    "pc" -> Metric(
      code = "pc",
      description = "Particle Count",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
    "pm1" -> Metric(
      code = "pm1",
      description = "PM1",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
    "pm10" -> Metric(
      code = "pm10",
      description = "PM10",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
    "pm2point5" -> Metric(
      code = "pm2point5",
      description = "PM25",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
    "pm4" -> Metric(
      code = "pm4",
      description = "PM 4",
      gaussianProcess = GaussianProcess(StatisticalFunctions.kseMean(stationaryVariance = 0.1, lengthScale = 0.01, timeLengthScale = 1.0 * 60 * 60, varianceOfOverallMean = 5 * 5))),
  )

  val metricsMap = fullMetricsMap.map({
    case (code, metric) => (code, metric.description)
  })

  def lookupGaussianProcess(code: String): GaussianProcess[StatisticalLocation] = fullMetricsMap(code).gaussianProcess
}
