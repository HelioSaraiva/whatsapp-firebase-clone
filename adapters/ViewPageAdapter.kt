package com.heliobuzato.whatsappfirebase.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.heliobuzato.whatsappfirebase.fragments.ContatosFragment
import com.heliobuzato.whatsappfirebase.fragments.ConversasFragment

class ViewPageAdapter(
    private val abas: List<String>,
    framentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(framentManager, lifecycle) {
    override fun getItemCount(): Int {

        return abas.size

    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            1 -> return ContatosFragment()
        }
        return ConversasFragment()
    }


}