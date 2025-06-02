package de.tub.dima.policyliner.dto;

import jakarta.ws.rs.DefaultValue;

public class SearchDTO {
    @DefaultValue("") private String filter;
    @DefaultValue("asc") private String sortOrder;
    @DefaultValue("id") private String sortColumn;
    @DefaultValue("10") private Integer pageSize;
    @DefaultValue("0") private Integer pageNumber;

    public SearchDTO(String filter, String sortOrder, String sortColumn, Integer pageSize, Integer pageNumber) {
        this.filter = filter;
        this.sortOrder = sortOrder;
        this.sortColumn = sortColumn;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
