package statistics

/**
 * AirQualityCategories based off the Defra categorisations of pollution:
 *
 * @see <a href="https://uk-air.defra.gov.uk/air-pollution/daqi?view=more-info>Defra air pollution categories</a>
 *
 * The class transforms values to colours for pollutants.
 */
object AirQualityCategories {

  private val low1Colour = Colour(156, 255, 156)
  private val low2Colour = Colour(49, 255, 0)
  private val low3Colour = Colour(49, 207, 0)
  private val moderate1Colour = Colour(255, 255, 0)
  private val moderate2Colour = Colour(255, 207, 0)
  private val moderate3Colour = Colour(255, 154, 0)
  private val high1Colour = Colour(255, 100, 100)
  private val high2Colour = Colour(255, 0, 0)
  private val high3Colour = Colour(153, 0, 0)
  private val veryHighColour = Colour(206, 48, 255)

  def metricValueColour(metric: String, value: Double): Colour = metric match {
    case "co" => fakeCoColour(value)
    case "no" => fakeColour(value)
    case "no2" => no2Colour(value)
    case "o3" => o3Colour(value)
    case "pm2point5" => pm25Colour(value)
    case "pm10" => pm10Colour(value)
    case _ => Colour()
  }

  private def o3Colour(value: Double): Colour = value match {
    case l1 if l1 < 33 => low1Colour
    case l2 if 34 until 66 contains l2 => low2Colour
    case l3 if 67 until 100 contains l3 => low3Colour
    case m1 if 101 until 120 contains m1 => moderate1Colour
    case m2 if 121 until 140 contains m2 => moderate2Colour
    case m3 if 141 until 160 contains m3 => moderate3Colour
    case h1 if 161 until 187 contains h1 => high1Colour
    case h2 if 188 until 213 contains h2 => high2Colour
    case h3 if 214 until 240 contains h3 => high3Colour
    case vh if vh > 241 => veryHighColour
  }

  private def no2Colour(value: Double): Colour = value match {
    case l1 if l1 < 68 => low1Colour
    case l2 if 68 until 134 contains l2 => low2Colour
    case l3 if 135 until 200 contains l3 => low3Colour
    case m1 if 201 until 267 contains m1 => moderate1Colour
    case m2 if 268 until 334 contains m2 => moderate2Colour
    case m3 if 335 until 400 contains m3 => moderate3Colour
    case h1 if 401 until 467 contains h1 => high1Colour
    case h2 if 468 until 534 contains h2 => high2Colour
    case h3 if 535 until 600 contains h3 => high3Colour
    case vh if vh > 600 => veryHighColour
  }

  private def fakeCoColour(value: Double): Colour = value match {
    case l1 if l1 < 0.1 => low1Colour
    case l2 if l2 > 0.1 && l2 < 0.15 => low3Colour
    case l3 if l3 > 0.2 && l3 < 0.25 => moderate2Colour
    case m1 if m1 > 0.25 && m1 < 0.3 => moderate3Colour
    case m2 if m2 > 0.3 && m2 < 0.35 => high2Colour
    case m3 if m3 > 0.35 && m3 < 0.4 => high3Colour
    case vh if vh > 0.4 => veryHighColour
  }

  private def fakeColour(value: Double): Colour = value match {
    case l1 if l1 < 1 => low1Colour
    case l2 if 1 until 3 contains l2 => low2Colour
    case l3 if 4 until 7 contains l3 => low3Colour
    case m1 if 8 until 10 contains m1 => moderate1Colour
    case m2 if 11 until 15 contains m2 => moderate2Colour
    case m3 if 16 until 20 contains m3 => moderate3Colour
    case h1 if 21 until 25 contains h1 => high1Colour
    case h2 if 26 until 30 contains h2 => high2Colour
    case h3 if 31 until 35 contains h3 => high3Colour
    case vh if vh > 36 => veryHighColour
  }

  private def pm25Colour(value: Double): Colour = value match {
    case l1 if l1 < 11 => low1Colour
    case l2 if 12 until 23 contains l2 => low2Colour
    case l3 if 24 until 35 contains l3 => low3Colour
    case m1 if 36 until 41 contains m1 => moderate1Colour
    case m2 if 42 until 47 contains m2 => moderate2Colour
    case m3 if 48 until 53 contains m3 => moderate3Colour
    case h1 if 54 until 58 contains h1 => high1Colour
    case h2 if 59 until 64 contains h2 => high2Colour
    case h3 if 65 until 70 contains h3 => high3Colour
    case vh if vh > 71 => veryHighColour
  }

  private def pm10Colour(value: Double): Colour = value match {
    case l1 if l1 < 16 => low1Colour
    case l2 if 17 until 33 contains l2 => low2Colour
    case l3 if 34 until 50 contains l3 => low3Colour
    case m1 if 51 until 58 contains m1 => moderate1Colour
    case m2 if 59 until 66 contains m2 => moderate2Colour
    case m3 if 67 until 75 contains m3 => moderate3Colour
    case h1 if 76 until 83 contains h1 => high1Colour
    case h2 if 84 until 91 contains h2 => high2Colour
    case h3 if 92 until 100 contains h3 => high3Colour
    case vh if vh > 101 => veryHighColour
  }

}
