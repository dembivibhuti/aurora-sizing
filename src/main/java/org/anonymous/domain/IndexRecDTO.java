package org.anonymous.domain;

import org.anonymous.grpc.IndexRecord;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IndexRecDTO implements Serializable {

    public String objKey;
    public Map<String, Double> doubleMap = new HashMap<>();
    public Map<String, String> stringMap = new HashMap<>();

    public byte[] toBytes() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(this);
            out.flush();
            byteOut.flush();
            return byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IndexRecDTO fromBytes(byte[] bytes) {
        try(ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(byteIn);) {
            return (IndexRecDTO) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IndexRecord toCmdMsgIndexGetByNameWithClientResponse() {
        /*CmdMsgIndexGetByNameWithClientResponse.MsgOnSuccess.Builder msgOnSuccess =
                CmdMsgIndexGetByNameWithClientResponse.MsgOnSuccess
                        .newBuilder();*/
        IndexRecord.Builder idxRecBuilder = IndexRecord.newBuilder().setSecurityName(objKey);
        doubleMap.entrySet().stream().forEach(stringDoubleEntry -> idxRecBuilder.putDoubleVal(stringDoubleEntry.getKey(), stringDoubleEntry.getValue()));
        stringMap.entrySet().stream().forEach(stringStringEntry -> idxRecBuilder.putStringVal(stringStringEntry.getKey(), stringStringEntry.getValue()));
        //msgOnSuccess.addIndexRecords(idxRecBuilder.build());
        //CmdMsgIndexGetByNameWithClientResponse response = CmdMsgIndexGetByNameWithClientResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.build()).build();
        return idxRecBuilder.build();
    }

    public static void main(String[] args) {
        IndexRecDTO indexRecDTO = new IndexRecDTO();
        indexRecDTO.objKey = "x";
        indexRecDTO.doubleMap.put("x", 1d);
        indexRecDTO.doubleMap.put("x", 2d);
        indexRecDTO.doubleMap.put("x", 3d);

        indexRecDTO.stringMap.put("x", "y");
        indexRecDTO.stringMap.put("y", "y");
        indexRecDTO.stringMap.put("z", "y");

        System.out.println(indexRecDTO.equals(IndexRecDTO.fromBytes(indexRecDTO.toBytes())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexRecDTO that = (IndexRecDTO) o;
        return Objects.equals(objKey, that.objKey) &&
                Objects.equals(doubleMap, that.doubleMap) &&
                Objects.equals(stringMap, that.stringMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objKey, doubleMap, stringMap);
    }
}
