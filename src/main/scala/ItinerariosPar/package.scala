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
        val siguiente = adj.find(_._1 == Org).map(_._2).fold(Nil: List[Vuelo])(_.filter(v => !visitados.contains(v.Dst)))

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




  def itinerariosAirePar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    val itsFun: (String, String) => List[Itinerario] =
      itinerariosPar(vuelos, aeropuertos)

    val aeropuertosPorCod: Map[String, Aeropuerto] =
      aeropuertos.map(a => a.Cod -> a).toMap

    def minutosUTC(cod: String, h: Int, m: Int): Int =
      h * 60 + m - aeropuertosPorCod(cod).GMT

    def duracionVuelo(v: Vuelo): Int = {
      val salidaUTC = minutosUTC(v.Org, v.HS, v.MS)
      val llegadaUTCBruta = minutosUTC(v.Dst, v.HL, v.ML)

      val llegadaUTC =
        if (llegadaUTCBruta < salidaUTC) llegadaUTCBruta + 24 * 60
        else llegadaUTCBruta

      llegadaUTC - salidaUTC
    }

    def tiempoAire(it: Itinerario): Int =
      it.map(duracionVuelo).sum

    // ===== UMBRAL: evita overhead en listas pequeñas =====
    val UMBRAL = 50

    // ===== Paralelismo de tareas: calculamos (itinerario, tiempoAire) en paralelo =====
    def tiemposPar(itins: List[Itinerario]): List[(Itinerario, Int)] = {
      // Si la lista es pequeña o tiene 1 elemento, procesar secuencialmente
      if (itins.length <= UMBRAL) {
        itins.map(it => (it, tiempoAire(it)))
      } else {
        // Dividir en dos mitades y procesar en paralelo
        val (izq, der) = itins.splitAt(itins.length / 2)
        val (tiemposIzq, tiemposDer) = parallel(
          tiemposPar(izq),
          tiemposPar(der)
        )
        tiemposIzq ::: tiemposDer
      }
    }

    // ===== Encontrar los 3 mejores de forma más eficiente =====
    def mejoresTres(conTiempos: List[(Itinerario, Int)]): List[Itinerario] = {
      if (conTiempos.length <= UMBRAL) {
        // Caso base: ordenar secuencialmente
        conTiempos.sortBy(_._2).take(3).map(_._1)
      } else {
        // Dividir, encontrar mejores de cada lado en paralelo
        val (izq, der) = conTiempos.splitAt(conTiempos.length / 2)

        val (mejoresIzq, mejoresDer) = parallel(
          izq.sortBy(_._2).take(3),
          der.sortBy(_._2).take(3)
        )

        // Combinar y seleccionar los 3 mejores globales
        (mejoresIzq ::: mejoresDer).sortBy(_._2).take(3).map(_._1)
      }
    }

    // Función expuesta al exterior
    (c1: String, c2: String) => {
      val todos: List[Itinerario] = itsFun(c1, c2)

      if (todos.isEmpty) Nil
      else {
        // Calculamos tiempos en paralelo
        val conTiempos: List[(Itinerario, Int)] = tiemposPar(todos)

        // Encontramos los 3 mejores (también puede ser en paralelo)
        mejoresTres(conTiempos)
      }
    }
  }




}
