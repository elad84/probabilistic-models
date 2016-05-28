package com.idc.parameter;

import com.idc.model.ObservationsData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by annishaa on 5/28/16.
 */
public class ObservationDataInputFileProvider {

    public ObservationsData readObservationsDataFile(String filename){
        BufferedReader br = null;
        ObservationsData observationsData = new ObservationsData();

        try {

            URL resource = ResourceLoader.getResource(filename);
            br = new BufferedReader(new FileReader(new File(resource.getFile())));

            //read title
            //X1 X2 X3 X4 X5 X6 X7 X8 X9 X10
            String sCurrentLine = br.readLine();
            List<String> variables = Arrays.asList(sCurrentLine.split("\\s+"));
            List<List<Integer>> data = new ArrayList<>();

            while ((sCurrentLine = br.readLine()) != null) {
                String[] observation = sCurrentLine.split("\\s+");
                List<Integer> observationList = getObservation(observation);
                data.add(observationList);
            }

            observationsData.setData(data);
            observationsData.setVariables(variables);

            Map<Integer, List<Integer>> variable2Data = getVariable2Data(data);
            observationsData.setVariable2Data(variable2Data);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return observationsData;
    }

    private Map<Integer, List<Integer>> getVariable2Data(List<List<Integer>> data){
        Map<Integer, List<Integer>> variable2Data = new HashMap<>();
        data.stream().forEach(observationList -> {
           for (int i=0;i<observationList.size();++i){
                if (!variable2Data.containsKey(i)){
                    variable2Data.put(i,new ArrayList<>());
                }
               variable2Data.get(i).add(observationList.get(i));
            }
        });
        return variable2Data;

    }

    private List<Integer> getObservation(String[] observation) {
        List<Integer> observationList = new ArrayList<>();
        for (String o : observation) {
            observationList.add(Integer.valueOf(o));
        }
        return observationList;
    }

    public static class ResourceLoader {

        public static URL getResource(String resource) {
            final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
            classLoaders.add(Thread.currentThread().getContextClassLoader());
            classLoaders.add(ResourceLoader.class.getClassLoader());

            for (ClassLoader classLoader : classLoaders) {
                final URL url = getResourceWith(classLoader, resource);
                if (url != null) {
                    return url;
                }
            }

            final URL systemResource = ClassLoader.getSystemResource(resource);
            if (systemResource != null) {
                return systemResource;
            } else {
                try {
                    return new File(resource).toURI().toURL();
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }

        private static URL getResourceWith(ClassLoader classLoader, String resource) {
            if (classLoader != null) {
                return classLoader.getResource(resource);
            }
            return null;
        }

    }
}
