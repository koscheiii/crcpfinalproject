/*
 * Modified By Isabelle Jeanjean
 * c2017-2023 Courtney Brown
 * Class: Project 2 Template
 * Description: This is a template for the project 2 code, which is an implementation of a Markov chain of order 1
 */

package com.example;

//importing the JMusic stuff
import jm.music.data.*;
import jm.util.*;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;

import processing.core.*;

//make sure this class name matches your file name, if not fix.
public class App extends PApplet {

	static MelodyPlayer player; // play a midi sequence
	static MidiFileToNotes midiNotes; // read a midi file
	static int noteCount = 0;

	//make cross-platform
	static FileSystem sys = FileSystems.getDefault();

	//the getSeperator() creates the appropriate back or forward slash based on the OS in which it is running -- OS X & Windows use same code :) 
	static String filePath = "mid"  + sys.getSeparator() +  "ABBA_-_Gimme_Gimme_Gimme.mid"; // path to the midi file -- you can change this to your file
															// location/name

	ClassicalRhythms test = new ClassicalRhythms(this);												
	public static void main(String[] args) {
		PApplet.main( "com.example.App");

		// run the unit tests
		int whichTest = Integer.parseInt(args[0]);

		// setup the melody player
		// uncomment below when you are ready to test or present sound output
		// make sure that it is commented out for your final submit to github (eg. when
		// pushing)


		// setup();
		// playMelody();

		
		//testAndTrainProbGen();
		//generateMelody();

		// MarkovChain();
		// MarkovChainGenerate();
	
		

		// uncomment to debug your midi file
		// this code MUST be commited when submitting unit tests or any code to github
		// playMidiFileDebugTest(filePath);
	}


	// test and declare prob gen
	public static void testAndTrainProbGen()
	{
		ProbabilityGenerator<Integer> pitchGen = new ProbabilityGenerator<Integer>();
		ProbabilityGenerator<Double> rhythmGen = new ProbabilityGenerator<Double>();

		ProbabilityGenerator<Integer> pitchGen2 = new ProbabilityGenerator<Integer>(); 
		ProbabilityGenerator<Double>  rhythmGen2 = new ProbabilityGenerator<Double>();


		for(int i = 0; i < 10000; i++) {
			ArrayList<Integer> pitches = pitchGen.generate(20); //array list for pitch generation
			ArrayList<Double> rhythms = rhythmGen.generate(20); //array list for rhythm generation
			
			// add the generated melodies to the lists
			pitchGen2.train(pitches);
			rhythmGen2.train(rhythms);
		
		}
		// train the probability generators with MIDI data
		pitchGen.train(midiNotes.getPitchArray()); 
		rhythmGen.train(midiNotes.getRhythmArray()); 

		 // print the probability distributions
		pitchGen.printProbabilityDistribution(false);
		rhythmGen.printProbabilityDistribution(false);
	}
	//round is false in train and true in generate

	// generate a melody from a probability distribution
	public static void generateMelody()
	{
		ProbabilityGenerator<Integer> pitchGen = new ProbabilityGenerator<Integer>(); //pitches list
		ProbabilityGenerator<Double> rhythmGen = new ProbabilityGenerator<Double>(); //rhythms list

		pitchGen.train(midiNotes.getPitchArray()); //getting pitch training
		rhythmGen.train(midiNotes.getRhythmArray()); //getting rhythm training

		ArrayList<Integer> pitches = pitchGen.generate(20); //array list for pitch generation
		ArrayList<Double> rhythms = rhythmGen.generate(20); //array list for rhythm generation

		player.setMelody(pitches); //set to generated notes
		player.setRhythm(rhythms); //set to generated notes

		 // print the probability distributions
		pitchGen.printProbabilityDistribution(true);
		rhythmGen.printProbabilityDistribution(true);
	}
	    
	public static void MarkovChain() 
	{
		// create instances of MarkovChainGenerator for pitch and rhythm
		MarkovChainGenerator<Integer> pitchMarkovChain = new MarkovChainGenerator<>();
		MarkovChainGenerator<Double> rhythmMarkovChain = new MarkovChainGenerator<>();

		// create the transition tables
		pitchMarkovChain.train(midiNotes.getPitchArray());
		rhythmMarkovChain.train(midiNotes.getRhythmArray());

		// print the transition tables
		pitchMarkovChain.printProbabilityDistribution(false);
		rhythmMarkovChain.printProbabilityDistribution(false);
	}

