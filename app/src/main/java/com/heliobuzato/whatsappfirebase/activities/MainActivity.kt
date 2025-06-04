package com.heliobuzato.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.heliobuzato.whatsappfirebase.R
import com.heliobuzato.whatsappfirebase.adapters.ViewPageAdapter
import com.heliobuzato.whatsappfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarToolbar()
        inicializarNavegacaoAbas()




    }

    private fun inicializarNavegacaoAbas() {

        val tabLayout = binding.tabLayoutPrincipal
        val viewPage = binding.viewPagerPrincipal

        //Adapter
        val abas = listOf("CONVERSAS", "CONTATOS")
        viewPage.adapter =ViewPageAdapter(
           abas, supportFragmentManager, lifecycle
        )
        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPage){aba, posicao ->
            aba.text = abas[posicao]
        }.attach()
    }

    private fun inicializarToolbar() {

        var toolbar = binding.includeMainToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
            //setDisplayHomeAsUpEnabled(true)
        }
        addMenuProvider(
            object: MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){

                        R.id.item_perfil -> {
                            startActivity(
                                Intent(applicationContext, PerfilActivity::class.java)
                            )

                        }
                        R.id.item_sair -> {
                            deslogarUsuario()

                        }

                    }
                    return true
                }

            }
        )
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Delogar")
            .setMessage("Deseja reaolmente sair?")
            .setNegativeButton("Não"){dialog, posicao ->}
            .setPositiveButton("Sim"){dialog, posicao ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }.create().show()

    }
}