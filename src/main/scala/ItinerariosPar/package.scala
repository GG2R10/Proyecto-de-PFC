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
          case Nil =>
            Nil

          case v :: Nil =>
            val nuevosVisitados = visitados + v.Org
            buscar(v.Dst, Dst, nuevosVisitados, actual :+ v)

          case v :: tail =>
            val nuevosVisitadosA = visitados + v.Org

            val (resA, resB) = parallel(
              buscar(v.Dst, Dst, nuevosVisitadosA, actual :+ v),

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

  def itinerariosTiempoPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    val itsFun: (String, String) => List[Itinerario] =
      itinerariosPar(vuelos, aeropuertos)

    def toMinutes(h: Int, m: Int): Int = h * 60 + m

    def tiempoTotal(it: Itinerario): Int = {
      val inicio = toMinutes(it.head.HS, it.head.MS)
      val fin = toMinutes(it.last.HL, it.last.ML)
      fin - inicio
    }

    val UMBRAL = 50

    def tiemposPar(itins: List[Itinerario]): List[(Itinerario, Int)] = {
      if (itins.length <= UMBRAL) {
        for (it <- itins) yield (it, tiempoTotal(it))
      } else {
        val (izq, der) = itins.splitAt(itins.length / 2)
        val (tiemposIzq, tiemposDer) = parallel(
          tiemposPar(izq),
          tiemposPar(der)
        )
        tiemposIzq ::: tiemposDer
      }
    }

    def mejoresTres(conTiempos: List[(Itinerario, Int)]): List[Itinerario] = {
      if (conTiempos.length <= UMBRAL) {
        val ordenados = for (t <- conTiempos) yield t
        ordenados.sortBy(_._2).take(3).map(_._1)
      } else {
        val (izq, der) = conTiempos.splitAt(conTiempos.length / 2)
        val (mejoresIzq, mejoresDer) = parallel(
          izq.sortBy(_._2).take(3),
          der.sortBy(_._2).take(3)
        )
        (mejoresIzq ::: mejoresDer).sortBy(_._2).take(3).map(_._1)
      }
    }

    (c1: String, c2: String) => {
      val todos: List[Itinerario] = itsFun(c1, c2)
      if (todos.isEmpty) Nil
      else {
        val conTiempos = tiemposPar(todos)
        mejoresTres(conTiempos)
      }
    }
  }

  def itinerariosEscalasPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    (cod1: String, cod2: String) => {
      val umbral = 50;

      def contarEscalas(itinerario: Itinerario): Int =
        itinerario match {
        case Nil => Int.MaxValue
        case _ => itinerario.length - 1 + itinerario.map(_.Esc).sum
      }

      val todosItinerarios = itinerariosPar(vuelos, aeropuertos)(cod1, cod2)

      if (todosItinerarios.length >= umbral) {
        val mitad = todosItinerarios.length / 2
        val (parte1, parte2) = todosItinerarios.splitAt(mitad)

        val (evaluados1, evaluados2) = parallel(
          parte1.map(it => (it, contarEscalas(it))),
          parte2.map(it => (it, contarEscalas(it)))
        )
        val evaluados = evaluados1 ++ evaluados2
        evaluados.sortBy(_._2).take(3).map(_._1)

      } else {
        val itinerariosOrdenados = todosItinerarios.sortBy(it => contarEscalas(it))
        itinerariosOrdenados.take(3)
      }
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

    val UMBRAL = 50

    def tiemposPar(itins: List[Itinerario]): List[(Itinerario, Int)] = {
      if (itins.length <= UMBRAL) {
        itins.map(it => (it, tiempoAire(it)))
      } else {
        val (izq, der) = itins.splitAt(itins.length / 2)
        val (tiemposIzq, tiemposDer) = parallel(
          tiemposPar(izq),
          tiemposPar(der)
        )
        tiemposIzq ::: tiemposDer
      }
    }

    def mejoresTres(conTiempos: List[(Itinerario, Int)]): List[Itinerario] = {
      if (conTiempos.length <= UMBRAL) {
        conTiempos.sortBy(_._2).take(3).map(_._1)
      } else {
        val (izq, der) = conTiempos.splitAt(conTiempos.length / 2)

        val (mejoresIzq, mejoresDer) = parallel(
          izq.sortBy(_._2).take(3),
          der.sortBy(_._2).take(3)
        )

        (mejoresIzq ::: mejoresDer).sortBy(_._2).take(3).map(_._1)
      }
    }

    (c1: String, c2: String) => {
      val todos: List[Itinerario] = itsFun(c1, c2)

      if (todos.isEmpty) Nil
      else {
        val conTiempos: List[(Itinerario, Int)] = tiemposPar(todos)
        mejoresTres(conTiempos)
      }
    }
  }

  def itinerarioSalidaPar(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]):
    (String, String, Int, Int) => Itinerario = {

    val aeromap = aeropuertos.map(a => (a.Cod, a)).toMap
    val itsPar = itinerariosPar(vuelos, aeropuertos)

    def llegadaIt(it: Itinerario): Int = {
      val last = it.last
      val dst = aeromap(last.Dst)
      last.HL*60 + last.ML - dst.GMT
    }

    def salidaIt(it: Itinerario): Int = {
      val first = it.head
      val org = aeromap(first.Org)
      first.HS*60 + first.MS - org.GMT
    }

    (c1, c2, h, m) => {
      val citaUTC = h*60 + m - aeromap(c2).GMT

      val its = itsPar(c1, c2).par

      val posibles = its.filter(it => llegadaIt(it) <= citaUTC)

      if (posibles.isEmpty) Nil
      else posibles.maxBy(salidaIt)
    }
  }
}
