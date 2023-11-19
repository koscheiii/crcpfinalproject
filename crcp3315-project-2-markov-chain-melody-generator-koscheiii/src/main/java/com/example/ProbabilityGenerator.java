/*
 * Modified by Isabelle Jeanjean 9/6/2023
 * From Courtney Brown
 * Class: ProbabliityGenerator
 * 
 */


package com.example;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class ProbabilityGenerator <E>
{
	// alphabet - set (an array or container) of all your unique tokens
	ArrayList<E> alphabet = new ArrayList<E>();

	// alphabet_counts - how often each unique token appears in the data
	ArrayList<Float> tokenCounts = new ArrayList<Float>();

	double tokenCount = 0;


	//training the data function
	void train(ArrayList<E> data)
	{

		for (E newTokens : data) {
			//find index of newTokens in alphabet
			int index = alphabet.indexOf(newTokens);

			//if index is NOT found
			if (index == -1) {
				index = alphabet.size();

				//add newTokens to alphabet array
				alphabet.add(newTokens);

				//add a 0 to alphabet counts array
				tokenCounts.add(0.0f);
			}

			// Increment the count for the token in alphabetCounts
			Float count = tokenCounts.get(index) + 1;
			tokenCounts.set(index, count);
			tokenCount++;
		}
	}

	//generate melody function
	public ArrayList<E> generate(int x) {
		
		ArrayList<E> generatedMelody = new ArrayList<>(); //create generated melody array
		for (int i = 0; i < x; i++) {	
			float random = (float) Math.random();  //generate random value between 0 and 1
			double value = 0; //initialize base value

			for (int j = 0; j < tokenCounts.size(); j++) { //looping through tokenCounts
				double count = tokenCounts.get(j) / tokenCount; //normalize the count
				value += count; //increase by value for each count
					
				if (random < value) {
					generatedMelody.add(alphabet.get(j)); //add the selected token to the melody
					break; //end of loop
				}
			}
		}
		return generatedMelody; //return the generated melody after the loop
	}





	
	//nested convenience class to return two arrays from sortArrays() method
	//students do not need to use this class
	protected class SortArraysOutput
	{
		public ArrayList<E> symbolsListSorted;
		public ArrayList<Float> symbolsCountSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//counts -- the number of times each symbol occurs (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//symbolsCountSorted -- list of the number of times each symbol occurs inorder of symbolsListSorted  (output)
	public SortArraysOutput sortArrays(ArrayList<E> symbols, ArrayList<Float> counts)	{

		SortArraysOutput sortArraysOutput = new SortArraysOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.symbolsCountSorted = new ArrayList<Float>();
	
		//sort the symbols list
		Collections.sort(sortArraysOutput.symbolsListSorted, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		//use the current sorted list to reference the counts and get the sorted counts
		for(int i=0; i<sortArraysOutput.symbolsListSorted.size(); i++)
		{
			int index = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(i));
			sortArraysOutput.symbolsCountSorted.add(counts.get(index));
		}

		return sortArraysOutput;

	}
	
	//Students should USE this method in your unit tests to print the probability distribution
	//HINT: you can overload this function so that it uses your class variables instead of taking in parameters
	//boolean is FALSE to test train() method & TRUE to test generate() method
	//symbols -- your alphabet or list of symbols (input)
	//counts -- the number of times each symbol occurs (input)
	//sumSymbols -- the count of how many tokens we have encountered (input)
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<Float> counts, double sumSymbols)
	{
		//sort the arrays so that elements appear in the same order every time and it is easy to test.
		SortArraysOutput sortResult = sortArrays(symbols, counts);
		ArrayList<E> symbolsListSorted = sortResult.symbolsListSorted;
		ArrayList<Float> symbolsCountSorted = sortResult.symbolsCountSorted;

		System.out.println("-----Probability Distribution-----");
		
		for (int i = 0; i < symbols.size(); i++)
		{
			if (round){
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: " + df.format((double)symbolsCountSorted.get(i) / sumSymbols));
			}
			else
			{
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: " + (double)symbolsCountSorted.get(i) / sumSymbols);
			}
		}
		
		System.out.println("------------");
	}
	
	public void printProbabilityDistribution(boolean round)
	{
		printProbabilityDistribution(round, alphabet, tokenCounts, tokenCount);
	}


}
