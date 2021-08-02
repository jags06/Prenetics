package com.example.preneticstest.dashboard.fragment

import android.os.Bundle
import android.view.View
import com.example.preneticstest.R
import com.example.preneticstest.base.fragment.BindingFragment
import com.example.preneticstest.base.viewpager.GENETICS_PAGE_INDEX
import com.example.preneticstest.base.viewpager.ViewPagerAdapter
import com.example.preneticstest.databinding.DashboardLayoutBinding
import com.google.android.material.tabs.TabLayoutMediator

class DashboardFragment : BindingFragment<DashboardLayoutBinding>() {


    override fun getLayoutResId(): Int {
        return R.layout.dashboard_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        initViewPager()
    }

    private fun initViewPager() {

        binding.viewPager.adapter = this.activity?.let { ViewPagerAdapter(it) }
        binding.viewPager.offscreenPageLimit =
            (binding.viewPager.adapter as ViewPagerAdapter).itemCount

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getStringArray(R.array.fragmentListName)[position]
        }.attach()

        binding.viewPager.currentItem = GENETICS_PAGE_INDEX
    }
}