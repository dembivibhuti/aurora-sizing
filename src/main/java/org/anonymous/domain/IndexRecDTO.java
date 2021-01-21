package org.anonymous.domain;

import org.anonymous.grpc.CmdMsgIndexGetByNameWithClientResponse;
import org.anonymous.grpc.IndexRecord;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

}
