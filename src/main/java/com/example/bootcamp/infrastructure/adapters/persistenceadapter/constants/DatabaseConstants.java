package com.example.bootcamp.infrastructure.adapters.persistenceadapter.constants;

public class DatabaseConstants {
    public static final String QUERY =
            "SELECT b.* FROM bootcamps b " +
                    "LEFT JOIN bootcamp_capacity bc ON b.id = bc.id_bootcamp " +
                    "GROUP BY b.id, b.name, b.description " +
                    "ORDER BY %s %s " +
                    "LIMIT :size OFFSET :offset ";
    public static final String SORT_BY_CAPACITIES = "capacities";
    public static final String SORT_BY_COUNT = "COUNT(bc.id_capacity)";
    public static final String SORT_BY_NAME = "b.name";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String SIZE = "size";
    public static final String OFFSET = "offset";
}
