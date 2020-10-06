package org.anonymous.sql;

public class Store {

    public static final String DROP_TABLE = "drop table objects";
    public static final String CREATE_TABLE = "create table objects ( \n" + "name varchar NOT NULL, \n"
            + "typeId integer NOT NULL, \n" + "lastTransaction bigint NOT NULL, \n"
            + "timeUpdated timestamp NOT NULL, \n" + "updateCount bigint NOT NULL, \n"
            + "dateCreated integer NOT NULL, \n" + "dbIdUpdated integer NOT NULL, \n"
            + "versionInfo integer NOT NULL, \n" + "sdbDiskMem bytea NOT NULL, \n" + "mem bytea NOT NULL, \n"
            + "Primary Key ( name ) )";

    public static final String CREATE_RECORD_INDEX_BY_TYPEID_NAME = "create unique index object_typeid_name on objects(typeId, name)";

    public static final String INSERT_RECORDS = "insert into objects values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String LOOKUP_OBJECTS = "select name from objects where lower(name) %s ? order by name %s LIMIT ?";

    public static final String LOOKUP_OBJECTS_BY_TYPEID = "select name from objects where lower(name) %s ? and typeId = ? order by name %s LIMIT ?";

    public static final String GET_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo from objects where name = ?";

    public static final String GET_SDB_MEM = "select sdbDiskMem from objects where name = ?";

    public static final String GET_MEM = "select mem from objects where name = ?";

    public static final String COUNT_RECORDS = "select count(name) from objects";

    public static final String GET_MANY_MEM_RECORDS = "select mem from objects where name in (%s)";

    public static final String GET_MANY_SDB_RECORDS = "select sdbDiskMem from objects where name in (%s)";

    public static final String GET_MANY_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, sdbDiskMem from objects where name in (%s)";

    public static final String GET_ALL_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, sdbDiskMem from objects where name = ?";

}