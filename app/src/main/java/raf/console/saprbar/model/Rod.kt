package raf.console.saprbar.model

import kotlin.math.abs

data class Rod(
    val length: Double,
    val area: Double,
    val elasticModulus: Double,
    val allowableStress: Double
) {
    var distributedLoad: Double = 0.0
    var axialForce: Double? = null
    var stress: Double? = null
    var displacement: Double? = null
    var safetyFactor: Double? = null
    val forceDiagram: MutableList<Pair<Double, Double>> = mutableListOf()
    val stressDiagram: MutableList<Pair<Double, Double>> = mutableListOf()
    val displacementDiagram: MutableList<Pair<Double, Double>> = mutableListOf()

    fun calculateSafetyFactor() {
        stress?.let { stress ->
            safetyFactor = if (stress != 0.0) allowableStress / abs(stress) else Double.POSITIVE_INFINITY
        }
    }
}