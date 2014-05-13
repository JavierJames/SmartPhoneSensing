package com.example.smartphonesensing2.KnnClassification;

import java.util.Arrays;


public class App {
    /* Classification settings*/
	final static int K = 3;
	static int TrainingDataSize =4; 
	static int TestingDataSize =2;
	static int num_activities = 2;
	

	static ArrayBuff [] TrainingData2 = new ArrayBuff [TrainingDataSize]; //training dataset
	static ArrayBuff [] TestingData2 = new ArrayBuff [TestingDataSize];  //testing dataset
	static Neighbour [][] Kneighbours2 = new Neighbour [TestingData2.length][K]; //fetch K closes neighbours for each testingdata

		   public static void main(String args[]) {

			 /*Create and Initialize array of Object  */
		    for(int i1=0; i1<TrainingDataSize; i1++)
	        {	
	        	TrainingData2[i1]= new ArrayBuff();
	        }
			   
			   for(int i1=0; i1< TestingDataSize; i1++)
	        {	
	        	TestingData2[i1]= new ArrayBuff();
	        }
		   
		   //for(int t=0; t<TrainingData2.length; t++){
			for(int t=0; t<TestingData2.length; t++){   
	        	for(int m=0; m<K; m++)	
	        	{	
	        		Kneighbours2[t][m]= new Neighbour();
	        	}
		   }  
	        
   			
		        TrainMethod();
				TestMethod();
				knnMethod();
				
			      
		     		      
		   }//end of main
	
	

		public static void TrainMethod(){

			   /* store training data */
			   TrainingData2[0].setRecord(0, 3, 3, 0, "Happy ");
				TrainingData2[1].setRecord(1, -1, -4, 0, "Sad ");
				TrainingData2[2].setRecord(2, 2, 3, 0, "Happy ");
				TrainingData2[3].setRecord(3, 0, -5, 0, "Sad ");
				
				return;
			}	    
	

			public static	void TestMethod(){
				
				/* store testing data*/
				TestingData2[0].setRecord(0, 3, 4, 0, "Unknown ");
				TestingData2[1].setRecord(1,3, 3, 0, "Unknown ");
				
					return;
				
				
			}
			
			private static void knnMethod() {
				// TODO Auto-generated method stub
				double distance=0;
				Neighbour [][] TestingData_KnnBuff_sorted = new Neighbour [TestingData2.length][K];	
				
				/* new today mon*/
				for(int t=0; t<TestingData2.length; t++){   
		        	for(int m=0; m<K; m++)	
		        	{	
		        		TestingData_KnnBuff_sorted[t][m]= new Neighbour();
		        	}
			   }  
				
				
						
				/* for each TestingData query, find K nearest neighbours in TrainingData*/
				for(int i=0; i<TestingData2.length; i++){
					
					Neighbour.setMaxDistance(1000); //reset max distance for each new cycle
		//			System.out.println("Record #:"+ i);
		
					for(int j=0; j<TrainingData2.length; j++){
						
						/* Get Distance */
						distance = EuclideanDistance(TrainingData2[j].getX(),TrainingData2[j].getY(),TrainingData2[j].getZ(), 
						          TestingData2[i].getX(), TestingData2[i].getY(), TestingData2[i].getZ());		
				
						
						/*import and sort Knearest neigbour with new distance  */
						if(distance < Neighbour.getMaxDistance()){ 
							TestingData_KnnBuff_sorted [i] = BubbleSort(distance, TrainingData2[j].getActivity(),TestingData_KnnBuff_sorted [i]); //store distance and activity of training data
							
						}
						
											
					} //end TrainingData loop

				
					
					/*classify Testingdata sample from sorted KNeighbours */
					String Classified_activity=null;
					Classified_activity= ClassifySample(TestingData_KnnBuff_sorted[i]); 
					System.out.println("Record #:"+ i + "\t"+ "Classified label:" +"\t" + Classified_activity );
			
				/*	System.out.println("~~~BubbleSort return array ~~~");
					for(int j=0; j<TestingData_KnnBuff_sorted[i].length; j++){
							System.out.println("distance: " + TestingData_KnnBuff_sorted[i][j].getDistance() + "\t" + "activity:"+ TestingData_KnnBuff_sorted[i][j].getActivity());
			      
					}
					*/		
					
				
//			/		System.out.println( "--------------------------------------------------\n");
									
				}//end TestingData loop
				
				
				
			}   
		    
			/*Sort array of K Objects by rank order */
			   private static Neighbour[] BubbleSort(double new_distance, String new_activity,  Neighbour [] KNN_array) {
				// TODO Auto-generated method stub
				Neighbour [] temp = new Neighbour [KNN_array.length+1]; //buffer to hold old + new neighbour
				
				/*Create and Initialize array of Object  */
		        for(int i1=0; i1< temp.length; i1++)
		        {	
		        	temp[i1]= new Neighbour();
		        }
		    
		       
		        /* back up nearest neighbour before sort */
				System.arraycopy(KNN_array, 0, temp, 0, KNN_array.length);
				
							
				/* append new data to the end array to be sorted*/
				temp[KNN_array.length].setActivity(new_activity);
				temp[KNN_array.length].setDistance(new_distance);
		    
				
				Arrays.sort(temp); 
				System.arraycopy(temp, 0, KNN_array, 0, KNN_array.length);
				
				/* set maximum distance for the next trainingdata cycle*/
				Neighbour.setMaxDistance(KNN_array[KNN_array.length-1].getDistance()); //get distance of last element
				
				
				return KNN_array;
				   
				
			}

			   		   

			private static double EuclideanDistance(float trainingDataX, float trainingDataY,
						float trainingDataZ, float testDataX, float testDataY,
						float testDataZ) {
					// TODO Auto-generated method stub
			    	  return Math.sqrt(Math.pow((trainingDataX-testDataX),2) + Math.pow((trainingDataY-testDataY),2) + Math.pow((trainingDataZ-testDataZ),2));
					
				}
		    
			
			private static String ClassifySample(Neighbour[] K_neighbours)
			{
				int array_length = K_neighbours.length;
				
				Knn_Classification [] activity_counter = new Knn_Classification [num_activities];
				String activitylabel = null; 
				
				
				for(int j=0; j<num_activities; j++) //not sure if this good
						
				{
					activity_counter[j]= new Knn_Classification();
					
				}
					
				/* list of all activities to classify by*/
				activity_counter[0].setActivity("Happy");
				activity_counter[1].setActivity("Sad");
				
				
				/*search through array , for each activity, and count */
				for(int j=0; j<activity_counter.length; j++){
					for(int i=0; i<array_length; i++)
					{
						
						if( K_neighbours[i].getActivity().startsWith(activity_counter[j].getActivity()) ){
							activity_counter[j].increment_count();
						}
						
					}
				
				}
				
			
				/* find max count */
				int max = activity_counter[0].return_count();
				if(activity_counter.length ==1)
					return activity_counter[0].getActivity();
				else{
					for(int j=0; j<activity_counter.length; j++){
						if(activity_counter[j].return_count() >= max){
							max=activity_counter[j].return_count();
							activitylabel = activity_counter[j].getActivity();
						
						}
					
					}
				}
				return activitylabel;
												
				
			}
			
			
}



