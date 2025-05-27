package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.QueryStatus;

public class QueryResponseDTO {
    private String id;
    private String query;
    private QueryStatus status;
    private String message;

    public QueryResponseDTO(String id, String query, QueryStatus status, String message) {
        this.id = id;
        this.query = query;
        this.status = status;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QueryStatus getStatus() {
        return status;
    }

    public void setStatus(QueryStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
