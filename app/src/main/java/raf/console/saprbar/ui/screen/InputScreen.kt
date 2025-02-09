package raf.console.saprbar.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import raf.console.saprbar.viewmodel.MainViewModel

@Composable
fun InputScreen(viewModel: MainViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = viewModel.length,
            onValueChange = { viewModel.length = it },
            label = { Text("Длина стержня (L)") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = viewModel.area,
            onValueChange = { viewModel.area = it },
            label = { Text("Площадь сечения (A)") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = viewModel.elasticModulus,
            onValueChange = { viewModel.elasticModulus = it },
            label = { Text("Модуль упругости (E)") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = viewModel.allowableStress,
            onValueChange = { viewModel.allowableStress = it },
            label = { Text("Допускаемое напряжение (σ)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.addRod() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить стержень")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.nodeIndex,
            onValueChange = { viewModel.nodeIndex = it },
            label = { Text("Индекс узла") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = viewModel.force,
            onValueChange = { viewModel.force = it },
            label = { Text("Сосредоточенная сила (F)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.addForce() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить силу")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.rodIndex,
            onValueChange = { viewModel.rodIndex = it },
            label = { Text("Индекс стержня") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = viewModel.distributedLoad,
            onValueChange = { viewModel.distributedLoad = it },
            label = { Text("Распределенная нагрузка (q)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.addDistributedLoad() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить распределенную нагрузку")
        }
    }
}