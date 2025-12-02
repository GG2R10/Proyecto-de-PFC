import Datos._
import common._
import Itinerarios._
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ParSeq

package object ItinerariosPar {
  def itinerariosPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]):
  (String, String) => List[Itinerario] = {

    val adj = vuelos.groupBy(_.Org)

    def buscar(Org: String, Dst: String, visitados: Set[String], actual: Itinerario): List[Itinerario] = {
      if (Org == Dst) List(actual)
      else {
        val siguiente = adj.getOrElse(Org, Nil).filter(v => !visitados.contains(v.Dst))

        siguiente match {

          // Caso base 1: no hay vuelos
          case Nil =>
            Nil

          // Caso base 2: un solo vuelo — NO paralelizamos
          case v :: Nil =>
            val nuevosVisitados = visitados + v.Org
            buscar(v.Dst, Dst, nuevosVisitados, actual :+ v)

          // Caso general: 2+ vuelos — paralelizamos con divide & conquer
          case v :: tail =>
            val nuevosVisitadosA = visitados + v.Org

            val (resA, resB) = parallel(
              // Task A: explorar la rama del primer vuelo
              buscar(v.Dst, Dst, nuevosVisitadosA, actual :+ v),

              // Task B: explorar recursivamente todas las demás ramas
              tail.flatMap { vueloRest =>
                val nuevosVisitadosB = visitados + vueloRest.Org
                buscar(vueloRest.Dst, Dst, nuevosVisitadosB, actual :+ vueloRest)
              }
            )

            resA ++ resB
        }
      }
    }

    (c1, c2) => buscar(c1, c2, Set(), Nil)
  }

  def itinerarioSalidaPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]):
  (String, String, Int, Int) => Itinerario = {

    val aeromap = aeropuertos.map(a => (a.Cod, a)).toMap
    val itsPar = itinerariosPar(vuelos, aeropuertos)

    def llegadaUTC(v: Vuelo): Int =
      v.HL*60 + v.ML - aeromap(v.Dst).GMT

    def salidaUTC(v: Vuelo): Int =
      v.HS*60 + v.MS - aeromap(v.Org).GMT

    def llegadaIt(it: Itinerario): Int = llegadaUTC(it.last)
    def salidaIt(it: Itinerario): Int = salidaUTC(it.head)

    (c1, c2, h, m) => {
      val citaUTC = h*60 + m - aeromap(c2).GMT

      val its = itsPar(c1, c2).par       // paralelismo en datos

      val posibles = its.filter(it => llegadaIt(it) <= citaUTC)

      if (posibles.isEmpty) Nil
      else posibles.maxBy(salidaIt)
    }
  }
}