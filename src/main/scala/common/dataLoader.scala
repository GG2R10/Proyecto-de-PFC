import scala.io.Source
import Datos._

package object Loader {

  def loadAeropuertos(path: String): List[Aeropuerto] =
    Source.fromResource(path).getLines().map { line =>
      val Array(cod, x, y, gmt) = line.split(",")
      Aeropuerto(cod, x.toInt, y.toInt, gmt.toInt)
    }.toList

  def loadVuelos(path: String): List[Vuelo] =
    Source.fromResource(path).getLines().map { line =>
      val Array(aln, num, org, hs, ms, dst, hl, ml, esc) = line.split(",")
      Vuelo(aln, num.toInt, org, hs.toInt, ms.toInt, dst, hl.toInt, ml.toInt, esc.toInt)
    }.toList
}