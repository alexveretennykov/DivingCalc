package com.example.divecalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import com.example.divecalculator.databinding.SacFragmentBinding

class SacFragment: Fragment() {
    private lateinit var binding: SacFragmentBinding
    private lateinit var listBottles: List<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SacFragmentBinding.inflate(inflater, container, false)

        // Inicializa los listeners de Botones 'Eliminar Botella'
        initListenerDeleteButtons()

        // Guarda los bloques de botellas en una lista
        listBottles = getAllBottleLayouts()

        // Oculta todos los layouts, menos el primero
        hideBottleLayouts()

        // Boton Añadir Botellas
        binding.btnAddBottle.setOnClickListener {
            addBottle()
        }

        // Boton Calcular SAC
        binding.btnCalcSac.setOnClickListener {
            calcTotalSac()
        }

        return binding.root
    }

    private fun addBottle(){
        // run lit@ -> break
        run lit@{
            listBottles.forEach {
                if (it.visibility == View.GONE) {
                    it.visibility = View.VISIBLE
                    return@lit
                }
            }
        }
    }

    private fun getAllBottleLayouts(): List<LinearLayout>{
        val list: MutableList<LinearLayout> = mutableListOf()

        list += binding.linearLayoutBottle1
        list += binding.linearLayoutBottle2
        list += binding.linearLayoutBottle3

        return list.toList()
    }

    // Click Listeners de la imagen de ocultar botellas
    private fun initListenerDeleteButtons(){

        binding.btnDeleteBottle1.setOnClickListener {
            binding.linearLayoutBottle1.visibility = View.GONE
        }

        binding.btnDeleteBottle2.setOnClickListener {
            binding.linearLayoutBottle2.visibility = View.GONE
        }

        binding.btnDeleteBottle3.setOnClickListener {
            binding.linearLayoutBottle3.visibility = View.GONE
        }

    }

    // Oculta todos los layout, menos el primero
    private fun hideBottleLayouts(){
        listBottles.forEach { it.visibility = View.GONE }

        listBottles[0].visibility = View.VISIBLE
    }

    // Calcula SAC Total
    private fun calcTotalSac(){
        var total = 0.0
        var count = 0

        val sac1 = calcSac1()
        val sac2 = calcSac2()
        val sac3 = calcSac3()

        if(sac1 > 0){
            total += sac1
            count += 1
        }

        if(sac2 > 0){
            total += sac2
            count += 1
        }

        if(sac3 > 0){
            total += sac3
            count += 1
        }

        if(total/count > 0) {
            total /= count
            val result = String.format("%.2f", total) + " l/min"
            binding.tvTotalSac.text = result
        }
    }

    // Calcula SAC - Botella 2
    private fun calcSac1():Double{
        if(binding.linearLayoutBottle1.visibility == View.VISIBLE && checkNotNullTextView1()){
            // Presion Consumida
            val bar = binding.etInitPressure1.text.toString().toInt() - binding.etFinPressure1.text.toString().toInt()
            // Litros de Gas Consumidos
            val liters = bar * binding.etBottleVolume1.text.toString().toInt()
            // ATA profundidad
            val depth = binding.etAvg1.text.toString().toDouble() / 10 + 1
            // SAC
            val sac = (liters / binding.etDiveTime1.text.toString().toInt()) / depth

            return sac
        }else{
            return -1.0
        }
    }

    // Calcula SAC - Botella 2
    private fun calcSac2():Double{
        if(binding.linearLayoutBottle2.visibility == View.VISIBLE && checkNotNullTextView2()){
            // Presion Consumida
            val bar = binding.etInitPressure2.text.toString().toInt() - binding.etFinPressure2.text.toString().toInt()
            // Litros de Gas Consumidos
            val liters = bar * binding.etBottleVolume2.text.toString().toInt()
            // ATA profundidad
            val depth = binding.etAvg2.text.toString().toDouble() / 10 + 1
            // SAC
            val sac = (liters / binding.etDiveTime2.text.toString().toInt()) / depth

            return sac
        }else{
            return -1.0
        }
    }

    // Calcula SAC - Botella 3
    private fun calcSac3():Double{
        if(binding.linearLayoutBottle3.visibility == View.VISIBLE && checkNotNullTextView3()){
            // Presion Consumida
            val bar = binding.etInitPressure3.text.toString().toInt() - binding.etFinPressure3.text.toString().toInt()
            // Litros de Gas Consumidos
            val liters = bar * binding.etBottleVolume3.text.toString().toInt()
            // ATA profundidad
            val depth = binding.etAvg3.text.toString().toDouble() / 10 + 1
            // SAC
            val sac = (liters / binding.etDiveTime3.text.toString().toInt()) / depth

            return sac
        }else{
            return -1.0
        }
    }

    // Comprueba que no hayan campos vacios - Botella 1
    private fun checkNotNullTextView1():Boolean{
        var result = true

        if(binding.etInitPressure1.text.isEmpty()) {
            result = false
            binding.etInitPressure1.hint = "required"
            binding.etInitPressure1.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etInitPressure1.hint = ""
        }

        if(binding.etFinPressure1.text.isEmpty()){
            result = false
            binding.etFinPressure1.hint = "required"
            binding.etFinPressure1.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etFinPressure1.hint = ""
        }

        if(binding.etBottleVolume1.text.isEmpty()){
            result = false
            binding.etBottleVolume1.hint = "required"
            binding.etBottleVolume1.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etBottleVolume1.hint = ""
        }

        if(binding.etAvg1.text.isEmpty()){
            result = false
            binding.etAvg1.hint = "required"
            binding.etAvg1.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etAvg1.hint = ""
        }

        if(binding.etDiveTime1.text.isEmpty()){
            result = false
            binding.etDiveTime1.hint = "required"
            binding.etDiveTime1.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etDiveTime1.hint = ""
        }

        return result
    }

    // Comprueba que no hayan campos vacios - Botella 2
    private fun checkNotNullTextView2():Boolean{
        var result = true

        if(binding.etInitPressure2.text.isEmpty()) {
            result = false
            binding.etInitPressure2.hint = "required"
            binding.etInitPressure2.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etInitPressure2.hint = ""
        }

        if(binding.etFinPressure2.text.isEmpty()){
            result = false
            binding.etFinPressure2.hint = "required"
            binding.etFinPressure2.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etFinPressure2.hint = ""
        }

        if(binding.etBottleVolume2.text.isEmpty()){
            result = false
            binding.etBottleVolume2.hint = "required"
            binding.etBottleVolume2.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etBottleVolume2.hint = ""
        }

        if(binding.etAvg2.text.isEmpty()){
            result = false
            binding.etAvg2.hint = "required"
            binding.etAvg2.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etAvg2.hint = ""
        }

        if(binding.etDiveTime2.text.isEmpty()){
            result = false
            binding.etDiveTime2.hint = "required"
            binding.etDiveTime2.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etDiveTime2.hint = ""
        }

        return result
    }

    // Comprueba que no hayan campos vacios - Botella 3
    private fun checkNotNullTextView3():Boolean{
        var result = true

        if(binding.etInitPressure3.text.isEmpty()) {
            result = false
            binding.etInitPressure3.hint = "required"
            binding.etInitPressure3.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etInitPressure3.hint = ""
        }

        if(binding.etFinPressure3.text.isEmpty()){
            result = false
            binding.etFinPressure3.hint = "required"
            binding.etFinPressure3.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etFinPressure3.hint = ""
        }

        if(binding.etBottleVolume3.text.isEmpty()){
            result = false
            binding.etBottleVolume3.hint = "required"
            binding.etBottleVolume3.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etBottleVolume3.hint = ""
        }

        if(binding.etAvg3.text.isEmpty()){
            result = false
            binding.etAvg3.hint = "required"
            binding.etAvg3.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etAvg3.hint = ""
        }

        if(binding.etDiveTime3.text.isEmpty()){
            result = false
            binding.etDiveTime3.hint = "required"
            binding.etDiveTime3.setHintTextColor(resources.getColor(R.color.red))
        }else{
            binding.etDiveTime3.hint = ""
        }

        return result
    }

}