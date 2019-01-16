package com.company.neuralnet;

class HiddenLayer extends Layer {
    public HiddenLayer(int non, int nopn, NeuronType nt, String type) {
        super(non, nopn, nt, type);

    }

    public void Recognize(NeuralNet net, Layer nextLayer) {
        double[] hidden_out = new double[neurons.length];
        for (int i = 0; i < neurons.length; ++i)
            hidden_out[i] = neurons[i].Output();
        nextLayer.Data(hidden_out);
    }

    public double[] BackwardPass(double[] gr_sums) {
        double[] gr_sum = new double[numofprevneurons];
        //сюда можно всунуть вычисление градиентных сумм для других скрытых слоёв
        //но градиенты будут вычисляться по-другому, то есть
        //через градиентные суммы следующего слоя и производные
        //Вычисление градиентных сумм для скрытого слоя
        for (int j = 0; j < gr_sum.length; ++j)//вычисление градиентных сумм текущего слоя
        {
            double sum = 0;
            for (int k = 0; k < neurons.length; ++k)
                //Расчет градентной суммы каждого нейрона пр. слоя= вес*дельта каждого выходного нейрона
                sum += neurons[k].weights[j] * neurons[k].Gradientor(0, neurons[k].Derivativator(neurons[k].Output()), gr_sums[k]);//через ошибку и производную
            gr_sum[j] = sum; //запись градиентной суммы по каждому нейрону предыдущего слоя
        }
        //Коррекция весов скрытого слоя
        for (int i = 0; i < numofneurons; ++i)
            for (int n = 0; n < numofprevneurons; ++n)
                //Новый вес = старый+скорость обучения*входной сигнал*дельту
                neurons[i].weights[n] += learningrate * neurons[i].inputs[n] * neurons[i].Gradientor(0, neurons[i].Derivativator(neurons[i].Output()), gr_sums[i]);
        return gr_sum;
    }
}
