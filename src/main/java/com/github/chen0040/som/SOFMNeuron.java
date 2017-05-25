package com.github.chen0040.som;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SOFMNeuron implements Cloneable {
	private int x;
	private int y;
	private int output;

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private double[] weights;

	public double[] getWeights(){
		return weights.clone();
	}

	public void setWeights(double[] weights){
		this.weights = weights.clone();
	}
	
	public SOFMNeuron()
	{

	}
	
	public double getDistance(SOFMNeuron rhs)
	{
		double dx=rhs.x - x;
		double dy=rhs.y - y;
		return Math.sqrt(dx*dx + dy*dy);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SOFMNeuron clone = (SOFMNeuron)super.clone();
		clone.copy(this);

		return clone;
	}

	public void copy(SOFMNeuron rhs){
		x = rhs.x;
		y = rhs.y;
		output = rhs.output;
		weights = rhs.weights==null ? null : rhs.weights.clone();
	}


	public void updateWeight(int j, double weight) {
		this.weights[j]  = weight;
	}


	public double getWeight(int j) {
		return this.weights[j];
	}
}
