package org.anonymous.sql;

public class Store {

    public static final String DROP_TABLE = "drop table objects";
    public static final String CREATE_TABLE = "create table objects ( \n" + "name varchar NOT NULL, \n"
            + "typeId integer NOT NULL, \n" + "lastTransaction bigint NOT NULL, \n"
            + "timeUpdated timestamp NOT NULL, \n" + "updateCount bigint NOT NULL, \n"
            + "dateCreated integer NOT NULL, \n" + "dbIdUpdated integer NOT NULL, \n"
            + "versionInfo integer NOT NULL, \n" + "sdbDiskMem bytea NOT NULL, \n" + "mem bytea NOT NULL, \n"
            + "nameLower varchar NOT NULL )";

    public static final String CREATE_RECORD_INDEX_BY_TYPEID_NAME = "create unique index object_typeid_name on objects(typeId, nameLower)";
    public static final String CREATE_RECORD_INDEX_BY_LOWER_NAME = "create unique index object_lower_name on objects(nameLower)";
    public static final String CREATE_RECORD_INDEX_BY_NAME = "create unique index object_name on objects(name)";

    public static final String INSERT_RECORDS = "insert into objects values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_RECORDS =
            "UPDATE objects SET \n" +
                    "   typeId = ?,\n" +
                    "   lastTransaction = ?,\n" +
                    "   timeUpdated = ?,\n" +
                    "   updateCount = ?,\n" +
                    "   dateCreated = ?,\n" +
                    "   dbIdUpdated = ?,\n" +
                    "   versionInfo = ?,\n" +
                    "   sdbDiskMem = ?,\n" +
                    "   mem = ?\n" +
                    "WHERE name = ? AND updateCount = ? AND dbIdUpdated = ?";

    public static final String LOOKUP_OBJECTS = "select name from objects where nameLower %s ? order by name %s LIMIT ?";

    public static final String LOOKUP_OBJECTS_BY_TYPEID = "select name from objects where nameLower %s ? and typeId = ? order by name %s LIMIT ?";

    public static final String OBJ_EXISTS = "Select count(1) from objects where nameLower = ?";

    public static final String OBJ_KEYS = "Select nameLower from objects";

    public static final String GET_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo from objects where nameLower = ?";

    public static final String GET_SDB_MEM = "select sdbDiskMem from objects where nameLower = ?";

    public static final String GET_MEM = "select mem from objects where nameLower = ?";

    public static final String COUNT_RECORDS = "select count(name) from objects";

    public static final String GET_MANY_SDB_RECORDS = "select sdbDiskMem from objects where nameLower in (%s)";

    public static final String GET_MANY_MEM_RECORDS = "select mem from objects where nameLower in (%s)";

    public static final String GET_MANY_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower in (%s)";

    public static final String GET_MANY_RECORDS_SUMM = "select name, typeId, mem from objects where nameLower LIKE %s";

    public static final String GET_FULL_OBJECT = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower = ?";

    public static final String DELETE_RECORDS = "delete from objects where name = ? and updateCount = ? and dbIdUpdated = ?";

    public static final String RENAME_RECORDS = "update objects set name = ?, nameLower = ?, lastTransaction = ?, timeUpdated = ?, updateCount = ?, dateCreated = ?, dbIdUpdated = ?,versionInfo = ?, sdbDiskMem = ? where name = ? and updateCount = ? and dbIdUpdated = ?";

    public static final String CREATE_TXN_ID_SEQ = "CREATE SEQUENCE txn_id_seq START 1";

    public static final String GET_NXT_TXN_ID = "SELECT nextval('txn_id_seq')";

    public static final String CREATE_TRANS_HEADER_TABLE = "CREATE TABLE tdms_trans_header_primary (\n" +
            "   txnlog_trans_id bigint NOT NULL,\n" +
            "   source_db_id int,\n" +
            "   source_trans_id bigint,\n" +
            "   trans_type_id integer NOT NULL,\n" +
            "   trans_timestamp_utc timestamp NOT NULL,\n" +
            "   trans_name varchar(32) NOT NULL,\n" +
            "   object_type_id integer NOT NULL,\n" +
            "   application_name_short varchar(64) NOT NULL,\n" +
            "   application_name_long varchar(256) NOT NULL,\n" +
            "   user_name varchar(64) NOT NULL,\n" +
            "   login_id varchar(32) NOT NULL,\n" +
            "   user_id varchar(32) NOT NULL,\n" +
            "   network_address bigint NOT NULL,\n" +
            "   network_address_long char(15) NOT NULL,\n" +
            "   trans_flags integer NOT NULL,\n" +
            "   detail_parts integer NOT NULL,\n" +
            "   detail_bytes integer NOT NULL,\n" +
            "   db_actual_timestamp_utc timestamp NOT NULL,\n" +
            "   mem bytea NOT NULL,\n" +
            "   PRIMARY KEY (txnlog_trans_id)\n" +
            ")";

    public static final String INSERT_TRANS_HEADER = "INSERT INTO tdms_trans_header_primary VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String CREATE_TRANS_PARTS_TABLE = "CREATE TABLE tdms_trans_part_primary (\n" +
            "   txnlog_trans_id bigint NOT NULL,\n" +
            "   part_id int NOT NULL,\n" +
            "   event_type_id integer NOT NULL,\n" +
            "   operation_id integer NOT NULL,\n" +
            "   ignore_errors char(1) NOT NULL,\n" +
            "   original bool NULL,\n" +
            "   apply bool NULL,\n" +
            "   dupinsert bool NULL,\n" +
            "   ignore_updcount_and_srcdbcheck bool NULL,\n" +
            "   mem bytea NOT NULL,\n" +
            "   CONSTRAINT fk_txnlog_trans_id\n" +
            "   FOREIGN KEY(txnlog_trans_id)\n" +
            "   REFERENCES tdms_trans_header_primary(txnlog_trans_id)\n" +
            ")";

    public static final String INSERT_TRANS_PARTS = "INSERT INTO tdms_trans_part_primary VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
}