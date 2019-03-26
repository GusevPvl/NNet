package com.company.neuralnet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellReference;

public class NNExperiments {
    public static void startExp(String paramsfile) {
        //HashMap для считывания параметров эксперимента
        Map<String, Integer> expirementparams = new HashMap<String, Integer>();
        //Коллекция для записи всех строк из файла
        List<String> ExpirementParamsFromFile;
        //Путь к файлу с параметрами
        Path path = Paths.get(paramsfile);
        try {
            //Считывание всех строк
            ExpirementParamsFromFile = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
            //Проход по всем строкам, разделение по :, запись в hashmap по ключу - значению
            for (int i = 0; i < ExpirementParamsFromFile.size(); i++) {
                String[] s = ExpirementParamsFromFile.get(i).split(":");
                expirementparams.put(s[0], Integer.valueOf(s[1]));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        boolean biasOnOf = expirementparams.get("biasOnOff") == 1 ? true : false;
        double learningrate = (double) 1 / expirementparams.get("learningRate");
        //Определение: тест или тренировка
        if (expirementparams.get("startMode") == 1) {
            //Создание книги для записи результатов теста
            Workbook wb = new HSSFWorkbook();
            //Создание листа для записи основных результатов
            Sheet sheet = wb.createSheet("ErrorResult");
            /*Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Эксперимент");
            cell = row.createCell(1);
            cell.setCellValue("Время поиска");
            cell = row.createCell(2);
            cell.setCellValue("Количество эпох");
            cell = row.createCell(3);
            cell.setCellValue("Ошибка");*/
            //Запись в файл
            try {
                FileOutputStream fileOut = new FileOutputStream("Expirements\\TestResults.xls");
                wb.write(fileOut);
                fileOut.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            NeuralNet nnet = new NeuralNet("NNetSettings.txt",
                    1, (double) 1 / expirementparams.get("trainingAccuracy"),
                    (expirementparams.get("trainingTimeLimit") * 1000), expirementparams.get("intialDataType"), biasOnOf,
                    expirementparams.get("epochCountLimit"), learningrate,expirementparams.get("writeAllResults"));
            nnet.Test();

        } else {
            //Определение необходимости создания новой книги
            if (expirementparams.get("newExpirementBook") == 1) {
                //Создание книги для записи результатов экспериментов
                Workbook wb = new HSSFWorkbook();
                //Создание листа для записи основных результатов
                Sheet sheet = wb.createSheet("TotalResult");
                Row row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("Эксперимент");
                cell = row.createCell(1);
                cell.setCellValue("Количество нейронов 1 скрытого слоя");
                cell = row.createCell(2);
                cell.setCellValue("Время поиска");
                cell = row.createCell(3);
                cell.setCellValue("Количество эпох");
                cell = row.createCell(4);
                cell.setCellValue("Ошибка обучения");
                cell = row.createCell(5);
                cell.setCellValue("Ошибка тестирования");
                cell = row.createCell(6);
                cell.setCellValue("Заданная точность");
                cell = row.createCell(7);
                cell.setCellValue("Скорость обучения");
                cell = row.createCell(8);
                cell.setCellValue("Нейрон смещения");
                cell = row.createCell(9);
                cell.setCellValue("Начальные значения");
                //Запись в файл
                try {
                    FileOutputStream fileOut = new FileOutputStream("Expirements\\ExpirementResults.xls");
                    wb.write(fileOut);
                    fileOut.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            //Список для хранения всех параметров экспериментов
            List<List<Integer>> allExpirementsNeurons = new LinkedList<>();
            //Создание и заполнение массива с возможными значениями количества нейронов на слое
            ArrayList<Integer> valuesofNeurons = new ArrayList<>();
            for (int neuronsnum = expirementparams.get("minNeuronOnLayer"); neuronsnum < expirementparams.get("maxNeuronOnLayer") + 1; neuronsnum += expirementparams.get("stepNeuronOnLayer")) {
                valuesofNeurons.add(neuronsnum);
            }
            //Запуск просчета количества вариаций экспериментов по слоям
            List<Integer> currResult;//Для хранения промежуточных результатов по нейронам на слоях
            for (int layersnum = expirementparams.get("minHiddenLayers"); layersnum < expirementparams.get("maxHiddenLayers") + 1; layersnum += expirementparams.get("stepHiddenLayers")) {
                //Если набор нейронов на слоях фиксированный
                if (expirementparams.get("fixedExpirement") == 1) {
                    path = Paths.get("Expirements\\FixedExpirement.txt");
                    try {
                        //Считывание всех строк из файла с заданными параметрами
                        ExpirementParamsFromFile = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
                        //Проход по всем строкам, разделение по " ", запись в currresult, затем в allExpirementsNeurons
                        for (int i = 0; i < ExpirementParamsFromFile.size(); i++) {
                            currResult = new LinkedList<>();
                            String[] s = ExpirementParamsFromFile.get(i).split(" ");
                            for (String subs :
                                    s) {
                                currResult.add(Integer.valueOf(subs));
                            }
                            allExpirementsNeurons.add(currResult);
                        }
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else {
                    //Определяется необходимость комбинирования количества нейронов по слоям
                    if (expirementparams.get("combineNeuronsOnLayers") == 1) {
                        //Если комбинируется - запускается рекурсивный метод
                        int[] ijk = new int[layersnum];
                        allExpirementsNeurons = permutations(layersnum, layersnum, ijk, valuesofNeurons, allExpirementsNeurons);
                    } else {
                        //Если нет - на каждый слои добавляется одинаковое количество нейронов
                        for (int znachCount = 0; znachCount < valuesofNeurons.size(); znachCount++) {
                            //Коллекция для записи результатов каждой конкретной итерации
                            currResult = new LinkedList<>();
                            //Цикл по количеству текущих слоев
                            for (int layersCount = 0; layersCount < layersnum; layersCount++) {
                                //На каждый слой добавляется одинаковое количество нейронов
                                currResult.add(valuesofNeurons.get(znachCount));
                            }
                            //Текущий результат итерации по количеству нейронов на слое записывается в коллекцию
                            allExpirementsNeurons.add(currResult);
                        }
                    }
                }
            }
            //Список точности обучения по экспериментам
            List<Double> trainingAccuracyExpirement = new LinkedList<>();
            trainingAccuracyExpirement.add((double) 1 / expirementparams.get("trainingAccuracy"));
            for (int step = 1; step < expirementparams.get("stepstrainingAccuracy"); step++) {
                trainingAccuracyExpirement.add((double) 1 / expirementparams.get("trainingAccuracy")/(expirementparams.get("dividertrainingAccuracy")*step));
            }
            //Список скорости обучения по экспериментам
            List<Double> learingrateExpirement=new LinkedList<>();
            learingrateExpirement.add((double) 1 / expirementparams.get("learningRate"));
            for (int step = 1; step < expirementparams.get("stepslearningRate"); step++) {
                learingrateExpirement.add((double) 1 / expirementparams.get("learningRate")/(expirementparams.get("dividerlearningRate")*step));
            }
            //Всего экспериментов
            int expamount = trainingAccuracyExpirement.size()*learingrateExpirement.size()*allExpirementsNeurons.size();
            int k = 1;//Счетчик экспериментов
            //Запуск экспериментов по точности обучения
            for (double trainingAccuracy:
                 trainingAccuracyExpirement) {
                //Запуск экспериментов по скорости обучения
                for (double learningrateExp:
                     learingrateExpirement) {

                    //Запуск экспериментов
                    for (List<Integer> currentExpirement :
                            allExpirementsNeurons) {
                        //Создание файла с параметрами текущего эксперимента
                        File outputfile = new File("NNetSettings.txt");
                        try (FileWriter writer = new FileWriter(outputfile, false)) {
                            //Цикл по количеству слоёв с записью нейронов на каждый слой
                            for (int neurons :
                                    currentExpirement) {
                                writer.append(Integer.toString(neurons));
                                writer.append('\n');
                            }
                            writer.flush();
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        //Запуск экспериментов по количеству наблюдений
                        for (int i = 0; i < expirementparams.get("observationsPerExpirement"); i++) {
                            System.out.println("Эксперимент " + k + " из " + expamount + " экспериментов");
                            //Запуск сети
                            NeuralNet nnet = new NeuralNet("NNetSettings.txt",
                                    0, trainingAccuracy,
                                    (expirementparams.get("trainingTimeLimit") * 1000), expirementparams.get("intialDataType"), biasOnOf,
                                    expirementparams.get("epochCountLimit"), learningrateExp,expirementparams.get("writeAllResults"));
                            nnet.Train();
                            nnet.Test();
                        }
                        k++;
                    }
                }
            }
        }
    }


    public static void startTest() {

    }

    //Метод получения перестановок значений
    static List<List<Integer>> permutations(int sloi, int ostatok, int[] current_ijk, List<Integer> znacheniya, List<List<Integer>> nlist) {
        //Запуск вложенных циклов по количеству чисел в выборке
        for (int i = 0; i < znacheniya.size(); i++) {
            //Значение в текущей выборке ищется по индексу в массиве входных значений
            current_ijk[ostatok - 1] = i;
            //Если это - последний уровень рекурсии
            if (ostatok - 1 == 0) {
                //Обнуляется "текущий" лист
                List<Integer> currResult = new LinkedList<>();
                //Записывается текущая выборка
                for (int j = 0; j < sloi; j++) {
                    currResult.add(znacheniya.get(current_ijk[j]));
                }
                //Добавляется в общий список
                nlist.add(currResult);
            } else {
                //Если не последний уровень - запуск рекурсии
                permutations(sloi, ostatok - 1, current_ijk, znacheniya, nlist);
            }
        }
        return nlist;//
    }

}


//Класс для хранения параметров экспериментов
class ExpParams {
    private int minHiddenLayers;
    private int maxHiddenLayers;
    private int minNeuronOnLayer;
    private int maxNeuronOnLayer;
    private int stepHiddenLayers;
    private int stepNeuronOnLayer;

    public void setMinHiddenLayers(int minHiddenLayers) {
        this.minHiddenLayers = minHiddenLayers;
    }

    public int getMinHiddenLayers() {
        return minHiddenLayers;
    }

    public void setMaxHiddenLayers(int maxHiddenLayers) {
        this.maxHiddenLayers = maxHiddenLayers;
    }

    public int getMaxHiddenLayers() {
        return maxHiddenLayers;
    }

    public void setMinNeuronOnLayer(int minNeuronOnLayer) {
        this.minNeuronOnLayer = minNeuronOnLayer;
    }

    public int getMinNeuronOnLayer() {
        return minNeuronOnLayer;
    }

    public void setMaxNeuronOnLayer(int maxNeuronOnLayer) {
        this.maxNeuronOnLayer = maxNeuronOnLayer;
    }

    public int getMaxNeuronOnLayer() {
        return maxNeuronOnLayer;
    }

    public void setStepHiddenLayers(int stepHiddenLayers) {
        this.stepHiddenLayers = stepHiddenLayers;
    }

    public int getStepHiddenLayers() {
        return stepHiddenLayers;
    }

    public void setStepNeuronOnLayer(int stepNeuronOnLayer) {
        this.stepNeuronOnLayer = stepNeuronOnLayer;
    }

    public int getStepNeuronOnLayer() {
        return stepNeuronOnLayer;
    }

}
