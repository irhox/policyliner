package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.AlertRepository;
import de.tub.dima.policyliner.dto.AlertDTO;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    // TODO: Implement sorting and filtering
    public PagedResponseDTO<AlertDTO> searchAlerts(SearchDTO searchDTO) {
        PanacheQuery<Alert> alertQuery = alertRepository.findAll();
        List<AlertDTO> alertList = alertQuery.page(
                Page.of(searchDTO.getPageNumber(), searchDTO.getPageSize())
        ).list().stream().map(this::convertToAlertDTO).toList();

        return createPagedResponseDTO(alertList, searchDTO, alertQuery.count());
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
        return new AlertDTO(alert.getId(), alert.message, alert.severity, alert.type, alert.isResolved, alert.createdAt);
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
