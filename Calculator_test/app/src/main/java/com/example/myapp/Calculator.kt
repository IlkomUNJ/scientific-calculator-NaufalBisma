package com.example.myapp

import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val buttonlist = listOf(
    "sin", "cos","tan", "log","ln",
    "asin","acos","atan","√","π",
    "C","(",")","xʸ","x!",
    "7","8","9","1/x","⌫",
    "4","5","6","+","*",
    "1","2","3","-","/",
    "AC","0", ".","%","=",
)

@Composable
fun Calculator(modifier: Modifier = Modifier, viewModel: CalculatorViewModel) {

    val equationText = viewModel.equationText.observeAsState()
    val resultText = viewModel.resultText.observeAsState()

    Box(modifier = modifier){
        Column (
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ){
            Text(
                text = equationText.value?:"",
                style = TextStyle(
                    fontSize = 30.sp,
                    textAlign = TextAlign.End
                ),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = resultText.value?:"",
                style = TextStyle(
                    fontSize = 60.sp,
                    textAlign = TextAlign.End
                ),
                maxLines = 2,
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
            ) {
                items(buttonlist) { btn ->
                    // mapping label untuk ditampilkan
                    val label = when (btn) {
                        "asin" -> "sin⁻¹"
                        "acos" -> "cos⁻¹"
                        "atan" -> "tan⁻¹"
                        else -> btn
                    }
                    CalculatorButton(
                        btn = label, // teks yang muncul di UI
                        onClick = { viewModel.onButtonClick(btn) } // logic tetap pakai "asin", "acos", "atan"
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(btn : String, onClick : ()-> Unit) {
    val isNumber = btn.all { it.isDigit() } || btn == "."
    val backgroundColor = if (isNumber) {
        Color.Gray
    } else {
        Color.DarkGray
    }
    Box (modifier = Modifier.padding(5.dp)){
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(10),
            containerColor = backgroundColor,
            contentColor = Color.White,
        ) {
            Text(text = btn, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}