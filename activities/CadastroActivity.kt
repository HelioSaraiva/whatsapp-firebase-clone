package com.heliobuzato.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.heliobuzato.whatsappfirebase.R
import com.heliobuzato.whatsappfirebase.databinding.ActivityCadastroBinding
import com.heliobuzato.whatsappfirebase.model.Usuario
import com.heliobuzato.whatsappfirebase.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStorage by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String



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
        inicializarEventosClick()

    }

    private fun validarCampos(): Boolean {
        nome = binding.editTextNome.text.toString()
        email = binding.editTextEmail.text.toString()
        senha = binding.editTextSenha.text.toString()


        if (nome.isNotEmpty()){
            binding.textInputLayoutNome.error = null
            if (email.isNotEmpty()){
                binding.textInputLayoutEmail.error = null
                if (senha.isNotEmpty()){
                    binding.textInputLayoutSenha.error = null
                    return true

                }else{
                    binding.textInputLayoutSenha.error = "Preecha seu e-mail!!"
                    return false

                }

            }else{
                binding.textInputLayoutEmail.error = "Preecha seu e-mail!!"
                return false

            }

        }else{
            binding.textInputLayoutNome.error = "Preecha seu nome!!"
            return false
        }
    }

    private fun inicializarEventosClick() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos())
                cadastrarUsuario(nome, email, senha)
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email,senha
        ).addOnCompleteListener { resultado ->
           if (resultado.isSuccessful){

               //Salvar os dados do usuario do Fire Store
               /*
               id, nome, email, foto(futuro)
               **/
               val idUsuario = resultado.result.user?.uid
               if (idUsuario != null){
                   val usuario = Usuario(idUsuario, nome, email)
                   salvarUsuarioFirestore(usuario)
               }



           }

        }.addOnFailureListener { erro ->
            try {
                throw erro

            }catch ( erroSenhaFraca: FirebaseAuthWeakPasswordException ){
                erroSenhaFraca.printStackTrace()
                exibirMensagem("Senha fraca, digite outra com letra, numeros e caracteres especiais")
            }catch ( erroUsuarioExistente: FirebaseAuthUserCollisionException ){
                erroUsuarioExistente.printStackTrace()
                exibirMensagem("E-mail já pertence a outro usuario")
            }catch ( erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException ){
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem("E-mail inválido, digite um outro e-mail!!")
            }

        }

    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        fireStorage
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {

                startActivity(
                    Intent(applicationContext, MainActivity::class.java))
                finish()

                exibirMensagem("Sucesso ao fazer seu cadastro")
            }.addOnFailureListener {
                exibirMensagem("Erro ao fazer seu cadastro")
            }



    }


    private fun inicializarToolbar() {
        var toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)

        }
    }
}