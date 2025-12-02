
package object Itinerarios {
  //importamos el paquete datos para usar nuestros case / type
  import Datos._

  //Funciones de Sebastian:
  //Funcion Itinerarios (funcionamiento exitoso)
  def itinerarios(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    val adj: Map[String, List[Vuelo]] =
      vuelos.groupBy(_.Org)

    def buscar(Org: String, Dst: String, visitados: Set[String], actual: Itinerario): List[Itinerario] = {
      if (Org == Dst) List(actual)
      else
        adj.getOrElse(Org, Nil)
          .filter(v => !visitados.contains(v.Dst))
          .flatMap { v =>
            val nuevosVisitados = visitados + v.Org
            val nuevoActual = actual :+ v
            buscar(v.Dst, Dst, nuevosVisitados, nuevoActual)
          }
    }

    (c1, c2) => buscar(c1, c2, Set(), Nil)
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
  //Final de funciones de sebastian ----------------------------------------------



  //Mis funciones---------------------------------------------------------------
  def itinerariosAire(vuelos: List[Vuelo], aeropuertos: List[Aeropuerto]): (String, String) => List[Itinerario] = {
    //Se usa la funcion de sabestian, para poder generar los itinerarios posibles.
    val itsFun: (String, String) => List[Itinerario] =
      itinerarios(vuelos, aeropuertos)

    // Mapa código -> aeropuerto para obtener GMT rápidamente
    val aeropuertosPorCod: Map[String, Aeropuerto] =
      aeropuertos.map(a => a.Cod -> a).toMap

    // Convierte hora local (h, m) de un aeropuerto en minutos UTC
    def minutosUTC(cod: String, h: Int, m: Int): Int =
      h * 60 + m - (aeropuertosPorCod(cod).GMT)

    //Org == Cod
    // Duración EN AIRE de un vuelo, en minutos
    // Usamos SOLO horarios + GMT, ignoramos Esc
    def duracionVuelo(v: Vuelo): Int = {
      val salidaUTC       = minutosUTC(v.Org, v.HS, v.MS)
      val llegadaUTCBruta = minutosUTC(v.Dst, v.HL, v.ML)

      //Si la llegada en UTC queda "antes" que la salida,
      //se asume q el vuelo llega al día siguiente (+24h)
      //es IMPOSIBLE que en UTC llegada<salida sin que pase la medianoche
      //es decir, si vemos este fenomeno, necesariamente cambio de dia
      val llegadaUTC =
        if (llegadaUTCBruta < salidaUTC) llegadaUTCBruta + 24 * 60
        else llegadaUTCBruta

      llegadaUTC - salidaUTC
    }

    // Tiempo total en aire de un itinerario = suma de las duraciones de sus vuelos
    def tiempoAire(it: Itinerario): Int =
      it.map(duracionVuelo).sum

    // Función que se expone al exterior
    (c1: String, c2: String) => {
      val todos: List[Itinerario] = itsFun(c1, c2)
      todos.sortBy(tiempoAire).take(3)
    }
  }








}
