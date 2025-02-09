package raf.console.saprbar.model

import android.annotation.SuppressLint

class Processor {
    val rods: MutableList<Rod> = mutableListOf()
    val nodes: MutableList<Node> = mutableListOf()

    fun addRod(rod: Rod) {
        rods.add(rod)
        if (nodes.isEmpty()) {
            nodes.add(Node())
            nodes.add(Node())
        } else {
            nodes.add(Node())
        }
        updateNodeCoordinates()
    }

    @SuppressLint("NewApi")
    fun removeRod(index: Int) {
        if (index in rods.indices) {
            rods.removeAt(index)
            if (nodes.size > 1) {
                nodes.removeLast()
            }
            updateNodeCoordinates()
        }
    }

    private fun updateNodeCoordinates() {
        var x = 0.0
        nodes.forEachIndexed { i, node ->
            node.x = x
            if (i < rods.size) {
                x += rods[i].length
            }
        }
    }

    fun solveSystem() {
        val n = nodes.size
        val K = Array(n) { DoubleArray(n) }
        val F = DoubleArray(n)

        // Формируем матрицу жесткости и вектор сил
        rods.forEachIndexed { i, rod ->
            val k = rod.elasticModulus * rod.area / rod.length
            K[i][i] += k
            K[i + 1][i + 1] += k
            K[i][i + 1] -= k
            K[i + 1][i] -= k
        }

        nodes.forEachIndexed { i, node ->
            F[i] += node.force
        }

        rods.forEachIndexed { i, rod ->
            if (rod.distributedLoad != 0.0) {
                F[i] += rod.distributedLoad * rod.length / 2
                F[i + 1] += rod.distributedLoad * rod.length / 2
            }
        }

        // Решаем систему уравнений
        val freeDofs = nodes.indices.filter { nodes[it].displacement == null }
        val fixedDofs = nodes.indices.filter { nodes[it].displacement != null }

        val u = DoubleArray(n)
        fixedDofs.forEach { i ->
            u[i] = nodes[i].displacement ?: 0.0
        }

        if (freeDofs.isNotEmpty()) {
            val KFree = Array(freeDofs.size) { i ->
                DoubleArray(freeDofs.size) { j ->
                    K[freeDofs[i]][freeDofs[j]]
                }
            }
            val FFree = DoubleArray(freeDofs.size) { i ->
                F[freeDofs[i]] - fixedDofs.sumOf { j ->
                    K[freeDofs[i]][j] * u[j]
                }
            }

            val uFree = solveLinearSystem(KFree, FFree)
            freeDofs.forEachIndexed { i, index ->
                u[index] = uFree[i]
            }
        }

        nodes.forEachIndexed { i, node ->
            node.displacement = u[i]
        }

        rods.forEachIndexed { i, rod ->
            val deltaU = u[i + 1] - u[i]
            if (rod.distributedLoad == 0.0) {
                rod.axialForce = rod.elasticModulus * rod.area / rod.length * deltaU
                rod.stress = rod.axialForce?.div(rod.area)
                rod.calculateSafetyFactor()
            } else {
                // Логика для распределенной нагрузки
            }
        }
    }

    private fun solveLinearSystem(matrix: Array<DoubleArray>, vector: DoubleArray): DoubleArray {
        // Реализация решения системы линейных уравнений
        return vector // Заглушка
    }
}