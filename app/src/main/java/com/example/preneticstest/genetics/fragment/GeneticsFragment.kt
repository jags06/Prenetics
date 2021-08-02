package com.example.preneticstest.genetics.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.preneticstest.R
import com.example.preneticstest.base.fragment.BindingFragment
import com.example.preneticstest.databinding.GeneticsLayoutBinding
import com.example.preneticstest.genetics.viewmodel.GeneticsViewModel
import org.koin.android.ext.android.inject

class GeneticsFragment : BindingFragment<GeneticsLayoutBinding>() {

    private val viewModel: GeneticsViewModel by inject()

    override fun getLayoutResId(): Int {
        return R.layout.genetics_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.genetcisViewModel = viewModel
        binding.lifecycleOwner = this
        binding.pdfViewer.fromAsset("paper.pdf")
            .scale(3f)
            .show()

    }
}