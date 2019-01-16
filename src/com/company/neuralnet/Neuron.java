package com.company.neuralnet;

class Neuron {
    public Neuron(double[] inputs, double[] weights, NeuronType type) {
        this.type = type;
        this.weights = weights;
        this.inputs = inputs;
    }

    private NeuronType type;
    public double[] weights;
    public double[] inputs;

    public double getWeights(int no) {
        return weights[no];
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double[] getInputs() {
        return inputs;
    }

    public void setInputs(double[] inputs) {
        this.inputs = inputs;
    }

    public double Output() {
        return Activator(inputs, weights);
    }

    private double Activator(double[] i, double[] w)//преобразования
    {
        double sum = 0;
        for (int l = 0; l < i.length; ++l)
            sum += i[l] * w[l];//линейные
        return Math.pow(1 + Math.exp(-sum), -1);//нелинейные
    }

    public double Derivativator(double outsignal) {
        return outsignal * (1 - outsignal);
    }

    public double Gradientor(double error, double dif, double g_sum) {
        if (type == NeuronType.output)
            return error * dif;
        else
            return g_sum * dif;

    }
    //public double Derivativator(double outsignal) =>outsignal *(1-outsignal);//формула производной для текущей функции активации уже выведена в ранее упомянутой книге
    //public double Gradientor(double error, double dif, double g_sum) =>(_type ==NeuronType.Output)?error *dif :g_sum *dif;//g_sum - это сумма градиентов следующего слоя
}
