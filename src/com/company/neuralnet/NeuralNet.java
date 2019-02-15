package com.company.neuralnet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NeuralNet {
    //все слои сети, инициализация в конструкторах
    InputLayer input_layer;// = new InputLayer(); //Инициализация входного слоя - задается отдельным классом
    public HiddenLayer hidden_layer;// = new HiddenLayer(200, input_layer.trainsetDB[1].length, NeuronType.hidden, "hidden"); //Инициализация скрытого слоя
    //public HiddenLayer hidden_layer1 = new HiddenLayer(4, 4, NeuronType.hidden, "hidden1"); //Инициализация скрытого слоя
    public OutputLayer output_layer;// = new OutputLayer(input_layer.errorDB[1].length, 200, NeuronType.output, "output"); //Ининциализация выходного слоя
    //Массив скрытых слоев
    public HiddenLayer[] hidden_layers;
    //массив для хранения выхода сети (фактические значения) - размерность: количество выходных нейронов
    public double[] fact;
    //Точность обучения
    double trainingAccuracy;
    //Предельное время обучения
    private int trainingTimeLimit;
    //Предельное количество эпох обучения
    private int epochCountLimit;
    //Скорость обучения - только для записи в файл, в слои передается напрямую
    private double learningrate;
    //Нейрон смещения - для записи в файл с результатами
    private boolean bias;

    //Стандартный конструктор
    public NeuralNet() {
        //все слои сети
        input_layer = new InputLayer(1, 1); //Инициализация входного слоя - задается отдельным классом
        hidden_layer = new HiddenLayer(200, input_layer.trainsetDB[1].length, NeuronType.hidden, "hidden", true, 0.001); //Инициализация скрытого слоя
        output_layer = new OutputLayer(input_layer.errorDB[1].length, 200, NeuronType.output, "output", true, 0.001); //Ининциализация выходного слоя
        fact = new double[input_layer.errorDB[1].length];//Инициализация массива фактических значений
    }

    //Конструктор для создания произвольного количества скрытых слоёв с заданным количеством нейронов
    public NeuralNet(String SettingsFile, Integer mode, double trainingAccuracy, int trainingTimeLimit,
                     Integer IntialDataType, boolean bias, int epochCountLimit, double learningrate) {
        //Задание точности, времени обучения, максимальное количество эпох, скорость обучения
        this.trainingAccuracy = trainingAccuracy;
        this.trainingTimeLimit = trainingTimeLimit;
        this.epochCountLimit = epochCountLimit;
        this.learningrate = learningrate;
        this.bias=bias;
        input_layer = new InputLayer(IntialDataType, mode); //Инициализация входного слоя - задается отдельным классом
        fact = new double[input_layer.errorDB[1].length];//Инициализация массива фактических значений
        List<String> NeuronsOnHiddenLayers;
        Path path = Paths.get(SettingsFile);
        try {
            NeuronsOnHiddenLayers = Files.readAllLines(path);
            hidden_layers = new HiddenLayer[NeuronsOnHiddenLayers.size()]; //Инициализация массива скрытых слоев
            if (mode == 0)
                WeightsFilesInitialize(SettingsFile, NeuronsOnHiddenLayers.size(), bias ? 1 : 0);
            //Создание первого скрытого слоя, количество предыдущих нейронов - количество нейронов входного слоя
            hidden_layers[0] = new HiddenLayer(Integer.valueOf(NeuronsOnHiddenLayers.get(0)), input_layer.trainsetDB[1].length, NeuronType.hidden, "hidden", bias, learningrate);
            //Создание остальных скрытых слоев
            for (int i = 1; i < NeuronsOnHiddenLayers.size(); i++) {
                hidden_layers[i] = new HiddenLayer(Integer.valueOf(NeuronsOnHiddenLayers.get(i)), hidden_layers[i - 1].numofneurons, NeuronType.hidden, "hidden" + i, bias, learningrate);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        output_layer = new OutputLayer(input_layer.errorDB[1].length, hidden_layers[hidden_layers.length - 1].numofneurons, NeuronType.output, "output", bias, learningrate); //Ининциализация выходного слоя//НЕЙРОН СМЕЩЕНИЯ
    }

    //ошибка одной итерации обучения
    double GetMSE(double[] errors) {
        double sum = 0;
        for (int i = 0; i < errors.length; ++i)
            sum += Math.pow(errors[i], 2);
        return sum;
    }

    //ошибка эпохи
    double GetCost(double[] mses) {
        double sum = 0;
        for (int i = 0; i < mses.length; ++i)
            sum += mses[i];
        return (sum / mses.length);
    }

    //Обнуление весов
    public static void ResetWeights() {
        File hiddenfile = new File("hidden.txt");
        File outputfile = new File("output.txt");
        try (FileWriter writer = new FileWriter(hiddenfile, false)) {
            for (int l = 0; l < 16000; ++l) {
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (FileWriter writer = new FileWriter(outputfile, false)) {
            for (int l = 0; l < 800; ++l) {
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //обучение
    public void Train()//backpropagation method
    {
        int epochCounter = 0;//Подсчет количества эпоз обучения
        double bestCost = 1.0;//Для просмотра текущего лучшего результата ошибки
        long startTrainTime = System.currentTimeMillis(); //Запись времени начала обчения
        final double threshold = 0.001;//порог ошибки
        double[] temp_mses = new double[input_layer.errorDB.length];//массив для хранения ошибок итераций
        double temp_cost = 0;//текущее значение ошибки по эпохе
        LinkedList<Double> cost_list = new LinkedList<Double>();//Коллекция для хранения значений ошибки
        //Цикл до достижения указанного порога ошибки
        do {
            //Цикл по количеству "тренировочных" сетов
            for (int i = 0; i < input_layer.trainsetDB.length; ++i) {
                //прямой проход
                //Получение входных значений первым скрытым слоем (без фукции активации и без умножения на веса)
                hidden_layers[0].Data(input_layer.trainsetDB[i]);
                //Расчет с проходом по скрытым слоям
                for (int j = 1; j < hidden_layers.length; j++) {
                    hidden_layers[j - 1].OutputCalculate(this, hidden_layers[j]);
                }
                hidden_layers[hidden_layers.length - 1].OutputCalculate(this, output_layer);
                //Вычисление выходных значений сети (запись в net.fact)
                output_layer.OutputCalculate(this, null);
                //вычисление ошибки по итерации
                double[] errors = new double[input_layer.errorDB[i].length];
                for (int x = 0; x < errors.length; ++x)
                    errors[x] = input_layer.errorDB[i][x] - fact[x];
                temp_mses[i] = GetMSE(errors);
                //обратный проход и коррекция весов
                double[] temp_gsums = output_layer.BackwardPass(errors); //Распространение ошибки выходного слоя на входные веса
                //Коррекция весов по скрытым слоям
                //Как минимум 1 скрытый слой существует
                double[] temp_gsums_hidden = hidden_layers[hidden_layers.length - 1].BackwardPass(temp_gsums);
                if (hidden_layers.length > 1) {
                    for (int j = hidden_layers.length - 2; j >= 0; j--) {
                        temp_gsums_hidden = hidden_layers[j].BackwardPass(temp_gsums_hidden);
                    }
                }
            }
            temp_cost = GetCost(temp_mses);//вычисление ошибки по эпохе
            cost_list.add(temp_cost);//Запись ошибки по эпохе в коллекцию
            //debugging output
            bestCost = bestCost > temp_cost ? temp_cost : bestCost;
            String formattedTempCost = new DecimalFormat("#0.00000000").format(temp_cost);
            String formattedBestCost = new DecimalFormat("#0.00000000").format(bestCost);
            System.out.print("\r");
            System.out.print(cost_list.size() + " : ");
            System.out.print("best = " + formattedBestCost);
            System.out.print(" : temp = " + formattedTempCost);
            epochCounter++;
            //Установка предельного времени обучения сети/количества эпох
            if (((epochCounter == epochCountLimit) && epochCountLimit != 0) || (((System.currentTimeMillis() - startTrainTime) > trainingTimeLimit) && trainingTimeLimit != 0)) {
                System.out.println();
                System.out.print("Превышено количество эпох/время обучения");
                break;
            }
        } while (temp_cost > trainingAccuracy);
        System.out.println();
        long stopTrainTime = System.currentTimeMillis(); //Запись времени завершения обучения
        //загрузка скорректированных весов в "память"
        //Запись весов скрытых слоев
        hidden_layers[0].WeightInitialize(MemoryMode.SET, "hidden");
        for (int i = 1; i < hidden_layers.length; i++)
            hidden_layers[i].WeightInitialize(MemoryMode.SET, "hidden" + i);
        //Запись весов выходного слоя
        output_layer.WeightInitialize(MemoryMode.SET, "output");

        //Запись результатов обучения в файл
        WriteTrainResultsToFile(MillisToHours(stopTrainTime - startTrainTime), cost_list);
    }

    //тестирование сети
    public void Test() {
        input_layer = new InputLayer(1, 1);
        //Массив для хранения фактических результатов по каждому тренировочному сету
        ArrayList<double[]> allFactTestResult = new ArrayList<>();
        //double[][] allFactTestResult=new double[input_layer.trainsetDB.length][input_layer.errorDB[1].length];
        //System.out.println("Результаты тестирования сети" + input_layer.trainset.length);
        double[] temp_mses = new double[input_layer.errorDB.length];//массив для хранения ошибок итераций
        for (int i = 0; i < input_layer.trainsetDB.length; ++i) {
            fact = new double[input_layer.errorDB[1].length];
            hidden_layers[0].Data(input_layer.trainsetDB[i]); //Как минимум 1 скрытый слой существует
            //Расчет с проходом по скрытым слоям
            for (int j = 1; j < hidden_layers.length; j++) {
                hidden_layers[j - 1].OutputCalculate(this, hidden_layers[j]);
            }
            hidden_layers[hidden_layers.length - 1].OutputCalculate(this, output_layer);
            output_layer.OutputCalculate(this, null);
            //Расчет ошибки текущего сета
            double[] errors = new double[input_layer.errorDB[i].length];
            for (int x = 0; x < errors.length; ++x)
                errors[x] = input_layer.errorDB[i][x] - fact[x];
            //Получение сркдр ошибки для всех итераций
            temp_mses[i] = GetMSE(errors);
            //Получение среднеквадратичной ошибки текущей итерации
            double mse = GetMSE(errors);
            for (int j = 0; j < fact.length; ++j)
                //System.out.print(fact[j] + " ");
            allFactTestResult.add(fact);
            //System.out.println();
            //System.out.println("Ошибка сета = " + mse);
        }
        String formattedMSE = new DecimalFormat("#0.000").format(GetCost(temp_mses));
        System.out.println("Общая ошибка тестирования = " + formattedMSE);
        WriteTestResultsToFile(formattedMSE, allFactTestResult);
    }

    //Метод преобразования миллисекунд в ЧЧ:ММ:СС.ССС
    private static String MillisToHours(long millis) {
        String returntext;
        double resulttime = (double) (millis) / 1000;
        int hours = (int) resulttime / 3600;
        int minutes = (int) (resulttime % 3600) / 60;
        double seconds = (double) ((resulttime % 3600) % 60);
        java.text.DecimalFormat numberFormat = new java.text.DecimalFormat("#.000");
        returntext = hours + ":" + minutes + ":" + numberFormat.format(seconds) + millis;
        return returntext;
    }

    //Метод записи результатов тестирования сети
    private void WriteTestResultsToFile(String testMSE, ArrayList<double[]> allFactTestResult) {
        //Запись результатов тестирования в таблицу Excel
        Workbook wb = new HSSFWorkbook();
        //Попытка открыть файл
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("Expirements\\ExpirementResults.xls"));
            wb = new HSSFWorkbook(fs);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //Открытие листа для записи общей ошибки тестирования
        Sheet sheet = ((HSSFWorkbook) wb).getSheet("TotalResult");
        //Вычисление последнего занятого столбца в листе с основными результатами
        int rownum = 0;
        for (Row row : sheet) {
            rownum = row.getRowNum();
        }
        //Создание названия эксперимента для открытия нужного листа книги
        String expName = "";
        //Цикл для создания названия файла с результатами/названия строки в Excel
        for (int i = 0; i < hidden_layers.length; i++) {
            expName += "_" + hidden_layers[i].numofneurons;
        }
        expName += sheet.getLastRowNum()+"Test";//добавление номера строки для идентификации, в случае нескольких запусков одного эксперимента
        Row row = sheet.getRow(rownum);
        Cell cell;
        cell = row.createCell(4);
        cell.setCellValue(testMSE);
        //Создание листа для записи экспериментов
        Sheet expSheet = wb.createSheet(expName);
        int rowCount = 0;//подсчет строк для записи
        try {
            //Проход по всем результатам тестирования сети
            for (int i = 0; i < allFactTestResult.size(); i++) {
                row = expSheet.createRow(i);
                for (int j = 0; j < allFactTestResult.get(i).length; j++) {

                    cell = row.createCell(j);
                    cell.setCellValue(allFactTestResult.get(i)[j]);
                    rowCount++;
                }
            }
            //Попытка записи файла
            FileOutputStream fileOut = new FileOutputStream("Expirements\\ExpirementResults.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    //Метод записи результатов обучения сети
    private void WriteTrainResultsToFile(String trainTime, LinkedList<Double> error_list) {
        //Запись результатов в таблицу Excel
        Workbook wb = new HSSFWorkbook();
        //Попытка открыть файл
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("Expirements\\ExpirementResults.xls"));
            wb = new HSSFWorkbook(fs);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //Открытие листа для записи основных результатов
        Sheet sheet = ((HSSFWorkbook) wb).getSheet("TotalResult");
        //Вычисление последнего занятого столбца в листе с основными результатами
        int rownum = 0;
        for (Row row : sheet) {
            rownum = row.getRowNum();
        }
        //Создание названия эксперимента
        String expName = "";
        //Цикл для создания названия файла с результатами/названия строки в Excel
        for (int i = 0; i < hidden_layers.length; i++) {
            expName += "_" + hidden_layers[i].numofneurons;
        }
        Row row = sheet.createRow(rownum + 1);
        expName += row.getRowNum();//добавление номера строки для идентификации, в случае нескольких запусков одного эксперимента
        Cell cell = row.createCell(0);
        cell.setCellValue(expName);
        cell = row.createCell(1);
        cell.setCellValue(trainTime);
        cell = row.createCell(2);
        cell.setCellValue(error_list.size());
        cell = row.createCell(3);
        cell.setCellValue(String.valueOf(error_list.getLast()));
        cell = row.createCell(5);
        cell.setCellValue(trainingAccuracy);
        cell = row.createCell(6);
        cell.setCellValue(learningrate);
        cell = row.createCell(7);
        cell.setCellValue(String.valueOf(bias));
        //Запись весов скрытых слоев
        Sheet expSheet = wb.createSheet(expName);
        int rowsNum = 0;
        //Вычисление количества строк, необходимых для записи. Сравнение количества связей скрытых слоев.
        for (int i = 0; i < hidden_layers.length; i++) {
            if (hidden_layers[i].numofneurons * hidden_layers[i].numofprevneurons > rowsNum)
                rowsNum = hidden_layers[i].numofneurons * hidden_layers[i].numofprevneurons;
        }
        //Сравнение с количеством срок в коллекции ошибок
        if (error_list.size() > rowsNum) rowsNum = error_list.size();
        //Сравнение количества строк с количеством связей выходного уровня
        if (output_layer.numofneurons > rowsNum) rowsNum = output_layer.numofneurons;
        //Строка и ячейка для записи результатов эксперимента
        Row expRow;
        Cell expCell;
        //Создание строк для записи весов и эволюции ошибок
        for (int i = 0; i < rowsNum; i++)
            expRow = expSheet.createRow(i);
        int rowCount = 0;
        //Блок try для записи результатов эксперимента
        try {
            //Проход по всем скрытым слоям, для записи весов
            for (int i = 0; i < hidden_layers.length; i++) {
                //Проход по всем нейронам
                for (int j = 0; j < hidden_layers[i].numofneurons; j++)
                    //Проход по всем весам (нейроны*количество предшествующих нейронов)
                    for (int k = 0; k < hidden_layers[i].numofprevneurons; k++) {
                        expRow = expSheet.getRow(rowCount);
                        expCell = expRow.createCell(i + 1);
                        expCell.setCellValue(hidden_layers[i].neurons[j].weights[k]);
                        rowCount++;
                    }
                rowCount = 0;
            }
            rowCount = 0; //Обнуление счетчика строк
            //Запись весов выходного слоя
            for (int j = 0; j < output_layer.numofneurons; j++)
                //Проход по всем весам (нейроны*количество предшествующих нейронов)
                for (int k = 0; k < output_layer.numofprevneurons; k++) {
                    expRow = expSheet.getRow(rowCount);
                    expCell = expRow.createCell(hidden_layers.length + 1);
                    expCell.setCellValue(output_layer.neurons[j].weights[k]);
                    rowCount++;
                }
            rowCount = 0; //Обнуление счетчика строк
            //Запись ошибок вычислений в 0 столбец
            for (int i = 0; i < error_list.size(); i++) {
                expRow = expSheet.getRow(rowCount);
                expCell = expRow.createCell(0);
                expCell.setCellValue(error_list.get(i));
                rowCount++;
            }

            //Попытка записи файла
            FileOutputStream fileOut = new FileOutputStream("Expirements\\ExpirementResults.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Запись результатов в текстовые файлы
        /*String fileName = "Expirements\\result\\result";
        //Цикл для создания названия файла с результатами
        for (int i = 0; i < hidden_layers.length; i++) {
            fileName += "_" + hidden_layers[i].numofneurons;
        }
        fileName += ".txt";
        File resultfile = new File(fileName);
        try (FileWriter writer = new FileWriter(resultfile, false)) {
            writer.append("Время обучения НС: " + text + "   \n");
            writer.append("Ошибка обучения НС: " + String.valueOf(error_list.getLast()) + "   \n");
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }*/
    }

    //Метод создания файлов с весами для слоев сети
    private void WeightsFilesInitialize(String SettingsFile, int HiddenLayersNumber, int bias) {

        int prevLayerNeurons = 0;//Количество нейронов предыдущего слоя
        File settings = new File(SettingsFile);
        try (FileReader reader = new FileReader(settings)) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine(); //Считывание количества нейронов 1 скрытого слоя
            //Как минимум 1 скрытый слой всегда задается, у него на входе-входной слой
            File hiddenfile = new File("hidden.txt");
            try (FileWriter writer = new FileWriter(hiddenfile, false)) {
                for (int l = 0; l < Integer.valueOf(line) * (input_layer.trainsetDB[1].length + bias); ++l) {
                    writer.append(Double.toString(Math.sin(l*5)/15));
                    writer.append('\n');
                }
                writer.flush();
                prevLayerNeurons = Integer.valueOf(line);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            //Задание количества нейронов остальных скрытых слоёв
            for (int i = 1; i < HiddenLayersNumber; i++) {
                line = br.readLine();//Считывание количества нейронов остальных скрытых слоев
                hiddenfile = new File("hidden" + i + ".txt");
                try (FileWriter writer = new FileWriter(hiddenfile, false)) {
                    for (int l = 0; l < Integer.valueOf(line) * (prevLayerNeurons + bias); ++l) {
                        writer.append(Double.toString(Math.sin(l*5)/15));
                        writer.append('\n');
                    }
                    writer.flush();
                    prevLayerNeurons = Integer.valueOf(line);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //Создание файла для выходного слоя
        File outputfile = new File("output.txt");
        try (FileWriter writer = new FileWriter(outputfile, false)) {
            for (int l = 0; l < (prevLayerNeurons + bias) * input_layer.errorDB[1].length; ++l) {
                writer.append(Double.toString(Math.sin(l*5)/15));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}

class trainSetGo extends Thread {


    public void run() {

    }
}