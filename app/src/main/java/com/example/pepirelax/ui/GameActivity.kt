package com.example.pepirelax.ui

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.example.pepirelax.R
import com.example.pepirelax.model.Jugada
import com.example.pepirelax.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.content_game.*
import kotlinx.android.synthetic.main.dialog_over.*

class GameActivity : AppCompatActivity() {

    lateinit var casillas: ArrayList<ImageView>

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var dbs: FirebaseFirestore

    lateinit var uid: String
    var jugadaId = ""
    var ganadorId = ""

    var playerOneName = ""
    var playerTwoName = ""
    var nameJugador = ""

    lateinit var jugada: Jugada
    var listenerJugada: ListenerRegistration? = null
    lateinit var firebaseUser: FirebaseUser

    lateinit var objectUser1: User
    lateinit var objectUser2: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action",null).show()
        }

        initViews()
        initGame()

    }

    private fun  initViews(){
        casillas = arrayListOf()
        casillas.add(findViewById(R.id.imageView0))
        casillas.add(findViewById(R.id.imageView1))
        casillas.add(findViewById(R.id.imageView2))
        casillas.add(findViewById(R.id.imageView3))
        casillas.add(findViewById(R.id.imageView4))
        casillas.add(findViewById(R.id.imageView5))
        casillas.add(findViewById(R.id.imageView6))
        casillas.add(findViewById(R.id.imageView7))
        casillas.add(findViewById(R.id.imageView8))
    }

    private fun initGame() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        dbs = FirebaseFirestore.getInstance()
        uid = firebaseUser.uid

        val extras: Bundle? = intent.extras
        jugadaId = extras!!.getString("jugadaId").toString()


    }

    override fun onStart() {
        super.onStart()
        jugadaListener()
    }

    private fun jugadaListener(){
        listenerJugada = dbs.collection("jugadas")
            .document(jugadaId)
            .addSnapshotListener { snapshot, error ->
                if(error != null){
                    toast("Error al obtener los datos de las jugadas")
                    return@addSnapshotListener
                }

                val source: String = if( snapshot != null && snapshot.metadata.hasPendingWrites() ) "local" else "server"

                if( snapshot!!.exists() && source == "server"){

                    //extrallendo datos de firestores a un objeto jugada
                    val a1 = (snapshot.data?.get("jugadorUnoId") ?:"" ) as String
                    val a2 = (snapshot.data?.get("jugadorDosId")  ?:"" ) as String
                    val a3 =  snapshot.data?.get("selectedCells").toString()
                    val items = a3.replace("\\[".toRegex(), "").replace("\\]".toRegex(), "").split(", ").toTypedArray()
                    val res: ArrayList<Int> = arrayListOf()
                    for (i in items.indices) {
                        res.add(Integer.parseInt(items[i]))
                    }
                    val a4 = snapshot.data?.get("turnoJugadorUno") as Boolean
                    val a5 = (snapshot.data?.get("ganadorId") ?:"" ) as String
                    val a6 = snapshot.data?.get("created") as Timestamp
                    val a7: Boolean = snapshot.data?.get("goOut") as Boolean
                    jugada = Jugada(a1, a2, res, a4, a5, a6, a7)

                    if(playerOneName.isEmpty() || playerTwoName.isEmpty()){
                        //obtener nombres de los jugadores
                        getPlayerNames()
                    }
                    updateUI()
                }
                UpdatePlayerUI()
            }
    }

    private fun updateUI() {
        for( i in 0 .. 8){
            val casilla = jugada.selectedCells[i]
            val ivCasillaActual: ImageView = casillas[i]
            when(casilla){
                0 -> ivCasillaActual.setImageResource(R.drawable.ic_empty_square)
                1 -> ivCasillaActual.setImageResource(R.drawable.ic_player_one)
                2 -> ivCasillaActual.setImageResource(R.drawable.ic_player_two)
            }

        }
    }

    private fun getPlayerNames(){
        //nombre jugador 1
        dbs.collection("users")
            .document(jugada.jugadorUnoId)
            .get()
            .addOnSuccessListener {
                val a1 = it.data?.get("uid") as String
                val a2 = it.data?.get("username") as String
                val a3 =  it.data?.get("imgProfile") as String
                val a4 =  it.data?.get("ngames").toString()
                val a5 =  it.data?.get("point").toString()
                Log.e("errorrrrrr:","$a4, $a5")
                val a6 = Integer.parseInt(a4)
                val a7 = Integer.parseInt(a5)
                //objectUser1 = it.toObject(User::class.java)!!
                objectUser1 = User(a1,a2,a3,a6,a7)
                playerOneName = it.get("username").toString()
                textViewPlayer1.text = playerOneName
                if(jugada.jugadorUnoId == uid){
                    nameJugador = playerOneName
                }
            }
        //nombre jugador 2
        dbs.collection("users")
            .document(jugada.jugadorDosId)
            .get()
            .addOnSuccessListener {
                val a1 = it.data?.get("uid") as String
                val a2 = it.data?.get("username") as String
                val a3 =  it.data?.get("imgProfile") as String
                val a4 =  it.data?.get("ngames").toString()
                val a5 =  it.data?.get("point").toString()
                Log.e("errorrrrrr:","$a4, $a5")
                val a6 = Integer.parseInt(a4)
                val a7 = Integer.parseInt(a5)
                //objectUser1 = it.toObject(User::class.java)!!
                objectUser2 = User(a1,a2,a3,a6,a7)
                playerTwoName = it.get("username").toString()
                textViewPlayer2.text = playerTwoName
                if(jugada.jugadorDosId == uid){
                    nameJugador = playerTwoName
                }
            }
    }


    override fun onStop() {
        listenerJugada?.remove()
        super.onStop()
    }


    fun casillaSeleccionada(view: View) {
        if(jugada.ganadorId.isNotEmpty()){
            toast("La partida ha terminado")
        }else {
            if(jugada.turnoJugadorUno && jugada.jugadorUnoId == uid){
                //jungando jugador 1
                actualizarJugada(view.tag.toString())
            }else if(!jugada.turnoJugadorUno && jugada.jugadorDosId == uid) {
                //jugando jugador 2
                actualizarJugada(view.tag.toString())
            } else {
                toast("No es tu turno aún")
            }
        }
    }

    private fun actualizarJugada(mov: String) {
        val posicionCasilla = Integer.parseInt(mov)

        if(jugada.selectedCells[posicionCasilla] == 0){
            if(jugada.turnoJugadorUno){
                casillas[posicionCasilla].setImageResource(R.drawable.ic_player_one)
                jugada.selectedCells[posicionCasilla] = 1
            }else {
                casillas[posicionCasilla].setImageResource(R.drawable.ic_player_two)
                jugada.selectedCells[posicionCasilla] = 2
            }

            when {
                solucionEncontrada() -> {
                    jugada.ganadorId = uid
                    Log.e("ganador es","$uid")
                    toast("Hay solución")
                }
                empateEncontrado() -> {
                    jugada.ganadorId = "EMPATE"
                    toast("Hay un empate")
                }
                else -> {
                    cambioTurno()
                }
            }

            //mandar la jugada a firestore
            dbs.collection("jugadas")
                .document(jugadaId)
                .set(jugada)
                .addOnSuccessListener {
                    Log.e("sucess","se guardo la jugada")
                }
                .addOnFailureListener {
                    Log.e("error","no se guardo la jugada")
                }
        }else {
            toast("Seleccione una casilla libre")
        }
    }

    private fun UpdatePlayerUI(){
        if(jugada.turnoJugadorUno){
            textViewPlayer1.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            textViewPlayer2.setTextColor(resources.getColor(R.color.grey))
        }else {
            textViewPlayer1.setTextColor(resources.getColor(R.color.grey))
            textViewPlayer2.setTextColor(resources.getColor(R.color.red))
        }
        if(jugada.ganadorId.isNotEmpty()){
            ganadorId = jugada.ganadorId
            cuadroDialogoFin()
        }
    }

    private fun empateEncontrado(): Boolean {
        var empate = false

        var casillaLibre = false
        for(i in 0 .. 8){
            if(jugada.selectedCells[i] == 0){
                casillaLibre = true
                break
            }
        }
        if(!casillaLibre)
            empate = true

        return empate
    }

    private fun solucionEncontrada(): Boolean{
        var solucion = false

        val cellsBusy: ArrayList<Int> = jugada.selectedCells
        if(cellsBusy[0] == cellsBusy[1] && cellsBusy[1] == cellsBusy[2] && cellsBusy[2] != 0){
            solucion = true
        }else if(cellsBusy[3] == cellsBusy[4] && cellsBusy[4] == cellsBusy[5] && cellsBusy[5] != 0) {
            solucion = true
        }else if(cellsBusy[6] == cellsBusy[7] && cellsBusy[7] == cellsBusy[8] && cellsBusy[8] != 0) {
            solucion = true
        }else if(cellsBusy[0] == cellsBusy[3] && cellsBusy[3] == cellsBusy[6] && cellsBusy[6] != 0) {
            solucion = true
        }else if(cellsBusy[1] == cellsBusy[4] && cellsBusy[4] == cellsBusy[7] && cellsBusy[7] != 0) {
            solucion = true
        }else if(cellsBusy[2] == cellsBusy[5] && cellsBusy[5] == cellsBusy[8] && cellsBusy[8] != 0) {
            solucion = true
        }else if(cellsBusy[0] == cellsBusy[4] && cellsBusy[4] == cellsBusy[8] && cellsBusy[8] != 0) {
            solucion = true
        }else if(cellsBusy[2] == cellsBusy[4] && cellsBusy[4] == cellsBusy[6] && cellsBusy[6] != 0) {
            solucion = true
        }

        return solucion
    }

    private fun cambioTurno(){
        jugada.turnoJugadorUno = !jugada.turnoJugadorUno
    }

    private fun toast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    fun cuadroDialogoFin(){

        //1.inicialización
        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }

        val dialogOver: View = layoutInflater.inflate(R.layout.dialog_over,null)


        // Obtenemos las referencias a los View components de nuestro layout
        var txtPoints: TextView = dialogOver.findViewById(R.id.textPoints);
        var txtInfo: TextView = dialogOver.findViewById(R.id.textInfo);
        var gameOverAni: LottieAnimationView = dialogOver.findViewById(R.id.animationOver);

        //2.establecer caracteristicas al generador de dialogo
        builder?.setTitle("Game Over")
        builder?.setCancelable(false)

        //aplicando elactivity que será como dialogo
        builder?.setView(dialogOver)
        when (ganadorId) {
            "EMPATE" -> {
                actualizarRanking(1)
                txtInfo.text = "¡$nameJugador has empatado el juego!"
                txtPoints.text = "+1 Punto"
            }
            uid -> {
                actualizarRanking(3)
                txtInfo.text = "¡$nameJugador has ganado el juego"
                txtPoints.text = "+3 Punto"
            }
            else -> {
                actualizarRanking(0)
                txtInfo.text = "¡$nameJugador has perdido el juego¡"
                txtPoints.text = "+0 Punto"
                gameOverAni.setAnimation("down_animation.json")
            }
        }

        //iniciar la animacion ya que auto play es falso app:lottie_autoPlay="false"
        //animationOver.repeatCount = 0    //solo funciona si app:lottie_loop="false"
        gameOverAni.playAnimation()

        //agregando botones al dialogo
        builder?.apply {
            setPositiveButton("Salir") { dialog, id ->
                finish()
            }
        }
        //crear el dialogo
        val dialog: AlertDialog? = builder?.create()

        //lanzar el dialogo
        dialog?.show()
    }

    private fun actualizarRanking(puntos: Int) {
        val jugadorActualizar: User
        if(nameJugador == objectUser1.username){
            objectUser1.point = (objectUser1.point + puntos)
            objectUser1.nGames = (objectUser1.nGames + 1)
            jugadorActualizar = objectUser1
        } else {
            objectUser2.point = (objectUser2.point + puntos)
            objectUser2.nGames = (objectUser2.nGames + 1)
            jugadorActualizar = objectUser2
        }

        dbs.collection("users")
            .document(uid)
            .set(jugadorActualizar)
            .addOnSuccessListener {
                Log.e("Succes: ","Se actualizo los usuarios con sus puntos")
            }
    }

}
