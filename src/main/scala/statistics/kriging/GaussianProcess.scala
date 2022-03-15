/*
gp.scala
Main GP object containing all of the Gaussian process updating logic
*/

package statistics.kriging

import breeze.linalg.{Vector => BVec, _}
import statistics.StatisticalLocation

case class GaussianProcess[Loc](values: Vector[Double], locations: Vector[StatisticalLocation],
                                Q: DenseMatrix[Double], k: (StatisticalLocation, StatisticalLocation) => Double,
                                nugV: Double) {

  // add a new observation
  def update(newValue: Double, newLocation: StatisticalLocation): GaussianProcess[Loc] = {
    // check if we already have an obs at this location
    val i = locations.indexOf(newLocation)
    if (i >= 0) // replace
      GaussianProcess(values.updated(i, newValue), locations, Q, k, nugV)
    else { // add
      // main task is to extend the precision matrix, Q
      val c = DenseVector((locations map (k(_, newLocation))).toArray)
      val Qc = Q * c
      val Qcm = Qc.toDenseMatrix // one row, for some reason
      val sss = k(newLocation, newLocation) - (c dot Qc) + nugV
      val Q1 = Q + (Qcm.t * Qcm)/sss // TODO: (not urgent) better to do with a BLAS rank 1 update
      val Q2 = DenseMatrix.vertcat(Q1, -Qcm/sss)
      val vv = DenseVector.vertcat(-Qc/sss, DenseVector(1.0/sss))
      val nQ = DenseMatrix.horzcat(Q2, vv.toDenseMatrix.t)
      GaussianProcess(values :+ newValue, locations :+ newLocation, nQ, k, nugV)
    }
  }

  // drop the first (oldest) observation
  def tail: GaussianProcess[Loc] = {
    val n = values.length
    assume(n > 1)
    // main task is to marginalise out an obs from Q
    val Qs = Q(1 until n, 1 until n)
    val s = Q(1 until n, 0)
    val nQ = Qs - (s * s.t)/Q(0,0) // TODO: (not urgent) better to use a BLAS rank 1 update
    GaussianProcess(values.tail, locations.tail, nQ, k, nugV)
  }

  // update preserving a max of mx observations
  def updateMx(nextValue: Double, nextLocation: StatisticalLocation, mx: Int = 200): GaussianProcess[Loc] = {
    val next = update(nextValue, nextLocation)
    if (values.length < mx) next else next.tail
  }

  def mean(nextLocation: StatisticalLocation): Double = {
    val c = DenseVector((locations map (k(_, nextLocation))).toArray)
    val Qc = Q * c
    DenseVector(values.toArray) dot Qc
  }

  def meanAndVar(nextLocation: StatisticalLocation): (Double, Double) = {
    val c = DenseVector((locations map (k(_, nextLocation))).toArray)
    val Qc = Q * c
    val mean = DenseVector(values.toArray) dot Qc
    val v = k(nextLocation, nextLocation) - (c dot Qc)
    (mean, v)
  }

} // GP

case object GaussianProcess {
  def apply[Loc](k: (StatisticalLocation, StatisticalLocation) => Double, nugV: Double = 0.0): GaussianProcess[Loc] =
    GaussianProcess(Vector[Double](), Vector[StatisticalLocation](), new DenseMatrix[Double](0,0), k, nugV)
}



// eof
