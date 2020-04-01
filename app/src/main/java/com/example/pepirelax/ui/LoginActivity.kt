package com.example.pepirelax.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pepirelax.R
import com.example.pepirelax.model.User
import com.example.pepirelax.ui.fragment.SingInFragment
import com.example.pepirelax.ui.fragment.SingUpFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity(),
                    SingInFragment.FragmentInteractionListener,
                    SingUpFragment.FragmentInteractionListener {
    //variable firebase autentificación
    lateinit var auth: FirebaseAuth

    //variables fragment sing in / sing up
    lateinit var singInFragment: SingInFragment
    lateinit var singUpFragment: SingUpFragment

    var errorl = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Acciones de Fragments
        singInFragment = SingInFragment()
        singUpFragment = SingUpFragment()

        loadFragment(singInFragment)

        btnSingUp.setOnClickListener {
            loadFragment(singUpFragment)
        }

        btnSingIn.setOnClickListener {
            loadFragment(singInFragment)
        }

        //Firebase inicialización
        auth = FirebaseAuth.getInstance()

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHeader,fragment)
            //addToBackStack(null) permite recordar los fragment cargados anteriores
            commit()
        }
    }

    //funciones de la interfaz para obtener datos de los fragment
    override fun fragmentInteraction(email: String, password: String, vail: Boolean) {
        signIn(email,password,vail)
    }

    override fun fragmentInteractionUp(username: String, email: String, password: String, valid: Boolean) {
        createAccount(username,email,password,valid)
    }

    //verificar si el usuario esta activo
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    //Crear cuenta
    private fun createAccount(username: String, email: String, password: String, valid: Boolean) {
        Log.d(TAG, "createAccount:$email")
        if (!valid) {
            toast("Ingrese su email o contraseña")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success ${it.result?.user?.uid}")
                    val user = auth.currentUser //???????????????'
                    registerDatabaseUser(username)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    toast("Authentication failed.")
                }
            }
    }

    private fun registerDatabaseUser(username: String){
        val uid = FirebaseAuth.getInstance().uid?:""
        val dbs= FirebaseFirestore.getInstance()
        //val dbu = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val imgProfile = ""
        val user = User(uid,username,imgProfile)

        /* database
        dbu.setValue(user)
            .addOnSuccessListener {
                Log.w("Register: ", "Success Full")
                auth.signOut()
                loadFragment(singInFragment)
            }*/

        dbs.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.w("Register: ", "Success Full")
                auth.signOut()
                loadFragment(singInFragment)
            }

    }

    //Ingresar a cuenta
    private fun signIn(email: String, password: String, valid: Boolean) {
        Log.d(TAG, "signIn:$email")
        if (!valid) {
            return
        }
        errorl = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    toast("Authentication failed.")
                    updateUI(null)
                }
            }
    }

    //accion despues de ingresar login
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this,HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            if(errorl)
                toast("error al logearse")
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }


    fun toast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }


}
