package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.AlertRepository;
import de.tub.dima.policyliner.dto.AlertDTO;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }


    public AlertDTO getAlertById(String alertId) {
        return convertToAlertDTO(alertRepository.findById(alertId));
    }

    // TODO: Implement sorting
    public PagedResponseDTO<AlertDTO> searchAlerts(SearchDTO searchDTO) {
        PanacheQuery<Alert> alertQuery = alertRepository.findFilteredAlerts(
                searchDTO.getFilter());

        List<AlertDTO> alertList = alertQuery.page(
                Page.of(searchDTO.getPageNumber(), searchDTO.getPageSize())
        ).list().stream().map(this::convertToAlertDTO).toList();

        return createPagedResponseDTO(alertList, searchDTO, alertQuery.count());
    }

    @Transactional
    public AlertDTO resolveAlert(String alertId) {
        Alert alert = alertRepository.findById(alertId);
        if (alert == null) throw new RuntimeException("No alert with id " + alertId);
        if (!alert.isResolved) {
            alert.isResolved = true;
            alert.resolvedAt = LocalDateTime.now();
        }
        Alert.getEntityManager().merge(alert);
        return convertToAlertDTO(alert);
    }

    public AlertDTO createAlert(AlertDTO alertDTO) {
        Alert newAlert = convertToAlert(alertDTO);
        newAlert.persist();

        Alert savedAlert = alertRepository.findById(newAlert.getId());
        return convertToAlertDTO(savedAlert);
    }


    private PagedResponseDTO<AlertDTO> createPagedResponseDTO(List<AlertDTO> alertList, SearchDTO searchDTO, long totalElements) {
        PagedResponseDTO<AlertDTO> page = new PagedResponseDTO<>();
        page.setCurrentPage(searchDTO.getPageNumber());
        page.setPageSize(searchDTO.getPageSize());
        page.setElements(alertList);
        page.setTotalElements(totalElements);
        page.setTotalPages((int) Math.ceil((double) totalElements /searchDTO.getPageSize()));

        return page;
    }

    private AlertDTO convertToAlertDTO(Alert alert) {
        return new AlertDTO(
                alert.getId(), alert.message, alert.severity, alert.type, alert.isResolved, alert.createdAt, alert.resolvedAt,
                !alert.type.equals(AlertType.POLICY) ?
                        alert.queries.stream().map(a -> a.id).collect(Collectors.toSet()) :
                        alert.policies.stream().map(p -> p.id).collect(Collectors.toSet())
                );
    }

    private Alert convertToAlert(AlertDTO alertDTO) {
        Alert alert = new Alert();
        alert.setId(alertDTO.getId());
        alert.severity = alertDTO.getSeverity();
        alert.message = alertDTO.getMessage();
        alert.type = alertDTO.getType();
        alert.isResolved = alertDTO.getIsResolved();
        return alert;
    }
}
