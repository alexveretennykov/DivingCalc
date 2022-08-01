package com.example.divecalculator.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.divecalculator.R
import com.example.divecalculator.viewmodel.MainViewModel
import com.example.divecalculator.databinding.HomeFragmentBinding

class HomeFragment: Fragment() {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            activity?.let {
                ViewModelProvider(it)[MainViewModel::class.java]
            }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater)

        binding.btnSac.setOnClickListener {
            viewModel.addClick()

            if (viewModel.runAd()){
                // TODO() -> Mostrar Anuncio aplicando la logica para mostrar un anuncio que ocupe toda la pantalla
            }

            findNavController().navigate(R.id.action_homeFragment_to_sacFragment)
        }

        return binding.root
    }
}