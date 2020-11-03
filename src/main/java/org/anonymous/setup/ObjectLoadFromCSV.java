package org.anonymous.setup;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.io.FileReader;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;


public class ObjectLoadFromCSV {
    private final static ConnectionProvider.Holder holder = ConnectionProvider.create();

    public static void main(String[] args) {
        try {
            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            TimeKeeper timekeeper = new TimeKeeper("load", false);
            
            FileReader filereader = new FileReader("/home/ec2-user/environment/aurora-sizing/TestData.csv"); 
            CSVReader csvReader = new CSVReaderBuilder(filereader) .withSkipLines(1).build(); 
            List<String[]> allData = csvReader.readAll(); 
  
            //objectRepository.insertObjectsFromCSV(9, allData, timekeeper);
            objectRepository.insertObjectsFromCSV(9, allData, timekeeper);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
          holder.close();
        }
    }
}
