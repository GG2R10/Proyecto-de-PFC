import Datos._
import Itinerarios._
import ItinerariosPar._
import org.scalameter._

// ============================================
// CONFIGURACIÓN DE SCALAMETER
// ============================================

val standardConfig = config(
  Key.exec.minWarmupRuns := 10,
  Key.exec.maxWarmupRuns := 20,
  Key.exec.benchRuns := 20,
  Key.verbose := false
) withWarmer(new Warmer.Default)

// ============================================
// SECCIÓN A: PAQUETES PEQUEÑOS (15 VUELOS)
// ============================================
/*
println("=" * 70)
println("SECCIÓN A - PAQUETES PEQUEÑOS (15 vuelos)")
println("=" * 70)

// --- A1 ---
println("\n>>> Pruebas con A1")
val timeA1Sec = standardConfig measure {
  itinerariosAire(vuelosA1, aeropuertosUSA)
}
val timeA1Par = standardConfig measure {
  itinerariosAirePar(vuelosA1, aeropuertosUSA)
}
println(s"A1 - Tiempo Secuencial: $timeA1Sec")
println(s"A1 - Tiempo Paralelo: $timeA1Par")
println(s"A1 - Aceleración: ${timeA1Sec.value / timeA1Par.value}")

// Verificar correctitud
val itsA1Sec = itinerariosAire(vuelosA1, aeropuertosUSA)
val itsA1Par = itinerariosAirePar(vuelosA1, aeropuertosUSA)
println(s"A1 - Verificación: Sec=${itsA1Sec.size} itinerarios, Par=${itsA1Par.size} itinerarios")

// --- A2 ---
println("\n>>> Pruebas con A2")
val timeA2Sec = standardConfig measure {
  itinerariosAire(vuelosA2, aeropuertosUSA)
}
val timeA2Par = standardConfig measure {
  itinerariosAirePar(vuelosA2, aeropuertosUSA)
}
println(s"A2 - Tiempo Secuencial: $timeA2Sec")
println(s"A2 - Tiempo Paralelo: $timeA2Par")
println(s"A2 - Aceleración: ${timeA2Sec.value / timeA2Par.value}")

// --- A3 ---
println("\n>>> Pruebas con A3")
val timeA3Sec = standardConfig measure {
  itinerariosAire(vuelosA3, aeropuertosUSA)
}
val timeA3Par = standardConfig measure {
  itinerariosAirePar(vuelosA3, aeropuertosUSA)
}
println(s"A3 - Tiempo Secuencial: $timeA3Sec")
println(s"A3 - Tiempo Paralelo: $timeA3Par")
println(s"A3 - Aceleración: ${timeA3Sec.value / timeA3Par.value}")

// --- A4 ---
println("\n>>> Pruebas con A4")
val timeA4Sec = standardConfig measure {
  itinerariosAire(vuelosA4, aeropuertosUSA)
}
val timeA4Par = standardConfig measure {
  itinerariosAirePar(vuelosA4, aeropuertosUSA)
}
println(s"A4 - Tiempo Secuencial: $timeA4Sec")
println(s"A4 - Tiempo Paralelo: $timeA4Par")
println(s"A4 - Aceleración: ${timeA4Sec.value / timeA4Par.value}")

// --- A5 ---
println("\n>>> Pruebas con A5")
val timeA5Sec = standardConfig measure {
  itinerariosAire(vuelosA5, aeropuertosUSA)
}
val timeA5Par = standardConfig measure {
  itinerariosAirePar(vuelosA5, aeropuertosUSA)
}
println(s"A5 - Tiempo Secuencial: $timeA5Sec")
println(s"A5 - Tiempo Paralelo: $timeA5Par")
println(s"A5 - Aceleración: ${timeA5Sec.value / timeA5Par.value}")

// Resumen Sección A
println("\n" + "=" * 70)
println("RESUMEN SECCIÓN A")
println("=" * 70)
val aceleracionesA = List(
  timeA1Sec.value / timeA1Par.value,
  timeA2Sec.value / timeA2Par.value,
  timeA3Sec.value / timeA3Par.value,
  timeA4Sec.value / timeA4Par.value,
  timeA5Sec.value / timeA5Par.value
)
println(s"Aceleración promedio: ${aceleracionesA.sum / aceleracionesA.size}")
println(s"Aceleración mínima: ${aceleracionesA.min}")
println(s"Aceleración máxima: ${aceleracionesA.max}")
*/

