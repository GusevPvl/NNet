package com.company;

import com.company.neuralnet.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellReference;


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

class WordLengthCallable
        implements Callable {
    private String word;

    public WordLengthCallable(String word) {
        this.word = word;
    }

    public Integer call() {
        return Integer.valueOf(word.length());
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        /*String[] wrd=new String[]{"asd","qwe"};

        ExecutorService pool = Executors.newFixedThreadPool(3);
        Set<Future<Integer>> set = new HashSet<Future<Integer>>();
        for (String word: wrd) {
            Callable<Integer> callable = new WordLengthCallable(word);
            Future<Integer> future = pool.submit(callable);
            set.add(future);
        }
        int sum = 0;
        for (Future<Integer> future : set) {
            sum += future.get();
        }
        System.out.printf("The sum of lengths is %s%n", sum);
        System.exit(sum);
*/
       /* Workbook wb = new HSSFWorkbook();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("IntialData\\IntialData.xls"));
            wb = new HSSFWorkbook(fs);

        } catch (Exception e) {

        }
        Sheet sheet = ((HSSFWorkbook) wb).getSheet("Trainset");
        int rownum=0;
        for (Row row : sheet) {
            System.out.println(row.getRowNum());
        }*/
        /*Row row;//=sheet.createRow(rownum+1);
        row=sheet.getRow(10);
        Cell cell = row.createCell(0);
        cell.setCellValue("Время:");*/

       /* try {
            FileOutputStream fileOut = new FileOutputStream("wb.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            //Обработка ошибки
        }*/


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
        //NeuralNet nnet=new NeuralNet("NNetSettings.txt", "Train",0.01,0,1);
        //nnet.Train();
        //nnet.Test();


        //Запуск экспериментов
        NNExperiments.startExp("Expirements\\ExpirementParams.txt");


        System.out.println("Ok");
    }
}


