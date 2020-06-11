package NN;

import java.util.Arrays;


import Train.Train;
import Compiler.Attribute;
import Compiler.CompilerT;
import Compiler.Node;
import Compiler.Compiler;



public class Network{

    double[][] output;
    double[][][] weights;
    double[][] bias;

    double[][] err;
    double[][] derivative;

    public int[] layerSizes;
    public int   inputSize;
    public int   outputSize;
    public int   networkSize;

    public Network(int... layerSizes) {
        this.layerSizes = layerSizes;
        this.inputSize = layerSizes[0];
        this.networkSize = layerSizes.length;
        this.outputSize = layerSizes[networkSize-1];

        this.output = new double[networkSize][];
        this.weights = new double[networkSize][][];
        this.bias = new double[networkSize][];

        this.err = new double[networkSize][];
        this.derivative = new double[networkSize][];

        for(int i = 0; i < networkSize; i++) {
            this.output[i] = new double[layerSizes[i]];
            this.err[i] = new double[layerSizes[i]];
            this.derivative[i] = new double[layerSizes[i]];

            this.bias[i] = Tools.createRandomArray(layerSizes[i], -0.5,0.7);

            if(i > 0) {
                weights[i] = Tools.createRandomArray(layerSizes[i],layerSizes[i-1], -1,1);
            }
        }
    }

    public double[] cal(double... input) {
        if(input.length != this.inputSize) return null;
        this.output[0] = input;
        for(int layer = 1; layer < networkSize; layer ++) {
            for(int neuron = 0; neuron < layerSizes[layer]; neuron ++) {

                double sum = bias[layer][neuron];
                for(int prevNeuron = 0; prevNeuron < layerSizes[layer-1]; prevNeuron ++) {
                    sum += output[layer-1][prevNeuron] * weights[layer][neuron][prevNeuron];
                }
                output[layer][neuron] = sigmoid(sum);
                derivative[layer][neuron] = output[layer][neuron] * (1 - output[layer][neuron]);
            }
        }
        return output[networkSize-1];
    }

    public void train(Train set, int loops, int batchSize) {
        if(set.inputSize != inputSize || set.outputSize != outputSize) return;
        for(int i = 0; i < loops; i++) {
            Train batch = set.extractBatch(batchSize);
            for(int b = 0; b < batchSize; b++) {
                this.train(batch.getInput(b), batch.getOutput(b), 0.3);
            }
            System.out.println(squareError (batch));
        }
    }

    public double squareError (double[] input, double[] target) {
        if(input.length != inputSize || target.length != outputSize) return 0;
        cal(input);
        double v = 0;
        for(int i = 0; i < target.length; i++) {
            v += (target[i] - output[networkSize-1][i]) * (target[i] - output[networkSize-1][i]);
        }
        return v / (2d * target.length);
    }

    public double squareError (Train set) {
        double v = 0;
        for(int i = 0; i< set.size(); i++) {
            v += squareError (set.getInput(i), set.getOutput(i));
        }
        return v / set.size();
    }

    public void train(double[] input, double[] target, double eta) {
        if(input.length != inputSize || target.length != outputSize) return;
        cal(input);
        backpropError(target);
        updateWeights(eta);
    }

    public void backpropError(double[] target) {
        for(int neuron = 0; neuron < layerSizes[networkSize-1]; neuron ++) {
            err[networkSize-1][neuron] = (output[networkSize-1][neuron] - target[neuron])
                    * derivative[networkSize-1][neuron];
        }
        for(int layer = networkSize-2; layer > 0; layer --) {
            for(int neuron = 0; neuron < layerSizes[layer]; neuron ++){
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < layerSizes[layer+1]; nextNeuron ++) {
                    sum += weights[layer + 1][nextNeuron][neuron] * err[layer + 1][nextNeuron];
                }
                this.err[layer][neuron] = sum * derivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double eta) {
        for(int layer = 1; layer < networkSize; layer++) {
            for(int neuron = 0; neuron < layerSizes[layer]; neuron++) {

                double delta = - eta * err[layer][neuron];
                bias[layer][neuron] += delta;

                for(int prevNeuron = 0; prevNeuron < layerSizes[layer-1]; prevNeuron ++) {
                    weights[layer][neuron][prevNeuron] += delta * output[layer-1][prevNeuron];
                }
            }
        }
    }

    private double sigmoid( double x) {
        return 1d / ( 1 + Math.exp(-x));
    }

    public static void main(String[] args){
        try {
            Network net = Network.loadNetwork("src/test2.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveNetwork(String fileName) throws Exception {
        Compiler p = new Compiler();
        p.create(fileName);
        Node root = p.getContent();
        Node netw = new Node("Network");
        Node ly = new Node("Layers");
        netw.addAttribute(new Attribute("sizes", Arrays.toString(this.layerSizes)));
        netw.addChild(ly);
        root.addChild(netw);
        for (int layer = 1; layer < this.networkSize; layer++) {

            Node c = new Node("" + layer);
            ly.addChild(c);
            Node w = new Node("weights");
            Node b = new Node("biases");
            c.addChild(w);
            c.addChild(b);

            b.addAttribute("values", Arrays.toString(this.bias[layer]));

            for (int we = 0; we < this.weights[layer].length; we++) {

                w.addAttribute("" + we, Arrays.toString(weights[layer][we]));
            }
        }
        p.close();
    }

    public static Network loadNetwork(String fileName) throws Exception {

    	Compiler p = new Compiler();

            p.load(fileName);
            String sizes = p.getValue(new String[] { "Network" }, "sizes");
            int[] si = CompilerT.parseIntArray(sizes);
            Network ne = new Network(si);

            for (int i = 1; i < ne.networkSize; i++) {
                String biases = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "biases" }, "values");
                double[] bias = CompilerT.parseDoubleArray(biases);
                ne.bias[i] = bias;

                for(int n = 0; n < ne.layerSizes[i]; n++){

                    String current = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "weights" }, ""+n);
                    double[] val = CompilerT.parseDoubleArray(current);

                    ne.weights[i][n] = val; 
                }
            }
            p.close();
            return ne;

    }

}
