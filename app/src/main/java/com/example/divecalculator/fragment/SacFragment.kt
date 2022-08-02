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
import com.example.divecalculator.databinding.SacFragmentBinding
import com.example.divecalculator.enum.SacProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SacFragment: Fragment() {
    private lateinit var binding: SacFragmentBinding
    private lateinit var listScubaTanks: List<LinearLayout>
    private lateinit var mapEditText1: Map<SacProperty, EditText>
    private lateinit var mapEditText2: Map<SacProperty, EditText>
    private lateinit var mapEditText3: Map<SacProperty, EditText>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SacFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.sacAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listScubaTanks = getAllScubaTankLayouts()
        mapEditText1 = getEditTextFromScubaTank1()
        mapEditText2 = getEditTextFromScubaTank2()
        mapEditText3 = getEditTextFromScubaTank3()

        // Oculta todos los layouts, menos el primero
        hideScubaTankLayouts()

        // Inicializa los listeners de Botones 'Eliminar Botella'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de la imagen de ocultar botellas
    private fun initButtonListeners(){
        // Boton Info SAC
        binding.btnInfoSac.setOnClickListener {
            dialogInfoSac()
        }

        // Boton Añadir Botellas
        binding.btnAddBottle.setOnClickListener {
            addBottle()
        }

        // Boton Calcular SAC
        binding.btnCalcSac.setOnClickListener {
            calcTotalSac()
        }

        binding.btnViewHistorySac.setOnClickListener{
            // TODO() -> Mostrar listado y grafica del historial
        }

        binding.btnSaveSac.setOnClickListener {
            // TODO() -> Guardar el resultado en local ¿Sqlite?
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de botellas
        binding.btnDeleteBottle1.setOnClickListener {
            if(binding.linearLayoutBottle2.visibility == View.VISIBLE ||
                binding.linearLayoutBottle3.visibility == View.VISIBLE) {
                binding.linearLayoutBottle1.visibility = View.GONE
            }
        }

        binding.btnDeleteBottle2.setOnClickListener {
            if(binding.linearLayoutBottle1.visibility == View.VISIBLE ||
                binding.linearLayoutBottle3.visibility == View.VISIBLE) {
                binding.linearLayoutBottle2.visibility = View.GONE
            }
        }

        binding.btnDeleteBottle3.setOnClickListener {
            if(binding.linearLayoutBottle1.visibility == View.VISIBLE ||
                binding.linearLayoutBottle2.visibility == View.VISIBLE) {
                binding.linearLayoutBottle3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de Scuba Tank
    private fun addBottle(){
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
            binding.linearLayoutBottle1,
            binding.linearLayoutBottle2,
            binding.linearLayoutBottle3
        )
    }

    // Devuelve el Map de todos los EditTExt de de Scuba Tank 1
    private fun getEditTextFromScubaTank1(): Map<SacProperty, EditText> {
        return mapOf(
            SacProperty.VOLUME to binding.etBottleVolume1,
            SacProperty.INITPRESS to binding.etInitPressure1,
            SacProperty.FINPRESS to binding.etFinPressure1,
            SacProperty.AVG to binding.etAvg1,
            SacProperty.DIVETIME to binding.etDiveTime1
        )
    }

    // Devuelve el Map de todos los EditTExt de de Scuba Tank 2
    private fun getEditTextFromScubaTank2(): Map<SacProperty, EditText>{
        return mapOf(
            SacProperty.VOLUME to binding.etBottleVolume2,
            SacProperty.INITPRESS to binding.etInitPressure2,
            SacProperty.FINPRESS to binding.etFinPressure2,
            SacProperty.AVG to binding.etAvg2,
            SacProperty.DIVETIME to binding.etDiveTime2
        )
    }

    // Devuelve el Map de todos los EditTExt de de Scuba Tank 3
    private fun getEditTextFromScubaTank3(): Map<SacProperty, EditText>{
        return mapOf(
            SacProperty.VOLUME to binding.etBottleVolume3,
            SacProperty.INITPRESS to binding.etInitPressure3,
            SacProperty.FINPRESS to binding.etFinPressure3,
            SacProperty.AVG to binding.etAvg3,
            SacProperty.DIVETIME to binding.etDiveTime3
        )
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

    // Devuelve una cadena con la unidad de medida segun el objeto Enum
    private fun getMeasurementUnitFromEnum(unit: SacProperty): String{
        return when(unit){
            SacProperty.VOLUME -> "l"
            SacProperty.INITPRESS -> "bar"
            SacProperty.FINPRESS -> "bar"
            SacProperty.AVG -> "m"
            SacProperty.DIVETIME -> "min"
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: SacProperty): String{
        return when(unit){
            SacProperty.VOLUME -> "15 l"
            SacProperty.INITPRESS -> "200 bar"
            SacProperty.FINPRESS -> "50 bar"
            SacProperty.AVG -> "16.3 m"
            SacProperty.DIVETIME -> "55 min"
        }
    }

    // Añade/elimina las unidades de medicion de cada campo, a parte añade la la pista de ayuda
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: SacProperty){
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

    // Oculta todos los layout Scuba Tank, menos el primero
    private fun hideScubaTankLayouts(){
        listScubaTanks.forEach { it.visibility = View.GONE }

        listScubaTanks[0].visibility = View.VISIBLE
    }

    // Calcula SAC Total de todos los Scuba Tank activos (visibles)
    private fun calcTotalSac(){
        var total = 0.0
        var count = 0

        val sac1 = calcSac(binding.linearLayoutBottle1, mapEditText1)
        val sac2 = calcSac(binding.linearLayoutBottle2, mapEditText2)
        val sac3 = calcSac(binding.linearLayoutBottle3, mapEditText3)

        if(sac1 > 0){
            total += sac1
            count ++
        }

        if(sac2 > 0){
            total += sac2
            count ++
        }

        if(sac3 > 0){
            total += sac3
            count ++
        }

        if(total/count > 0) {
            total /= count
            val result = String.format("%.2f", total) + " l/min"
            binding.tvTotalSac.text = result
        }else{
            binding.tvTotalSac.text = "0.00 l/min"
        }
    }

    // Calcula SAC
    private fun calcSac(layout: LinearLayout, map: Map<SacProperty, EditText>):Double{
        return if(layout.visibility == View.VISIBLE && checkNotNullEditTextList(map)){
            val initPress = map[SacProperty.INITPRESS]?.text.toString().split(" ")[0].toInt()
            val finPress = map[SacProperty.FINPRESS]?.text.toString().split(" ")[0].toInt()
            val bottleVolume = map[SacProperty.VOLUME]?.text.toString().split(" ")[0].toInt()
            val avgDepth = map[SacProperty.AVG]?.text.toString().split(" ")[0].toDouble()
            val diveTime = map[SacProperty.DIVETIME]?.text.toString().split(" ")[0].toInt()

            // Formula SAC
            (((initPress - finPress) * bottleVolume) / (avgDepth / 10 + 1) ) / diveTime
        }else{
            // Negativo en caso de Error
            -1.0
        }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<SacProperty, EditText>):Boolean{
        var result = true
        map.forEach {
            if(it.value.text.isEmpty()){
                it.value.hint = resources.getString(R.string.required)
                it.value.setHintTextColor(resources.getColor(R.color.red))
                result = false
            }
        }
        return result
    }

    // Dialogo con la Info de SAC
    private fun dialogInfoSac() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.what_is_sac))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoSacHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}