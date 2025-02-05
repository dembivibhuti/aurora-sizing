package org.anonymous.sql;

public class Store {

    public static final String DROP_TABLE = "drop table objects";
    public static final String CREATE_TABLE = "create table objects ( \n" + "name varchar NOT NULL, \n"
            + "typeId integer NOT NULL, \n" + "lastTransaction bigint NOT NULL, \n"
            + "timeUpdated timestamp NOT NULL, \n" + "updateCount bigint NOT NULL, \n"
            + "dateCreated integer NOT NULL, \n" + "dbIdUpdated integer NOT NULL, \n"
            + "versionInfo integer NOT NULL, \n" + "sdbDiskMem bytea NOT NULL, \n" + "mem bytea NOT NULL, \n"
            + "nameLower varchar NOT NULL )";

    public static final String CREATE_TABLE_2 = "create table objects_2 ( \n" + "name varchar NOT NULL UNIQUE, \n"
            + "typeId integer NOT NULL, \n" + "lastTransaction bigint NOT NULL, \n"
            + "timeUpdated timestamp NOT NULL, \n" + "updateCount bigint NOT NULL, \n"
            + "dateCreated integer NOT NULL, \n" + "dbIdUpdated integer NOT NULL, \n"
            + "versionInfo integer NOT NULL, \n"  + "mem bytea NOT NULL, \n"
            + "nameLower varchar NOT NULL,  \n" +
              " CONSTRAINT fk_lastTransaction \n" +
              " FOREIGN KEY( lastTransaction ) \n" +
              " REFERENCES txn_id_map(txn_id) )";

    public static final String TXN_ID_MAP = "create table txn_id_map ( " +
            " txn_id BIGSERIAL, " +
            " ext_txn_id BIGINT UNIQUE, " +
            " created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, " +
            " PRIMARY KEY(txn_id)" +
            ")";

    public static final String POPULATE_EXT_TXN_ID_FUNC = "CREATE OR REPLACE FUNCTION &&%.generate_txn_id () RETURNS trigger AS \n" +
            "$BODY$\n" +
            "   DECLARE\n" +
            "       varLastTxnId txn_id_map.ext_txn_id%type;\n" +
            "   BEGIN\n" +
            "       FOR i IN 1..100 LOOP      \n" +
            "           BEGIN                 \n" +
            "               SELECT COALESCE(max(ext_txn_id),0) INTO varLastTxnId from txn_id_map;\n" +
            "               UPDATE txn_id_map set ext_txn_id = varLastTxnId + 1 where txn_id = NEW.txnlog_trans_id; \n" +
            "               RETURN NEW;\n" +
            "           EXCEPTION\n" +
            "               WHEN OTHERS THEN\n" +
            "                   END; \n" +
            "       END LOOP;\n" +
            "   END;\n" +
            "$BODY$\n"  +
            "LANGUAGE 'plpgsql'";

    public static final String ATTACH_TRIGG_TRANS_HEADER_TABLE = "CREATE TRIGGER generate_txn_id \n" +
            " AFTER INSERT \n" +
            " ON tdms_trans_header_primary_2 \n" +
            " FOR EACH ROW \n" +
            "   EXECUTE PROCEDURE generate_txn_id()";

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

    public static final String LOOKUP_OBJECTS = "select name from objects where nameLower %s ? order by nameLower %s LIMIT ?";
    public static final String LOOKUP_OBJECTS_FROM_INDEX = "select name from %s where nameLower %s ? order by nameLower %s LIMIT ?";
    //public static final String LOOKUP_OBJECTS = "select name from objects where nameLower %s ? LIMIT ?";

    public static final String LOOKUP_OBJECTS_BY_TYPEID = "select name from objects where nameLower %s ? and typeId = ? order by nameLower %s LIMIT ?";

    public static final String OBJ_EXISTS = "Select count(1) from objects where nameLower = ?";

    public static final String OBJ_KEYS = "Select name from objects LIMIT ? OFFSET ?";

    public static final String GET_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo from objects where nameLower = ?";

    public static final String GET_SDB_MEM = "select sdbDiskMem from objects where nameLower = ?";

    public static final String GET_MEM = "select mem from objects where nameLower = ?";

    public static final String COUNT_RECORDS = "select count(1) from objects";

    public static final String GET_MANY_SDB_RECORDS = "select sdbDiskMem from objects where nameLower in (%s)";

    public static final String GET_MANY_MEM_RECORDS = "select mem from objects where nameLower in (%s)";

    public static final String GET_MANY_RECORDS = "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo, mem from objects where nameLower in (%s)";

    public static final String GET_MANY_RECORDS_SUMM = "select name, typeId, mem from objects where nameLower LIKE '%s'";

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

    public static final String CREATE_TRANS_HEADER_TABLE_2 = "CREATE TABLE tdms_trans_header_primary_2 (\n" +
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
            "   CONSTRAINT fk_txnlog_trans_id \n" +
            "   FOREIGN KEY (txnlog_trans_id)\n" +
            "   REFERENCES txn_id_map(txn_id)" +
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

    public static final String CREATE_TABLE_RECORD_INDEX_BY_LOWER_NAME= "create unique index %s on %s(nameLower)";

    public static final String COUNT_RECORDS_FOR_ANY_TABLE = "select count(name) from %s";

    public static final String CREATE_INDEX_TABLE = "CREATE TABLE index_records (\n" +
            "   name varchar NOT NULL,\n" +
            "   timeUpdated double NOT NULL,\n" +
            "   creator_name varchar(32) NOT NULL,\n" +
            " PRIMARY KEY (name)\n" + ")";

    public static final String GET_INDEX_RECORDS = "select creator_name, timeUpdated from index_records where name = ?";

    public static final String INSERT_INDEX_RECORDS = "insert into index_records values (?, ?, ?)";

    public static final String GET_MANY_INDEX_RECORDS = "select * from %s where nameLower in (%s)";

    public static final String GET_FULL_INDEX_RECORD = "Select %s from %s where name = '%s'";

    public static final String TEST_INDEX_TABLE = "CREATE TABLE test_table (\n" +
            "   double_val double(8) NOT NULL,\n" +
            "   string_val varchar(32) NOT NULL,\n" +
            "   name varchar NOT NULL,\n" +
            "   nameLower varchar NOT NULL,\n" +
            " PRIMARY KEY (name)\n" + ")";

    public static final String INSERT_TEST_INDEX_RECORDS = "insert into test_table values (?, ?, ?, ?)";

    public static final String INSERT_CSV_INDEX_RECORD = "insert into %s (%s) values (%s)";

    public static final String GET_FROM_INDEX_TABLE_WITH_LIMIT = "select * from %s LIMIT %s OFFSET %s";

    public static final String GET_INDEX_RECORDS_WITH_CLIENT_IN_BATCHES = "select * from %s where nameLower >= ? order by nameLower asc LIMIT ? OFFSET ?";

    public static final String GET_INDEX_RECORD = "select * from %s where nameLower = ?";
}