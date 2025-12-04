import Datos._
package object Itinerarios {
  def itinerarios(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    val adj: Map[String, List[Vuelo]] =
      vuelos.groupBy(_.Org)

    def buscar(Org: String, Dst: String, visitados: Set[String], actual: Itinerario): List[Itinerario] = {
      if (Org == Dst) List(actual)
      else
        adj.find(_._1 == Org).fold(Nil: List[Vuelo])(_._2)
          .filter(v => !visitados.contains(v.Dst))
          .flatMap { v =>
            val nuevosVisitados = visitados + v.Org
            val nuevoActual = actual :+ v
            buscar(v.Dst, Dst, nuevosVisitados, nuevoActual)
          }
    }

    (c1, c2) => buscar(c1, c2, Set(), Nil)
  }

  def itinerariosTiempo(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {

    def toMinutes(h: Int, m: Int): Int = h * 60 + m

    def tiempoTotal(it: Itinerario): Int = {
      val salida = it.head
      val llegada = it.last
      toMinutes(llegada.HL, llegada.ML) - toMinutes(salida.HS, salida.MS)
    }

    (c1: String, c2: String) => {
      val todos = itinerarios(vuelos, aeropuertos)(c1, c2)
      val conTiempos: List[(Itinerario, Int)] = for (it <- todos) yield (it, tiempoTotal(it))

      conTiempos.sortBy(_._2).take(3).map(_._1)
    }
  }

  def itinerariosEscalas(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    (cod1: String, cod2: String) => {

      // Función auxiliar para contar el número total de escalas
      def contarEscalas(itinerario: Itinerario): Int = {
        itinerario match {
          case Nil => Int.MaxValue
          case _ => itinerario.length - 1 + itinerario.map(_.Esc).sum
        }
      }

      val todosItinerarios = itinerarios(vuelos, aeropuertos)(cod1, cod2)
      val itinerariosOrdenados = todosItinerarios.sortBy(it => contarEscalas(it))
      itinerariosOrdenados.take(3)
    }
  }

  def itinerariosAire(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    val itsFun: (String, String) => List[Itinerario] =
      itinerarios(vuelos, aeropuertos)

    val aeropuertosPorCod: Map[String, Aeropuerto] =
      aeropuertos.map(a => a.Cod -> a).toMap

    def minutosUTC(cod: String, h: Int, m: Int): Int =
      h * 60 + m - (aeropuertosPorCod(cod).GMT)

    def duracionVuelo(v: Vuelo): Int = {
      val salidaUTC       = minutosUTC(v.Org, v.HS, v.MS)
      val llegadaUTCBruta = minutosUTC(v.Dst, v.HL, v.ML)

      val llegadaUTC =
        if (llegadaUTCBruta < salidaUTC) llegadaUTCBruta + 24 * 60
        else llegadaUTCBruta

      llegadaUTC - salidaUTC
    }

    def tiempoAire(it: Itinerario): Int =
      it.map(duracionVuelo).sum

    (c1: String, c2: String) => {
      val todos: List[Itinerario] = itsFun(c1, c2)
      todos.sortBy(tiempoAire).take(3)
    }
  }

  def itinerarioSalida(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String, Int, Int) => Itinerario = {
    val aeromap = aeropuertos.map(a => (a.Cod, a)).toMap
    val todosItinerarios = itinerarios(vuelos, aeropuertos)

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
      val citaUTC = m + h*60 - aeromap(c2).GMT

      val its = todosItinerarios(c1, c2)

      val posibles = its.filter(it => llegadaIt(it) <= citaUTC)

      if (posibles.isEmpty) Nil
      else posibles.maxBy(salidaIt)
    }
  }
}