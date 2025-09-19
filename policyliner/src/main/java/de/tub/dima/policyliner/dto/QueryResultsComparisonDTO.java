package de.tub.dima.policyliner.dto;

public class QueryResultsComparisonDTO {
    private long currentCount;
    private long previousCount;
    private long currentTotalCount;
    private long previousTotalCount;

    public QueryResultsComparisonDTO(long currentCount, long previousCount, long currentTotalCount, long previousTotalCount) {
        this.currentCount = currentCount;
        this.previousCount = previousCount;
        this.currentTotalCount = currentTotalCount;
        this.previousTotalCount = previousTotalCount;
    }

    public long getCurrentCount() {
        return currentCount;
    }
    public long getPreviousCount() {
        return previousCount;
    }
    public long getCurrentTotalCount() {
        return currentTotalCount;
    }
    public long getPreviousTotalCount() {
        return previousTotalCount;
    }
    public void setCurrentCount(long currentCount) {
        this.currentCount = currentCount;
    }
    public void setPreviousCount(long previousCount) {
        this.previousCount = previousCount;
    }
    public void setCurrentTotalCount(long currentTotalCount) {
        this.currentTotalCount = currentTotalCount;
    }
    public void setPreviousTotalCount(long previousTotalCount) {
        this.previousTotalCount = previousTotalCount;
    }

    @Override
    public String toString() {
        return "QueryResultsComparisonDTO{" +
                "currentCount=" + currentCount +
                ", previousCount=" + previousCount +
                ", currentTotalCount=" + currentTotalCount +
                ", previousTotalCount=" + previousTotalCount +
                '}';
    }
}
