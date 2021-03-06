package com.example.smartphonesensing2.localization.classification;

import com.example.smartphonesensing2.localization.histogram.TrainingData;

import java.util.ArrayList;

public class NaiveBayesian extends Bayesian implements ClassifierAPI{

	public NaiveBayesian(String filepath) {
		super(filepath,"Deterministic Bayesian");
		// TODO Auto-generated constructor stub
	}

	
	/* 
	 * Get Training Data for this unique Classifier
	 * */ 	
	public ArrayList<TrainingData>  getPersonalTrainingData()
	{
		return tds;
	}
	
	
	
	/* Train classifier, to know what PMF Table to use */
	public void trainClassifier(ArrayList<TrainingData> trainingData){
	
		this.tds = trainingData;
		
		
	}
	
	
	/*
	 * This function takes in the new observation sample, and returns the classification type. 
	 *   */

	public int classifyObservation(ArrayList<Integer> observations){

		int bayesian_result = 0;
   
    	float [] classification_result = new float [2];
    	
    	float[] sense_results = new float [numberOfCells];         

  
    	int cellNumber;
    	int ap_index;
    	
		/*for each training data, and its corresponding observation, apply the sense model 
		 * So find the probability of being in Cell[i], and having that rssi value for that given AP, 
		 * Obtain an array with that probability for each cell
		*/
		
		for(int t=0; t<tds.size(); t++)
		{
			
			ap_index = NextStrongestAP(observations);
		
			System.out.println("AP name: "+tds.get(ap_index).getName() + "observation:"+observations.get(ap_index));
			//fetch the conditional probability of being in all cells and having that given rssi value for that given AP
			sense_results = senseOneAP(observations.get(ap_index), tds.get(ap_index).getPMF()); //P(e[i]=r|H)
			posterior = vector_mult(this.prior, sense_results);	
			System.arraycopy(this.posterior, 0, this.prior, 0, this.posterior.length); // update prior after 1 step.    
			
			classification_result=getMaxValueandClassify(posterior);
			
			cellNumber= (int)(classification_result[0] +1);
			
			//update end result only if classification had a valid cell id
			if( cellNumber >= 1)
			{
				bayesian_result = (int)(classification_result[0] +1);
			}
			
		 //   System.out.println("cellnumber:"+cellNumber);
		    ClassificationEstimations.add( (int)(classification_result[0] +1));
	     //	System.out.println("Cell:" + ClassificationEstimations.get(t)); 
		    System.out.println("Cell: "+ (classification_result[0]+1) + "Probability: "+classification_result[1] );
						
		}
				
	    return bayesian_result;
	
	

	}
	
	
	
	/*
	 * This function takes in the new observation sample, but only for One AP, and returns the classification type
	 * In form of the cell number. 
	 *   */
 
	public ArrayList<Integer> classifyObservation( int observation, TrainingData td)
	{
	
		ArrayList<Integer> location = new ArrayList<Integer>();
   
    	float[] sense_results = new float [numberOfCells];         

  	
	   //fetch the conditional probability of being in all cells and having that given rssi value for that given AP
		sense_results = senseOneAP(observation, td.getPMF()); //P(e[i]=r|H)
		posterior = vector_mult(this.prior, sense_results);	

		System.arraycopy(this.posterior, 0, this.prior, 0, this.posterior.length); // update prior after 1 step.    

		location = getMaxValueandClassify2(this.posterior);
			
		return location;
		
		
	}

	
	
	
	
	
	
}
