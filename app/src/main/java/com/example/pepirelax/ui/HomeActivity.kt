package com.example.pepirelax.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.pepirelax.R
import com.example.pepirelax.model.Jugada
import com.example.pepirelax.ui.fragment.GameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    lateinit var firebaseUser: FirebaseUser
    lateinit var uid: String
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var dbs: FirebaseFirestore

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
        dbs.collection("jugadas")
            .whereEqualTo("jugadorDosId","")
            .get()
            .addOnCompleteListener {
                if(it.result?.size() == 0){

                }else {
                    val docJugada = it.result?.documents?.get(0)
                    jugadaId = docJugada!!.id
                    val jugada = docJugada.toObject(Jugada::class.java)
                    jugada?.jugadorDosId = uid

                    dbs.collection("jugadas")
                        .document(jugadaId)
                        .set(jugada!!)
                        .addOnSuccessListener {
                            startGame()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"Error al encontrar partida",Toast.LENGTH_LONG)
                        }
                }
            }
    }

    private fun startGame(){
        var intent = Intent(this,GameActivity::class.java)
        intent.putExtra("jugadaId",jugadaId)
        startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        changeMenuVisibility(true)
    }

}
