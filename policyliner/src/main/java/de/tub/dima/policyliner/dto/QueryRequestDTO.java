package de.tub.dima.policyliner.dto;

public class QueryRequestDTO {
    private String query;
    private String userId;
    private String userRole;


    public QueryRequestDTO(String query, String userId, String userRole) {
        this.query = query;
        this.userId = userId;
        this.userRole = userRole;
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
}
