package com.example.smartphonesensing2.KnnClassification;

public class Knn_Classification extends Neighbour{
	public int activity_count;
	private boolean activity_checked;
	static int check_position;
	
	
	
	
	public Knn_Classification(){
		activity_count =0;
		activity_checked = false ;
		
	}
	
	
	public void increment_count(){
		activity_count ++;
		
	}
	
	public int return_count(){
		return activity_count;
		
	}
	
	
	public void activity_check(){
		activity_checked =true;
	}
	
	public boolean get_activity_check(){
		return activity_checked ;
	}
	
	public static void next_check(){
		check_position++ ;
	}
	public static int  get_next_check(){
		return check_position;
	}
	
}
