package com.example.pepirelax.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.example.pepirelax.R
import com.example.pepirelax.model.Jugada
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var firebaseUser: FirebaseUser
    lateinit var uid: String    //ide del jugador actual
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var dbs: FirebaseFirestore
    private var listenerRegistration: ListenerRegistration? = null

    var jugadaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val auth = FirebaseAuth.getInstance()

        initProgresBar()
        initFirebase()
        eventos()
    }

    private fun initFirebase(){
        firebaseAuth = FirebaseAuth.getInstance()
        dbs = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        uid = firebaseUser.uid
    }

    private fun eventos() {
        buttonJugar.setOnClickListener {
            changeMenuVisibility(false)
            buscarPartida()
        }

        buttonRanking.setOnClickListener {

        }
    }

    private fun buscarPartida(){
        textViewJugadas.text = "Buscando una Partida Libre"
        animation_view.playAnimation()

        dbs.collection("jugadas")
            .whereEqualTo("jugadorDosId","")
            .get()
            .addOnCompleteListener {
                if(it.result?.size() == 0){
                    crearNuevaJugada()
                }else {
                    var encontrado: Boolean = false
                    for(docJugada: DocumentSnapshot in it.result?.documents!!){
                        if(docJugada.get("jugadorUnoId") != uid){
                            encontrado = true
                            //val docJugada: DocumentSnapshot = it.result?.documents!![0] //observacion de null
                            jugadaId = docJugada.id
                            /*
                            val a1 = (docJugada.data?.get("jugadorUnoId") ?:"" ) as String
                            val a2 = (docJugada.data?.get("JugadorDosId") ?:"" ) as String
                            val a3: String =  docJugada.data?.get("selectedCells").toString()

                            val items = a3.replace("\\[".toRegex(), "").replace("\\]".toRegex(), "").split(", ").toTypedArray()
                            val res: ArrayList<Int> = arrayListOf()
                            for (i in items.indices) {
                                res.add(Integer.parseInt(items[i]))
                            }

                            val a4 = docJugada.data?.get("turnoJugadorUno") as Boolean
                            val a5 = (docJugada.data?.get("ganadorId") ?:"" ) as String
                            val a6 = docJugada.data?.get("created") as Timestamp
                            val a7: Boolean = docJugada.data?.get("goOut") as Boolean

                            val jugada: Jugada = Jugada(a1, a2, res, a4, a5, a6, a7)

                            //val jugada: Jugada? = docJugada.toObject(Jugada::class.java) //operacion posible null
                            jugada.jugadorDosId = uid*/
                            dbs.collection("jugadas")
                                .document(jugadaId)
                                .update("jugadorDosId",uid)
                                .addOnSuccessListener {
                                    textViewJugadas.text = "¡Partida encontrada! Se iniciará la partida"

                                    animation_view.repeatCount = 0
                                    animation_view.setAnimation("checked_animation.json")
                                    animation_view.playAnimation()

                                    val handler = Handler()
                                    val r: Runnable = Runnable {
                                        startGame()
                                    }
                                    handler.postDelayed(r,2000)
                                }
                                .addOnFailureListener {
                                    changeMenuVisibility(true)
                                    Toast.makeText(this,"Error al encontrar partida",Toast.LENGTH_LONG)
                                }

                            /*
                            dbs.collection("jugadas")
                                .document(jugadaId)
                                .set(jugada)
                                .addOnSuccessListener {
                                    startGame()
                                }
                                .addOnFailureListener {
                                    changeMenuVisibility(true)
                                    Toast.makeText(this,"Error al encontrar partida",Toast.LENGTH_LONG)
                                }*/
                        }
                        break
                    }
                    if(!encontrado){
                        crearNuevaJugada()
                    }

                }
            }
    }

    private fun crearNuevaJugada(){
        textViewJugadas.text = "Creando Nueva Jugada ..."
        val nuevaJugada = Jugada(uid)

        dbs.collection("jugadas")
            .add(nuevaJugada)
            .addOnSuccessListener {
                jugadaId = it.id
                esperarJugador()
                //creada la jugada solo queda esperar al jugador 2
            }
            .addOnFailureListener {
                changeMenuVisibility(true)
                Toast.makeText(this,"Error al crear la nueva Jugada",Toast.LENGTH_LONG)
            }
    }

    private fun esperarJugador() {
        textViewJugadas.text = "Esperando Otro Jugador ..."

        listenerRegistration = dbs.collection("jugadas")
            .document(jugadaId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) { //&& snapshot.exists()
                    if(snapshot.get("jugadorDosId") != ""){
                        textViewJugadas.text = "¡Ingreso un retador! Comienza el juego"

                        animation_view.repeatCount = 0
                        animation_view.setAnimation("checked_animation.json")
                        animation_view.playAnimation()

                        val handler = Handler()
                        val r: Runnable = Runnable {
                            startGame()
                        }
                        handler.postDelayed(r,1500)
                    }
                }
            }
    }

    private fun startGame(){
        textViewJugadas.text = "Creando una jugada nueva ..."
        listenerRegistration?.remove()
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("jugadaId",jugadaId) //creaba una constante
        startActivity(intent)
        jugadaId = ""
    }

    private fun initProgresBar(){
        progresBarJugadas.isIndeterminate = true
        textViewJugadas.text = "Cargando ..."

        changeMenuVisibility(true)
    }

    private fun changeMenuVisibility(showMenu: Boolean){
        if(showMenu){
            layoutProgressBar.visibility = View.GONE
            layoutMenuJuego.visibility = View.VISIBLE
        }else{
            layoutProgressBar.visibility = View.VISIBLE
            layoutMenuJuego.visibility = View.GONE
        }
    }

    override fun onStop() {
        listenerRegistration?.remove()
        if(jugadaId != ""){
            dbs.collection("jugadas")
                .document(jugadaId)
                .delete()
                .addOnCompleteListener {
                    jugadaId = ""
                }
        }
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if(jugadaId != ""){
            changeMenuVisibility(false)
            esperarJugador()
        }else {
            changeMenuVisibility(true)
        }
    }

}
