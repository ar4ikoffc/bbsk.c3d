package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.BadRequestException
import io.github.bbsk.c5d.model.reference.dto.bulk.TravelMatrixRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

@Service
class TravelMatrixService {

    private val log = LoggerFactory.getLogger(TravelMatrixService::class.java)

    private val currentMatrixId = AtomicReference<String?>(null)
    private val pointIndexMap = ConcurrentHashMap<String, Int>()

    @Volatile
    private var durations: Array<IntArray> = emptyArray()

    @Volatile
    private var distances: Array<IntArray> = emptyArray()

    @Volatile
    private var cachedPoints: List<String> = emptyList()

    fun save(request: TravelMatrixRequest): Int {
        val n = request.points.size
        if (request.durationsSec.size != n) {
            throw BadRequestException(
                "INVALID_MATRIX_DIMENSIONS",
                "Размерность durations_sec (${request.durationsSec.size}) не совпадает с количеством точек points ($n)"
            )
        }
        if (request.distancesM.size != n) {
            throw BadRequestException(
                "INVALID_MATRIX_DIMENSIONS",
                "Размерность distances_m (${request.distancesM.size}) не совпадает с количеством точек points ($n)"
            )
        }

        val newPointIndexMap = ConcurrentHashMap<String, Int>(n)
        request.points.forEachIndexed { index, point ->
            newPointIndexMap[point] = index
        }

        val newDurations = Array(n) { i ->
            request.durationsSec[i].toIntArray()
        }
        val newDistances = Array(n) { i ->
            request.distancesM[i].toIntArray()
        }

        currentMatrixId.set(request.matrixId)
        pointIndexMap.clear()
        pointIndexMap.putAll(newPointIndexMap)
        durations = newDurations
        distances = newDistances
        cachedPoints = request.points

        log.info("Матрица перемещений '{}' успешно сохранена. Точек: {}", request.matrixId, n)
        return n
    }

    fun isLoaded(): Boolean = currentMatrixId.get() != null

    fun getMatrixId(): String? = currentMatrixId.get()

    fun getPoints(): List<String> = cachedPoints

    fun getDurationSec(from: String, to: String): Int? {
        val i = pointIndexMap[from] ?: return null
        val j = pointIndexMap[to] ?: return null
        return durations.getOrNull(i)?.getOrNull(j)
    }

    fun getDistanceM(from: String, to: String): Int? {
        val i = pointIndexMap[from] ?: return null
        val j = pointIndexMap[to] ?: return null
        return distances.getOrNull(i)?.getOrNull(j)
    }
}
