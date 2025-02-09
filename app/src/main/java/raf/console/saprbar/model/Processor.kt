package raf.console.saprbar.model


import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

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

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun removeRod(rodIndex: Int) {
        if (rodIndex in rods.indices) {
            rods.removeAt(rodIndex)
            if (nodes.size > 1) {
                nodes.removeLast()
            }
            updateNodeCoordinates()
        } else {
            throw IllegalArgumentException("Invalid rod index")
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

    fun setBoundaryCondition(nodeIndex: Int, displacement: Double? = null) {
        if (nodeIndex in nodes.indices) {
            nodes[nodeIndex].displacement = displacement
            nodes[nodeIndex].support = displacement != null
        } else {
            throw IllegalArgumentException("Invalid node index")
        }
    }

    fun addConcentratedForce(nodeIndex: Int, force: Double) {
        if (nodeIndex in nodes.indices) {
            nodes[nodeIndex].force += force
        } else {
            throw IllegalArgumentException("Invalid node index")
        }
    }

    fun addDistributedLoad(rodIndex: Int, load: Double) {
        if (rodIndex in rods.indices) {
            rods[rodIndex].distributedLoad = load
        } else {
            throw IllegalArgumentException("Invalid rod index")
        }
    }

    fun solveSystem() {
        val n = nodes.size
        val K = Array(n) { DoubleArray(n) }
        val F = DoubleArray(n)

        // Form global stiffness matrix and force vector
        rods.forEachIndexed { i, rod ->
            val k = rod.elasticModulus * rod.area / rod.length
            K[i][i] += k
            K[i + 1][i + 1] += k
            K[i][i + 1] -= k
            K[i + 1][i] -= k
        }

        // Account for concentrated forces at nodes
        nodes.forEachIndexed { i, node ->
            F[i] += node.force
        }

        // Account for distributed loads on rods
        rods.forEachIndexed { i, rod ->
            if (rod.distributedLoad != 0.0) {
                F[i] += rod.distributedLoad * rod.length / 2
                F[i + 1] += rod.distributedLoad * rod.length / 2
            }
        }

        // Clear diagrams for each rod before solving
        rods.forEach {
            it.forceDiagram.clear()
            it.stressDiagram.clear()
            it.displacementDiagram.clear()
        }

        // Divide degrees of freedom into free and fixed
        val freeDofs = nodes.indices.filter { nodes[it].displacement == null }
        val fixedDofs = nodes.indices.filter { nodes[it].displacement != null }

        // Set known displacements for fixed nodes
        val u = DoubleArray(n)
        fixedDofs.forEach { i ->
            u[i] = nodes[i].displacement ?: 0.0
        }

        // Solve the system of equations for free degrees of freedom
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

            // Attempt to solve directly, use pseudoinverse as fallback
            try {
                val uFree = solveLinearSystem(KFree, FFree)
                freeDofs.forEachIndexed { i, index ->
                    u[index] = uFree[i]
                }
            } catch (e: SingularMatrixException) {
                println("Singular matrix encountered, using pseudoinverse")
                var uFree = pseudoinverse(KFree) * FFree
                freeDofs.forEachIndexed { i, index ->
                    u[index] = uFree[i]
                }
            }
        }

        // Update node displacements
        nodes.forEachIndexed { i, node ->
            node.displacement = u[i]
        }

        // Calculate forces, stresses, and displacements for each rod
        rods.forEachIndexed { i, rod ->
            val deltaU = u[i + 1] - u[i]
            val numPoints = 11  // Number of points for diagrams

            if (rod.distributedLoad == 0.0) {
                // Case without distributed load
                rod.axialForce = rod.elasticModulus * rod.area / rod.length * deltaU
                rod.stress = rod.axialForce?.div(rod.area)

                // Populate diagrams
                for (j in 0 until numPoints) {
                    val x = rod.length * j / (numPoints - 1)
                    rod.forceDiagram.add(x to (rod.axialForce ?: 0.0))
                    rod.stressDiagram.add(x to (rod.stress ?: 0.0))
                    val u_x = u[i] + (deltaU * x / rod.length)
                    rod.displacementDiagram.add(x to u_x)
                }
            } else {
                // Case with distributed load
                val N0 = rod.elasticModulus * rod.area / rod.length * deltaU + rod.distributedLoad * rod.length / 2
                val N1 = N0 - rod.distributedLoad * rod.length

                rod.leftForce = N0
                rod.rightForce = N1
                rod.axialForce = N0
                rod.stress = rod.axialForce?.div(rod.area)
                rod.maxForce = max(abs(N0), abs(N1))
                rod.maxStress = rod.maxForce?.div(rod.area)

                // Populate diagrams
                for (j in 0 until numPoints) {
                    val x = rod.length * j / (numPoints - 1)
                    val Nx = N0 - rod.distributedLoad * x
                    rod.forceDiagram.add(x to Nx)
                    val sigma_x = Nx / rod.area
                    rod.stressDiagram.add(x to sigma_x
                        val u_elastic = u intArrayOf(i) + (deltaU * x / rod.length)
                    val u_distributed =
                        (rod.distributedLoad * x / (2 * rod.elasticModulus * rod.area)) * (rod.length - x)
                    val u_x = u_elastic + u_distributed
                    rod.displacementDiagram.add(x to u_x)
                }
            }

            rod.calculateSafetyFactor()
        }
    }

    private fun solveLinearSystem(matrix: Array<DoubleArray>, vector: DoubleArray): DoubleArray {
        val n = vector.size
        val augmentedMatrix = Array(n) { DoubleArray(n + 1) }

        // Create augmented matrix [A|b]
        for (i in 0 until n) {
            for (j in 0 until n) {
                augmentedMatrix[i][j] = matrix[i][j]
            }
            augmentedMatrix[i][n] = vector[i]
        }

        // Gaussian elimination (forward elimination)
        for (i in 0 until n) {
            // Find pivot element in column i
            var maxElement = abs(augmentedMatrix[i][i])
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(augmentedMatrix[k][i]) > maxElement) {
                    maxElement = abs(augmentedMatrix[k][i])
                    maxRow = k
                }
            }

            // Swap rows if necessary
            if (maxRow != i) {
                val temp = augmentedMatrix[i]
                augmentedMatrix[i] = augmentedMatrix[maxRow]
                augmentedMatrix[maxRow] = temp
            }

            // Make all rows below pivot 0
            if (augmentedMatrix[i][i] == 0.0) {
                throw SingularMatrixException("Singular matrix encountered during Gaussian elimination")
            }
            for (k in i + 1 until n) {
                val factor = augmentedMatrix[k][i] / augmentedMatrix[i][i]
                for (j in i until n + 1) {
                    augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j]
                }
            }
        }

        // Back-substitution
        val solution = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            solution[i] = augmentedMatrix[i][n]
            for (j in i + 1 until n) {
                solution[i] -= augmentedMatrix[i][j] * solution[j]
            }
            solution[i] /= augmentedMatrix[i][i]
        }

        return solution
    }

    // Function for calculating pseudoinverse using SVD
    fun pseudoinverse(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val m = matrix.size
        val n = matrix[0].size

        // Convert the Kotlin matrix to a format suitable for the algorithm
        val a = Array(m) { DoubleArray(n) }
        for (i in 0 until m) {
            for (j in 0 until n) {
                a[i][j] = matrix[i][j]
            }
        }

        // Perform SVD
        val svd = SVD(a)
        val U = svd.u
        val S = svd.s
        val V = svd.v

        // Create the diagonal matrix with the singular values
        val SInv = Array(n) { DoubleArray(m) }
        for (i in 0 until minOf(m, n)) {
            SInv[i][i] = if (S[i] > 1e-15) 1.0 / S[i] else 0.0  // Use a tolerance to avoid division by zero
        }

        // Calculate the pseudoinverse
        val U_T = transpose(U)
        val V_T = transpose(V)

        val temp = multiply(V, SInv)
        return multiply(temp, U_T)
    }

    // Function for matrix multiplication
    fun multiply(A: Array<DoubleArray>, B: Array<DoubleArray>): Array<DoubleArray> {
        val m = A.size
        val n = A[0].size
        val p = B[0].size
        val C = Array(m) { DoubleArray(p) }
        for (i in 0 until m) {
            for (j in 0 until p) {
                for (k in 0 until n) {
                    C[i][j] += A[i][k] * B[k][j]
                }
            }
        }
        return C
    }

    // Function for transposing a matrix
    fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val m = matrix.size
        val n = matrix[0].size
        val transposedMatrix = Array(n) { DoubleArray(m) }
        for (i in 0 until m) {
            for (j in 0 until n) {
                transposedMatrix[j][i] = matrix[i][j]
            }
        }
        return transposedMatrix
    }

    fun validateData(): Boolean {
        if (rods.isEmpty()) {
            throw IllegalArgumentException("No rods in the structure.")
        }
        if (nodes.size != rods.size + 1) {
            throw IllegalArgumentException("Incorrect number of nodes.")
        }
        val fixedNodes = nodes.count { it.displacement != null }
        if (fixedNodes == 0) {
            throw IllegalArgumentException("Structure is not constrained. Fix at least one node.")
        }
        for (i in rods.indices) {
            if (rods[i].stress != null) {
                if (abs(rods[i].stress!!) > rods[i].allowableStress) {
                    throw IllegalArgumentException("Stress in rod ${i + 1} exceeds allowable stress!")
                }
                rods[i].calculateSafetyFactor()
                if (rods[i].safetyFactor != null && rods[i].safetyFactor!! < 1.1) {
                    throw IllegalArgumentException("Insufficient safety factor in rod ${i + 1}: ${rods[i].safetyFactor!!}")
                }
            }
        }
        return true
    }

    fun getResults(): List<Map<String, Any?>> {
        val results = mutableListOf<Map<String, Any?>>()
        rods.forEachIndexed { i, rod ->
            val deltaAxialForce = rod.axialForce?.minus(rods.getOrNull(i - 1)?.axialForce ?: 0.0)
            val deltaStress = rod.stress?.minus(rods.getOrNull(i - 1)?.stress ?: 0.0)

            results.add(
                mapOf(
                    "rod_number" to i + 1,
                    "length" to rod.length,
                    "axial_force" to rod.axialForce,
                    "delta_axial_force" to deltaAxialForce,
                    "stress" to rod.stress,
                    "delta_stress" to deltaStress,
                    "displacement" to rod.displacement,
                    "allowable_stress" to rod.allowableStress,
                    "safety_factor" to rod.safetyFactor
                )
            )
        }
        return results
    }

    fun clearProject() {
        rods.clear()
        nodes.clear()
    }
}

class SingularMatrixException(message: String) : Exception(message)

// Helper class for Singular Value Decomposition (SVD)
class SVD(A: Array<DoubleArray>) {
    var u: Array<DoubleArray>
    var s: DoubleArray
    var v: Array<DoubleArray>

    init {
        val m = A.size
        val n = A[0].size
        u = Array(m) { DoubleArray(m) }
        s = DoubleArray(minOf(m, n))
        v = Array(n) { DoubleArray(n) }

        // Perform SVD using the JAMA library
        val aJAMA = org.jama.Matrix(A, m, n)
        val svdJAMA = aJAMA.singularValueDecomposition

        // Copy the results to the class fields
        val UJAMA = svdJAMA.u
        val SJAMA = svdJAMA.singularValues
        val VJAMA = svdJAMA.v

        for (i in 0 until m) {
            for (j in 0 until m) {
                u[i][j] = UJAMA[i][j]
            }
        }
        for (i in 0 until minOf(m, n)) {
            s[i] = SJAMA[i]
        }
        for (i in 0 until n) {
            for (j in 0 until n) {
                v[i][j] = VJAMA[i][j]
            }
        }
    }
}

