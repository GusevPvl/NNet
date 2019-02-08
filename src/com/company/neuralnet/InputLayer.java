package com.company.neuralnet;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.sql.*;

enum MemoryMode {GET, SET}

enum NeuronType {hidden, output}

class InputLayer {
    public double[][] trainset = new double[][]{
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };
    public double[][] error = new double[][]{
            {0, 1, 0},
            {1, 0, 1},
            {1, 0, 1},
            {0, 1, 1}
    };
    public String[] resultname = new String[]{
            "xor", "xand", "or"
    };

    //Тренировочный сет для загрузки из БД/Excel
    public double[][] trainsetDB;
    //Массив результатов для загрузки из БД/Excel
    public double[][] errorDB;

    //Статический метод, возвращающий новый входной слой со значениями из Excel-файла ("InputData.xls")
    public static InputLayer InputDataFromExcel() {
        InputLayer inputLayer = new InputLayer(1,1);


        return inputLayer;
    }

    //Конструктор с загрузкой данных из БД/Excel
    public InputLayer(Integer type, Integer TrainTestType) {
        //Если считывание исходных данных происходит из Excel
        if (type == 1) {
            String TrainSet = "Trainset";
            String ErrorSet = "Errorset";
            if (TrainTestType == 1) {
                TrainSet = "Testset";
                ErrorSet = "Testerror";
            }
            //Считывание книги в IntialData
            Workbook IntialData = new HSSFWorkbook();
            try {
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("IntialData\\IntialData.xls"));
                IntialData = new HSSFWorkbook(fs);

            } catch (Exception e) {

            }
            //Считывание листа с trainset
            Sheet sheet = ((HSSFWorkbook) IntialData).getSheet(TrainSet);
            //Инициализация массива trainsetDB. sheet.getLastRowNum()-получение последнего индекса по строкам, sheet.getRow(0).getLastCellNum() - получение последнего индекса по столбцам
            trainsetDB = new double[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];
            //Проход по всем строкам и столбцам и запись в trainset
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (row.getRowNum() > 0)
                        trainsetDB[row.getRowNum() - 1][cell.getColumnIndex()] = Double.valueOf(cell.toString());
                }
            }
            //Считывание листа с errorset
            sheet = ((HSSFWorkbook) IntialData).getSheet(ErrorSet);
            //Инициализация массива errorDB. sheet.getLastRowNum()-получение последнего индекса по строкам, sheet.getRow(0).getLastCellNum() - получение последнего индекса по столбцам
            //-1, т.к. первая строка не считается (там записаны названия)
            errorDB = new double[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];
            //Проход по всем строкам и столбцам и запись в errorset
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (row.getRowNum() > 0)
                        errorDB[row.getRowNum() - 1][cell.getColumnIndex()] = Double.valueOf(cell.toString());
                }
            }


        }
        //Если считывание исходных данных происходит из БД
        else {
            Connection conn = null; //Соединение с БД
            String dbName = "NNDB"; //Имя БД
            String serverip = "192.168.2.227"; //IP сервера
            String url = "jdbc:sqlserver://" + serverip + "\\Student" + ";databaseName=" + dbName + ""; //url-запрос на подключение
            Statement stmt = null; //Выражение для выполнения запроса
            ResultSet result = null; //Поле для выгрузки результатов запросов
            ResultSet result1 = null;
            String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //Используемый драйвер для подключения к MySQL
            String databaseUserName = "Student"; //Имя пользователя
            String databasePassword = "S123456s"; //Пароль пользователя
            try {
                Class.forName(driver).newInstance(); //Подключение драйвера
                conn = DriverManager.getConnection(url, databaseUserName, databasePassword); //Запрос соединения
                stmt = conn.createStatement(); //Создание выражения на текущем соединении
                //Запись trainsetDB
                result = null; //Обнуление поля результатов запроса
                result = stmt.executeQuery("select COUNT(*) from ExpirementInputs "); //Получение количества строк в таблице
                int rowsnum = 0; //Поле для записи количества строк
                if (result.next()) {
                    rowsnum = result.getInt(1); //Получение количества строк
                }
                result = null; //Обнуление поля результатов запроса
                result = stmt.executeQuery("SELECT * FROM ExpirementInputs");
                int columnnum = 0; //Поле для получение количества столбцов для создания массива тренировочного сета
                columnnum = result.getMetaData().getColumnCount() - 1; //Получение количества столбцов для создания массива тренировочного сета (9столбец - номер эксперимента)
                trainsetDB = new double[rowsnum][columnnum];//Создание тренировочного сета количество строк * количество столбцов
                //Считывание данных тренировочного сета
                while (result.next()) {
                    for (int i = 0; i < columnnum; i++) {
                        trainsetDB[result.getRow() - 1][i] = result.getInt(i + 1); //Получение данных построчно (кроме 9 столбца)
                    }
                    System.out.println(result.getRow());
                }
                //Вывод trainsetDB для проверки
                for (int i = 0; i < rowsnum; i++) {
                    for (int j = 0; j < columnnum; j++)
                        System.out.print(trainsetDB[i][j]);
                    System.out.println();
                }

                //Запись errorDB из БД
                result = null; //Обнуление поля результатов запроса
                result = stmt.executeQuery("select COUNT(*) from ExpPSRes "); //Получение количества строк в таблице
                if (result.next()) {
                    rowsnum = result.getInt(1); //Получение количества строк в ExpPSRes
                }
                result = null; //Обнуление поля результатов запроса
                result = stmt.executeQuery("SELECT * FROM ExpPSRes");
                columnnum = result.getMetaData().getColumnCount() - 1; //Получение количества столбцов для создания массива тренировочного сета ExpPSRes
                errorDB = new double[rowsnum][columnnum];//Создание тренировочного сета количество строк * количество столбцов
                //Считывание данных тренировочного сета
                while (result.next()) {
                    for (int i = 0; i < columnnum; i++) {
                        errorDB[result.getRow() - 1][i] = result.getDouble(i + 1); //Получение данных построчно
                    }
                    System.out.println(result.getRow());
                }
                //Вывод trainsetDB для проверки
                for (int i = 0; i < rowsnum; i++) {
                    for (int j = 0; j < columnnum; j++)
                        System.out.print(errorDB[i][j] + " ");
                    System.out.println();
                }
                System.out.println("Connection OK");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public double[][] getTrainset() {
        return trainset;
    }
}
