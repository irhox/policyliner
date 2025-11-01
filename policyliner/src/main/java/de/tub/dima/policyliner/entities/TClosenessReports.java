package de.tub.dima.policyliner.entities;

import java.util.List;

public class TClosenessReports {
    private List<TClosenessReport> reports;

    public TClosenessReports(List<TClosenessReport> reports) {
        this.reports = reports;
    }

    public List<TClosenessReport> getReports() {
        return reports;
    }
    public void setReports(List<TClosenessReport> reports) {
        this.reports = reports;
    }

    @Override
    public String toString() {
        return "TClosenessReports{" +
                "reports=" + reports.toString() +
                '}';
    }
}
