package com.heliobuzato.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.heliobuzato.whatsappfirebase.R
import com.heliobuzato.whatsappfirebase.databinding.ActivityLoginBinding
import com.heliobuzato.whatsappfirebase.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

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

        inicializarEventosClick()
        //firebaseAuth.signOut() //deslogar usario para teste

    }

    override fun onStart() {
        super.onStart()
        verificarUsarioLogado()


    }

    private fun verificarUsarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if(usuarioAtual != null){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun inicializarEventosClick() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }

        binding.buttonLogar.setOnClickListener {

            if (validarCampos()){
                logarUsuario()
            }

        }

    }

    private fun logarUsuario() {

        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener { resultado ->
            if (resultado.isSuccessful){
                exibirMensagem("Logado com sucesso")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java))
                finish()


            }

        }.addOnFailureListener { erro ->
            try {
                throw erro

            }catch ( erroUsuarioInvalido: FirebaseAuthInvalidUserException){
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("Usuario não existe")
            }catch ( erroCredenciaisInvalido: FirebaseAuthInvalidCredentialsException){
                erroCredenciaisInvalido.printStackTrace()
                exibirMensagem("Email ou senha incorreta")
            }catch (e: Exception){
                e.printStackTrace()
                //exibirMensagem("Erro ao fazer login: ${e.message}")
                exibirMensagem("Email ou senha incorreta")
            }

        }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (email.isNotEmpty()){ //Não esta vazio
            binding.textInputLayoutLoginEmail.error = null
            if (senha.isNotEmpty()){
                binding.textInputLayoutLoginSenha.error = null
                return true

            }else{
                binding.textInputLayoutLoginSenha.error = "Preencher a senha"
                return false

            }

        }else{ //esta vazio
            binding.textInputLayoutLoginEmail.error = "Preencha seu e-mail"
            return false

        }

    }
}