package com.example.smartphonesensing2.KnnClassification;

public class Neighbour implements Comparable<Neighbour>{
	
	/* class variables */
	private static double max_distance; //keep track of maximum distance in K neighbours
	
	/*instances */
	private  double distance; //distance of individual neighbour
	private String activity; //label of individual neighbour
		
	public Neighbour(){
		distance =500; 
		activity ="Unknown";
	//	max_distance = 10000; //maybe this causing the problem
		
	}

	public Neighbour(double distance, String activity){
		this.distance =distance; 
		this.activity =activity;
	//	max_distance = 10000; //maybe this causing the problem
		
	}
	
		
	public void setDistance(double d) {
		//this.distance = d;
		this.distance = d;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public static double getMaxDistance() {
		return max_distance;
	}
	
	public static void setMaxDistance(double new_maxdistance) {
		max_distance = new_maxdistance;
	}
	
	public void setActivity(String a) {
		this.activity = a;
	}
	
	public String getActivity() {
		return this.activity;
	}


	public int compareTo(Neighbour another) {
		// TODO Auto-generated method stub
		
		double comparedistance = ((Neighbour) another).getDistance();
		
		//return 0;
		return  (int) (this.distance-comparedistance);
	}
}

	
	
	


