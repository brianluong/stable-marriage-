package main;
/**
     *======================================================================
     * Course:    CS3230 Design and Analysis of Algorithms
     *            Fall 2014, School of Computing, NUS
     *            Skeleton Code provided by Jonathan
     *
     * Student:   Brian Luong
     * Tutorial:  G2 (for example) 
     * Program:   Stable Marriage 
     * Language:  Java
     * Purpose:   Skeleton code for Programming Assignment 1.
     *            Student MUST use the skeleton code and fill
     *            in the implementation code necessary to make
     *            it work.
     *      
     *            NOTE: The method findStableMarriage()  
     *                  MUST NOT be modified.
     *
     *========================================================================
*/

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

class skeleton
{
	
	static int[] matchedBranches, matchedInterns, branchProposeCount, branchHireLimit;
	static int[][] branchPref, internPref, inverseIntern;
	static ArrayList<Integer>[] matchedBranchesV2;
	static int M, N;
    
	public static void main(String[] args)
	{
		
		IntegerScanner sc = new IntegerScanner(System.in);
	    PrintWriter pr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out))); // use this (a much faster output routine) instead of Java System.out.println (slow)
	 
	    int T;
	    T = sc.nextInt();  
	    
	    for (int i = 1; i <= T; i++) {
	    	M = sc.nextInt(); 										// # of Branches
	    	N = sc.nextInt(); 										// # of Interns 
	    
	    	branchHireLimit = new int[M + 1];						// Store # interns each branch hires
	    	for (int j = 1; j <= M; j++) {
	    		branchHireLimit[j] = sc.nextInt();		
	    	}
	    	
	    
	    	matchedBranchesV2 = (ArrayList<Integer>[]) new ArrayList[M];
	    	matchedBranches = new int [M + 1]; 						// already init to 0
	    	matchedInterns = new int [N + 1]; 						// already init to 0
	    	branchProposeCount = new int [N + 1];
	    	branchPref = new int [N + 1][N + 1];
		    internPref = new int [M + 1][M + 1];
		    inverseIntern = new int[M + 1][M + 1];
		    
		    for (int j = 1; j <= N; j++) {
		    	for (int k = 1; k <= N; k++) { 						// Saving preferences for each Branch
		    		branchPref[j][k] = sc.nextInt();
		    	}	
		    }
		    
		    for (int j = 1; j <= M; j++) { 							// Saving preferences for each Intern
		    	for (int k = 1; k <= M; k++) {
		    		internPref[j][k] = sc.nextInt();
		    	}
		    }
		    
		    for (int j = 1; j <= N; j++) { // Creating inverse preferences for Interns
		    	for (int k = 1; k <= N; k++) {
		    		inverseIntern[j][internPref[j][k]] = k;
		    	}
		    }
		    
		    findStableMarriage();
		    for (int a = 1; a <= N; a++)  {
		    	pr.printf("%1$d %2$d\n", a, matchedBranches[a]); 
		    }
	    }
	    
	    pr.close(); // do not forget to use this

	}
	
	private static void initializeEachInternAndBranchFree() {}

	//return true iff there is a branch who is free and has not proposed anyone
	private static boolean someBranchIsFreeAndHasNotProposed()
	{
		for (int i = 1; i <= N; i++) {
			if ((matchedBranches[i] == 0) && (branchProposeCount[i] < N)) {
					return true;
			}	
		}
		return false;
	}

	//return the index of the Branch who is free and has not proposed anyone
	private static int chooseThatBranch()
	{
		for (int i = 1; i <= N; i++) {
			if (matchedBranches[i] == 0) {
				if (branchProposeCount[i] < N) {
					return i;
				}
			}
		}
		return 0;
	}

	//return the index of the first intern on branch #branchIndex's list whom it has not yet proposed
	private static int firstInternOnBranchList(int manIndex)
	{
		return branchPref[manIndex][1 + branchProposeCount[manIndex]];
	}

	//return true iff intern #internIndex is free
	private static boolean isFree(int internIndex)
	{
		return (matchedInterns[internIndex] == 0);
	}

	//assign branch #branchIndex and intern #internIndex to be engaged
	private static void assign(int branchIndex,int internIndex)
	{
		matchedBranches[branchIndex] = internIndex;
		matchedInterns[internIndex] = branchIndex;
		branchProposeCount[branchIndex]++;
	}

	//return true iff Woman #womanIndex prefers Man #firstManIndex to Man #secondManIndex
	private static boolean prefers(int internIndex,int firstBranchIndex,int secondBranchIndex)
	{
		return (inverseIntern[internIndex][firstBranchIndex] < inverseIntern[internIndex][secondBranchIndex]);
	}

	//set Man #manIndex to be free
	private static void setFree(int branchIndex)
	{
		matchedBranches[branchIndex] = 0;
	}

	//return the index of man who is the fiance of Woman #womanIndex
	private static int fiance(int internIndex)
	{
		return matchedInterns[internIndex];
	}

	//Man #manIndex get rejected by Woman #womanIndex
	private static void rejected(int branchIndex,int internIndex)
	{
		branchProposeCount[branchIndex]++;
	}

	private static void findStableMarriage()
	{
		//Initialize each person to be free.
		initializeEachInternAndBranchFree(); 
		//while (some man is free and hasn't proposed to every woman)
		while (someBranchIsFreeAndHasNotProposed())
		{
			//Choose such a man m
			int freeBranch = chooseThatBranch();
			//w = 1st woman on m's list to whom m has not yet proposed
			int firstIntern = firstInternOnBranchList(freeBranch);
			//if (w is free)
			if (isFree(firstIntern)) 
			{
				//assign m and w to be engaged
				assign(freeBranch,firstIntern);
			}
			//else if (w prefers m to her fianceÌ m')
			else if (prefers(firstIntern,freeBranch,fiance(firstIntern)))
			{
				//assign m and w to be engaged, and m' to be free
				setFree(fiance(firstIntern)); assign(freeBranch,firstIntern);
			//else
			} else
			{
				//w rejects m
				rejected(freeBranch,firstIntern);
			}
		}
	}

}

class IntegerScanner { // use this (a much faster input routine) instead of standard Java Scanner class (slow)
	  BufferedInputStream bis;
	  IntegerScanner(InputStream is) {
	    bis = new BufferedInputStream(is, 1000000);
	  }
	 
	  public int nextInt() {   
	    int result = 0;
	    try {
	      int cur = bis.read();
	      if(cur == -1)
	        return -1;
	     
	      while(cur < 48 || cur > 57) {
	        cur = bis.read();
	      }
	      while(cur >= 48 && cur <= 57) {
	        result = result * 10 + (cur - 48);
	        cur = bis.read();
	      }
	      return result;
	    }
	    catch(IOException ioe) {
	      return -1;
	    }
	  }
	}