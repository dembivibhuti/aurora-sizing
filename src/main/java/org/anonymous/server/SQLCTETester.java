package org.anonymous.server;

import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.domain.ObjectDTO;

import java.sql.*;
import java.util.Date;

import static org.anonymous.sql.Store.*;

public class SQLCTETester {

    private static final ConnectionProviderHolder connectionProviderHolder = HikariCPConnectionProvider.create();

    public static void main(String[] args) {
        wipe();
        runnDDL();

        ObjRecord insertObj = new ObjRecord();
        insertObj.name = "NAME";
        insertObj.typeId = 1;
        insertObj.updateCount = 1;
        insertObj.dateCreated = 1;
        insertObj.dbIdUpdated = 1;
        insertObj.versionInfo = 1;
        insertObj.timeUpdated = new Timestamp(new Date().getTime());
        insertObj.mem = new byte[]{0, 0};
        opCTE(insertObj);
        opCTE(insertObj);
        connectionProviderHolder.close();
    }

    private static void runnDDL() {
        try (Connection connection = connectionProviderHolder.rwConnectionProvider.getConnection()) {
            connection
                    .prepareStatement(TXN_ID_MAP)
                    .executeUpdate();
            connection
                    .prepareStatement(CREATE_TABLE_2)
                    .executeUpdate();
            connection
                    .prepareStatement(CREATE_TRANS_HEADER_TABLE_2)
                    .executeUpdate();
            connection
                    .prepareStatement(POPULATE_EXT_TXN_ID_FUNC.replace("&&%", System.getProperty("dataSource.currentSchema")))
                    .executeUpdate();
            connection
                    .prepareStatement(ATTACH_TRIGG_TRANS_HEADER_TABLE)
                    .executeUpdate();
            connection.commit();
            System.out.println("created schema");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private static void opCTE(ObjRecord insertObj) {
        try (Connection connection = connectionProviderHolder.rwConnectionProvider.getConnection()) {
            PreparedStatement insertRec = connection
                    .prepareStatement("WITH gen_txn_id AS ( INSERT INTO txn_id_map ( txn_id ) VALUES ( DEFAULT ) RETURNING txn_id ), " +
                            "              ins_obj_batch AS ( insert into objects_2 values (?, ?, ( Select txn_id from gen_txn_id ), ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING RETURNING name ), " +
                            "              ins_trans_header_2 AS ( INSERT INTO tdms_trans_header_primary_2 VALUES (( Select txn_id from gen_txn_id ), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?))" +
                            "              SELECT name, 'INSERT' as ops, (SELECT name from objects_2 where name = insert_tab.name) as oldName, -1 as txn_id, (SELECT updateCount from objects_2 where name = insert_tab.name) as updateCount, (SELECT dbIdUpdated from objects_2 where name = insert_tab.name) as dbIdUpdated from  ( values  (?) ) as insert_tab(name) where name NOT IN ( Select name from ins_obj_batch )");


            insertRec.setString(1, insertObj.name);
            insertRec.setInt(2, insertObj.typeId);
            insertRec.setTimestamp(3, insertObj.timeUpdated);
            insertRec.setLong(4, insertObj.updateCount);
            insertRec.setInt(5, insertObj.dateCreated);
            insertRec.setInt(6, insertObj.dbIdUpdated);
            insertRec.setInt(7, insertObj.versionInfo);
            insertRec.setBytes(8, new byte[]{0, 0});
            insertRec.setString(9, insertObj.name.toLowerCase());

            insertRec.setInt(10, 1);
            insertRec.setLong(11, 1);
            insertRec.setInt(12, 1);
            insertRec.setTimestamp(13, new Timestamp(0));
            insertRec.setString(14, "INS");
            insertRec.setInt(15, 1);
            insertRec.setString(16, "");
            insertRec.setString(17, "");//no application name in proto
            insertRec.setString(18, "");//no username in proto
            insertRec.setString(19, "");
            insertRec.setString(20, "");
            insertRec.setLong(21, 0);//no network address in proto
            insertRec.setString(22, "");
            insertRec.setInt(23, 1);
            insertRec.setInt(24, 2);
            insertRec.setInt(25, 0);
            insertRec.setTimestamp(26, new Timestamp(System.currentTimeMillis()));
            insertRec.setBytes(27, new byte[0]);

            insertRec.setString(28, "NAME");
                
            ResultSet rs = insertRec.executeQuery();
            getFailures(rs);
            connection.commit();
            System.out.println("Txn Complete");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private static void getFailures(ResultSet rs) throws SQLException {
        while(rs.next()) {
            System.out.println(" Soft Txn ID = " + rs.getLong("txn_id"));
            System.out.println(" Name = " + rs.getString("name"));
            System.out.println(" Old Name = " + rs.getString("name"));
            System.out.println(" Ops = " + rs.getString("ops"));
            System.out.println(" DB Updated = " + rs.getLong("dbIdUpdated"));
            System.out.println(" Update Count = " + rs.getLong("updateCount"));
        }
    }

    private static void wipe() {
        try (Connection connection = connectionProviderHolder.rwConnectionProvider.getConnection()) {
            connection
                    .prepareStatement("drop table if exists objects_2")
                    .executeUpdate();
            connection
                    .prepareStatement("drop table if exists tdms_trans_header_primary_2")
                    .executeUpdate();
            connection
                    .prepareStatement("drop table if exists txn_id_map")
                    .executeUpdate();
            connection.commit();
            System.out.println("deleted schema");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    static class ObjRecord {
        public String name;
        public int typeId;
        public long lastTransaction;
        public long updateCount;
        public int dateCreated;
        public int dbIdUpdated;
        public int versionInfo;
        public Timestamp timeUpdated;
        public byte[] mem;
        public String nameLower;

    }
}
