//Isabelle Jeanjean
//MarkovChainGenerator

package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;

public class MarkovChainGenerator<E> extends ProbabilityGenerator <E>
{
    ArrayList<ArrayList<Float>> transitionTable = new ArrayList<>();
    ProbabilityGenerator<E> probabilityGenerator = new ProbabilityGenerator<>(); //used in generate

    public void train(ArrayList<E> inputData) {
       
		int lastIndex = -1; //for each token in the input array 

        for (E token : inputData) { //tokenIndex = size of alphabet
            int tokenIndex = alphabet.indexOf(token);

            if (tokenIndex == -1) {
                tokenIndex = alphabet.size(); //tokenIndex = size of alphabet

                ArrayList<Float> newRow = new ArrayList<>(alphabet.size()); //add a new row to the transition table (expand vertically)
                for (int i = 0; i < alphabet.size(); i++) {
                    newRow.add(0.0f);
                }
                transitionTable.add(newRow);

                for (ArrayList<Float> row : transitionTable) { //add a new column (expand horizontally)
                    row.add(0.0f);
                }

                alphabet.add(token); //add the token to the alphabet array 
                tokenCounts.add(0.0f);
            }

            //increment the count for the token in alphabetCounts
            Float count = tokenCounts.get(tokenIndex) + 1;
            tokenCounts.set(tokenIndex, count);
            tokenCount++;

            //add the transition count to the transition table
            if (lastIndex > -1) {
                ArrayList<Float> row = transitionTable.get(lastIndex); //use lastIndex to get the correct row (array) in transition table.
                Float transitionCount = row.get(tokenIndex) + 1; //use the tokenIndex to index the correct column (value of the row you accessed)
                row.set(tokenIndex, transitionCount);
            }

            lastIndex = tokenIndex;

        }
			System.out.println(alphabet);
			System.out.println(transitionTable);


			probabilityGenerator.train(inputData);
    }

	ArrayList<ArrayList<Float>> tableProbDist()
	{
		ArrayList<ArrayList<Float>> tt = new ArrayList<>();
        for (ArrayList<Float> row : transitionTable) {
            
			ArrayList<Float> newRow = new ArrayList<>();
			float count = sumRow(row);

			for (int j = 0; j < row.size(); j++) {

                if (count > 0) {
					newRow.add(row.get(j) / count);
                }
				else {
					newRow.add(0.0f);
				}
            }
			tt.add(newRow);
        }
		return tt;
	}

	float sumRow(ArrayList<Float> row)
	{
		float sum = 0;
		for (Float value : row) {
			sum += value;
		}
		return sum;
	}

    // // Function to get the transition table
    // public ArrayList<ArrayList<Float>> getTransitionTable() {
    //     return transitionTable;
    // }

	// public static void MarkovChain(MarkovChainGenerator<Integer> pitchMarkovChain, MarkovChainGenerator<Double> rhythmMarkovChain) {
	// 	// Print the transition tables
	// 	pitchMarkovChain.printProbabilityDistribution(false);
	// 	rhythmMarkovChain.printProbabilityDistribution(false);
	// }
	

	public E generate(E initToken) {
		E currentToken = initToken;//initialize the current token
	
		ArrayList<E> generatedSequence = new ArrayList<>(); //initialize the generated sequence
	
			ArrayList<Float> row = transitionTable.get(alphabet.indexOf(currentToken)); //get the row for the current token
	
			if (row == null) {
				//handle the case when the token is not found in the transition table
				currentToken = probabilityGenerator.generate(1).get(0); //probabilityGenerator to generate the next token
				generatedSequence.add(currentToken);
			} else {
				tokenCount = sumRow(row);
				tokenCounts = row;

				if (tokenCount == 0) {
					return probabilityGenerator.generate(1).get(0);
				}
				
				ArrayList <E> newToken = super.generate(1);
				return newToken.get(0);


			}
		
	
		return currentToken;
	}
	
	public ArrayList<E> generate(E initToken, int numberOfTokensToGenerate) {
		ArrayList<E> generatedSequence = new ArrayList<>();
		E currentToken = initToken;
	
		for (int i = 0; i < numberOfTokensToGenerate; i++) {
			currentToken = generate(currentToken);
			generatedSequence.add(currentToken);
		}
	
		return generatedSequence;
	}
	
	public ArrayList<E> generate(int numberOfTokensToGenerate) {
		return generate(probabilityGenerator.generate(1).get(0), numberOfTokensToGenerate);
	}
	
	




	
	
  	//nested convenience class to return two arrays from sortTransitionTable() method
	//students do not need to use this class
	protected class SortTTOutput
	{
		public ArrayList<E> symbolsListSorted;
		public ArrayList<ArrayList<Float>> ttSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//tt -- the unsorted transition table (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//ttSorted -- the transition table that changes reflecting the symbols sorting to remain accurate  (output)
	public SortTTOutput sortTT(ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt){

		SortTTOutput sortArraysOutput = new SortTTOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.ttSorted = new ArrayList<ArrayList<Float>>();
	
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
			sortArraysOutput.ttSorted.add(new ArrayList<Float>());
			for( int j=0; j<tt.get(index).size(); j++)
			{
				int index2 = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(j));
				sortArraysOutput.ttSorted.get(i).add(tt.get(index).get(index2));
			}
		}

		return sortArraysOutput;

	}
	




	//this prints the transition table
	//symbols - the alphabet or list of symbols found in the data
	//tt -- the transition table of probabilities (not COUNTS!) for each symbol coming after another
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)
	{
		//sort the transition table
		SortTTOutput sorted = sortTT(symbols, tt);
		symbols = sorted.symbolsListSorted;
		tt = sorted.ttSorted;

		System.out.println("-----Transition Table -----");

		System.out.println(symbols);

		for (int i=0; i<tt.size(); i++)
		{
			System.out.print("["+symbols.get(i) + "] ");
			for(int j=0; j<tt.get(i).size(); j++)
			{
				if(round)
				{
					DecimalFormat df = new DecimalFormat("#.##");
					System.out.print(df.format((double)tt.get(i).get(j)) + " ");
				}
				else
				{
					System.out.print((double)tt.get(i).get(j) + " ");
				}

			}
			System.out.println();


		}
		System.out.println();

		System.out.println("------------");
	}

	public void printProbabilityDistribution(boolean round)
	{
		printProbabilityDistribution(round, alphabet, tableProbDist());
	}

}