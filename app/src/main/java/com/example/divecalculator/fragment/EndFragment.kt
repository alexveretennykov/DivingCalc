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
import com.example.divecalculator.databinding.EndFragmentBinding
import com.example.divecalculator.enum.EndProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EndFragment: Fragment() {
    private lateinit var binding: EndFragmentBinding
    private lateinit var listEndLayouts: List<LinearLayout>
    private lateinit var mapEditText1: Map<EndProperty, EditText>
    private lateinit var mapEditText2: Map<EndProperty, EditText>
    private lateinit var mapEditText3: Map<EndProperty, EditText>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EndFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.endAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listEndLayouts = getAllEndLayouts()
        mapEditText1 = getEditTextFromEnd1()
        mapEditText2 = getEditTextFromEnd2()
        mapEditText3 = getEditTextFromEnd3()

        // Oculta todos los layouts, menos el primero
        hideEndCalcLayouts()

        // Inicializa los listeners de Botones 'Eliminar Bloque END'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de los botones/imagenes
    private fun initButtonListeners(){
        // Boton Info END
        binding.btnInfoEnd.setOnClickListener {
            dialogInfoEnd()
        }

        // Boton de añadir mas bloques END
        binding.btnAddEndBlock.setOnClickListener {
            addEndBlock()
        }

        // Boton Calcular END
        binding.btnCalcEnd.setOnClickListener {
            calcAllEnd()
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de END
        binding.btnDeleteEnd1.setOnClickListener {
            if(binding.linearLayoutEnd2.visibility == View.VISIBLE ||
                binding.linearLayoutEnd3.visibility == View.VISIBLE) {
                binding.linearLayoutEnd1.visibility = View.GONE
            }
        }

        binding.btnDeleteEnd2.setOnClickListener {
            if(binding.linearLayoutEnd1.visibility == View.VISIBLE ||
                binding.linearLayoutEnd3.visibility == View.VISIBLE) {
                binding.linearLayoutEnd2.visibility = View.GONE
            }
        }

        binding.btnDeleteEnd3.setOnClickListener {
            if(binding.linearLayoutEnd1.visibility == View.VISIBLE ||
                binding.linearLayoutEnd2.visibility == View.VISIBLE) {
                binding.linearLayoutEnd3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de END
    private fun addEndBlock(){
        // run lit@ -> break
        run lit@{
            listEndLayouts.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de END
    private fun getAllEndLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutEnd1,
            binding.linearLayoutEnd2,
            binding.linearLayoutEnd3
        )
    }

    // Devuelve el Map de todos los EditTExt de END 1
    private fun getEditTextFromEnd1(): Map<EndProperty, EditText> {
        return mapOf(
            EndProperty.USER_HELIUM to binding.etHeliumPercentage1,
            EndProperty.USER_DEPTH to binding.etDepth1,
            EndProperty.END to binding.etEndCalculated1
        )
    }

    // Devuelve el Map de todos los EditTExt de END 2
    private fun getEditTextFromEnd2(): Map<EndProperty, EditText>{
        return mapOf(
            EndProperty.USER_HELIUM to binding.etHeliumPercentage2,
            EndProperty.USER_DEPTH to binding.etDepth2,
            EndProperty.END to binding.etEndCalculated2
        )
    }

    // Devuelve el Map de todos los EditTExt de END 3
    private fun getEditTextFromEnd3(): Map<EndProperty, EditText>{
        return mapOf(
            EndProperty.USER_HELIUM to binding.etHeliumPercentage3,
            EndProperty.USER_DEPTH to binding.etDepth3,
            EndProperty.END to binding.etEndCalculated3
        )
    }

    // Oculta todos los layout de bloques END, menos el primero
    private fun hideEndCalcLayouts(){
        listEndLayouts.forEach { it.visibility = View.GONE }

        listEndLayouts[0].visibility = View.VISIBLE
    }

    // Calcula END de todos los bloques activos (visibles)
    private fun calcAllEnd(){
        calcMod(binding.linearLayoutEnd1, mapEditText1)
        calcMod(binding.linearLayoutEnd2, mapEditText2)
        calcMod(binding.linearLayoutEnd3, mapEditText3)
    }

    // Calcula END
    private fun calcMod(layout: LinearLayout, map: Map<EndProperty, EditText>){
       if(layout.visibility == View.VISIBLE  && checkNotNullEditTextList(map)){
           val he = map[EndProperty.USER_HELIUM]?.text.toString().split(" ")[0].toInt()
           val depth = map[EndProperty.USER_DEPTH]?.text.toString().split(" ")[0].toInt()

           // Calculo
           // END -> (depth + 10) × (fraction of O2 + fraction of N2) in trimix − 10
           val end = String.format("%.0f", (depth + 10) * ((100-he)/100.0) - 10) + " m"

           // Muestra los resultados
           map[EndProperty.END]?.setText(end.toString())
        }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<EndProperty, EditText>):Boolean{
        var result = true

        if(map[EndProperty.USER_HELIUM]?.text.toString() == "") {
            map[EndProperty.USER_HELIUM]?.hint = resources.getString(R.string.required)
            map[EndProperty.USER_HELIUM]?.setHintTextColor(resources.getColor(R.color.red))
            result = false
        }

        if(map[EndProperty.USER_DEPTH]?.text.toString() == ""){
            map[EndProperty.USER_DEPTH]?.hint = resources.getString(R.string.required)
            map[EndProperty.USER_DEPTH]?.setHintTextColor(resources.getColor(R.color.red))
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
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: EndProperty){
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
    private fun getMeasurementUnitFromEnum(unit: EndProperty): String{
        return when(unit){
            EndProperty.USER_HELIUM -> "%"
            EndProperty.USER_DEPTH -> "m"
            EndProperty.END -> "m"
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: EndProperty): String{
        return when(unit){
            EndProperty.USER_HELIUM -> "35 %"
            EndProperty.USER_DEPTH -> "50 m"
            EndProperty.END -> "29 m"
        }
    }

    // Dialogo con la Info de EAD
    private fun dialogInfoEnd() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.what_is_end))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoEndHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}