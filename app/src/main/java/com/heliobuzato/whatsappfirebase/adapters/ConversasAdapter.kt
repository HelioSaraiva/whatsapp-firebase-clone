package com.heliobuzato.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.heliobuzato.whatsappfirebase.databinding.ItemContatosBinding
import com.heliobuzato.whatsappfirebase.databinding.ItemConversasBinding
import com.heliobuzato.whatsappfirebase.model.Conversa
import com.heliobuzato.whatsappfirebase.model.Usuario
import com.squareup.picasso.Picasso

class ConversasAdapter(
    private val onClick: (Conversa) -> Unit
): Adapter<ConversasAdapter.ConversasViewHolder>() {

    private var listaConversa = emptyList<Conversa>()
    fun adicionarLista(lista: List<Conversa>){
        listaConversa = lista
        notifyDataSetChanged()
    }

    inner class ConversasViewHolder(
        private val binding: ItemConversasBinding
    ): ViewHolder(binding.root){

        fun bind(conversa: Conversa){

            binding.textConversaNome.text = conversa.nome
            binding.textCorversaUltima.text = conversa.ultimaMensagem
            Picasso.get()
                .load(conversa.foto)
                .into(binding.imageConversaFoto)

            //Evento click
            binding.clItemConversa.setOnClickListener {
                onClick(conversa)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemConversasBinding.inflate(
            inflater,
            parent,
            false
        )

        return ConversasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversa[position]
        holder.bind(conversa)
    }

    override fun getItemCount(): Int {
        return listaConversa.size
    }


}