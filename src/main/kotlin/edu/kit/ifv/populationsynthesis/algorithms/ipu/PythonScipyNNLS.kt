package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import java.io.BufferedOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteRecursively
import kotlin.math.max

object PythonScipyNNLS: GenericIPU {




    @OptIn(ExperimentalPathApi::class)
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>,
    ) {

        val tempDir = Files.createTempDirectory("nnls_py_")
        try {



            val mtxFile: Path = tempDir.resolve("mtx.bin")
            val vecFile: Path =tempDir.resolve("vec.bin")
            val resultFile: Path = tempDir.resolve("out.bin")
            val startFile = tempDir.resolve("start.bin")
            val pythonExe: Path = Path("C:/Repositories/Privat/fi7528/synthesislibrary/python/venv/Scripts" +
                    "/python.exe")
            val pythonScript: Path = Path("C:/Repositories/Privat/fi7528/synthesislibrary/python" +
                    "/nnls_worker.py")
            val valueArray = vectors.flatMap { it.signature.toList(observers.size) }.toDoubleArray()
            val targetArray = observers.map { it.expected }.toDoubleArray()

            valueArray.writeTo(mtxFile)
            targetArray.writeTo(vecFile)
            vectors.map { it.scalar }.toDoubleArray().writeTo(startFile)

            val process = ProcessBuilder(
                pythonExe.absolutePathString(),
                pythonScript.absolutePathString(),
                observers.size.toString(),
                vectors.size.toString(),
                mtxFile.absolutePathString(),
                vecFile.absolutePathString(),
                resultFile.absolutePathString(),
                startFile.absolutePathString()

                ).redirectErrorStream(true).start()

            val exitCode = process.waitFor()
            require(exitCode == 0) {
                "Python failed with code $exitCode"
            }

            val output = resultFile.readDoubleArray()
            vectors.zip(output.toList()).forEach { (vector, d) ->
                vector.scalar = d
            }
            TODO("load in vectors based on input")
        } finally {
            tempDir.deleteRecursively()
        }



    }

    private fun DoubleArray.writeTo(path: Path) {
        BufferedOutputStream(Files.newOutputStream(path)).use { out ->
            val buffer = ByteBuffer.allocate(size * Double.SIZE_BYTES).order(ByteOrder.LITTLE_ENDIAN)
            forEach { buffer.putDouble(it) }
            out.write(buffer.array())

        }
    }

    private fun Path.readDoubleArray(): DoubleArray {
        val bytes = toFile().readBytes()
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return DoubleArray(bytes.size / Double.SIZE_BYTES) {buffer.double}

    }

    private fun Signature.toList(maxValue: Int): List<Double> {
        return (0 until maxValue).map { this[it] }
    }
}