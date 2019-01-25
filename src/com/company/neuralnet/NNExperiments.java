package com.company.neuralnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class NNExperiments {
    public static void startExp(String paramsfile){
        File settings = new File(paramsfile);
        try (FileReader reader = new FileReader(settings)) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine(); //Считывание данных эксперимента

            String[] expirementparamtrs;
            expirementparamtrs = line.split(":");
            for (int i=0;i<expirementparamtrs.length;i++){
                System.out.println(expirementparamtrs[i]);
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
