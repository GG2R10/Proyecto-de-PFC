import Datos._
import Itinerarios._

val itsAireCurso = itinerariosAire(vuelosCurso, aeropuertosCurso)
val itsa1 = itsAireCurso("MID", "SVCS")
val itsa2 = itsAireCurso("CLO", "SVCS")

// 4 itinerarios CLO−SVO
val itsa3 = itsAireCurso("CLO", "SVO")

// 2 itinerarios CLO−MEX
val itsa4 = itsAireCurso("CLO", "MEX")

// 2 itinerarios CTG−PTY
val itsa5 = itsAireCurso("CTG", "PTY")