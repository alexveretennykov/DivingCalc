package com.example.divecalculator.fragment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.divecalculator.R
import com.example.divecalculator.viewmodel.MainViewModel
import com.example.divecalculator.databinding.HomeFragmentBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment: Fragment() {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: MainViewModel
    private var interstitial:InterstitialAd? = null
    private var actionId: Int = -1

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

        if(viewModel.loadAd()) initAds()

        binding.btnInfoHome.setOnClickListener {
            dialogInfoCalculators()
        }

        binding.btnMod.setOnClickListener {
            navigateToCalculator(R.id.action_homeFragment_to_modFragment)
        }

        binding.btnSac.setOnClickListener {
            navigateToCalculator(R.id.action_homeFragment_to_sacFragment)
        }

        binding.btnEad.setOnClickListener {
            navigateToCalculator(R.id.action_homeFragment_to_eadFragment)
        }

        return binding.root
    }

    // Navega hacia el Fragmento indicando, ejecutando la logica de muestreo de Anuncios
    private fun navigateToCalculator(id: Int) {
        actionId = id
        viewModel.addClick()

        if (viewModel.runAd()){
            showAd()
        }else {
            findNavController().navigate(actionId)
        }
    }

    // Muestra la Publicidad cargada y continua con la navegacion dentro de Corrutina
    private fun showAd() {
        if(interstitial != null) {
            interstitial?.show(requireActivity())
        }else{
            Log.d("Admob", "Ad not loaded")
        }

        GlobalScope.launch(Dispatchers.Main) {
            findNavController().navigate(actionId)
        }
    }

    // Precarga del Anuncio
    private fun initAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-3940256099942544/1033173712",
            adRequest, object : InterstitialAdLoadCallback(){

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitial = interstitialAd
            }
            override fun onAdFailedToLoad(p0: LoadAdError) {
                interstitial = null
            }
        })
    }

    // Dialogo con la Info de SAC
    private fun dialogInfoCalculators() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.meaning_of_acronyms))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoCalculatorsHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}