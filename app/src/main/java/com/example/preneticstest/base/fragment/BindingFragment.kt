package com.example.preneticstest.base.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.preneticstest.base.AutoDisposable

abstract class BindingFragment<T : ViewDataBinding> : Fragment() {
    protected val autoDisposable = AutoDisposable()

    @LayoutRes
    abstract fun getLayoutResId(): Int

    protected lateinit var binding: T
        private set


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        autoDisposable.bindTo(this.lifecycle)
        return DataBindingUtil.inflate<T>(inflater, getLayoutResId(), container, false)
            .apply { binding = this }.root
    }

   

}