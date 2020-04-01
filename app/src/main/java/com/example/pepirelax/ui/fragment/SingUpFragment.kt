package com.example.pepirelax.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.pepirelax.R
import kotlinx.android.synthetic.main.fragment_sing_up.*

class SingUpFragment : Fragment() {

    private var listener: FragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sing_up, container, false)
    }

    fun onButtonPressedUp(username: String, email: String, password: String, valid: Boolean){
        listener?.fragmentInteractionUp(username,email,password,valid)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractionListener){
            listener = context
        }else {
            throw RuntimeException(context.toString() + "must implement FragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface FragmentInteractionListener{
        fun fragmentInteractionUp(username: String, email: String, password: String, valid: Boolean)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        suBtnRegister.setOnClickListener {
            var valid = true

            val username = suInputRegName.text.toString()
            if (TextUtils.isEmpty(username)) {
                suInputRegName.error = "Required."
                valid = false
            } else {
                suInputRegName.error = null
            }

            val email = suInputRegEmail.text.toString()
            if (TextUtils.isEmpty(email)) {
                suInputRegEmail.error = "Required."
                valid = false
            } else {
                suInputRegEmail.error = null
            }

            val password = suInputPassword.text.toString()
            if (TextUtils.isEmpty(password)) {
                suInputPassword.error = "Required."
                valid = false
            } else {
                suInputPassword.error = null
            }

            onButtonPressedUp(suInputRegName.text.toString(),suInputRegEmail.text.toString(),suInputPassword.text.toString(),valid)
        }
    }

}
