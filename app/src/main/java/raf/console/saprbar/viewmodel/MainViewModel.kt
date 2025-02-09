package raf.console.saprbar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import raf.console.saprbar.model.Processor
import raf.console.saprbar.model.Rod

class MainViewModel : ViewModel() {
    private val _processor = MutableStateFlow(Processor())
    val processor: StateFlow<Processor> get() = _processor

    var length = ""
    var area = ""
    var elasticModulus = ""
    var allowableStress = ""
    var distributedLoad = ""
    var force = ""
    var nodeIndex = ""
    var rodIndex = ""

    fun addRod() {
        viewModelScope.launch {
            val rod = Rod(
                length = length.toDoubleOrNull() ?: 0.0,
                area = area.toDoubleOrNull() ?: 0.0,
                elasticModulus = elasticModulus.toDoubleOrNull() ?: 0.0,
                allowableStress = allowableStress.toDoubleOrNull() ?: 0.0
            )
            _processor.value.addRod(rod)
        }
    }

    fun removeRod(index: Int) {
        viewModelScope.launch {
            _processor.value.removeRod(index)
        }
    }

    fun addForce() {
        viewModelScope.launch {
            val index = nodeIndex.toIntOrNull() ?: return@launch
            val forceValue = force.toDoubleOrNull() ?: return@launch
            _processor.value.nodes.getOrNull(index)?.force = forceValue
        }
    }

    fun addDistributedLoad() {
        viewModelScope.launch {
            val index = rodIndex.toIntOrNull() ?: return@launch
            val load = distributedLoad.toDoubleOrNull() ?: return@launch
            _processor.value.rods.getOrNull(index)?.distributedLoad = load
        }
    }

    fun solveSystem() {
        viewModelScope.launch {
            _processor.value.solveSystem()
        }
    }
}