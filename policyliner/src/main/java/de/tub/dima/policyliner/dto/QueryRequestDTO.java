package de.tub.dima.policyliner.dto;

public class QueryRequestDTO {
    private String query;
    private String userId;

    public QueryRequestDTO(String query, String userId) {
        this.query = query;
        this.userId = userId;
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
}
