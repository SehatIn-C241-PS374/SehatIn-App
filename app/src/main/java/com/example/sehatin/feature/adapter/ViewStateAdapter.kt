package com.example.sehatin.feature.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sehatin.feature.profile.FragmentFavorite
import com.example.sehatin.feature.profile.FragmentHistory

class ViewStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0){
            FragmentFavorite()
        }else{
            FragmentHistory()
        }

    }


}