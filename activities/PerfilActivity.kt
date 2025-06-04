package com.heliobuzato.whatsappfirebase.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.heliobuzato.whatsappfirebase.R
import com.heliobuzato.whatsappfirebase.databinding.ActivityPerfilBinding
import com.heliobuzato.whatsappfirebase.utils.exibirMensagem
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val fireStorage by lazy {
        FirebaseFirestore.getInstance()
    }


    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val gerencidorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.imagePerfil.setImageURI(uri)
            upLoadImagemStorage(uri)

        } else {
            exibirMensagem("nenhuma imagem selecionada")
        }
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
        solicitarPermissoes()
        inicializarEventosClick()


    }

    override fun onStart() {
        super.onStart()
        recuperarDadosInciais()
    }

    private fun recuperarDadosInciais() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario != null){
            fireStorage
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val dadosUsuarios = documentSnapshot.data
                    if (dadosUsuarios != null){

                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String

                        binding.editNomePerfil.setText(nome)
                        if (foto.isNotEmpty()){
                            Picasso
                                .get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }

                    }

                }
        }else{

        }
    }

    private fun upLoadImagemStorage(uri: Uri) {

        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {

            //fotos -> usuario -> idUasuario -> perfil.jpg
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener {task ->

                    exibirMensagem("Sucesso ao fazer upload da imagem")
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->

                        val dados = mapOf(
                            "foto" to url.toString()
                        )
                        atualizarDadosPerfil(idUsuario, dados)

                    }

                }.addOnFailureListener {
                    exibirMensagem("Erro ao fazer upload da imagem")
                }


        }
    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {

        fireStorage
            .collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao atualizar dados")
            }.addOnFailureListener {
                exibirMensagem("Erro ao atualizar dados")
            }

    }

    private fun inicializarEventosClick() {
        binding.fabSelecionar.setOnClickListener {
            if (temPermissaoGaleria) {
                gerencidorGaleria.launch("image/*")


            } else {
                exibirMensagem("Não tem permisão para acessar agleria")
                solicitarPermissoes()
            }
        }
        binding.btnAtualizar.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if (nomeUsuario.isNotEmpty()){

                val idUsuario = firebaseAuth.currentUser?.uid
                if (idUsuario != null){
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil(idUsuario, dados)
                }


            }else{

                exibirMensagem("Preecha o nome para atualizar")
            }

        }
    }

    private fun solicitarPermissoes() {
        //verificar se usuario tem permissao
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        //lista de permissoes negadas
        val listaPermissaoNegada = mutableListOf<String>()
        if (!temPermissaoCamera)
            listaPermissaoNegada.add(Manifest.permission.CAMERA)
        if (!temPermissaoGaleria)
            listaPermissaoNegada.add(Manifest.permission.READ_MEDIA_IMAGES)

        if (listaPermissaoNegada.isNotEmpty()) {

            //Solicitar multiplas permissoes
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->
                temPermissaoCamera = permissoes[Manifest.permission.CAMERA]
                    ?: temPermissaoCamera
                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: temPermissaoGaleria
            }
            gerenciadorPermissoes.launch(listaPermissaoNegada.toTypedArray())

        }
    }

    private fun inicializarToolbar() {
        var toolbar = binding.includePerfilToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}