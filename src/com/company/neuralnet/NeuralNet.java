package com.company.neuralnet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NeuralNet {
    //все слои сети
    InputLayer input_layer = new InputLayer(); //Инициализация входного слоя - задается отдельным классом
    public HiddenLayer hidden_layer = new HiddenLayer(4, input_layer.trainset[1].length, NeuronType.hidden, "hidden"); //Инициализация скрытого слоя
//    public HiddenLayer hidden_layer1 = new HiddenLayer(4, 4, NeuronType.hidden, "hidden1"); //Инициализация скрытого слоя
    public OutputLayer output_layer = new OutputLayer(input_layer.error[1].length, 4, NeuronType.output, "output"); //Ининциализация выходного слоя

    public HiddenLayer[] hidden_layers;
    //массив для хранения выхода сети (фактические значения) - размерность: количество выходных нейронов
    public double[] fact = new double[input_layer.error[1].length];
    //Стандартный конструктор
    public NeuralNet(){}
    //Конструктор:
    public NeuralNet(int HiddenLayersNumber){
        for (int i=0;i<HiddenLayersNumber;i++){

        }

//Для создания весов скрытых слоев
        try (FileWriter writer = new FileWriter("pikpik.txt", false)) {

            writer.append('\n');
            writer.append("dasdasd");
            writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
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
    public static void ResetWeights(){
        File hiddenfile = new File("hidden.txt");
        File outputfile = new File("output.txt");
        try (FileWriter writer = new FileWriter(hiddenfile, false)) {
            for (int l = 0; l < 8; ++l){
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (FileWriter writer = new FileWriter(outputfile, false)) {
            for (int l = 0; l < 12; ++l){
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    //непосредственно обучение
    public static void Train(NeuralNet net)//backpropagation method
    {
        final double threshold = 0.001;//порог ошибки
        double[] temp_mses = new double[4];//массив для хранения ошибок итераций
        double temp_cost = 0;//текущее значение ошибки по эпохе
        //Цикл до достижения указанного порога ошибки
        do {
            //Цикл по количеству "тренировочных" сетов
            for (int i = 0; i < net.input_layer.trainset.length; ++i) {
                //прямой проход
                //Получение входных значений первым скрытым слоем (без фукции активации и без умножения на веса)
                net.hidden_layer.Data(net.input_layer.trainset[i]);
                //Расчет выходных значений слоёв и переача их на следующий слой без активации
                net.hidden_layer.OutputCalculate(null, net.output_layer);
                //net.hidden_layer1.OutputCalculate(net, net.output_layer);
                //Вычисление выходных значений сети (запись в net.fact)
                net.output_layer.OutputCalculate(net, null);
                //вычисление ошибки по итерации
                double[] errors = new double[net.input_layer.error[i].length];
                for (int x = 0; x < errors.length; ++x)
                    errors[x] = net.input_layer.error[i][x] - net.fact[x];
                temp_mses[i] = net.GetMSE(errors);
                //обратный проход и коррекция весов
                double[] temp_gsums = net.output_layer.BackwardPass(errors); //Распространение ошибки выходного слоя на входные веса
                //double[] temp_gsums1 = net.hidden_layer1.BackwardPass(temp_gsums);
                net.hidden_layer.BackwardPass(temp_gsums);
            }
            temp_cost = net.GetCost(temp_mses);//вычисление ошибки по эпохе
            //debugging
            System.out.println(Double.toString(temp_cost));
            //WriteLine($"{temp_cost}");
        } while (temp_cost > threshold);
        //загрузка скорректированных весов в "память"
        net.hidden_layer.WeightInitialize(MemoryMode.SET, "hidden");
        //net.hidden_layer1.WeightInitialize(MemoryMode.SET, "hidden1");
        net.output_layer.WeightInitialize(MemoryMode.SET, "output");
    }

    //тестирование сети
    public static void Test(NeuralNet net) {
        for (int i = 0; i < net.input_layer.trainset.length; ++i) {
            net.hidden_layer.Data(net.input_layer.trainset[i]);
            net.hidden_layer.OutputCalculate(null, net.output_layer);
            net.output_layer.OutputCalculate(net, null);
            for (int j = 0; j < net.fact.length; ++j)
                System.out.println("Fact "+net.input_layer.resultname[j]+ " =" + Double.toString(Math.round(net.fact[j])));
        }
    }
}