// ============================================
// SECCIÓN B: PAQUETES MEDIANOS (40 VUELOS)
// ============================================
/*
println("\n" + "=" * 70)
println("SECCIÓN B - PAQUETES MEDIANOS (40 vuelos)")
println("=" * 70)

// --- B1 ---
println("\n>>> Pruebas con B1")
val timeB1Sec = standardConfig measure {
  itinerariosAire(vuelosB1, aeropuertosUSA)
}
val timeB1Par = standardConfig measure {
  itinerariosAirePar(vuelosB1, aeropuertosUSA)
}
println(s"B1 - Tiempo Secuencial: $timeB1Sec")
println(s"B1 - Tiempo Paralelo: $timeB1Par")
println(s"B1 - Aceleración: ${timeB1Sec.value / timeB1Par.value}")

// --- B2 ---
println("\n>>> Pruebas con B2")
val timeB2Sec = standardConfig measure {
  itinerariosAire(vuelosB2, aeropuertosUSA)
}
val timeB2Par = standardConfig measure {
  itinerariosAirePar(vuelosB2, aeropuertosUSA)
}
println(s"B2 - Tiempo Secuencial: $timeB2Sec")
println(s"B2 - Tiempo Paralelo: $timeB2Par")
println(s"B2 - Aceleración: ${timeB2Sec.value / timeB2Par.value}")

// --- B3 ---
println("\n>>> Pruebas con B3")
val timeB3Sec = standardConfig measure {
  itinerariosAire(vuelosB3, aeropuertosUSA)
}
val timeB3Par = standardConfig measure {
  itinerariosAirePar(vuelosB3, aeropuertosUSA)
}
println(s"B3 - Tiempo Secuencial: $timeB3Sec")
println(s"B3 - Tiempo Paralelo: $timeB3Par")
println(s"B3 - Aceleración: ${timeB3Sec.value / timeB3Par.value}")

// --- B4 ---
println("\n>>> Pruebas con B4")
val timeB4Sec = standardConfig measure {
  itinerariosAire(vuelosB4, aeropuertosUSA)
}
val timeB4Par = standardConfig measure {
  itinerariosAirePar(vuelosB4, aeropuertosUSA)
}
println(s"B4 - Tiempo Secuencial: $timeB4Sec")
println(s"B4 - Tiempo Paralelo: $timeB4Par")
println(s"B4 - Aceleración: ${timeB4Sec.value / timeB4Par.value}")

// --- B5 ---
println("\n>>> Pruebas con B5")
val timeB5Sec = standardConfig measure {
  itinerariosAire(vuelosB5, aeropuertosUSA)
}
val timeB5Par = standardConfig measure {
  itinerariosAirePar(vuelosB5, aeropuertosUSA)
}
println(s"B5 - Tiempo Secuencial: $timeB5Sec")
println(s"B5 - Tiempo Paralelo: $timeB5Par")
println(s"B5 - Aceleración: ${timeB5Sec.value / timeB5Par.value}")

// Resumen Sección B
println("\n" + "=" * 70)
println("RESUMEN SECCIÓN B")
println("=" * 70)
val aceleracionesB = List(
  timeB1Sec.value / timeB1Par.value,
  timeB2Sec.value / timeB2Par.value,
  timeB3Sec.value / timeB3Par.value,
  timeB4Sec.value / timeB4Par.value,
  timeB5Sec.value / timeB5Par.value
)
println(s"Aceleración promedio: ${aceleracionesB.sum / aceleracionesB.size}")
println(s"Aceleración mínima: ${aceleracionesB.min}")
println(s"Aceleración máxima: ${aceleracionesB.max}")
*/

