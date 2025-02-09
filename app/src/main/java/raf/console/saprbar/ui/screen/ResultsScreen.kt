package raf.console.saprbar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import raf.console.saprbar.viewmodel.MainViewModel

@Composable
fun ResultsScreen(viewModel: MainViewModel) {
    val processor by viewModel.processor.collectAsState()

    // Заголовки таблицы
    val headers = listOf(
        "№ стержня",
        "Длина (L)",
        "Площадь (A)",
        "Модуль упр. (E)",
        "Допускаемое напряжение",
        "Коэффициент запаса",
        "Нагрузка q",
        "N начало",
        "N конец",
        "σ начало",
        "σ конец",
        "Перемещение начало",
        "Перемещение конец"
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        // Заголовок таблицы
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                headers.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Данные таблицы
        items(processor.rods) { rod ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // № стержня
                Text(
                    text = "${processor.rods.indexOf(rod) + 1}",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Длина (L)
                Text(
                    text = "%.6f".format(rod.length),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Площадь (A)
                Text(
                    text = "%.6f".format(rod.area),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Модуль упр. (E)
                Text(
                    text = "%.6f".format(rod.elasticModulus),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Допускаемое напряжение
                Text(
                    text = "%.6f".format(rod.allowableStress),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Коэффициент запаса
                Text(
                    text = rod.safetyFactor?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Нагрузка q
                Text(
                    text = "%.6f".format(rod.distributedLoad),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // N начало
                Text(
                    text = rod.forceDiagram.firstOrNull()?.second?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // N конец
                Text(
                    text = rod.forceDiagram.lastOrNull()?.second?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // σ начало
                Text(
                    text = rod.stressDiagram.firstOrNull()?.second?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // σ конец
                Text(
                    text = rod.stressDiagram.lastOrNull()?.second?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Перемещение начало
                Text(
                    text = rod.displacementDiagram.firstOrNull()?.second?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )

                // Перемещение конец
                Text(
                    text = rod.displacementDiagram.lastOrNull()?.second?.let { "%.6f".format(it) } ?: "N/A",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}