package com.company.neuralnet;

public class NeuralNet{
    //все слои сети
    InputLayer input_layer = new InputLayer(); //Инициализация входного слоя - задается отдельным классом
    public HiddenLayer hidden_layer = new HiddenLayer(4, 2, NeuronType.hidden, "hidden"); //Инициализация скрытого слоя
    public HiddenLayer hidden_layer1 = new HiddenLayer(4, 4, NeuronType.hidden, "hidden1"); //Инициализация скрытого слоя
    public OutputLayer output_layer = new OutputLayer(2, 4, NeuronType.output, "output"); //Ининциализация выходного слоя
    //массив для хранения выхода сети
    public double[] fact = new double[2];//не ругайте за 2 пожалуйста

    //Конструктор:

    //ошибка одной итерации обучения
    double GetMSE(double[] errors) {
        double sum = 0;
        for (int i = 0; i < errors.length; ++i)
            sum += Math.pow(errors[i], 2);
        return  sum;
    }

    //ошибка эпохи
    double GetCost(double[] mses) {
        double sum = 0;
        for (int i = 0; i < mses.length; ++i)
            sum += mses[i];
        return (sum / mses.length);
    }

    //непосредственно обучение
    public static void Train(NeuralNet net)//backpropagation method
    {
        final double threshold = 0.2;//порог ошибки
        double[] temp_mses = new double[4];//массив для хранения ошибок итераций
        double temp_cost = 0;//текущее значение ошибки по эпохе
        do {
            for (int i = 0; i < net.input_layer.trainset.length; ++i) {
                //прямой проход
                net.hidden_layer.Data(net.input_layer.trainset[i]);
                net.hidden_layer.Recognize(null, net.hidden_layer1);
                net.hidden_layer1.Recognize(net,net.output_layer);
                net.output_layer.Recognize(net, null);
                //вычисление ошибки по итерации
                double[] errors = new double[net.input_layer.error[i].length];
                for (int x = 0; x < errors.length; ++x)
                    errors[x] = net.input_layer.error[i][x] - net.fact[x];
                temp_mses[i] = net.GetMSE(errors);
                //обратный проход и коррекция весов
                double[] temp_gsums = net.output_layer.BackwardPass(errors);
                double[] temp_gsums1 = net.hidden_layer1.BackwardPass(temp_gsums);
                net.hidden_layer.BackwardPass(temp_gsums1);
            }
            temp_cost = net.GetCost(temp_mses);//вычисление ошибки по эпохе
            //debugging
            System.out.println(Double.toString(temp_cost));
            //WriteLine($"{temp_cost}");
        } while (temp_cost > threshold);
        //загрузка скорректированных весов в "память"
        net.hidden_layer.WeightInitialize(MemoryMode.SET, "hidden");
        net.hidden_layer1.WeightInitialize(MemoryMode.SET, "hidden1");
        net.output_layer.WeightInitialize(MemoryMode.SET, "output");
    }

    //тестирование сети
    public static void Test(NeuralNet net) {
        for (int i = 0; i < net.input_layer.trainset.length; ++i) {
            net.hidden_layer.Data(net.input_layer.trainset[i]);
            net.hidden_layer.Recognize(null, net.output_layer);
            net.output_layer.Recognize(net, null);
            for (int j = 0; j < net.fact.length; ++j)
                System.out.println("Fact="+Double.toString(Math.round(net.fact[j])));
        }
    }
}

