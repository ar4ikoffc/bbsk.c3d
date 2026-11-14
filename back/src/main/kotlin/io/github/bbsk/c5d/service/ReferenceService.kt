package io.github.bbsk.c5d.service

import io.github.bbsk.c5d.exception.NotFoundException
import io.github.bbsk.c5d.model.reference.dto.bulk.LoadResultResponse
import io.github.bbsk.c5d.model.reference.dto.bulk.ReferenceDataRequest
import io.github.bbsk.c5d.model.reference.entity.*
import io.github.bbsk.c5d.model.reference.enums.WorkTypeCode
import io.github.bbsk.c5d.repository.*
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ReferenceService(
    private val entityManager: EntityManager,
    private val serviceCenterRepository: ServiceCenterRepository,
    private val accessNodeRepository: AccessNodeRepository,
    private val buildingRepository: BuildingRepository,
    private val workTypeRepository: WorkTypeRepository,
    private val shiftRepository: ShiftRepository,
    private val staffRepository: StaffRepository
) {

    private val log = LoggerFactory.getLogger(ReferenceService::class.java)

    @Transactional
    fun loadReferenceData(request: ReferenceDataRequest): LoadResultResponse {
        log.info("Начата полная перезагрузка справочных данных...")

        // 1. Очистка существующих данных в строгом порядке целостности внешних ключей
        cleanupDatabase()

        // 2. Сервисные центры
        val savedServiceCenters = mutableMapOf<String, ServiceCenter>()
        for (scDto in request.serviceCenters) {
            val sc = ServiceCenter(
                id = UUID.randomUUID(),
                code = scDto.serviceCenterId.trim(),
                name = scDto.name.trim(),
                address = scDto.address.trim(),
                lat = scDto.lat,
                lon = scDto.lon,
                openTime = scDto.openingHours.open,
                closeTime = scDto.openingHours.close,
                restockDurationMin = scDto.restockDurationMin
            )
            savedServiceCenters[sc.code] = sc
        }
        serviceCenterRepository.saveAll(savedServiceCenters.values)

        // 3. Узлы доступа связи
        val savedNodes = mutableMapOf<String, AccessNode>()
        for (nodeDto in request.accessNodes) {
            val node = AccessNode(
                id = UUID.randomUUID(),
                code = nodeDto.accessNodeId.trim(),
                nodeType = nodeDto.nodeType,
                technology = nodeDto.technology,
                name = nodeDto.name.trim(),
                address = nodeDto.address.trim(),
                lat = nodeDto.lat,
                lon = nodeDto.lon
            )
            savedNodes[node.code] = node
        }
        accessNodeRepository.saveAll(savedNodes.values)

        // 4. Обслуживаемые здания (дедупликация домов, встречающихся в нескольких узлах)
        val savedBuildings = mutableMapOf<String, Building>()
        for (nodeDto in request.accessNodes) {
            val node = savedNodes[nodeDto.accessNodeId.trim()]
                ?: throw NotFoundException(
                    "ACCESS_NODE_NOT_FOUND",
                    "Узел доступа '${nodeDto.accessNodeId}' не найден"
                )

            for (bDto in nodeDto.buildings) {
                val code = bDto.buildingId.trim()
                if (!savedBuildings.containsKey(code)) {
                    val zoneSc = bDto.zoneServiceCenterId?.let { scCode ->
                        savedServiceCenters[scCode.trim()]
                            ?: throw NotFoundException(
                                "SERVICE_CENTER_NOT_FOUND",
                                "СЦ зоны '$scCode' для дома '$code' не найден"
                            )
                    }

                    val building = Building(
                        id = UUID.randomUUID(),
                        code = code,
                        accessNode = node,
                        zoneServiceCenter = zoneSc,
                        address = bDto.address.trim(),
                        lat = bDto.lat,
                        lon = bDto.lon,
                        buildingType = bDto.buildingType,
                        overheadLine = bDto.overheadLine
                    )
                    savedBuildings[code] = building
                }
            }
        }
        buildingRepository.saveAll(savedBuildings.values)

        // 5. Типы работ
        val savedWorkTypes = mutableMapOf<WorkTypeCode, WorkType>()
        for (wtDto in request.workTypes) {
            val wt = WorkType(
                id = UUID.randomUUID(),
                code = wtDto.code,
                name = wtDto.name.trim(),
                durationMin = wtDto.durationMin,
                maxStartDelayMin = wtDto.maxStartDelayMin,
                deadlineKind = wtDto.deadlineKind,
                deadlineValue = wtDto.deadlineValue,
                repeatDeadlineValue = wtDto.repeatDeadlineValue,
                pgpRelevant = wtDto.pgpRelevant,
                profiles = wtDto.profiles.toMutableSet()
            )
            savedWorkTypes[wt.code] = wt
        }
        workTypeRepository.saveAll(savedWorkTypes.values)

        // 6. Смены и сотрудники
        val shiftsMap = mutableMapOf<String, Shift>()
        val savedStaff = mutableListOf<Staff>()

        for (sDto in request.staff) {
            val sc = savedServiceCenters[sDto.serviceCenterId.trim()]
                ?: throw NotFoundException(
                    "SERVICE_CENTER_NOT_FOUND",
                    "Сервисный центр '${sDto.serviceCenterId}' для сотрудника '${sDto.staffId}' не найден"
                )

            val shiftKey = "${sDto.shift.start}_${sDto.shift.end}_${sDto.shift.breakDurationMin}_${sDto.shift.breakWindow.from}_${sDto.shift.breakWindow.to}"
            val shift = shiftsMap.getOrPut(shiftKey) {
                Shift(
                    id = UUID.randomUUID(),
                    startTime = sDto.shift.start,
                    endTime = sDto.shift.end,
                    breakDurationMin = sDto.shift.breakDurationMin,
                    breakWindowFrom = sDto.shift.breakWindow.from,
                    breakWindowTo = sDto.shift.breakWindow.to
                )
            }

            val staff = Staff(
                id = UUID.randomUUID(),
                code = sDto.staffId.trim(),
                displayName = sDto.name.trim(),
                serviceCenter = sc,
                shift = shift,
                heightPermit = sDto.heightPermit,
                experienceYears = sDto.experienceYears,
                equipmentStartOnt = sDto.equipmentStart.ont,
                equipmentStartRouter = sDto.equipmentStart.router,
                equipmentStartStb = sDto.equipmentStart.stb,
                equipmentCapacityOnt = sDto.equipmentCapacity.ont,
                equipmentCapacityRouter = sDto.equipmentCapacity.router,
                equipmentCapacityStb = sDto.equipmentCapacity.stb,
                profiles = sDto.profiles.toMutableSet(),
                technologies = sDto.technologies.toMutableSet()
            )
            savedStaff.add(staff)
        }

        shiftRepository.saveAll(shiftsMap.values)
        staffRepository.saveAll(savedStaff)

        log.info(
            "Справочники успешно загружены: СЦ={}, Сотрудников={}, Узлов={}, Домов={}, Типов работ={}",
            savedServiceCenters.size,
            savedStaff.size,
            savedNodes.size,
            savedBuildings.size,
            savedWorkTypes.size
        )

        return LoadResultResponse(
            status = "loaded",
            counts = mapOf(
                "service_centers" to savedServiceCenters.size,
                "staff" to savedStaff.size,
                "access_nodes" to savedNodes.size,
                "buildings" to savedBuildings.size,
                "work_types" to savedWorkTypes.size
            )
        )
    }

    private fun cleanupDatabase() {
        val cleanupQuery = """
            DELETE FROM app.event;
            DELETE FROM app.alert;
            DELETE FROM app.pgp_group_order;
            DELETE FROM app.pgp_group;
            DELETE FROM app.unassigned_order;
            DELETE FROM app.route_stop;
            DELETE FROM app.route;
            DELETE FROM app.work_order;
            DELETE FROM app.work_day_absent_staff;
            DELETE FROM app.work_day;
            DELETE FROM app.staff_technology;
            DELETE FROM app.staff_profile;
            DELETE FROM app.staff;
            DELETE FROM app.shift;
            DELETE FROM app.building;
            DELETE FROM app.access_node;
            DELETE FROM app.service_center;
            DELETE FROM app.work_type_profile;
            DELETE FROM app.work_type;
        """.trimIndent()

        entityManager.createNativeQuery(cleanupQuery).executeUpdate()
        entityManager.flush()
        entityManager.clear()
    }
}