	public static void MarkovChainGenerate() 
	{
		// create instances of MarkovChainGenerator for pitch and rhythm
		MarkovChainGenerator<Integer> pitchMarkovChain = new MarkovChainGenerator<>();
		MarkovChainGenerator<Double> rhythmMarkovChain = new MarkovChainGenerator<>();

		// generate and train pitch data
		MarkovChainGenerator<Integer> pitchGen = new MarkovChainGenerator<>();
		pitchGen.train(midiNotes.getPitchArray());
		for (int i = 0; i < 10000; i++) {
			ArrayList<Integer> pitches = pitchGen.generate(20);
			pitchMarkovChain.train(pitches);
		}

		// train the pitch probability generator with MIDI pitch data
		//pitchMarkovChain.train(midiNotes.getPitchArray());

		// generate and train rhythm data
		MarkovChainGenerator<Double> rhythmGen = new MarkovChainGenerator<>();
		rhythmGen.train(midiNotes.getRhythmArray());
		for (int i = 0; i < 10000; i++) {
			ArrayList<Double> rhythms = rhythmGen.generate(20);
			rhythmMarkovChain.train(rhythms);
		}

		// print the transition tables
		pitchMarkovChain.printProbabilityDistribution(true);
		rhythmMarkovChain.printProbabilityDistribution(true);
	}
	
	// doing all the setup stuff
	public void setup() {

		// playMidiFile(filePath); //use to debug -- this will play the ENTIRE file --
		// use ONLY to check if you have a valid path & file & it plays
		// it will NOT let you know whether you have opened file to get the data in the
		// form you need for the assignment

		midiSetup(filePath);
		
	}

	//PROCESSING STUFF

	public void settings()
	{
		// size(500, 500);
		fullScreen();
	}

	public void draw()
	{
		background(0);
		rect(width/2, height/2, 50, 50);
		test.draw();
	}




	// plays the midi file using the player -- so sends the midi to an external
	// synth such as Kontakt or a DAW like Ableton or Logic
	static public void playMelody() {

		assert (player != null); // this will throw an error if player is null -- eg. if you haven't called
									// setup() first

		while (!player.atEndOfMelody()) {
			player.play(); // play each note in the sequence -- the player will determine whether is time
							// for a note onset
		}

	}

	// opens the midi file, extracts a voice, then initializes a melody player with
	// that midi voice (e.g. the melody)
	// filePath -- the name of the midi file to play
	static void midiSetup(String filePath) {

		// Change the bus to the relevant port -- if you have named it something
		// different OR you are using Windows
		player = new MelodyPlayer(100, "Bus 1"); // sets up the player with your bus.

		midiNotes = new MidiFileToNotes(filePath); // creates a new MidiFileToNotes -- reminder -- ALL objects in Java
													// must
													// be created with "new". Note how every object is a pointer or
													// reference. Every. single. one.

		// // which line to read in --> this object only reads one line (or ie, voice or
		// ie, one instrument)'s worth of data from the file
		midiNotes.setWhichLine(0); // this assumes the melody is midi channel 0 -- this is usually but not ALWAYS
									// the case, so you can try other channels as well, if 0 is not working out for
									// you.

		noteCount = midiNotes.getPitchArray().size(); // get the number of notes in the midi file

		assert (noteCount > 0); // make sure it got some notes (throw an error to alert you, the coder, if not)

		// sets the player to the melody to play the voice grabbed from the midi file
		// above
		player.setMelody(midiNotes.getPitchArray());
		player.setRhythm(midiNotes.getRhythmArray());
	}

	static void resetMelody() {
		player.reset();

	}

	// this function is not currently called. you may call this from setup() if you
	// want to test
	// this just plays the midi file -- all of it via your software synth. You will
	// not use this function in upcoming projects
	// but it could be a good debug tool.
	// filename -- the name of the midi file to play
	static void playMidiFileDebugTest(String filename) {
		Score theScore = new Score("Temporary score");
		Read.midi(theScore, filename);
		Play.midi(theScore);
	}

}
