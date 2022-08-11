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
import com.example.divecalculator.databinding.TodFragmentBinding
import com.example.divecalculator.enum.TodProperty
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.exp
import kotlin.math.ln

class TodFragment: Fragment() {
    private lateinit var binding: TodFragmentBinding
    private lateinit var listTodLayouts: List<LinearLayout>
    private lateinit var listEtMaps: List<Map<TodProperty, EditText>>
    private lateinit var listSwitchBtns: List<SwitchMaterial>
    lateinit var bannerAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TodFragmentBinding.inflate(inflater, container, false)

        // Publicidad Admob
        bannerAdView = binding.todAdmobBanner
        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        // Inicializa las listas
        listTodLayouts = getOrderedListTodLayouts()
        listEtMaps = getOrderedListEtMaps()
        listSwitchBtns = getOrderedListSaltWaterBtns()

        // Oculta todos los layouts, menos el primero
        hideTodCalcLayouts()

        // Inicializa los listeners de Botones 'Eliminar Bloque TOD'
        initButtonListeners()

        // Inicializa los listeners de Edit Text
        initEditTextListeners()

        return binding.root
    }

    // Click Listeners de los botones/imagenes
    private fun initButtonListeners(){
        // Boton Info TOD
        binding.btnInfoTod.setOnClickListener {
            dialogInfoTod()
        }

        // Boton de añadir mas bloques TOD
        binding.btnAddTodBlock.setOnClickListener {
            addTodBlock()
        }

        // Boton Calcular TOD
        binding.btnCalcTod.setOnClickListener {
            calcAllTod()
        }

        // Listeners de la imagen 'Delete' para ocultar los bloques de TOD
        binding.btnDeleteTod1.setOnClickListener {
            if(binding.linearLayoutTod2.visibility == View.VISIBLE ||
                binding.linearLayoutTod3.visibility == View.VISIBLE) {
                binding.linearLayoutTod1.visibility = View.GONE
            }
        }

        binding.btnDeleteTod2.setOnClickListener {
            if(binding.linearLayoutTod1.visibility == View.VISIBLE ||
                binding.linearLayoutTod3.visibility == View.VISIBLE) {
                binding.linearLayoutTod2.visibility = View.GONE
            }
        }

        binding.btnDeleteTod3.setOnClickListener {
            if(binding.linearLayoutTod1.visibility == View.VISIBLE ||
                binding.linearLayoutTod2.visibility == View.VISIBLE) {
                binding.linearLayoutTod3.visibility = View.GONE
            }
        }
    }

    // Hace visible el primer layout invisible que encuentre de TOD
    private fun addTodBlock(){
        // run lit@ -> break
        run lit@{
            listTodLayouts.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    // Devuelve el listado de layout de bloques TOD
    private fun getOrderedListTodLayouts(): List<LinearLayout>{
        return listOf(
            binding.linearLayoutTod1,
            binding.linearLayoutTod2,
            binding.linearLayoutTod3
        )
    }

    // Devuelve el listado de Mapas de EditText de bloques TOD
    private fun getOrderedListEtMaps(): List<Map<TodProperty, EditText>> {
        return listOf(
            getEditTextFromTod1(),
            getEditTextFromTod2(),
            getEditTextFromTod3()
        )
    }

    // Devuelve el listado de botones de seleccion de Agua Salada
    private fun getOrderedListSaltWaterBtns(): List<SwitchMaterial> {
        return listOf(
            binding.btnSaltWater1,
            binding.btnSaltWater2,
            binding.btnSaltWater3
        )
    }

    // Devuelve el Map de todos los EditTExt de TOD 1
    private fun getEditTextFromTod1(): Map<TodProperty, EditText> {
        return mapOf(
            TodProperty.ALTITUDE to binding.etAltitude1,
            TodProperty.DEPTH to binding.etDepth1,
            TodProperty.USER_STOP to binding.etSafetyStop1,
            TodProperty.TOD to binding.etTodCalculated1,
            TodProperty.STOP_CALCULATED to binding.etSafetyStopCalculated1
        )
    }

    // Devuelve el Map de todos los EditTExt de TOD 2
    private fun getEditTextFromTod2(): Map<TodProperty, EditText>{
        return mapOf(
            TodProperty.ALTITUDE to binding.etAltitude2,
            TodProperty.DEPTH to binding.etDepth2,
            TodProperty.USER_STOP to binding.etSafetyStop2,
            TodProperty.TOD to binding.etTodCalculated2,
            TodProperty.STOP_CALCULATED to binding.etSafetyStopCalculated2
        )
    }

    // Devuelve el Map de todos los EditTExt de TOD 3
    private fun getEditTextFromTod3(): Map<TodProperty, EditText>{
        return mapOf(
            TodProperty.ALTITUDE to binding.etAltitude3,
            TodProperty.DEPTH to binding.etDepth3,
            TodProperty.USER_STOP to binding.etSafetyStop3,
            TodProperty.TOD to binding.etTodCalculated3,
            TodProperty.STOP_CALCULATED to binding.etSafetyStopCalculated3
        )
    }

    // Oculta todos los layout de bloques TOD, menos el primero
    private fun hideTodCalcLayouts(){
        listTodLayouts.forEach { it.visibility = View.GONE }

        listTodLayouts[0].visibility = View.VISIBLE
    }

    // Calcula TOD de todos los bloques activos (visibles)
    private fun calcAllTod(){
        calcTod(0)
        calcTod(1)
        calcTod(2)
    }

    // Calcula TOD
    /* TOD -> Da * (1 atm / Pa) * (33 fsw / 34 ffw).
           Da -> maximum depth of a dive at altitude
           Pa -> (1 atm) * exp(5.255876 * log(1 – (C * A)))
                where A = altitude
                where C = 0. 0000068756 / 1 foot = 0. 000022558 / 1 meter

           PA metric -> 1 * exp(5.255876 * log(1 - ((0.000022558 / 1) * altitude)))
           PA imperial -> 1 * exp(5.255876 * log(1 - ((0.0000068756 / 1) * altitude)))
    */
    private fun calcTod(index: Int){
       if(listTodLayouts[index].visibility == View.VISIBLE  && checkNotNullEditTextList(listEtMaps[index])){
           val altitude = listEtMaps[index][TodProperty.ALTITUDE]?.text.toString().split(" ")[0].toInt()
           val depth = listEtMaps[index][TodProperty.DEPTH]?.text.toString().split(" ")[0].toInt()
           val stop = listEtMaps[index][TodProperty.USER_STOP]?.text.toString().split(" ")[0].toInt()
           val waterSalt = listSwitchBtns[index].isChecked

           // Atmospheric pressure at altitude
           val pa = 1 * exp(5.255876 * ln(1 - ((0.000022558 / 1) * altitude)))

           // TOD
           val tod = if(waterSalt) {
               String.format("%.0f", depth * (1 / pa))  + " m"
           }else{
               // Hace la conversion para adaptar la presion en agua dulce a las tablas de agua salada
               String.format("%.0f", depth * (1 / pa) * (33.0/34.0)) + " m"
           }

           // Safety Stop
           val todSafetyStop = if(waterSalt) {
               String.format("%.1f", stop * (pa / 1)) + " m"
           }else{
               String.format("%.1f", stop * (pa / 1) * (34.0/33.0))  + " m"
           }

           // Muestra los resultados
           listEtMaps[index][TodProperty.TOD]?.setText(tod)
           listEtMaps[index][TodProperty.STOP_CALCULATED]?.setText(todSafetyStop)
        }
    }

    // Comprueba que los EditText de la lista no esten vacios
    private fun checkNotNullEditTextList(map: Map<TodProperty, EditText>):Boolean{
        var result = true

        if(map[TodProperty.ALTITUDE]?.text.toString() == "") {
            map[TodProperty.ALTITUDE]?.hint = resources.getString(R.string.required)
            map[TodProperty.ALTITUDE]?.setHintTextColor(resources.getColor(R.color.red))
            result = false
        }

        if(map[TodProperty.DEPTH]?.text.toString() == ""){
            map[TodProperty.DEPTH]?.hint = resources.getString(R.string.required)
            map[TodProperty.DEPTH]?.setHintTextColor(resources.getColor(R.color.red))
            result = false
        }

        if(map[TodProperty.USER_STOP]?.text.toString() == ""){
            map[TodProperty.USER_STOP]?.hint = resources.getString(R.string.required)
            map[TodProperty.USER_STOP]?.setHintTextColor(resources.getColor(R.color.red))
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
    private fun setStyleEditText(et: EditText, hasFocus: Boolean, enum: TodProperty){
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
    private fun getMeasurementUnitFromEnum(unit: TodProperty): String{
        return when(unit){
            TodProperty.ALTITUDE -> "m"
            TodProperty.DEPTH -> "m"
            TodProperty.USER_STOP -> "m"
            TodProperty.TOD -> "m"
            TodProperty.STOP_CALCULATED -> "m"
        }
    }

    // Devuelve una cadena con la ayuda segun el objeto Enum
    private fun getHintFromEnum(unit: TodProperty): String{
        return when(unit){
            TodProperty.ALTITUDE -> "1800 m"
            TodProperty.DEPTH -> "30 m"
            TodProperty.USER_STOP -> "5 m"
            TodProperty.TOD -> "36 m"
            TodProperty.STOP_CALCULATED -> "4.1 m"
        }
    }

    // Dialogo con la Info de EAD
    private fun dialogInfoTod() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.what_is_tod))
            .setMessage(Html.fromHtml(resources.getString(R.string.infoTodHtml), Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton("OK") { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

}