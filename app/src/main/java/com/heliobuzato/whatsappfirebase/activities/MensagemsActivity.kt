package com.heliobuzato.whatsappfirebase.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.heliobuzato.whatsappfirebase.R
import com.heliobuzato.whatsappfirebase.adapters.MensagensAdapter
import com.heliobuzato.whatsappfirebase.databinding.ActivityMensagemsBinding
import com.heliobuzato.whatsappfirebase.model.Conversa
import com.heliobuzato.whatsappfirebase.model.Usuario
import com.heliobuzato.whatsappfirebase.utils.Constantes
import com.heliobuzato.whatsappfirebase.utils.Mensagem
import com.heliobuzato.whatsappfirebase.utils.exibirMensagem
import com.squareup.picasso.Picasso

class MensagemsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagemsBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStorage by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var listenerRegistration: ListenerRegistration

    private var dadosDestinatario: Usuario? = null

    private var dadosUsuarioRemetente: Usuario? = null //usuario logado

    private lateinit var mensagensAdapter: MensagensAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        recuperarDadosUsuarios()
        inicializarToolbar()
        inicializarEventosClick()
        inicializarRecyclerView()
        inicializarListeners()

    }

    private fun inicializarRecyclerView() {
        with(binding){
            mensagensAdapter = MensagensAdapter()
          rvMensagem.adapter = mensagensAdapter
          rvMensagem.layoutManager = LinearLayoutManager(applicationContext)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {

            listenerRegistration =  fireStorage
                .collection(Constantes.MENSAGENS)
                .document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener{querySnapshot, erro ->

                    if(erro != null){
                        exibirMensagem("Erro ao recuperar mensagens")
                    }

                    val listaMensagem = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->

                        val mensagen = documentSnapshot.toObject(Mensagem::class.java)
                        if (mensagen != null){
                            listaMensagem.add(mensagen)
                            //Log.i("L_mensagens", mensagen.mensagem)
                        }

                    }
                    //Lista
                    if (listaMensagem.isNotEmpty()){
                        //Carregar no adpter
                        mensagensAdapter.adicionarLista(listaMensagem)
                    }

                }


        }
    }

    private fun inicializarEventosClick() {

        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }


    }

    private fun salvarMensagem(textoMensagem: String) {

        if (textoMensagem.isNotEmpty()) {
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
                val mensagem = Mensagem(
                    idUsuarioRemetente, textoMensagem
                )


                //Salvar para o rementente
                SalavarMensagemFireStore(idUsuarioRemetente, idUsuarioDestinatario, mensagem)

                //Foto e nome remetente
                val convesaRemetente = Conversa(
                    idUsuarioRemetente, idUsuarioDestinatario,
                    dadosDestinatario!!.foto, dadosDestinatario!!.nome,
                    textoMensagem
                )
                salvarConversaFirestore(convesaRemetente)


                //Salvar mesma mensagem para o detinatario
                SalavarMensagemFireStore(idUsuarioDestinatario, idUsuarioRemetente, mensagem)

                //Foto e nome detinatario
                val convesaDestinatario = Conversa(
                    idUsuarioDestinatario, idUsuarioRemetente,
                    dadosUsuarioRemetente!!.foto,dadosUsuarioRemetente!!.nome, //dados usuario logado
                    textoMensagem
                )
                salvarConversaFirestore(convesaDestinatario)

                binding.editMensagem.text?.clear()


            }

        }
    }

    private fun salvarConversaFirestore(conversa: Conversa) {

        fireStorage
            .collection(Constantes.CONVERSAS)
            .document(conversa.idUsuarioRemetente)
            .collection(Constantes.ULTIMAS_CONVERSAS)
            .document(conversa.idUsuarioDestinatario)
            .set(conversa)
            .addOnFailureListener {
                exibirMensagem("Erro ao salvar conversa")
            }

    }

    private fun SalavarMensagemFireStore(
        idUsuarioRemetente: String, idUsuarioDestinatario: String, mensagem: Mensagem
    ) {
        fireStorage
            .collection(Constantes.MENSAGENS)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar mensagem")
            }
    }

    private fun inicializarToolbar() {
        var toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null) {
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imgFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
            // Log.i("logMenu","dadosDestinatarioNome: ${dadosDestinatario!!.nome} foto ${dadosDestinatario!!.foto}")

        }
    }


    private fun recuperarDadosUsuarios() {

        //dadois usuario logado
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if (idUsuarioRemetente != null){
            fireStorage
                .collection(Constantes.USUARIOS)
                .document(idUsuarioRemetente)
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if (usuario != null){
                        dadosUsuarioRemetente = usuario

                    }

                }
        }

        //recuperando dados destinatario
        val extras = intent.extras
        if (extras != null) {

            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("dadosDestinatario", Usuario::class.java)
            } else {
                extras.getParcelable("dadosDestinatario")
            }
        }
    }
}