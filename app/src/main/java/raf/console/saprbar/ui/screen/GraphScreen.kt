package raf.console.saprbar.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import raf.console.saprbar.viewmodel.MainViewModel

@Composable
fun GraphScreen(viewModel: MainViewModel) {
    val processor by viewModel.processor.collectAsState()

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Отрисовка графиков
        processor.rods.forEach { rod ->
            rod.forceDiagram.forEach { (x, force) ->
                drawCircle(Color.Blue, radius = 4f, center = Offset(x.toFloat(), force.toFloat()))
            }
        }
    }
}