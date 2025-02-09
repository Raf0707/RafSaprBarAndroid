package raf.console.saprbar.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import raf.console.saprbar.viewmodel.MainViewModel

@Composable
fun ResultsScreen(viewModel: MainViewModel) {
    val processor by viewModel.processor.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(processor.rods) { rod ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Стержень: Длина = ${rod.length}, Напряжение = ${rod.stress}")
                Text("Усилие: ${rod.axialForce}, Коэффициент запаса: ${rod.safetyFactor}")
            }
        }
    }
}