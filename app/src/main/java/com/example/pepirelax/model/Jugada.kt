package com.example.pepirelax.model

import java.util.*
import kotlin.collections.ArrayList

data class Jugada (var jugadorUnoId: String, var jugadorDosId: String = "", var selectedCells: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0,0,0),
                   var turnoJugadorUno: Boolean = true, var ganadorId: String = "", var created: Date = Date(), var goOut: Boolean= false)