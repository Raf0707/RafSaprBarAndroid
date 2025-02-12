package raf.console.saprbar.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import raf.console.saprbar.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf("input") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("САПР для расчета напряжений") })
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = currentScreen == "input",
                    onClick = { currentScreen = "input" },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Ввод") },
                    label = { Text("Ввод") }
                )
                BottomNavigationItem(
                    selected = currentScreen == "results",
                    onClick = { currentScreen = "results" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Результаты") },
                    label = { Text("Результаты") }
                )
                BottomNavigationItem(
                    selected = currentScreen == "graph",
                    onClick = { currentScreen = "graph" },
                    icon = { Icon(Icons.Default.ShowCharts, contentDescription = "Графики") },
                    label = { Text("Графики") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                "input" -> InputScreen(viewModel)
                "results" -> ResultsScreen(viewModel)
                "graph" -> GraphScreen(viewModel)
            }
        }
    }
}