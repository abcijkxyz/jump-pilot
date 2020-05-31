package com.cadplan.jump.utils;

public class DataWrapper {
	private String simbol = "";
	private Integer dimension = 1;
	private int distance = 1;

	private double offset = 0.0D;

	private   boolean rotate = false;

	public DataWrapper(String simbol, Integer dimension, Integer distance, double offset, boolean rotate) {
		this.dimension = dimension;
		this.simbol = simbol;
		this.distance = distance;
		this.offset= offset;
		this.rotate=rotate;
	}

	public DataWrapper() {
	}

	public String getSimbol() {
		return this.simbol;
	}

	public void setSimbol(String simbol) {
		this.simbol = simbol;
	}

	public Integer getdimension() {
		return this.dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}



	public double getOffset() {
		return this.offset;
	}


	public void setOffset(double offset) {
		this.offset = offset;
	}


	public boolean getRotate() {
		return this.rotate;
	}


	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}


	public int getDistance() {
		return this.distance;
	}


	public void setDistance(int distance) {
		this.distance=distance;
	}

}
