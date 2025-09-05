package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.QueryComparatorType;

public class QueryRequestDTO {
    private String query;
    private String userId;
    private String userRole;
    private QueryComparatorType comparatorType;


    public QueryRequestDTO(String query, String userId, String userRole, QueryComparatorType comparatorType) {
        this.query = query;
        this.userId = userId;
        this.userRole = userRole;
        this.comparatorType = comparatorType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public QueryComparatorType getComparatorType() {
        return comparatorType;
    }

    public void setComparatorType(QueryComparatorType comparatorType) {
        this.comparatorType = comparatorType;
    }
}
