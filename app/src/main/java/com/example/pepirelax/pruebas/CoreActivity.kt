package com.example.pepirelax.pruebas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.pepirelax.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_core.*
import java.util.*

class CoreActivity : AppCompatActivity() {

    private val adapter by lazy { ControllerPage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        //toolbar importacion
        setSupportActionBar(findViewById(R.id.myToolbar))

        //tabLayout incluido
        pager.adapter = adapter

        TabLayoutMediator(tab_layout,pager) { tab, position ->
            when(position){
                0   ->  {   tab.text = "Juegos"
                            //tab.setIcon(R.drawable.ic_home_black_24dp)
                        }
                1 -> tab.text = "Amistades"
                2 -> tab.text = "Otros"
            }
        }.attach()

        /* No es necesario
        tab_layout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                pager.currentItem = tab_layout.selectedTabPosition
                if (p0 != null) {
                    if(p0.position == 0)
                        adapter.notifyDataSetChanged()
                    if(p0.position == 1)
                        adapter.notifyDataSetChanged()
                    if(p0.position == 2)
                        adapter.notifyDataSetChanged()

                }
            }
        })*/

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_sing,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menuProfile -> {
            true
        }

        R.id.menuSetting -> {
            true
        }

        else -> super.onOptionsItemSelected(item)

    }


}
