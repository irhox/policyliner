package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.database.data.DataDBService;
import jakarta.enterprise.context.ApplicationScoped;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class QueryParserService {

    private final DataDBService dataDBService;

    public QueryParserService(DataDBService dataDBService) {
        this.dataDBService = dataDBService;
    }

    public Set<String> getTableNames(String query) throws JSQLParserException {
        return TablesNamesFinder.findTables(query);
    }

    public Set<String> getColumnNames(String query) {
        String columns = query.substring(query.indexOf("SELECT")+6, query.indexOf("FROM")).trim();
        if (columns.trim().equals("*")) {
            try {
                Set<String> tableNames = getTableNames(query);
                return dataDBService.getColumnNamesOfViews(tableNames);
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
        }
        return Arrays.stream(columns.split(","))
                .map(c -> {
                    String columnName = c.trim();
                    if (c.contains("as")){
                        columnName = c.substring(0, c.indexOf("as")).trim();
                    } else if (c.contains("AS")) {
                        columnName = c.substring(0, c.indexOf("AS")).trim();
                    }
                    if (columnName.contains(".")) {
                        columnName = columnName.substring(columnName.indexOf(".")+1);
                    }
                    columnName = columnName.replaceAll(";", "");
                    return columnName.trim();
                }).collect(Collectors.toSet());
    }

    public Set<String> getWhereClauses(String query) {
        if (!query.contains("WHERE")) {
            return Set.of();
        }

        int endIndex = query.length();
        if (query.contains("OFFSET")) {
            endIndex = query.indexOf("OFFSET");
        }
        if (query.contains("LIMIT")) {
            endIndex = query.indexOf("LIMIT");
        }
        if (query.contains("ORDER BY")) {
            endIndex = query.indexOf("ORDER BY");
        }
        if (query.contains("GROUP BY")) {
            endIndex = query.indexOf("GROUP BY");
        }
        String whereClause = query.substring(query.indexOf("WHERE")+5, endIndex).trim();
        return Arrays.stream(whereClause.split("(?i)\\bAND\\b|\\bOR\\b")).map(String::trim).collect(Collectors.toSet());
    }

    public Set<String> groupByColumnNames(String query) {
        if (!query.contains("GROUP BY")) {
            return Set.of();
        }

        int endIndex = query.length();
        if (query.contains("OFFSET")) {
            endIndex = query.indexOf("OFFSET");
        }
        if (query.contains("LIMIT")) {
            endIndex = query.indexOf("LIMIT");
        }
        if (query.contains("ORDER BY")) {
            endIndex = query.indexOf("ORDER BY");
        }
        if (query.contains("HAVING")) {
            endIndex = query.indexOf("HAVING");
        }
        String groupByClause = query.substring(query.indexOf("GROUP BY")+8, endIndex).trim();
        return Arrays.stream(groupByClause.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    public Set<String> havingConditions(String query) {
        if (!query.contains("HAVING")) {
            return Set.of();
        }

        int endIndex = query.length();
        if (query.contains("OFFSET")) {
            endIndex = query.indexOf("OFFSET");
        }
        if (query.contains("LIMIT")) {
            endIndex = query.indexOf("LIMIT");
        }
        if (query.contains("ORDER BY")) {
            endIndex = query.indexOf("ORDER BY");
        }
        String havingClause = query.substring(query.indexOf("HAVING")+6, endIndex).trim();
        return Arrays.stream(havingClause.split("(?i)\\bAND\\b|\\bOR\\b")).map(String::trim).collect(Collectors.toSet());
    }
}
