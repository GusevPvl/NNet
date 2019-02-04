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
    //Метод вызова функции активации (инкапсуляция для разных функций активации)
    public double Output() {
        return Activator(inputs, weights); //Возвращение "активированного" значения
    }

    private double Activator(double[] i, double[] w)//Вычисление "активированного" значения
    {
        double sum = 0;
        for (int l = 0; l < i.length; ++l)
            sum += i[l] * w[l];//Получение произведения входных значений всех синапсов на веса
        return Math.pow(1 + Math.exp(-sum), -1);//Сигмоидальная функция активации
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
}
