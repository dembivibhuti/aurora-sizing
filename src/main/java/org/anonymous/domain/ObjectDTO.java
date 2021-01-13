package org.anonymous.domain;

import com.google.protobuf.ByteString;
import org.anonymous.grpc.CmdGetByNameExtResponse;
import org.anonymous.grpc.Metadata;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class ObjectDTO implements Serializable {

    public String name;
    public int typeId;
    public long lastTransaction;
    public long updateCount;
    public int dateCreated;
    public int dbIdUpdated;
    public int versionInfo;
    public String timeUpdated;

    public byte[] mem;

    public byte[] toBytes() {
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(baos);

        w.writeInt(100);
        w.write(byteArray);

        w.flush();
        */
        ByteBuffer buffer = ByteBuffer.allocate(4 + name.length() + 4 + 8 + 8 + 4 + 4 + 4 + 4 + timeUpdated.length() + 4 + mem.length);
        buffer.putInt(name.length());
        buffer.put(name.getBytes());
        buffer.putInt(typeId);
        buffer.putLong(lastTransaction);
        buffer.putLong(updateCount);
        buffer.putInt(dateCreated);
        buffer.putInt(dbIdUpdated);
        buffer.putInt(versionInfo);
        buffer.putInt(timeUpdated.length());
        buffer.put(timeUpdated.getBytes());
        buffer.putInt(mem.length);
        buffer.put(mem);
        return buffer.array();
    }

    public static ObjectDTO fromBytes(byte[] bytes) throws IOException {
        ObjectDTO objectDTO = new ObjectDTO();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int nameLength = buffer.getInt();
        byte[] name = new byte[nameLength];
        buffer.get(name, 0, nameLength);
        objectDTO.name = new String(name);

        objectDTO.typeId = buffer.getInt();
        objectDTO.lastTransaction = buffer.getLong();
        objectDTO.updateCount = buffer.getLong();
        objectDTO.dateCreated = buffer.getInt();
        objectDTO.dbIdUpdated = buffer.getInt();
        objectDTO.versionInfo = buffer.getInt();

        int timUpdatedLength = buffer.getInt();
        byte[] timeUpdated = new byte[timUpdatedLength];
        buffer.get(timeUpdated, 0, timUpdatedLength);
        objectDTO.timeUpdated = new String(timeUpdated);

        int memLength = buffer.getInt();
        objectDTO.mem = new byte[memLength];
        buffer.get(objectDTO.mem, 0, memLength);
        return objectDTO;
    }


    public CmdGetByNameExtResponse.MsgOnSuccess toCmdGetByNameExtResponseMsgOnSuccess() {
        return CmdGetByNameExtResponse.MsgOnSuccess.newBuilder().
                setMem(ByteString.copyFrom(mem)).
                setMetadata(Metadata.newBuilder().
                        setSecurityName(name).
                        setSecurityType(typeId).
                        setLastTxnId(lastTransaction).
                        setUpdateCount(updateCount).
                        setDateCreated(dateCreated).
                        setDbIdUpdated(dbIdUpdated).
                        setVersionInfo(versionInfo).
                        setTimeUpdate(timeUpdated)).
                build();
    }

    public static void main(String[] args) throws IOException {
        ObjectDTO objectDTO = new ObjectDTO();
        objectDTO.name = "sdf";
        objectDTO.typeId = 1;
        objectDTO.lastTransaction = 12;
        objectDTO.updateCount = 21;
        objectDTO.dateCreated = 21;
        objectDTO.dbIdUpdated = 32;
        objectDTO.versionInfo = 3;
        objectDTO.timeUpdated = "45";
        objectDTO.mem = new byte[3];

        ObjectDTO shouldbesame = ObjectDTO.fromBytes(objectDTO.toBytes());


        System.out.println(shouldbesame.equals(objectDTO));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectDTO objectDTO = (ObjectDTO) o;
        return typeId == objectDTO.typeId &&
                lastTransaction == objectDTO.lastTransaction &&
                updateCount == objectDTO.updateCount &&
                dateCreated == objectDTO.dateCreated &&
                dbIdUpdated == objectDTO.dbIdUpdated &&
                versionInfo == objectDTO.versionInfo &&
                Objects.equals(name, objectDTO.name) &&
                Objects.equals(timeUpdated, objectDTO.timeUpdated) &&
                Arrays.equals(mem, objectDTO.mem);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(name, typeId, lastTransaction, updateCount, dateCreated, dbIdUpdated, versionInfo, timeUpdated);
        result = 31 * result + Arrays.hashCode(mem);
        return result;
    }
}
