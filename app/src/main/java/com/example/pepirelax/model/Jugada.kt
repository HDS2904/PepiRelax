package com.example.pepirelax.model

import java.util.*

data class Jugada (var jugadorUnoId: String, var jugadorDosId: String = "", var celdasSeleccionadas: List<Int>? = null,
                   var turnoJugadorUno: Boolean = true, var ganadorId: String = "", var created: Date = Date(), var abandonoId: String = "")