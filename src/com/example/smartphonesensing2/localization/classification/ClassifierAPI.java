package com.example.smartphonesensing2.localization.classification;

import com.example.smartphonesensing2.localization.histogram.TrainingData;

import java.util.ArrayList;

public interface ClassifierAPI {
	
	
	/*Train classifier, to know what PMF Table to use */
	public void trainClassifier(ArrayList<TrainingData> trainingData);
	public int classifyObservation(ArrayList<Integer> observations);

}
