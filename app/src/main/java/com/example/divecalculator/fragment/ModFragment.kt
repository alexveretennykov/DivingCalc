package com.example.divecalculator.fragment

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.divecalculator.R
import com.example.divecalculator.databinding.ModFragmentBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.floor

class ModFragment: Fragment() {
    private lateinit var binding: ModFragmentBinding
    private lateinit var listScubaTanks: List<LinearLayout>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.sacAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listScubaTanks = getAllScubaTankLayouts()

        // Oculta todos los layouts, menos el primero
        hideModCalcLayouts()

        // Inicializa los listeners de Botones 'Eliminar Botella'
        initButtonListeners()

        return binding.root
    }

    // Click Listeners de la imagen de ocultar botellas
    private fun initButtonListeners(){
        // Boton Info MOD
        binding.btnInfoMod.setOnClickListener {
            dialogInfoMod()
        }

        // Boton Añadir Botellas
        binding.btnAddModBlock.setOnClickListener {
            addModBloq()
        }

        // Boton Calcular MOD
        binding.btnCalcMod.setOnClickListener {
            calcAllMod()
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de MOD
        binding.btnDeleteMod1.setOnClickListener {
            if(binding.linearLayoutMod2.visibility == View.VISIBLE ||
                binding.linearLayoutMod3.visibility == View.VISIBLE) {
                binding.linearLayoutMod1.visibility = View.GONE
            }
        }

        binding.btnDeleteMod2.setOnClickListener {
            if(binding.linearLayoutMod1.visibility == View.VISIBLE ||
                binding.linearLayoutMod3.visibility == View.VISIBLE) {
                binding.linearLayoutMod2.visibility = View.GONE
            }
        }

        binding.btnDeleteMod3.setOnClickListener {
            if(binding.linearLayoutMod1.visibility == View.VISIBLE ||
                binding.linearLayoutMod2.visibility == View.VISIBLE) {
                binding.linearLayoutMod3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de Scuba Tank
    private fun addModBloq(){
        // run lit@ -> break
        run lit@{
            listScubaTanks.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de Scuba Tank
    private fun getAllScubaTankLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutMod1,
            binding.linearLayoutMod2,
            binding.linearLayoutMod3
        )
    }

    // Oculta todos los layout de bloques MOD, menos el primero
    private fun hideModCalcLayouts(){
        listScubaTanks.forEach { it.visibility = View.GONE }

        listScubaTanks[0].visibility = View.VISIBLE
    }

    // Calcula MOD de todos los bloques activos (visibles)
    private fun calcAllMod(){
        calcMod1()
    }

    // TODO() -> Comprobar que los campos no esten vacios
    private fun calcMod1() {
        val o2 = binding.etOxygenPercentage1.text.toString().toDouble()
        val ppo2 = binding.etUserPpo1.text.toString().toDouble()

        // Calculos
        val userMod = String.format("%.0f", floor(((ppo2 / (o2/100)) - 1) * 10)) + " m"
        val mod12 = String.format("%.0f", floor(((1.2 / (o2/100)) - 1) * 10)) + " m"
        val mod14 = String.format("%.0f", floor(((1.4 / (o2/100)) - 1) * 10)) + " m"
        val mod16 = String.format("%.0f", floor(((1.6 / (o2/100)) - 1) * 10)) + " m"

        // Muestra los resultados
        binding.etResultMod1.setText(userMod)
        binding.etPpo121.setText(mod12)
        binding.etPpo141.setText(mod14)
        binding.etPpo161.setText(mod16)
    }

    // Dialogo con la Info de MOD
    private fun dialogInfoMod() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.what_is_mod))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoModHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}