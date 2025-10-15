package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.MetricSeverity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "privacy_metric")
public class PrivacyMetric extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    public String name;
    public String description;
    public String value;
    public String valueType;
    @Enumerated(EnumType.STRING)
    public MetricSeverity metricSeverity;

    @ManyToOne
    public Policy policy;
}
