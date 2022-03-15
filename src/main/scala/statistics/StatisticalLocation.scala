package statistics

case class Colour(red: Int = 0, green: Int = 0, blue: Int = 0, alpha: Double = 0.0);

case class StatisticalLocation(id: String = "", sensorName: String = "", value: Double = 0.0, time: Int = 0, lat: Double = 0.0, lng: Double = 0.0, colour: Colour = Colour())
case class StatisticalLocations(locations: List[StatisticalLocation])
