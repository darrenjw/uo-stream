package urban_observatory.websocket

import org.scalatest.flatspec.AnyFlatSpec

class MetricsSpec extends AnyFlatSpec {

  "co" should "find description CO" in {
    val coMetric = Metrics.fullMetricsMap("co")
    assert(coMetric.description === "CO")
  }

  "no2" should "find description no2" in {
    val coMetric = Metrics.fullMetricsMap("no2")
    assert(coMetric.description === "NO2")
  }

}