// ============================================
// SECCIÓN C: PAQUETES GRANDES (100 VUELOS)
// ============================================
/*
println("\n" + "=" * 70)
println("SECCIÓN C - PAQUETES GRANDES (100 vuelos)")
println("=" * 70)

// --- C1 ---
println("\n>>> Pruebas con C1")
val timeC1Sec = standardConfig measure {
  itinerariosAire(vuelosC1, aeropuertosUSA)
}
val timeC1Par = standardConfig measure {
  itinerariosAirePar(vuelosC1, aeropuertosUSA)
}
println(s"C1 - Tiempo Secuencial: $timeC1Sec")
println(s"C1 - Tiempo Paralelo: $timeC1Par")
println(s"C1 - Aceleración: ${timeC1Sec.value / timeC1Par.value}")

// --- C2 ---
println("\n>>> Pruebas con C2")
val timeC2Sec = standardConfig measure {
  itinerariosAire(vuelosC2, aeropuertosUSA)
}
val timeC2Par = standardConfig measure {
  itinerariosAirePar(vuelosC2, aeropuertosUSA)
}
println(s"C2 - Tiempo Secuencial: $timeC2Sec")
println(s"C2 - Tiempo Paralelo: $timeC2Par")
println(s"C2 - Aceleración: ${timeC2Sec.value / timeC2Par.value}")

// --- C3 ---
println("\n>>> Pruebas con C3")
val timeC3Sec = standardConfig measure {
  itinerariosAire(vuelosC3, aeropuertosUSA)
}
val timeC3Par = standardConfig measure {
  itinerariosAirePar(vuelosC3, aeropuertosUSA)
}
println(s"C3 - Tiempo Secuencial: $timeC3Sec")
println(s"C3 - Tiempo Paralelo: $timeC3Par")
println(s"C3 - Aceleración: ${timeC3Sec.value / timeC3Par.value}")

// --- C4 ---
println("\n>>> Pruebas con C4")
val timeC4Sec = standardConfig measure {
  itinerariosAire(vuelosC4, aeropuertosUSA)
}
val timeC4Par = standardConfig measure {
  itinerariosAirePar(vuelosC4, aeropuertosUSA)
}
println(s"C4 - Tiempo Secuencial: $timeC4Sec")
println(s"C4 - Tiempo Paralelo: $timeC4Par")
println(s"C4 - Aceleración: ${timeC4Sec.value / timeC4Par.value}")

// --- C5 ---
println("\n>>> Pruebas con C5")
val timeC5Sec = standardConfig measure {
  itinerariosAire(vuelosC5, aeropuertosUSA)
}
val timeC5Par = standardConfig measure {
  itinerariosAirePar(vuelosC5, aeropuertosUSA)
}
println(s"C5 - Tiempo Secuencial: $timeC5Sec")
println(s"C5 - Tiempo Paralelo: $timeC5Par")
println(s"C5 - Aceleración: ${timeC5Sec.value / timeC5Par.value}")

// Resumen Sección C
println("\n" + "=" * 70)
println("RESUMEN SECCIÓN C")
println("=" * 70)
val aceleracionesC = List(
  timeC1Sec.value / timeC1Par.value,
  timeC2Sec.value / timeC2Par.value,
  timeC3Sec.value / timeC3Par.value,
  timeC4Sec.value / timeC4Par.value,
  timeC5Sec.value / timeC5Par.value
)
println(s"Aceleración promedio: ${aceleracionesC.sum / aceleracionesC.size}")
println(s"Aceleración mínima: ${aceleracionesC.min}")
println(s"Aceleración máxima: ${aceleracionesC.max}")
*/

