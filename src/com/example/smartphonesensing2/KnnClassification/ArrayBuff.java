package com.example.smartphonesensing2.KnnClassification;

public class ArrayBuff {
	int id;
	float x;
	float y;
	float z;
	String label;
	
	public ArrayBuff(){
		
		//x=(float)0;
		//y=(float)0;
		//z=(float)0; 
		//label="Unknown"; 
		this(0,0,0,0,"Unknown");
		
	}
	
	public ArrayBuff(int timestamp, float x_value, float y_value, float z_value, String activity ){
		
		x=x_value;
		y=y_value;
		z=z_value; 
		label=activity; 
		
	}

	public void setX(float x_value){
		
		//x=x_value;
	}
	
	public void setY(float y_value){
		
		y=y_value;
	}

	public void showState()
	{
		System.out.println("X:"+x + " " + "Y:"+y + " " + "Z:"+ z + " " );
	}

	public void setZ(float z_value){
		
		z=z_value;
	}

	public void setCoordinates(float x_value, float y_value,float z_value){
		
		x=x_value;
		y=y_value;
		z=z_value; 
	}
	
	public void setRecord(int time, float x_value, float y_value,float z_value, String activity){
		id=time;
		x=x_value;
		y=y_value;
		z=z_value; 
		label=activity;
	}
	
	public float getX(){
		
		return x;
	}
	
	public float getY(){
		
		return y;
	}

	public float getZ(){
	
	return z;
	}
	
	public String getActivity(){
		
		return label;
		}
	
	public void setActivity(String activity){
		
		this.label =activity;
		}
	
	
	/*
	
	class Neighbour {
		private double distance; //distance of individual neighbour
		private String activity; //label of individual neighbour
		public double max_distance; //keep track of maximum distance in K neighbours
		
		
		public Neighbour(){
			distance =0; 
			activity ="Unknown";
			max_distance = 10000;
			
		}
		
		
		public void setDistance(double d) {
			this.distance = d;
		}
		
		public double getDistance() {
			return this.distance;
		}
		
		public void setActivity(String a) {
			this.activity = a;
		}
		
		public String getActivity() {
			return this.activity;
		}
	}
*/
	
	
	
}



