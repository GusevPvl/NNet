package com.company;

import com.company.neuralnet.*;


import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Main {
    public static void main(String[] args) {
        Workbook wb = new HSSFWorkbook();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("wb.xls"));
            wb = new HSSFWorkbook(fs);

        }
        catch (Exception e) {

        }

        Sheet sheet1 = wb.createSheet("new sheet");
        Sheet sheet2 = wb.createSheet("second sheet");
        //if(args.length > 0 && args[0].equals("-xls")) wb = new HSSFWorkbook();
        try {
            FileOutputStream fileOut = new FileOutputStream("wb.xls");
            wb.write(fileOut);
            fileOut.close();
        }
        catch (Exception e) {
            //Обработка ошибки
        }


        /*List<String> list1;
        try  {
            Path path = Paths.get("hidden.txt");
            list1 = Files.readAllLines(path);
            for (int i=0;i<list1.size();i++)
            {
                System.out.println(list1.get(i));
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }*/

        //Запуск по новому
        /*NeuralNet nnet=new NeuralNet("NNetSettings.txt", 1,"Train");
        nnet.Train();
        nnet.Test();*/
        //NNExperiments.startExp("Expirements\\ExpirementParams.txt");

        System.out.println("Ok");
    }
}
