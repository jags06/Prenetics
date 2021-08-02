package com.example.preneticstest.base.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.preneticstest.genetics.fragment.GeneticsFragment
import com.example.preneticstest.heart.fragment.HeartFragment
import com.example.preneticstest.profile.fragment.ProfileFragment

const val GENETICS_PAGE_INDEX = 0
const val HEART_PAGE_INDEX = 1
const val PROFILE_PAGE_INDEX = 2


class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(

        GENETICS_PAGE_INDEX to { GeneticsFragment() },
        HEART_PAGE_INDEX to { HeartFragment() },
        PROFILE_PAGE_INDEX to { ProfileFragment() },


    )

    override fun getItemCount() = tabFragmentsCreators.size
    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}