// ============================================
// SECCIÓN D: PAQUETES MUY GRANDES (500 VUELOS)
// ============================================
/*
println("\n" + "=" * 70)
println("SECCIÓN D - PAQUETES MUY GRANDES (500 vuelos)")
println("=" * 70)

// ADVERTENCIA: Estas pruebas pueden tomar MUCHO tiempo
// Se recomienda probar de uno en uno

// --- D1 ---
println("\n>>> Pruebas con D1")
val timeD1Sec = standardConfig measure {
  itinerariosAire(vuelosD1, aeropuertosUSA)
}
val timeD1Par = standardConfig measure {
  itinerariosAirePar(vuelosD1, aeropuertosUSA)
}
println(s"D1 - Tiempo Secuencial: $timeD1Sec")
println(s"D1 - Tiempo Paralelo: $timeD1Par")
println(s"D1 - Aceleración: ${timeD1Sec.value / timeD1Par.value}")

// --- D2 ---
println("\n>>> Pruebas con D2")
val timeD2Sec = standardConfig measure {
  itinerariosAire(vuelosD2, aeropuertosUSA)
}
val timeD2Par = standardConfig measure {
  itinerariosAirePar(vuelosD2, aeropuertosUSA)
}
println(s"D2 - Tiempo Secuencial: $timeD2Sec")
println(s"D2 - Tiempo Paralelo: $timeD2Par")
println(s"D2 - Aceleración: ${timeD2Sec.value / timeD2Par.value}")

// --- D3 ---
println("\n>>> Pruebas con D3")
val timeD3Sec = standardConfig measure {
  itinerariosAire(vuelosD3, aeropuertosUSA)
}
val timeD3Par = standardConfig measure {
  itinerariosAirePar(vuelosD3, aeropuertosUSA)
}
println(s"D3 - Tiempo Secuencial: $timeD3Sec")
println(s"D3 - Tiempo Paralelo: $timeD3Par")
println(s"D3 - Aceleración: ${timeD3Sec.value / timeD3Par.value}")

// --- D4 ---
println("\n>>> Pruebas con D4")
val timeD4Sec = standardConfig measure {
  itinerariosAire(vuelosD4, aeropuertosUSA)
}
val timeD4Par = standardConfig measure {
  itinerariosAirePar(vuelosD4, aeropuertosUSA)
}
println(s"D4 - Tiempo Secuencial: $timeD4Sec")
println(s"D4 - Tiempo Paralelo: $timeD4Par")
println(s"D4 - Aceleración: ${timeD4Sec.value / timeD4Par.value}")

// --- D5 ---
println("\n>>> Pruebas con D5")
val timeD5Sec = standardConfig measure {
  itinerariosAire(vuelosD5, aeropuertosUSA)
}
val timeD5Par = standardConfig measure {
  itinerariosAirePar(vuelosD5, aeropuertosUSA)
}
println(s"D5 - Tiempo Secuencial: $timeD5Sec")
println(s"D5 - Tiempo Paralelo: $timeD5Par")
println(s"D5 - Aceleración: ${timeD5Sec.value / timeD5Par.value}")

// Resumen Sección D
println("\n" + "=" * 70)
println("RESUMEN SECCIÓN D")
println("=" * 70)
val aceleracionesD = List(
  timeD1Sec.value / timeD1Par.value,
  timeD2Sec.value / timeD2Par.value,
  timeD3Sec.value / timeD3Par.value,
  timeD4Sec.value / timeD4Par.value,
  timeD5Sec.value / timeD5Par.value
)
println(s"Aceleración promedio: ${aceleracionesD.sum / aceleracionesD.size}")
println(s"Aceleración mínima: ${aceleracionesD.min}")
println(s"Aceleración máxima: ${aceleracionesD.max}")
*/

// ============================================
// PRUEBAS RÁPIDAS DE CORRECTITUD
// ============================================
/*
println("\n" + "=" * 70)
println("VERIFICACIÓN RÁPIDA DE CORRECTITUD")
println("=" * 70)

// Verifica que ambas implementaciones den la misma cantidad de itinerarios
def verificar(nombre: String, vuelos: List[Vuelo]): Unit = {
  val sec = itinerariosAire(vuelos, aeropuertosUSA)
  val par = itinerariosAirePar(vuelos, aeropuertosUSA)
  val check = if (sec.size == par.size) "✓" else "✗"
  println(s"$check $nombre: Sec=${sec.size} funciones, Par=${par.size} funciones")
}

verificar("A1", vuelosA1)
verificar("B1", vuelosB1)
verificar("C1", vuelosC1)
// verificar("D1", vuelosD1) // Descomentar si quieres verificar D1
*/

println("\n¡Listo! Descomenta la sección que quieras probar.")