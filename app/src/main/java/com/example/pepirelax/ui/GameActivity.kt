package com.example.pepirelax.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import com.example.pepirelax.R
import com.example.pepirelax.model.Jugada
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.content_game.*

class GameActivity : AppCompatActivity() {

    lateinit var casillas: ArrayList<ImageView>

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var dbs: FirebaseFirestore

    lateinit var uid: String
    lateinit var jugadaId: String

    var playerOneName = ""
    var playerTwoName = ""

    lateinit var jugada: Jugada
    var listenerJugada: ListenerRegistration? = null
    lateinit var firebaseUser: FirebaseUser


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
                    Toast.makeText(this,"Error al obtener jugada",Toast.LENGTH_LONG)
                    return@addSnapshotListener
                }

                var source: String = if( snapshot != null && snapshot.metadata.hasPendingWrites() ) "local" else "server"

                if( snapshot!!.exists() && source == "server"){

                    val a1 = (snapshot.data?.get("jugadorUnoId") ?:"" ) as String
                    val a2 = (snapshot.data?.get("JugadorDosId") ?:"" ) as String
                    val a3: String =  snapshot.data?.get("selectedCells").toString()

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
                    //jugada = snapshot.onObject

                    if(playerOneName.isEmpty() || playerTwoName.isEmpty()){
                        getPlayerNames()
                    }
                }
            }
    }

    private fun getPlayerNames(){
        //nombre jugador 1
        dbs.collection("users")
            .document(jugada.jugadorUnoId)
            .get()
            .addOnSuccessListener {
                playerOneName = it.get("username").toString()
                textViewPlayer1.text = playerOneName
            }
        //nombre jugador 2
        dbs.collection("users")
            .document(jugada.jugadorDosId)
            .get()
            .addOnSuccessListener {
                playerTwoName = it.get("username").toString()
                textViewPlayer2.text = playerTwoName
            }
    }


    override fun onStop() {
        listenerJugada?.remove()
        super.onStop()
    }

    fun casillaSeleccionada(view: View) {
        if(!jugada.ganadorId.isEmpty()){
            Toast.makeText(this,"La partida ha terminado",Toast.LENGTH_LONG)
        }else {
            if(jugada.turnoJugadorUno && jugada.jugadorUnoId == uid){
                //jungando jugador 1
                actualizarJugada(view.tag.toString())
            }else if(!jugada.turnoJugadorUno && jugada.jugadorDosId == uid) {
                //jugando jugador 2

            } else {
                Toast.makeText(this,"No es tu turno aún",Toast.LENGTH_LONG)
            }
        }
    }

    private fun actualizarJugada(mov: String) {

    }


}
