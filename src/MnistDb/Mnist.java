package MnistDb;

import java.io.File;

import NN.Network;
import NN.Tools;
import Train.Train;


public class Mnist {

	  public static void main(String[] args) {

	        try {
	            Network net = Network.loadNetwork("src/mnist1.txt");
	            testTrainSet(net, createTrainSet(1000,2000), 10);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }

	    public static Train createTrainSet(int start, int end) {

	        Train set = new Train(28 * 28, 10);

	        try {

	            String path = new File("").getAbsolutePath();

	            MnistImage m = new MnistImage(path + "/src/trainImage.idx3-ubyte", "rw");
	            MnistLabel l = new MnistLabel(path + "/src/trainLabel.idx1-ubyte", "rw");

	            for(int i = start; i <= end; i++) {
	                if(i % 100 ==  0){
	                    System.out.println("prepared: " + i);
	                }

	                double[] input = new double[28 * 28];
	                double[] output = new double[10];

	                output[l.readLabel()] = 1d;
	                for(int j = 0; j < 28*28; j++){
	                    input[j] = (double)m.read() / (double)256;
	                }

	                set.addData(input, output);
	                m.next();
	                l.next();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	         return set;
	    }

	    public static void trainData(Network net,Train set, int epochs, int loops, int batch_size, String output_file) {
	        for(int e = 0; e < epochs;e++) {
	            net.train(set, loops, batch_size);
	            System.out.println( e);
	            try {
	                net.saveNetwork(output_file);
	            } catch (Exception e1) {
	                e1.printStackTrace();
	            }
	        }
	    }

	    public static void testTrainSet(Network net, Train set, int printSteps) {
	        int correct = 0;
	        for(int i = 0; i < set.size(); i++) {

	            double highest = Tools.highestValue(net.cal(set.getInput(i)));
	            double actualHighest = Tools.highestValue(set.getOutput(i));
	            if(highest == actualHighest) {
	                correct ++ ;
	            }
	            if(i % printSteps == 0) {
	                System.out.println(i + ": " + (double)correct / (double) (i + 1));
	            }
	        }
	        System.out.println("Testing finished, RESULT: " + correct + " / " + set.size()+ "  -> " + (double)correct / (double)set.size() +" %");
	    }
	}
