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
import com.example.divecalculator.databinding.BestMixFragmentBinding
import com.example.divecalculator.enum.BestMixProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.floor

class BestMixFragment: Fragment() {
    private lateinit var binding: BestMixFragmentBinding
    private lateinit var listMixLayouts: List<LinearLayout>
    private lateinit var listEtMaps: List<Map<BestMixProperty, EditText>>
    private lateinit var listSwitchBtns: List<SwitchMaterial>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BestMixFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.bestMixAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listMixLayouts = getOrderedListMixLayouts()
        listEtMaps = getOrderedListEtMaps()
        listSwitchBtns = getOrderedListTrimixBtns()

        // Oculta todos los layouts, menos el primero
        hideBestMixLayouts()

        // Inicializa los listeners de Botones 'Eliminar Bloque TOD'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de los botones/imagenes
    private fun initButtonListeners(){
        // Boton Info Best Mix
        binding.btnInfoBestMix.setOnClickListener {
            dialogInfoBestMix()
        }

        // Boton de añadir mas bloques Best Mix
        binding.btnAddBestMixBlock.setOnClickListener {
            addTodBlock()
        }

        // Boton Calcular Best Mix
        binding.btnCalcBestMix.setOnClickListener {
            calcAllTod()
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de Best Mix
        binding.btnDeleteBestMix1.setOnClickListener {
            if(binding.linearLayoutBestMix2.visibility == View.VISIBLE ||
                binding.linearLayoutBestMix3.visibility == View.VISIBLE) {
                binding.linearLayoutBestMix1.visibility = View.GONE
            }
        }

        binding.btnDeleteBestMix2.setOnClickListener {
            if(binding.linearLayoutBestMix1.visibility == View.VISIBLE ||
                binding.linearLayoutBestMix3.visibility == View.VISIBLE) {
                binding.linearLayoutBestMix2.visibility = View.GONE
            }
        }

        binding.btnDeleteBestMix3 .setOnClickListener {
            if(binding.linearLayoutBestMix1.visibility == View.VISIBLE ||
                binding.linearLayoutBestMix2.visibility == View.VISIBLE) {
                binding.linearLayoutBestMix3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de Best Mix
    private fun addTodBlock(){
        // run lit@ -> break
        run lit@{
            listMixLayouts.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de bloques Best Mix
    private fun getOrderedListMixLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutBestMix1,
            binding.linearLayoutBestMix2,
            binding.linearLayoutBestMix3
        )
    }

    // Devuelve el listado de Mapas de EditText de bloques Best Mix
    private fun getOrderedListEtMaps(): List<Map<BestMixProperty, EditText>> {
        return listOf(
            getEditTextFromTod1(),
            getEditTextFromTod2(),
            getEditTextFromTod3()
        )
    }

    // Devuelve el listado de botones de seleccion de Agua Salada
    private fun getOrderedListTrimixBtns(): List<SwitchMaterial> {
        return listOf(
            binding.btnTrimix1,
            binding.btnTrimix2,
            binding.btnTrimix3
        )
    }

    // Devuelve el Map de todos los EditTExt de Best Mix 1
    private fun getEditTextFromTod1(): Map<BestMixProperty, EditText> {
        return mapOf(
            BestMixProperty.DEPTH to binding.etDepth1,
            BestMixProperty.MIX12 to binding.etPpo121,
            BestMixProperty.MIX14 to binding.etPpo141,
            BestMixProperty.MIX16 to binding.etPpo161,
            BestMixProperty.STANDARD_MIX to binding.etRecommendedMix1
        )
    }

    // Devuelve el Map de todos los EditTExt de Best Mix 2
    private fun getEditTextFromTod2(): Map<BestMixProperty, EditText>{
        return mapOf(
            BestMixProperty.DEPTH to binding.etDepth2,
            BestMixProperty.MIX12 to binding.etPpo122,
            BestMixProperty.MIX14 to binding.etPpo142,
            BestMixProperty.MIX16 to binding.etPpo162,
            BestMixProperty.STANDARD_MIX to binding.etRecommendedMix2
        )
    }

    // Devuelve el Map de todos los EditTExt de Best Mix 3
    private fun getEditTextFromTod3(): Map<BestMixProperty, EditText>{
        return mapOf(
            BestMixProperty.DEPTH to binding.etDepth3,
            BestMixProperty.MIX12 to binding.etPpo123,
            BestMixProperty.MIX14 to binding.etPpo143,
            BestMixProperty.MIX16 to binding.etPpo163,
            BestMixProperty.STANDARD_MIX to binding.etRecommendedMix3
        )
    }

    // Oculta todos los layout de bloques Best Mix, menos el primero
    private fun hideBestMixLayouts(){
        listMixLayouts.forEach { it.visibility = View.GONE }

        listMixLayouts[0].visibility = View.VISIBLE
    }

    // Calcula Best Mix de todos los bloques activos (visibles)
    private fun calcAllTod(){
        calcBestMix(0)
        calcBestMix(1)
        calcBestMix(2)
    }

    // Calcula Best Mix
    private fun calcBestMix(index: Int){
        if(listMixLayouts[index].visibility == View.VISIBLE  && checkNotNullEditTextList(listEtMaps[index])){
            val depth = listEtMaps[index][BestMixProperty.DEPTH]?.text.toString().split(" ")[0].toInt()
            val trimix = listSwitchBtns[index].isChecked

            // Calcula la mejor mezcla y la muestra
            if(trimix) {
                // TODO() -> Verificar formula, no cuadra.
                // Presion Parcial de Nitrogeno (maxima con aire)
                // ppn2 = 0.79 (nitrogeno) * 4 ATA
                // val ppn2 = 3.16
                // val nitrogen = (ppn2 / (depth+10)) * 1000
                // (100-nitrogen-oxy12)

                val oxy12 = (1.2 / (depth+10)) * 1000
                val oxy14= (1.4 / (depth+10)) * 1000
                val oxy16 = (1.6 / (depth+10)) * 1000

                val mix12 = String.format("%.0f", oxy12 ) + "/" + String.format("%.0f", 100.0 - oxy12 - (100-oxy12)/(depth/30.0) )
                val mix14 = String.format("%.0f", oxy14 ) + "/" + String.format("%.0f", 100.0 - oxy14 - (100-oxy14)/(depth/30.0) )
                val mix16 = String.format("%.0f", oxy16 ) + "/" + String.format("%.0f", 100.0 - oxy16 - (100-oxy16)/(depth/30.0) )

                listEtMaps[index][BestMixProperty.MIX12]?.setText(mix12)
                listEtMaps[index][BestMixProperty.MIX14]?.setText(mix14)
                listEtMaps[index][BestMixProperty.MIX16]?.setText(mix16)
                listEtMaps[index][BestMixProperty.STANDARD_MIX]?.setText(getStandardBestMix(depth))
            }else{
                val mix12 = String.format("%.0f", floor((1.2 / (depth+10))*1000) ) + "% Oxygen"
                val mix14 = String.format("%.0f", floor((1.4 / (depth+10))*1000) ) + "% Oxygen"
                val mix16 = String.format("%.0f", floor((1.6 / (depth+10))*1000) ) + "% Oxygen"

                listEtMaps[index][BestMixProperty.MIX12]?.setText(mix12)
                listEtMaps[index][BestMixProperty.MIX14]?.setText(mix14)
                listEtMaps[index][BestMixProperty.MIX16]?.setText(mix16)
                listEtMaps[index][BestMixProperty.STANDARD_MIX]?.setText(getStandardBestMix(depth))
            }
        }
    }

    private fun getStandardBestMix(depth : Int): String{
        return when (depth) {
            in 1..20 -> "Air"
            in 21..33 -> "Nitrox 32"
            in 34..40 -> "Air or Trimix"
            in 41..51 -> "Trimix 21/35"
            in 52..62 -> "Trimix 18/45"
            in 63..74 -> "Trimix 15/55"
            in 75..92 -> "Trimix 12/60"
            in 93..110 -> "Trimix 10/70"
            else -> ""
        }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<BestMixProperty, EditText>):Boolean{
        var result = true

        if(map[BestMixProperty.DEPTH]?.text.toString() == ""){
            map[BestMixProperty.DEPTH]?.hint = resources.getString(R.string.required)
            map[BestMixProperty.DEPTH]?.setHintTextColor(resources.getColor(R.color.orange_red))
            result = false
        }

        return result
    }

    // Inicializa Listeners que se ejecutan al cambiar el Focus de los EditText
    // Añade/elimina las unidades de medicion de cada campo
    private fun initEditTextListeners(){
        // Recorre la lista de Diccionarios y a continuacion recorre cada Diccionario de EditText
        listEtMaps.forEach {
            it.forEach { (t, u) ->
                u.setOnFocusChangeListener { _, hasFocus ->
                    setStyleEditText(u, hasFocus, t)
                }
                u.hint = getHintFromEnum(t)
            }
        }
    }

    // Añade/elimina las unidades de medicion de cada campo, a parte añade la la pista de ayuda
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: BestMixProperty){
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
    private fun getMeasurementUnitFromEnum(unit: BestMixProperty): String{
        return when(unit){
            BestMixProperty.DEPTH -> "m"
            else -> ""
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: BestMixProperty): String{
        return when(unit){
            BestMixProperty.DEPTH -> "30 m"
            else -> ""
        }
    }

    // Dialogo con la Info de Best Mix
    private fun dialogInfoBestMix() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.what_is_best_mix))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoBestMixHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}