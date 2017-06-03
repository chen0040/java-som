package com.github.chen0040.som;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;


@Getter
@Setter
public class SOFMNet {
	private static final Random random = new Random();

	private int rows;
	private int cols;
	private double sigma0;
	private double tau1;
	private int selfOrganizingPhaseEpoches =1000; //the iteration number in the phase 1: self-organizing phase
	private int epoches =0;
	private double eta0 =0.1;
	private int inputDimension =0;

	@Setter(AccessLevel.NONE)
	private final List<SOFMNeuron> neurons = new ArrayList<>();



	public SOFMNet(){

	}
	
	//input_size should be smaller or equal to the actual input size
	public SOFMNet(int rows, int cols, int inputDimension)
	{
		this.rows =rows;
		this.cols =cols;
		this.inputDimension =inputDimension;

		for(int y=0; y< this.rows; ++y)
		{
			for(int x=0; x< this.cols; ++x)
			{
				SOFMNeuron neuron=new SOFMNeuron();
				neuron.setX(x);
				neuron.setY(y);
				neuron.setWeights(new double[this.inputDimension]);
				neuron.setOutput(x * this.rows + y);
				neurons.add(neuron);
			}
		}

		sigma0 = 0.707 * Math.sqrt((this.rows -1)*(this.rows -1)+(this.cols -1)*(this.cols -1));
		tau1 = 1000 / Math.log(sigma0);
	}
	

	public SOFMNeuron neuronAt(int row, int col)
	{
		return neurons.get(row * cols + col);
	}

	
	public void initialize(Vector<Double> lowest_weight, Vector<Double> highest_weight)
	{
		int [] seq=new int[neurons.size()];
		for(int i=0; i< neurons.size(); ++i)
		{
			seq[i]=i;
		}
		
		
		for(int j=0; j< inputDimension; ++j)
		{
			double inc=(highest_weight.get(j).doubleValue() - lowest_weight.get(j).doubleValue()) / neurons.size();
			
			for(int i=0; i< neurons.size(); ++i)
			{
				int k= random.nextInt(neurons.size());
				int tmp=seq[i];
				seq[i]=seq[k];
				seq[k]=tmp;
			}
			
			for(int i=0; i< neurons.size(); ++i)
			{
				SOFMNeuron neuron= neurons.get(i);
				neuron.updateWeight(j, lowest_weight.get(j).doubleValue()+inc*seq[i]+inc* random.nextDouble());
			}
		}
		
		epoches =0;
	}
	
	protected double eta(int n)
	{
		double result= eta0 * Math.exp(-n/ tau1);
		if(result < 0.01) result=0.01;
		return result;
	}
	
	protected double h(double distance, int n)
	{
		if(n < selfOrganizingPhaseEpoches) //self-organizing phase
		{
			double sigma= sigma0 * Math.exp(- n / tau1);
			return Math.exp(-distance * distance / (2*sigma*sigma));
		}
		else //convergence phase
		{
			return 1;
		}
	}
	
	public void train(double[] input)
	{
		//determine the winner neuron
		SOFMNeuron m_winner=null;
		double max_sum = Double.MAX_VALUE;
		for(int i=0; i< neurons.size(); i++)
		{
			SOFMNeuron neuron= neurons.get(i);
			double sum=0;
			for(int j=0; j< inputDimension; j++)
			{
				double d=input[j] - neuron.getWeight(j);
				sum+=d*d;
			}
			if(sum < max_sum)
			{
				m_winner=neuron;
				max_sum=sum;
			}
		}
		
		//update neuron weights
		for(int i=0; i< neurons.size(); ++i)
		{
			SOFMNeuron neuron= neurons.get(i);
			if(neuron == m_winner) continue;
			double d=neuron.getDistance(m_winner);
			for(int j=0; j< inputDimension; ++j)
			{
				double current_weight=neuron.getWeight(j);
				double weight_delta=eta(epoches) * h(d, epoches) * (input[j] - current_weight);
				neuron.updateWeight(j, current_weight+weight_delta);
			}
		}
		
		
		epoches++;
	}
	
	public SOFMNeuron match(double[] input)
	{
		SOFMNeuron winner=null;
		double max_sum=Double.MAX_VALUE;
		for(int i=0; i< neurons.size(); i++)
		{
			SOFMNeuron neuron= neurons.get(i);
			double sum=0;
			for(int j=0; j< inputDimension; j++)
			{
				double d=input[j] - neuron.getWeight(j);
				sum+=d*d;
			}
			if(sum < max_sum)
			{
				winner=neuron;
				max_sum=sum;
			}
		}

		return winner;
	}
}
