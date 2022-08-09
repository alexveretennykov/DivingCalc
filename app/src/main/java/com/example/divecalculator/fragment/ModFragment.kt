package com.example.divecalculator.fragment

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.divecalculator.R
import com.example.divecalculator.databinding.ModFragmentBinding
import com.example.divecalculator.enum.ModProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.floor

class ModFragment: Fragment() {
    private lateinit var binding: ModFragmentBinding
    private lateinit var listModLayouts: List<LinearLayout>
    private lateinit var mapEditText1: Map<ModProperty, EditText>
    private lateinit var mapEditText2: Map<ModProperty, EditText>
    private lateinit var mapEditText3: Map<ModProperty, EditText>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.modAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listModLayouts = getAllModLayouts()
        mapEditText1 = getEditTextFromMod1()
        mapEditText2 = getEditTextFromMod2()
        mapEditText3 = getEditTextFromMod3()

        // Oculta todos los layouts, menos el primero
        hideModCalcLayouts()

        // Inicializa los listeners de Botones 'Eliminar Botella'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de los botones/imagenes
    private fun initButtonListeners(){
        // Boton Info MOD
        binding.btnInfoMod.setOnClickListener {
            dialogInfoMod()
        }

        // Boton de añadir mas bloques MOD
        binding.btnAddModBlock.setOnClickListener {
            addModBlock()
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

    // Hace visible el primer layout invisible que encuentre de MOD
    private fun addModBlock(){
        // run lit@ -> break
        run lit@{
            listModLayouts.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de Scuba Tank
    private fun getAllModLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutMod1,
            binding.linearLayoutMod2,
            binding.linearLayoutMod3
        )
    }

    // Devuelve el Map de todos los EditTExt de MOD 1
    private fun getEditTextFromMod1(): Map<ModProperty, EditText> {
        return mapOf(
            ModProperty.USER_O2 to binding.etOxygenPercentage1,
            ModProperty.USER_PO2 to binding.etUserPpo1,
            ModProperty.USER_MOD to binding.etResultMod1,
            ModProperty.MOD12 to binding.etPpo121,
            ModProperty.MOD14 to binding.etPpo141,
            ModProperty.MOD16 to binding.etPpo161
        )
    }

    // Devuelve el Map de todos los EditTExt de MOD 2
    private fun getEditTextFromMod2(): Map<ModProperty, EditText>{
        return mapOf(
            ModProperty.USER_O2 to binding.etOxygenPercentage2,
            ModProperty.USER_PO2 to binding.etUserPpo2,
            ModProperty.USER_MOD to binding.etResultMod2,
            ModProperty.MOD12 to binding.etPpo122,
            ModProperty.MOD14 to binding.etPpo142,
            ModProperty.MOD16 to binding.etPpo162
        )
    }

    // Devuelve el Map de todos los EditTExt de MOD 3
    private fun getEditTextFromMod3(): Map<ModProperty, EditText>{
        return mapOf(
            ModProperty.USER_O2 to binding.etOxygenPercentage3,
            ModProperty.USER_PO2 to binding.etUserPpo3,
            ModProperty.USER_MOD to binding.etResultMod3,
            ModProperty.MOD12 to binding.etPpo123,
            ModProperty.MOD14 to binding.etPpo143,
            ModProperty.MOD16 to binding.etPpo163
        )
    }

    // Oculta todos los layout de bloques MOD, menos el primero
    private fun hideModCalcLayouts(){
        listModLayouts.forEach { it.visibility = View.GONE }

        listModLayouts[0].visibility = View.VISIBLE
    }

    // Calcula MOD de todos los bloques activos (visibles)
    private fun calcAllMod(){
        calcMod(binding.linearLayoutMod1, mapEditText1)
        calcMod(binding.linearLayoutMod2, mapEditText2)
        calcMod(binding.linearLayoutMod3, mapEditText3)
    }

    // Calcula MOD
    private fun calcMod(layout: LinearLayout, map: Map<ModProperty, EditText>){
       if(layout.visibility == View.VISIBLE && checkNotNullEditTextList(map)){
           val o2 = map[ModProperty.USER_O2]?.text.toString().toDouble()

           // Calculo opcional
           if(map[ModProperty.USER_PO2]?.text.toString() != "") {
               val ppo2 = map[ModProperty.USER_PO2]?.text.toString().toDouble()
               val userMod = String.format("%.0f", floor(((ppo2 / (o2 / 100)) - 1) * 10)) + " m"
               map[ModProperty.USER_MOD]?.setText(userMod)
               map[ModProperty.USER_MOD]?.hint = getHintFromEnum(ModProperty.USER_MOD)
           }else{
               map[ModProperty.USER_MOD]?.setText("")
               map[ModProperty.USER_MOD]?.hint = ""
           }

           // Calculos
           val mod12 = String.format("%.0f", floor(((1.2 / (o2/100)) - 1) * 10)) + " m"
           val mod14 = String.format("%.0f", floor(((1.4 / (o2/100)) - 1) * 10)) + " m"
           val mod16 = String.format("%.0f", floor(((1.6 / (o2/100)) - 1) * 10)) + " m"

           // Muestra los resultados
           map[ModProperty.MOD12]?.setText(mod12)
           map[ModProperty.MOD14]?.setText(mod14)
           map[ModProperty.MOD16]?.setText(mod16)
        }else{
           map[ModProperty.USER_MOD]?.hint = getHintFromEnum(ModProperty.USER_MOD)
           map[ModProperty.USER_MOD]?.setText("")
           map[ModProperty.MOD12]?.setText("")
           map[ModProperty.MOD14]?.setText("")
           map[ModProperty.MOD16]?.setText("")
       }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<ModProperty, EditText>):Boolean{
        var result = true

        if(map[ModProperty.USER_O2]?.text.toString() == "") {
            map[ModProperty.USER_O2]?.hint = resources.getString(R.string.required)
            map[ModProperty.USER_O2]?.setHintTextColor(resources.getColor(R.color.red))
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
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: ModProperty){
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
    private fun getMeasurementUnitFromEnum(unit: ModProperty): String{
        return when(unit){
            ModProperty.USER_O2 -> "%"
            ModProperty.USER_PO2 -> "ppo2"
            ModProperty.USER_MOD -> "m"
            ModProperty.MOD12 -> "m"
            ModProperty.MOD14 -> "m"
            ModProperty.MOD16 -> "m"
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: ModProperty): String{
        return when(unit){
            ModProperty.USER_O2 -> "32 %"
            ModProperty.USER_PO2 -> "1.5 ppo2"
            ModProperty.USER_MOD -> "36 m"
            ModProperty.MOD12 -> "27 m"
            ModProperty.MOD14 -> "33 m"
            ModProperty.MOD16 -> "40 m"
        }
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