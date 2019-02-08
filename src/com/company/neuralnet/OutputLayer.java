package com.company.neuralnet;

class OutputLayer extends Layer {
    public OutputLayer(int non, int nopn, NeuronType nt, String type, boolean bias) {
        super(non, nopn, nt, type, bias);
    }

    public void OutputCalculate(NeuralNet net, Layer nextLayer) {
        for (int i = 0; i < neurons.length; ++i)
            net.fact[i] = neurons[i].Output(); //Запись выходных (фактических) значений сети
    }

    public double[] BackwardPass(double[] errors) {
        double[] gr_sum = new double[numofprevneurons];
        for (int j = 0; j < gr_sum.length; ++j)//вычисление градиентных сумм выходного слоя
        {
            double sum = 0;
            for (int k = 0; k < neurons.length; ++k)
                //Расчет градентной суммы каждого нейрона пр. слоя= вес*дельта каждого выходного нейрона
                sum += neurons[k].weights[j] * neurons[k].Gradientor(errors[k], neurons[k].Derivativator(neurons[k].Output()), 0);//через ошибку и производную
            gr_sum[j] = sum; //запись градиентной суммы по каждому нейрону предыдущего слоя
        }
        //коррекция весов
        for (int i = 0; i < numofneurons; ++i)
            for (int n = 0; n < numofprevneurons; ++n)
                //Новый вес=скорость обучения*входное значение нейрона*дельту
                neurons[i].weights[n] += learningrate * neurons[i].inputs[n] * neurons[i].Gradientor(errors[i], neurons[i].Derivativator(neurons[i].Output()), 0);
        return gr_sum;
    }
}
