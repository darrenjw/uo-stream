package statistics

/**
 * space-time covariance functions
 */
object StatisticalFunctions {

  private val UNKNOWN_CONSTANT_TODO = -0.5
  private val RELATIVE_DISTANCE_ONE_UNIT_LATITUDE = 1.75

  /**
   * basic separable squared exponential kernel (hence "kse")
   *
   * @param stationaryVariance
   * @param lengthScale
   * @param timeLengthScale
   * @param x1
   * @param x2
   * @return
   */
  private def kernelSquaredExponential(stationaryVariance: Double, lengthScale: Double, timeLengthScale: Double)(x1: StatisticalLocation, x2: StatisticalLocation): Double =
    stationaryVariance * math.exp(UNKNOWN_CONSTANT_TODO * (
      (x1.lat - x2.lat) * (x1.lat - x2.lat)/ (lengthScale * lengthScale) +
        (x1.lng - x2.lng) * (x1.lng - x2.lng) / (RELATIVE_DISTANCE_ONE_UNIT_LATITUDE * lengthScale * RELATIVE_DISTANCE_ONE_UNIT_LATITUDE * lengthScale) +
        ((x1.time - x2.time) * (x1.time - x2.time)).toDouble / (timeLengthScale * timeLengthScale)
    ))

  /**
   * add a mean contribution into the basic kernel (hence "ksem")
   *
   * @param stationaryVariance stationary variance
   * @param lengthScale length scale (in units of latitude)
   * @param timeLengthScale time scale (in units of seconds)
   * @param varianceOfOverallMean variance of the (unknown) overall mean level
   * @param x1
   * @param x2
   * @return
   */
  def kseMean(stationaryVariance: Double, lengthScale: Double, timeLengthScale: Double, varianceOfOverallMean: Double)(
    x1: StatisticalLocation, x2: StatisticalLocation): Double = varianceOfOverallMean + kernelSquaredExponential(stationaryVariance, lengthScale, timeLengthScale)(x1, x2)
}
