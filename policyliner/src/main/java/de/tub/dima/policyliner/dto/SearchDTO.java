package de.tub.dima.policyliner.dto;

public class SearchDTO {
    private String filter;
    private String sortOrder;
    private String sortColumn;
    private Integer pageSize;
    private Integer pageNumber;
    private String booleanFilter;

    public SearchDTO(String filter, String sortOrder, String sortColumn, Integer pageSize, Integer pageNumber, String booleanFilter) {
        this.filter = filter;
        this.sortOrder = sortOrder;
        this.sortColumn = sortColumn;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.booleanFilter = booleanFilter;
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

    public String getBooleanFilter() {
        return booleanFilter;
    }

    public void setBooleanFilter(String booleanFilter) {
        this.booleanFilter = booleanFilter;
    }
}
