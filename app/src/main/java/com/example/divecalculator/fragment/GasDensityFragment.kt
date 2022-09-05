package com.example.divecalculator.fragment

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.divecalculator.R
import com.example.divecalculator.databinding.GasDensityFragmentBinding
import com.example.divecalculator.enum.GasDensityProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GasDensityFragment: Fragment() {
    private lateinit var binding: GasDensityFragmentBinding
    private lateinit var listGasDensityLayouts: List<LinearLayout>
    private lateinit var mapEditText1: Map<GasDensityProperty, EditText>
    private lateinit var mapEditText2: Map<GasDensityProperty, EditText>
    private lateinit var mapEditText3: Map<GasDensityProperty, EditText>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GasDensityFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.gasDensityAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listGasDensityLayouts = getAllGasDensityLayouts()
        mapEditText1 = getEditTextFromGasDensity1()
        mapEditText2 = getEditTextFromGasDensity2()
        mapEditText3 = getEditTextFromGasDensity3()

        // Oculta todos los layouts, menos el primero
        hideGasDensityCalcLayouts()

        // Inicializa los listeners de Botones 'Eliminar Bloque END'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de los botones/imagenes
    private fun initButtonListeners(){
        // Boton Info
        binding.btnInfoGasDensity.setOnClickListener {
            dialogInfoGasDensity()
        }

        // Boton de añadir mas bloques de Gas Density
        binding.btnAddEndBlock.setOnClickListener {
            addGasDensityBlock()
        }

        // Boton Calcular GasDensity
        binding.btnCalcEnd.setOnClickListener {
            calcAllGasDensity()
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de GasDensity
        binding.btnDeleteGasDensity1.setOnClickListener {
            if(binding.linearLayoutGasDensity2.visibility == View.VISIBLE ||
                binding.linearLayoutGasDensity3.visibility == View.VISIBLE) {
                binding.linearLayoutGasDensity1.visibility = View.GONE
            }
        }

        binding.btnDeleteGasDensity2.setOnClickListener {
            if(binding.linearLayoutGasDensity1.visibility == View.VISIBLE ||
                binding.linearLayoutGasDensity3.visibility == View.VISIBLE) {
                binding.linearLayoutGasDensity2.visibility = View.GONE
            }
        }

        binding.btnDeleteGasDensity3.setOnClickListener {
            if(binding.linearLayoutGasDensity1.visibility == View.VISIBLE ||
                binding.linearLayoutGasDensity2.visibility == View.VISIBLE) {
                binding.linearLayoutGasDensity3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de GasDensity
    private fun addGasDensityBlock(){
        // run lit@ -> break
        run lit@{
            listGasDensityLayouts.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de GasDensity
    private fun getAllGasDensityLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutGasDensity1,
            binding.linearLayoutGasDensity2,
            binding.linearLayoutGasDensity3
        )
    }

    // Devuelve el Map de todos los EditTExt de GasDensity 1
    private fun getEditTextFromGasDensity1(): Map<GasDensityProperty, EditText> {
        return mapOf(
            GasDensityProperty.HE to binding.etHeliumPercentage1,
            GasDensityProperty.O2 to binding.etOxygenPercentage1,
            GasDensityProperty.DEPTH to binding.etDepth1,
            GasDensityProperty.DENSITY to binding.etGasDensity1,
            GasDensityProperty.END to binding.etEnd1,
            GasDensityProperty.PPO2 to binding.etPpo1
        )
    }

    // Devuelve el Map de todos los EditTExt de GasDensity 2
    private fun getEditTextFromGasDensity2(): Map<GasDensityProperty, EditText>{
        return mapOf(
            GasDensityProperty.HE to binding.etHeliumPercentage2,
            GasDensityProperty.O2 to binding.etOxygenPercentage2,
            GasDensityProperty.DEPTH to binding.etDepth2,
            GasDensityProperty.DENSITY to binding.etGasDensity2,
            GasDensityProperty.END to binding.etEnd2,
            GasDensityProperty.PPO2 to binding.etPpo2
        )
    }

    // Devuelve el Map de todos los EditTExt de GasDensity 3
    private fun getEditTextFromGasDensity3(): Map<GasDensityProperty, EditText>{
        return mapOf(
            GasDensityProperty.HE to binding.etHeliumPercentage3,
            GasDensityProperty.O2 to binding.etOxygenPercentage3,
            GasDensityProperty.DEPTH to binding.etDepth3,
            GasDensityProperty.DENSITY to binding.etGasDensity3,
            GasDensityProperty.END to binding.etEnd3,
            GasDensityProperty.PPO2 to binding.etPpo3
        )
    }

    // Oculta todos los layout de bloques GasDensity, menos el primero
    private fun hideGasDensityCalcLayouts(){
        listGasDensityLayouts.forEach { it.visibility = View.GONE }

        listGasDensityLayouts[0].visibility = View.VISIBLE
    }

    // Calcula END de todos los bloques activos (visibles)
    private fun calcAllGasDensity(){
        calcEnd(binding.linearLayoutGasDensity1, mapEditText1)
        calcEnd(binding.linearLayoutGasDensity2, mapEditText2)
        calcEnd(binding.linearLayoutGasDensity3, mapEditText3)
    }

    // Calcula END
    private fun calcEnd(layout: LinearLayout, map: Map<GasDensityProperty, EditText>){
       if(layout.visibility == View.VISIBLE  && checkNotNullEditTextList(map)){
           val he = map[GasDensityProperty.HE]?.text.toString().split(" ")[0].toInt()
           val o2 = map[GasDensityProperty.O2]?.text.toString().split(" ")[0].toInt()
           val depth = map[GasDensityProperty.DEPTH]?.text.toString().split(" ")[0].toInt()

           //Densidad de gases
           val oxy = 1.429
           val hel = 0.1786
           val nit = 1.2506

           // Calculo
           var den = (oxy*o2 + hel*he + nit*(100-he-o2))/100 * (depth/10.0+1)
           val density = String.format("%.2f", den ) + " g/L"
           val end = String.format("%.0f", (depth + 10) * ((100-he) / 100.0) - 10) + " m"
           val ppo2 = String.format("%.2f", ((depth + 10) * o2) / 1000.0) + " ppo2"

           // Muestra los resultados
           map[GasDensityProperty.DENSITY]?.setText(density)
           map[GasDensityProperty.END]?.setText(end)
           map[GasDensityProperty.PPO2]?.setText(ppo2)

           // Estilo de EditText
           when(den){
               in 0.0..5.20 -> map[GasDensityProperty.DENSITY]?.setTextColor(resources.getColor(R.color.green))
               in 5.21..6.20 -> map[GasDensityProperty.DENSITY]?.setTextColor(resources.getColor(R.color.orange))
               in 6.21..999.9 -> map[GasDensityProperty.DENSITY]?.setTextColor(resources.getColor(R.color.red))
               else -> map[GasDensityProperty.DENSITY]?.setTextColor(resources.getColor(R.color.black))
           }
        }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<GasDensityProperty, EditText>):Boolean{

        var result = true

        if(map[GasDensityProperty.HE]?.text.toString() == "") {
            map[GasDensityProperty.HE]?.setText("0")
        }

        if(map[GasDensityProperty.DEPTH]?.text.toString() == "") {
            map[GasDensityProperty.DEPTH]?.hint = resources.getString(R.string.required)
            map[GasDensityProperty.DEPTH]?.setHintTextColor(resources.getColor(R.color.orange_red))
            result = false
        }

        if(map[GasDensityProperty.O2]?.text.toString() == "") {
            map[GasDensityProperty.O2]?.hint = resources.getString(R.string.required)
            map[GasDensityProperty.O2]?.setHintTextColor(resources.getColor(R.color.orange_red))
            result = false
        }

        return result
    }

    // Inicializa Listeners que se ejecutan al cambiar el Focus de los EditText
    // Añade/elimina las unidades de medicion de cada campo
    private fun initEditTextListeners(){
        mapEditText1.forEach { (t, u) ->
            u.setOnFocusChangeListener { _, hasFocus ->
                setStyleEditText(u, hasFocus, t)
            }
            u.hint = getHintFromEnum(t)
        }

        mapEditText2.forEach { (t, u) ->
            u.setOnFocusChangeListener { _, hasFocus ->
                setStyleEditText(u, hasFocus, t)
            }
            u.hint = getHintFromEnum(t)
        }

        mapEditText3.forEach { (t, u) ->
            u.setOnFocusChangeListener { _, hasFocus ->
                setStyleEditText(u, hasFocus, t)
            }
            u.hint = getHintFromEnum(t)
        }
    }

    // Añade/elimina las unidades de medicion de cada campo, a parte añade la la pista de ayuda
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: GasDensityProperty){
        val unit = getMeasurementUnitFromEnum(enum)
        val hint = getHintFromEnum(enum)

        if(hasFocus) {
            if (et.text.isNotEmpty()) {
                et.setText(
                    et.text.toString().split(" ")[0]
                )
            } else {
                et.hint = hint
                et.setHintTextColor(resources.getColor(R.color.gray))
            }
        }else {
            if(et.text.isNotEmpty()) {
                val text = "${et.text} $unit"
                et.setText(text)
            }
        }
    }

    // Devuelve una cadena con la unidad de medida segun el objeto Enum
    private fun getMeasurementUnitFromEnum(unit: GasDensityProperty): String{
        return when(unit){
            GasDensityProperty.HE -> "%"
            GasDensityProperty.O2 -> "%"
            GasDensityProperty.DEPTH -> "m"
            GasDensityProperty.DENSITY -> "g/L"
            GasDensityProperty.END -> "m"
            GasDensityProperty.PPO2 -> "ppo2"
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: GasDensityProperty): String{
        return when(unit){
            GasDensityProperty.HE -> "0 %"
            GasDensityProperty.O2 -> "32 %"
            GasDensityProperty.DEPTH -> "30 m"
            GasDensityProperty.DENSITY -> "5.23 g/L"
            GasDensityProperty.END -> "30 m"
            GasDensityProperty.PPO2 -> "1.28 ppo2"
        }
    }

    // Dialogo con la Info de Gas Density
    private fun dialogInfoGasDensity() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.whats_is_gas_density))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoGasDensityHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}