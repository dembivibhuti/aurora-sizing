package org.anonymous.setup;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.io.FileReader;
import java.util.List;


public class ObjectLoadFromCSV {
    private final static ConnectionProvider.Holder holder = ConnectionProvider.create();

    public static void main(String[] args) {
        try {
            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            TimeKeeper timekeeper = new TimeKeeper("load", false);
            FileReader filereader = new FileReader("C:/Users/Ria Bhatia/IdeaProjects/aurora-sizing/data/index/ClassicIndex.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader) .withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            objectRepository.insertIndexRecordsFromCSV(allData);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
          holder.close();
        }
    }
}
