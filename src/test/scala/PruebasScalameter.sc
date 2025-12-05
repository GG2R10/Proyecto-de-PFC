import Loader._
import Datos._
import Itinerarios._
import ItinerariosPar._
import org.scalameter._

// La funcion esa de ScalaMeter
def medirTiempo[T](nombre: String)(body: => T): (T, Double) = {
  val tiempo = config(
    KeyValue(Key.exec.minWarmupRuns, 10),
    KeyValue(Key.exec.maxWarmupRuns, 10),
    KeyValue(Key.verbose, false)
  ).withWarmer(new Warmer.Default).measure(body)

  (body, tiempo.value)
}

val aeropuertosCurso = loadAeropuertos("aeropuertos.csv")
val origen = "HOU"
val destino = "DFW"
val hora = 18
val min = 30

case class Resultado(nombre: String, secuencial: Double, paralelo: Double, speedup: Double, longitudSec: Int, longitudPar: Int)

val vuelosArchivos = List(
  ("50 vuelos", "50vuelos.csv"),
  ("100 vuelos", "100vuelos.csv")
  // ("200 vuelos", "200vuelos.csv") los 200 vuelos están deshabilitados para no matar sus computadoras cuando lo corran. Es demasiada escencia
)

var resultados = List.empty[Resultado]

def medirYGuardar[T](nombre: String, secFunc: => T, parFunc: => T, longitud: T => Int): Unit = {
  val (resSec, tSec) = medirTiempo(s"$nombre secuencial")(secFunc)
  val (resPar, tPar) = medirTiempo(s"$nombre paralelo")(parFunc)
  val speedup = tSec / tPar
  resultados ::= Resultado(nombre, tSec, tPar, speedup, longitud(resSec), longitud(resPar))
}

// Bucle para las 3 pruebas
for ((etiqueta, archivoVuelos) <- vuelosArchivos) {

  println(s"\n=== Pruebas con $etiqueta ===\n")

  val vuelosCurso = loadVuelos(archivoVuelos)

  // --- Pruebas de todas las funciones ---
  medirYGuardar(s"itinerarios ($etiqueta)",
    itinerarios(vuelosCurso, aeropuertosCurso)(origen, destino),
    itinerariosPar(vuelosCurso, aeropuertosCurso)(origen, destino),
    (it: List[Itinerario]) => it.length
  )

  medirYGuardar(s"itinerarioSalida ($etiqueta)",
    itinerarioSalida(vuelosCurso, aeropuertosCurso)(origen, destino, hora, min),
    itinerarioSalidaPar(vuelosCurso, aeropuertosCurso)(origen, destino, hora, min),
    (_: Itinerario) => 1
  )

  medirYGuardar(s"itinerariosEscalas ($etiqueta)",
    itinerariosEscalas(vuelosCurso, aeropuertosCurso)(origen, destino),
    itinerariosEscalasPar(vuelosCurso, aeropuertosCurso)(origen, destino),
    (it: List[Itinerario]) => it.length
  )

  medirYGuardar(s"itinerariosTiempo ($etiqueta)",
    itinerariosTiempo(vuelosCurso, aeropuertosCurso)(origen, destino),
    itinerariosTiempoPar(vuelosCurso, aeropuertosCurso)(origen, destino),
    (it: List[Itinerario]) => it.length
  )

  medirYGuardar(s"itinerariosAire ($etiqueta)",
    itinerariosAire(vuelosCurso, aeropuertosCurso)(origen, destino),
    itinerariosAirePar(vuelosCurso, aeropuertosCurso)(origen, destino),
    (it: List[Itinerario]) => it.length
  )
}

// mega papu resumen final
println(f"\n| Función                       | Secuencial (ms) | Paralelo (ms) | Speedup | # Itinerarios sec | # Itinerarios par |")
println("|-------------------------------|----------------|---------------|---------|-----------------|-----------------|")
for (r <- resultados.reverse) {
  println(f"| ${r.nombre}%-30s | ${r.secuencial}%14.2f | ${r.paralelo}%13.2f | ${r.speedup}%7.2f | ${r.longitudSec}%15d | ${r.longitudPar}%15d |")
}
