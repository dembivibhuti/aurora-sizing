package org.anonymous.module;

import java.util.HashMap;

public class QueryData {
    private String query;
    private HashMap<Integer, String> queryColoumMap;

    public QueryData(String query, HashMap<Integer, String> queryColoumMap){
        this.query = query;
        this.queryColoumMap = queryColoumMap;
    }

    public String getQuery() {
        return query;
    }

    public HashMap<Integer, String> getQueryColoumMap() {
        return queryColoumMap;
    }
}