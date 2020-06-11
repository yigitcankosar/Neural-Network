package Train;

import java.util.ArrayList;
import java.util.Arrays;

import NN.Tools;


public class Train {
	 public int inputSize;
	    public int outputSize;

	    private ArrayList<double[][]> data = new ArrayList<>();

	    public Train(int inputSize, int outputSize) {
	        this.inputSize = inputSize;
	        this.outputSize = outputSize;
	    }

	    public void addData(double[] in, double[] expected) {
	        if(in.length != inputSize || expected.length != outputSize) return;
	        data.add(new double[][]{in, expected});
	    }

	    public Train extractBatch(int size) {
	        if(size > 0 && size <= this.size()) {
	            Train set = new Train(inputSize, outputSize);
	            Integer[] ids = Tools.randomValues(0,this.size() - 1, size);
	            for(Integer i:ids) {
	                set.addData(this.getInput(i),this.getOutput(i));
	            }
	            return set;
	        }else return this;
	    }

	    public static void main(String[] args) {
	        Train set = new Train(3,2);

	        for(int i = 0; i < 8; i++) {
	            double[] a = new double[3];
	            double[] b = new double[2];
	            for(int k = 0; k < 3; k++) {
	                a[k] = (double)((int)(Math.random() * 10)) / (double)10;
	                if(k < 2) {
	                    b[k] = (double)((int)(Math.random() * 10)) / (double)10;
	                }
	            }
	            set.addData(a,b);
	        }

	        System.out.println(set);
	        System.out.println(set.extractBatch(3));
	    }

	    public String toString() {
	        String s = "TrainSet ["+inputSize+ " ; "+outputSize+"]\n";
	        int index = 0;
	        for(double[][] r:data) {
	            s += index +":   "+Arrays.toString(r[0]) +"  >-||-<  "+Arrays.toString(r[1]) +"\n";
	            index++;
	        }
	        return s;
	    }

	    public int size() {
	        return data.size();
	    }

	    public double[] getInput(int index) {
	        if(index >= 0 && index < size())
	            return data.get(index)[0];
	        else return null;
	    }

	    public double[] getOutput(int index) {
	        if(index >= 0 && index < size())
	            return data.get(index)[1];
	        else return null;
	    }

	    public int getinputSize() {
	        return inputSize;
	    }

	    public int getoutputSize() {
	        return outputSize;
	    }
	}
