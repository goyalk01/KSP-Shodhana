package com.ksp.shodhana.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for constructing ZCQL queries safely.
 * Prevents SQL injection through parameterized-style construction.
 *
 * Usage:
 * <pre>
 *   String query = ZcqlBuilder.select("Crime")
 *       .columns("fir_number", "crime_type", "district")
 *       .where("district", "=", "Bengaluru Urban")
 *       .and("status", "=", "Open")
 *       .orderBy("date_reported", "DESC")
 *       .limit(20)
 *       .build();
 * </pre>
 */
public class ZcqlBuilder {

    private final String tableName;
    private List<String> columns = new ArrayList<>();
    private final List<String> conditions = new ArrayList<>();
    private String orderByClause;
    private Integer limitValue;
    private Integer offsetValue;

    private ZcqlBuilder(String tableName) {
        this.tableName = tableName;
    }

    public static ZcqlBuilder select(String tableName) {
        return new ZcqlBuilder(tableName);
    }

    public ZcqlBuilder columns(String... cols) {
        this.columns = List.of(cols);
        return this;
    }

    public ZcqlBuilder where(String column, String operator, String value) {
        conditions.add(column + " " + operator + " '" + escapeValue(value) + "'");
        return this;
    }

    public ZcqlBuilder where(String column, String operator, long value) {
        conditions.add(column + " " + operator + " " + value);
        return this;
    }

    public ZcqlBuilder and(String column, String operator, String value) {
        return where(column, operator, value);
    }

    public ZcqlBuilder and(String column, String operator, long value) {
        return where(column, operator, value);
    }

    public ZcqlBuilder like(String column, String pattern) {
        conditions.add(column + " LIKE '%" + escapeValue(pattern) + "%'");
        return this;
    }

    public ZcqlBuilder between(String column, String from, String to) {
        conditions.add(column + " BETWEEN '" + escapeValue(from) + "' AND '" + escapeValue(to) + "'");
        return this;
    }

    public ZcqlBuilder orderBy(String column, String direction) {
        this.orderByClause = column + " " + direction;
        return this;
    }

    public ZcqlBuilder limit(int limit) {
        this.limitValue = limit;
        return this;
    }

    public ZcqlBuilder offset(int offset) {
        this.offsetValue = offset;
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder("SELECT ");

        if (columns.isEmpty()) {
            sb.append("*");
        } else {
            sb.append(String.join(", ", columns));
        }

        sb.append(" FROM ").append(tableName);

        if (!conditions.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        if (orderByClause != null) {
            sb.append(" ORDER BY ").append(orderByClause);
        }

        if (limitValue != null) {
            sb.append(" LIMIT ").append(limitValue);
        }

        if (offsetValue != null) {
            sb.append(" OFFSET ").append(offsetValue);
        }

        return sb.toString();
    }

    /**
     * Basic escaping to prevent ZCQL injection.
     * Catalyst ZCQL doesn't support prepared statements,
     * so we escape single quotes as a safety measure.
     */
    private String escapeValue(String value) {
        if (value == null) return "";
        return value.replace("'", "\\'");
    }
}
