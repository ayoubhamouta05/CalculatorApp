package com.example.testcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testcalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var GLOBAL_RESULT = 0.0
    var CURRENT_OPERATION = ""
    var SECOND_OPERAND = 0.0
    var OPERATION_CLICKED = false
    var TYPING = false
    var IS_ADVENCED = false

    private lateinit var HISTORIQUE_OPERATION : ArrayList<String>
    lateinit var operationAdapter : OperationsAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initButtons()
        hideAdvencedItems()

        HISTORIQUE_OPERATION = arrayListOf()
        setupHistoriqueRV()

    }
    private fun setupHistoriqueRV() {
        operationAdapter = OperationsAdapter()
        var manager = LinearLayoutManager(this@MainActivity)

        binding.operationsRv.apply {
            manager.reverseLayout = true
            layoutManager = manager

            adapter = operationAdapter
            operationAdapter.differ.submitList(HISTORIQUE_OPERATION)

        }
    }

    fun initButtons() {
        binding.one.setOnClickListener {
            clickDigit("1")
        }
        binding.two.setOnClickListener {
            clickDigit("2")
        }
        binding.three.setOnClickListener {
            clickDigit("3")
        }
        binding.four.setOnClickListener {
            clickDigit("4")
        }
        binding.five.setOnClickListener {
            clickDigit("5")
        }
        binding.six.setOnClickListener {
            clickDigit("6")
        }
        binding.seven.setOnClickListener {
            clickDigit("7")
        }
        binding.eight.setOnClickListener {
            clickDigit("8")
        }
        binding.nine.setOnClickListener {
            clickDigit("9")
        }
        binding.zero.setOnClickListener {
            clickDigit("0")
        }
        binding.delete.setOnClickListener {
            removeDigit()
        }
        binding.plus.setOnClickListener {
            operationClicked("+")
        }
        binding.minus.setOnClickListener {
            operationClicked("-")
        }
        binding.multiply.setOnClickListener {
            operationClicked("*")
        }
        binding.devide.setOnClickListener {
            operationClicked("/")
        }
        binding.equal.setOnClickListener {
            calcResult(CURRENT_OPERATION)
        }
        binding.ac.setOnClickListener {
            reset()
        }
        binding.sign.setOnClickListener {
            addSign()
        }
        binding.point.setOnClickListener {
            addPoint()
        }
        binding.advLayout.setOnClickListener {
           addAnimation(IS_ADVENCED)
        }
    }

    fun clickDigit(numberClicked: String) {
        if (!OPERATION_CLICKED && !TYPING) {
            binding.result.text =
                numberClicked // in case we did an operation then we clicked a digit
            GLOBAL_RESULT = 0.0
        } else if (binding.result.text.toString() == "0") {
            binding.result.text = numberClicked
        } else if (OPERATION_CLICKED) {
            OPERATION_CLICKED = false
            binding.result.text = numberClicked
        } else if (binding.result.text == "ERROR") {
            reset()
        } else {
            binding.result.text = "${binding.result.text}${numberClicked}"
        }
        TYPING = true

    }

    fun operationClicked(operation: String) {
        if (binding.result.text != "ERROR") {
            if (CURRENT_OPERATION == "") { // In case we didn't click any operation
                CURRENT_OPERATION = operation
                OPERATION_CLICKED = true
                if (GLOBAL_RESULT == 0.0) {
                    GLOBAL_RESULT = binding.result.text.toString().toDouble()
                } else {
                    calcResult(CURRENT_OPERATION)
                    CURRENT_OPERATION = ""
                }
                TYPING = false
            } else { // in case we clicked before an operation and we
                // clicked new one we have to calculate the previous
                // operation before going to the next operation
                OPERATION_CLICKED = true
                if (GLOBAL_RESULT == 0.0) {
                    GLOBAL_RESULT = binding.result.text.toString().toDouble()
                } else {
                    calcResult(CURRENT_OPERATION)
                    CURRENT_OPERATION = operation

                }
                TYPING = false
            }

        }


    }

    private fun calcResult(operation: String)  {

        val operations = mapOf(
            "+" to { x: Double, y: Double -> x + y },
            "-" to { x: Double, y: Double -> x - y },
            "*" to { x: Double, y: Double -> x * y },
            "/" to { x: Double, y: Double -> x / y }
        )
        val operatorFun = operations[operation]

        if (CURRENT_OPERATION == "/") {
            SECOND_OPERAND = binding.result.text.toString().toDouble()
            if (TYPING) {
                if (SECOND_OPERAND == 0.0) {
                    binding.result.text = "ERROR"
                    GLOBAL_RESULT = 0.0
                } else {
                    GLOBAL_RESULT /= SECOND_OPERAND
                    checkIntResult()
                    TYPING = false
                }
            }
        } else {
            SECOND_OPERAND = binding.result.text.toString().toDouble()
            if (TYPING) {
                GLOBAL_RESULT = operatorFun?.let { it(GLOBAL_RESULT, SECOND_OPERAND) }!!
                checkIntResult()
            }
        }
        TYPING = false

    }

    private fun checkIntResult(){
        if (GLOBAL_RESULT - GLOBAL_RESULT.toInt() == 0.0) {
            binding.result.text = "${GLOBAL_RESULT.toInt()}"
            HISTORIQUE_OPERATION.add(GLOBAL_RESULT.toInt().toString())
        } else {
            binding.result.text = "${GLOBAL_RESULT}"
            HISTORIQUE_OPERATION.add(GLOBAL_RESULT.toString())
        }
        // update the recycler View list
        operationAdapter.differ.submitList(HISTORIQUE_OPERATION)
        operationAdapter.notifyItemInserted(HISTORIQUE_OPERATION.size - 1)
    }

    private fun reset() {
        TYPING = false
        OPERATION_CLICKED = false
        GLOBAL_RESULT = 0.0
        CURRENT_OPERATION = ""
        binding.result.text = "0"
    }

    private fun removeDigit() {
        if (binding.result.text != "ERROR") {
            if (TYPING) {
                if (binding.result.text.toString().length > 1) {
                    binding.result.text = binding.result.text.subSequence(
                        0,
                        binding.result.text.toString().length - 1
                    )
                } else {
                    binding.result.text = "0"
                }
            }

        }

    }

    private fun addSign() {
        if(binding.result.text!="ERROR"){
            if(binding.result.text.toString()!="0"){
                if(binding.result.text.toString()[0]=='-'){
                    binding.result.text=binding.result.text.toString().replace("-","")
                }else{
                    binding.result.text = "-${binding.result.text}"
                }
                if(GLOBAL_RESULT!=0.0){
                    GLOBAL_RESULT=-GLOBAL_RESULT
                }
            }
        }
        // todo : fix the problem of sign when the sign


    }

    private fun addPoint() {
        if (binding.result.text != "ERROR") {
            if (!binding.result.text.toString().contains(".")) {
                binding.result.text = "${binding.result.text}."
                // todo : fix the bug here
            }
        }
    }

    private fun hideAdvencedItems(){
        binding.apply {
            advSquarCard.visibility = View.GONE
            advFactorialCard.visibility = View.GONE
            advUpsideDownCard.visibility = View.GONE
            advPCard.visibility = View.GONE
            advExpCard.visibility = View.GONE
            firstAdvRowLayout.visibility = View.GONE
            secondAdvRowLayout.visibility = View.GONE
        }
    }

    private fun showAdvencedItems(){
        binding.apply {
            advSquarCard.visibility = View.VISIBLE
            advFactorialCard.visibility = View.VISIBLE
            advUpsideDownCard.visibility = View.VISIBLE
            advPCard.visibility = View.VISIBLE
            advExpCard.visibility = View.VISIBLE
            firstAdvRowLayout.visibility = View.VISIBLE
            secondAdvRowLayout.visibility = View.VISIBLE

        }
    }

    private fun addAnimation(isAdvenced : Boolean){
        if (isAdvenced){
            hideAdvencedItems()
            binding.advLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_toright))
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                binding.advLayout.clearAnimation()
            }
        }else{
            showAdvencedItems()
            binding.advLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_toleft))
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                binding.advLayout.clearAnimation()
            }
        }
        IS_ADVENCED = !isAdvenced
    }


}