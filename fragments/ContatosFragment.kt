package com.heliobuzato.whatsappfirebase.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.heliobuzato.whatsappfirebase.activities.MensagemsActivity
import com.heliobuzato.whatsappfirebase.adapters.ContatosAdapter
import com.heliobuzato.whatsappfirebase.databinding.FragmentContatosBinding
import com.heliobuzato.whatsappfirebase.model.Usuario
import com.heliobuzato.whatsappfirebase.utils.Constantes


class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding

    private lateinit var eventoSnapshot: ListenerRegistration

    private lateinit var  contatosAdapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val fireStorage by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )

        contatosAdapter = ContatosAdapter{ usuario ->
            val intent = Intent(context, MensagemsActivity::class.java)
            intent.putExtra("dadosDestinatario", usuario)
            //intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        eventoSnapshot = fireStorage
            .collection(Constantes.USUARIOS)
            .addSnapshotListener { querySnapshot, error ->

                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents
                documentos?.forEach { documentSnapshot ->

                    val idUsuarioLogado = firebaseAuth.currentUser?.uid
                    val usuario = documentSnapshot.toObject(Usuario::class.java)

                   if (usuario != null && idUsuarioLogado != null){
                       if (idUsuarioLogado != usuario.id){
                           listaContatos.add(usuario)
                       }
                   }
                }

                //Lista de contatos (atualizar o RecyclerView)
                if(listaContatos.isNotEmpty()){
                    contatosAdapter.adicionarLista(listaContatos)
                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }


}