package com.example.pepirelax.pruebas

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ControllerPage (fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    companion object{
        private const val ARG_OBJECT = "object"
    }

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        val fragment = PageGamesFragment()
        when(position){
            0 -> {  fragment.arguments = Bundle().apply {
                        putInt(ARG_OBJECT, position + 1)
                        //se puede pasar mas argumentos
                    }
            }
            1   ->  {

            }
            2   ->  {

            }
        }


        return fragment
    }
}