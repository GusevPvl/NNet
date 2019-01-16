package com.company.neuralnet;

public class NetActions {
    //непосредственно обучение
    public static void Train(NeuralNet net)//backpropagation method
    {
        final double threshold = 0.001;//порог ошибки
        double[] temp_mses = new double[4];//массив для хранения ошибок итераций
        double temp_cost = 0;//текущее значение ошибки по эпохе
        do {
            for (int i = 0; i < net.input_layer.trainset.length; ++i) {
                //прямой проход
                net.hidden_layer.Data(net.input_layer.trainset[i]);
                net.hidden_layer.Recognize(null, net.output_layer);
                net.output_layer.Recognize(net, null);
                //вычисление ошибки по итерации
                double[] errors = new double[net.input_layer.error[i].length];
                for (int x = 0; x < errors.length; ++x)
                    errors[x] = net.input_layer.error[i][x] - net.fact[x];
                temp_mses[i] = net.GetMSE(errors);
                //обратный проход и коррекция весов
                double[] temp_gsums = net.output_layer.BackwardPass(errors);
                net.hidden_layer.BackwardPass(temp_gsums);
            }
            temp_cost = net.GetCost(temp_mses);//вычисление ошибки по эпохе
            //debugging
            System.out.println(Double.toString(temp_cost));
            //WriteLine($"{temp_cost}");
        } while (temp_cost > threshold);
        //загрузка скорректированных весов в "память"
        net.hidden_layer.WeightInitialize(MemoryMode.SET, "hidden");
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
