package com.example.myapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {

    private val _equationText = MutableLiveData("")
    val equationText: LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText: LiveData<String> = _resultText

    private var isInverse = false

    private val factorial = object : Function("fact", 1) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toInt()
            if (n < 0 || n != args[0].toInt()) {
                throw IllegalArgumentException("Argument factorial harus bilangan bulat >= 0")
            }
            var result = 1.0
            for (i in 2..n) result *= i
            return result
        }
    }
    // Inverse trig dalam derajat
    private val asinFunc = object : Function("asin", 1) {
        override fun apply(vararg args: Double): Double {
            return Math.toDegrees(Math.asin(args[0]))
        }
    }
    private val acosFunc = object : Function("acos", 1) {
        override fun apply(vararg args: Double): Double {
            return Math.toDegrees(Math.acos(args[0]))
        }
    }
    private val atanFunc = object : Function("atan", 1) {
        override fun apply(vararg args: Double): Double {
            return Math.toDegrees(Math.atan(args[0]))
        }
    }

    fun onButtonClick(btn: String) {
        val current = _equationText.value ?: ""

        when (btn) {
            "AC" -> {
                _equationText.value = ""
                _resultText.value = "0"
                return
            }

            "C" -> {
                if (current.isNotEmpty()) {
                    val lastOpIndex = current.lastIndexOfAny(charArrayOf('+', '-', '*', '/', '^'))
                    _equationText.value = if (lastOpIndex >= 0) {
                        current.substring(0, lastOpIndex + 1)
                    } else {
                        ""
                    }
                }
                return
            }

            "⌫" -> {
                if (current.isNotEmpty()) {
                    _equationText.value = current.dropLast(1)
                }
                return
            }

            "=" -> {
                val res = evaluateExpression(current)
                _resultText.value = res
                _equationText.value = if (res != "Error") res else ""
                return
            }

            "inv" -> {
                isInverse = !isInverse
                return
            }

            "sin", "cos", "tan",
            "asin","acos","atan",
            "log", "ln", "√" -> {
                _equationText.value = "$current$btn("
                return
            }

            "x!" -> {
                _equationText.value = "$current" + "fact("
                return
            }

            "xʸ" -> {
                _equationText.value = "$current^"
                return
            }

            "π" -> {
                _equationText.value = "$current${Math.PI}"
                return
            }

            "÷" -> {
                _equationText.value = "$current/"
                return
            }

            "×" -> {
                _equationText.value = "$current*"
                return
            }
            "1/x" -> {
                if (current.isNotEmpty()) {
                    _equationText.value = "1/($current)"
                } else {
                    _equationText.value = "1/"
                }
                return
            }

            else -> {
                _equationText.value = "$current$btn"
            }
        }
    }

    private fun evaluateExpression(exp: String): String {
        return try {
            val sanitized = exp
                .replace("√", "sqrt")
                .replace("ln", "log")
                .replace(Regex("(?<!a)sin\\(([^)]+)\\)"), "sin(toRadians($1))")
                .replace(Regex("(?<!a)cos\\(([^)]+)\\)"), "cos(toRadians($1))")
                .replace(Regex("(?<!a)tan\\(([^)]+)\\)"), "tan(toRadians($1))")

            val toRadians = object : Function("toRadians", 1) {
                override fun apply(vararg args: Double): Double {
                    return Math.toRadians(args[0])
                }
            }
            val toDegrees = object : Function("toDegrees", 1) {
                override fun apply(vararg args: Double): Double {
                    return Math.toDegrees(args[0])
                }
            }

            val expression = ExpressionBuilder(sanitized)
                .function(factorial)
                .function(toRadians)
                .function(toDegrees)
                .function(asinFunc)
                .function(acosFunc)
                .function(atanFunc)
                .build()

            val result = expression.evaluate()
            val df = DecimalFormat("#,###.#######")
            df.format(result)
        } catch (e: Exception) {
            "Error"
        }
    }
}

