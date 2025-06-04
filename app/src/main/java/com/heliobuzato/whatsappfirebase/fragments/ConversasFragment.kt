package com.heliobuzato.whatsappfirebase.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.heliobuzato.whatsappfirebase.R
import com.heliobuzato.whatsappfirebase.activities.MensagemsActivity
import com.heliobuzato.whatsappfirebase.adapters.ContatosAdapter
import com.heliobuzato.whatsappfirebase.adapters.ConversasAdapter
import com.heliobuzato.whatsappfirebase.databinding.FragmentContatosBinding
import com.heliobuzato.whatsappfirebase.databinding.FragmentConversasBinding
import com.heliobuzato.whatsappfirebase.model.Conversa
import com.heliobuzato.whatsappfirebase.model.Usuario
import com.heliobuzato.whatsappfirebase.utils.Constantes
import com.heliobuzato.whatsappfirebase.utils.exibirMensagem


class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding

    private lateinit var eventoSnapshot: ListenerRegistration


    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val fireStorage by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var  conversasAdapter: ConversasAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConversasBinding.inflate(
            inflater, container, false
        )

        conversasAdapter = ConversasAdapter{ conversa ->
            val intent = Intent(context, MensagemsActivity::class.java)

            val usuario = Usuario(
                id = conversa.idUsuarioDestinatario,
                nome = conversa.nome,
                foto = conversa.foto
            )

            intent.putExtra("dadosDestinatario", usuario)
            //intent.putExtra("origem", Constantes.ORIGEM_CONVERSA)
            startActivity(intent)
        }
        binding.rvConversas.adapter = conversasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

    private fun adicionarListenerConversas() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid //usuario logado
        if (idUsuarioRemetente != null) {
            eventoSnapshot = fireStorage
                .collection(Constantes.CONVERSAS)
                .document(idUsuarioRemetente)
                .collection(Constantes.ULTIMAS_CONVERSAS)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, erro ->

                    if (erro != null) {
                        activity?.exibirMensagem("Erro ao recuperar conversa")
                    }
                    val listaConversa = mutableListOf<Conversa>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->
                        val conversa = documentSnapshot.toObject(Conversa::class.java)
                        if (conversa != null) {
                            listaConversa.add(conversa)
                            Log.i("info_conversa", "${conversa.nome} - ${conversa.ultimaMensagem}")
                        }

                    }
                    if (listaConversa.isNotEmpty()) {
                        //Atualizar o adpter
                        conversasAdapter.adicionarLista(listaConversa)

                    }

                }
        }

    }

}