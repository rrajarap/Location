package org.rrajarap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;


public class CsvToJson {

    public static void main(String[] args) throws IOException {
        final File file = new File(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
                .getResource("Data.csv"))
                .getFile());

        Map<String, PinCodeData> data = new HashMap<>();

        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line;

            while ((line = br.readLine()) != null) {
                ProcessLine(data, line);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        //Printing to string
        //String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        //System.out.println(jsonString);

        //Write to file unformatted
        //mapper.writeValue(Paths.get("dataOut.json").toFile(), data);

        //Formatted file. If file already exists, will overwrite it.
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("dataOut.json").toFile(), data);

    }

    private static void ProcessLine(Map<String, PinCodeData> data, String line) {
        String[] items = line.split(",");
        String state = items[0];
        String city = items[1];
        String mandal = items[2];
        String village = items[3];
        String pinCode = items[4];

        PinCodeData pinCodeDataObj;
        if (data.containsKey(pinCode)) {
            pinCodeDataObj = data.get(pinCode);
        } else {
            pinCodeDataObj = new PinCodeData();
            data.put(pinCode, pinCodeDataObj);
        }

        pinCodeDataObj.state = state;

        DistData distDataObj;
        if (pinCodeDataObj.labelToDistDataMap.containsKey(city)) {
            distDataObj = pinCodeDataObj.labelToDistDataMap.get(city);
        } else {
            distDataObj = new DistData();
            pinCodeDataObj.labelToDistDataMap.put(city, distDataObj);
            pinCodeDataObj.dists.add(distDataObj);
        }

        distDataObj.city = city;

        Mandal mandalObj;
        if (distDataObj.labelToMandalMap.containsKey(mandal)) {
            mandalObj = distDataObj.labelToMandalMap.get(mandal);
        } else {
            mandalObj = new Mandal();
            mandalObj.label = mandal;
            mandalObj.value = mandal;
            distDataObj.labelToMandalMap.put(mandal, mandalObj);
            distDataObj.mandals.add(mandalObj);
        }

        Area areaObj;
        if (!mandalObj.labelToAreaMap.containsKey(village)) {
            areaObj = new Area();
            areaObj.label = village;
            areaObj.value = village;
            mandalObj.labelToAreaMap.put(village, areaObj);
            mandalObj.areas.add(areaObj);
        }
    }


    public static class Mandal {
        public String label;
        public String value;
        public List<Area> areas = new ArrayList<>();

        @JsonIgnore
        public Map<String, Area> labelToAreaMap = new HashMap<>();
    }

    public static class Area {
        public String label;
        public String value;
    }

    public static class PinCodeData {
        public String state;
        public List<DistData> dists = new ArrayList<>();

        @JsonIgnore
        public Map<String, DistData> labelToDistDataMap = new HashMap<>();
    }

    public static class DistData {
        public String city;
        public List<Mandal> mandals = new ArrayList<>();

        @JsonIgnore
        public Map<String, Mandal> labelToMandalMap = new HashMap<>();
    }

}
