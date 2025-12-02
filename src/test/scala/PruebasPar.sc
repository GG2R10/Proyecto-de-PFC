
/*
import Datos._
import ItinerariosPar._
import Itinerarios._
import scala.util.Random
import org.scalameter._

// Ejemplo curso pequeño Sebastian
val itsCursoPar = itinerariosPar(vuelosCurso,aeropuertosCurso)
//2.1 Aeropuertos incomunicados
val its1 = itsCursoPar("MID", "SVCS")
val its2 = itsCursoPar("CLO", "SVCS")
// 4 itinerarios CLO-SVO
val its3 = itsCursoPar("CLO","SVO")
//2 itinerarios CLO-MEX
val its4 = itsCursoPar("CLO", "MEX")
//2 itinerarios CTG-PTY
val its5 = itsCursoPar("CTG","PTY")

//todas las funciones de sebastian sirven xd
*/

/*
// --- Función auxiliar para medir tiempo ---
def medirTiempo[T](nombre: String)(body: => T): (T, Double) = {
  val tiempo = config(
    KeyValue(Key.exec.minWarmupRuns, 20),
    KeyValue(Key.exec.maxWarmupRuns, 60),
    KeyValue(Key.verbose, false)
  ).withWarmer(new Warmer.Default).measure(body)

  println(s"$nombre tardó: ${tiempo.value} ms")
  (body, tiempo.value)
}

// --- Ejemplo de parámetros de prueba ---
val origen = "CLO"
val destino = "SVO"
val hora = 18
val min = 30

// --- Medición de itinerarios normales vs paralelos ---
val (itsSeq, tSeq) = medirTiempo("itinerarios secuencial") {
  itinerarios(vuelosCurso, aeropuertosCurso)(origen, destino)
}

val (itsPar, tPar) = medirTiempo("itinerarios paralelo") {
  itinerariosPar(vuelosCurso, aeropuertosCurso)(origen, destino)
}

val speedupIts = tSeq / tPar
println(f"Speedup itinerarios: $speedupIts%.2f veces")

// --- Medición de itinerarioSalida normal vs paralelo ---
val (salidaSeq, tSalSeq) = medirTiempo("itinerarioSalida secuencial") {
  itinerarioSalida(vuelosCurso, aeropuertosCurso)(origen, destino, hora, min)
}

val (salidaPar, tSalPar) = medirTiempo("itinerarioSalida paralelo") {
  itinerarioSalidaPar(vuelosCurso, aeropuertosCurso)(origen, destino, hora, min)
}

val speedupSal = tSalSeq / tSalPar
println(f"Speedup itinerarioSalida: $speedupSal%.2f veces")

// --- Opcional: comparación rápida de resultados ---
println(s"\nResultados itinerarios secuencial: ${itsSeq.length} rutas")
println(s"Resultados itinerarios paralelo: ${itsPar.length} rutas")
println(s"ItinerarioSalida secuencial: $salidaSeq")
println(s"ItinerarioSalida paralelo: $salidaPar")
*/

import Datos._  // Asegúrate de que los datos de 'vuelos' y 'aeropuertos' están correctamente importados
import Itinerarios._
import ItinerariosPar._
import org.scalameter._

// --- Función auxiliar para medir tiempo ---
def medirTiempo[T](nombre: String)(body: => T): (T, Double) = {
  val tiempo = config(
    KeyValue(Key.exec.minWarmupRuns, 20),  // Menos tiempo de calentamiento
    KeyValue(Key.exec.maxWarmupRuns, 60),  // Más tiempo de calentamiento
    KeyValue(Key.verbose, false)           // No mostrar detalles (puedes activar si quieres)
  ).withWarmer(new Warmer.Default).measure {
    body // Ejecutar el bloque que deseas medir aquí
  }

  println(s"$nombre tardó: ${tiempo.value} ms")
  (body, tiempo.value)  // Devuelve los resultados y el tiempo medido
}

// --- Función para obtener un subconjunto de los datos ---
def obtenerSubconjuntoDatos(vuelosSub: List[Vuelo], aeropuertosSub: List[Aeropuerto]): (List[Vuelo], List[Aeropuerto]) = {
  (vuelosSub, aeropuertosSub)  // Ya estamos tomando subconjuntos, por lo que solo los devolvemos
}

// --- Lista de tamaños para las pruebas ---
val tamaños = List(vuelosC1, vuelosC2, vuelosC3, vuelosC4, vuelosC5)  // Lista de vuelos por paquetes

// --- Probar diferentes tamaños de datos ---
for (vuelosSub <- tamaños) {
  val aeropuertosSub = aeropuertos  // Usar todos los aeropuertos, pero solo un subconjunto de vuelos

  println(s"Probando con ${vuelosSub.length} vuelos y ${aeropuertosSub.length} aeropuertos")

  // Medición de itinerarios secuencial
  val (itsSeq, tSeq) = medirTiempo("itinerarios secuencial") {
    itinerarios(vuelosSub, aeropuertosSub)("CLO", "SVO")  // Puedes cambiar los aeropuertos según tus pruebas
  }

  // Medición de itinerarios paralelo
  val (itsPar, tPar) = medirTiempo("itinerarios paralelo") {
    itinerariosPar(vuelosSub, aeropuertosSub)("CLO", "SVO")
  }

  // Calcular la aceleración
  val speedup = tSeq / tPar
  println(f"Speedup itinerarios: $speedup%.2f veces")

  // Opcional: comparación rápida de resultados
  println(s"Resultados itinerarios secuencial: ${itsSeq.length} rutas")
  println(s"Resultados itinerarios paralelo: ${itsPar.length} rutas")
}
