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
import com.example.divecalculator.databinding.EadFragmentBinding
import com.example.divecalculator.enum.EadProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EadFragment: Fragment() {
    private lateinit var binding: EadFragmentBinding
    private lateinit var listEadLayouts: List<LinearLayout>
    private lateinit var mapEditText1: Map<EadProperty, EditText>
    private lateinit var mapEditText2: Map<EadProperty, EditText>
    private lateinit var mapEditText3: Map<EadProperty, EditText>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EadFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.eadAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listEadLayouts = getAllEadLayouts()
        mapEditText1 = getEditTextFromEad1()
        mapEditText2 = getEditTextFromEad2()
        mapEditText3 = getEditTextFromEad3()

        // Oculta todos los layouts, menos el primero
        hideEadCalcLayouts()

        // Inicializa los listeners de Botones 'Eliminar Botella'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de los botones/imagenes
    private fun initButtonListeners(){
        // Boton Info EAD
        binding.btnInfoEad.setOnClickListener {
            dialogInfoEad()
        }

        // Boton de añadir mas bloques EAD
        binding.btnAddEadBlock.setOnClickListener {
            addEadBlock()
        }

        // Boton Calcular EAD
        binding.btnCalcEad.setOnClickListener {
            calcAllMod()
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de EAD
        binding.btnDeleteEad1.setOnClickListener {
            if(binding.linearLayoutEad2.visibility == View.VISIBLE ||
                binding.linearLayoutEad3.visibility == View.VISIBLE) {
                binding.linearLayoutEad1.visibility = View.GONE
            }
        }

        binding.btnDeleteEad2.setOnClickListener {
            if(binding.linearLayoutEad1.visibility == View.VISIBLE ||
                binding.linearLayoutEad3.visibility == View.VISIBLE) {
                binding.linearLayoutEad2.visibility = View.GONE
            }
        }

        binding.btnDeleteEad3.setOnClickListener {
            if(binding.linearLayoutEad1.visibility == View.VISIBLE ||
                binding.linearLayoutEad2.visibility == View.VISIBLE) {
                binding.linearLayoutEad3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de EAD
    private fun addEadBlock(){
        // run lit@ -> break
        run lit@{
            listEadLayouts.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de Scuba Tank
    private fun getAllEadLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutEad1,
            binding.linearLayoutEad2,
            binding.linearLayoutEad3
        )
    }

    // Devuelve el Map de todos los EditTExt de EAD 1
    private fun getEditTextFromEad1(): Map<EadProperty, EditText> {
        return mapOf(
            EadProperty.USER_O2 to binding.etOxygenPercentage1,
            EadProperty.USER_DEPTH to binding.etDepth1,
            EadProperty.EAD to binding.etEadCalculated1
        )
    }

    // Devuelve el Map de todos los EditTExt de EAD 2
    private fun getEditTextFromEad2(): Map<EadProperty, EditText>{
        return mapOf(
            EadProperty.USER_O2 to binding.etOxygenPercentage2,
            EadProperty.USER_DEPTH to binding.etDepth2,
            EadProperty.EAD to binding.etEadCalculated2
        )
    }

    // Devuelve el Map de todos los EditTExt de EAD 3
    private fun getEditTextFromEad3(): Map<EadProperty, EditText>{
        return mapOf(
            EadProperty.USER_O2 to binding.etOxygenPercentage3,
            EadProperty.USER_DEPTH to binding.etDepth3,
            EadProperty.EAD to binding.etEadCalculated3
        )
    }

    // Oculta todos los layout de bloques MOD, menos el primero
    private fun hideEadCalcLayouts(){
        listEadLayouts.forEach { it.visibility = View.GONE }

        listEadLayouts[0].visibility = View.VISIBLE
    }

    // Calcula EAD de todos los bloques activos (visibles)
    private fun calcAllMod(){
        calcMod(binding.linearLayoutEad1, mapEditText1)
        calcMod(binding.linearLayoutEad2, mapEditText2)
        calcMod(binding.linearLayoutEad3, mapEditText3)
    }

    // Calcula EAD
    private fun calcMod(layout: LinearLayout, map: Map<EadProperty, EditText>){
       if(layout.visibility == View.VISIBLE  && checkNotNullEditTextList(map)){
           val o2 = map[EadProperty.USER_O2]?.text.toString().split(" ")[0].toInt()
           val depth = map[EadProperty.USER_DEPTH]?.text.toString().split(" ")[0].toInt()

           // Calculo
           // EAD -> (Depth + 10) × Fraction of N2 / 0.79 − 10
           val ead = String.format("%.0f", (depth + 10) * ((100-o2)/100.0) / 0.79 - 10) + " m"

           // Muestra los resultados
           map[EadProperty.EAD]?.setText(ead.toString())
        }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<EadProperty, EditText>):Boolean{
        var result = true

        if(map[EadProperty.USER_O2]?.text.toString() == "") {
            map[EadProperty.USER_O2]?.hint = resources.getString(R.string.required)
            map[EadProperty.USER_O2]?.setHintTextColor(resources.getColor(R.color.red))
            result = false
        }

        if(map[EadProperty.USER_DEPTH]?.text.toString() == ""){
            map[EadProperty.USER_DEPTH]?.hint = resources.getString(R.string.required)
            map[EadProperty.USER_DEPTH]?.setHintTextColor(resources.getColor(R.color.red))
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
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: EadProperty){
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
    private fun getMeasurementUnitFromEnum(unit: EadProperty): String{
        return when(unit){
            EadProperty.USER_O2 -> "%"
            EadProperty.USER_DEPTH -> "m"
            EadProperty.EAD -> "m"
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: EadProperty): String{
        return when(unit){
            EadProperty.USER_O2 -> "32 %"
            EadProperty.USER_DEPTH -> "30 m"
            EadProperty.EAD -> "24 m"
        }
    }

    // Dialogo con la Info de EAD
    private fun dialogInfoEad() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.what_is_ead))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoEadHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}