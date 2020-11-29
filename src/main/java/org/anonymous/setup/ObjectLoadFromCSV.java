package org.anonymous.setup;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.io.FileReader;
import java.util.List;


public class ObjectLoadFromCSV {
    private final static ConnectionProviderHolder holder = HikariCPConnectionProvider.create();

    public static void main(String[] args) {
        try {
            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            TimeKeeper timekeeper = new TimeKeeper("load", false);

            FileReader filereader = new FileReader(System.getProperty("csvFile"));
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();

            if("true".equals(System.getProperty("recon"))) {
                objectRepository.checkReconDataLoadFromCSV(allData);
            } else {
                objectRepository.insertObjectsFromCSV(allData, timekeeper);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            holder.close();
        }
    }
}
