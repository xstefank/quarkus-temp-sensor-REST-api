package org.kost.tempSensor;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.kost.exceptions.ServiceException;
import org.kost.tempSensorType.TempSensorTypeEntity;
import org.kost.tempSensorType.TempSensorTypeRepository;
import org.kost.tempSensorType.TempSensorTypeService;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@AllArgsConstructor
@Slf4j
public class TempSensorService {
    private final TempSensorRepository tempSensorRepository;
    private final TempSensorMapper tempSensorMapper;

    private final TempSensorTypeRepository tempSensorTypeRepository;

    public List<TempSensor> findAll() {
        return
                this.tempSensorMapper.toDomainList(tempSensorRepository.findAll().list());
    }

    public Optional<TempSensor> findById(@NonNull Integer tempSensorId) {
        return tempSensorRepository.findByIdOptional(tempSensorId)
                .map(tempSensorMapper::toDomain);
    }

    @Transactional
    public void save(@Valid TempSensor tempSensor) {
        log.debug("Saving TempSensorType: {}", tempSensor);
        TempSensorEntity entity = tempSensorMapper.toEntity(tempSensor);
        System.out.println(entity);
        entity.setTempSensorTypeEntity(tempSensorTypeRepository.findById(tempSensor.tempSensorTypeId));
        System.out.println(entity);
        tempSensorRepository.persist(entity);
        tempSensorMapper.updateDomainFromEntity(entity, tempSensor);
    }

    @Transactional
    public void update(@Valid TempSensor tempSensor) {
        log.debug("Updating tempSensor: {}", tempSensor);
        if (Objects.isNull(tempSensor.getTempSensorId())) {
            throw new ServiceException("tempSensor does not have a tempSensorID");
        }
        TempSensorEntity entity = tempSensorRepository.findByIdOptional(tempSensor.getTempSensorId())
                .orElseThrow(() -> new ServiceException("No tempSensor found for tempSensor[%s]", tempSensor.getTempSensorId()));
        tempSensorMapper.updateEntityFromDomain(tempSensor, entity);
        tempSensorRepository.persist(entity);
        tempSensorMapper.updateDomainFromEntity(entity, tempSensor);
    }
}
