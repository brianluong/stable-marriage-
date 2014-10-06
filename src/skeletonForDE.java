import java.io.BufferedInputStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

class skeletonForDE
{
	
	static int[] matchedBranches, matchedInterns, branchProposeCount, branchHireLimit;
	static int[][] branchPref, internPref, inverseIntern;
	static Stack<Integer>[] matchedBranchesV2;
	static int M, N, internToSetFree;
    
	@SuppressWarnings("unchecked")
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
	    	
	    
	    	matchedBranchesV2 = (Stack<Integer>[]) new Stack[M + 1];	// initialize stack
	    	for (int j = 1; j <=M; j++) {
	    		matchedBranchesV2[j] = new Stack<Integer>();
	    	}
	    	
	    	matchedBranches = new int [M + 1]; 						// already init to 0
	    	matchedInterns = new int [N + 1]; 						// already init to 0
	    	branchProposeCount = new int [M + 1];
	    	branchPref = new int [M + 1][N + 1];
		    internPref = new int [N + 1][M + 1];
		    inverseIntern = new int[N + 1][M + 1];
		    
		    for (int j = 1; j <= M; j++) {
		    	for (int k = 1; k <= N; k++) { 						// Saving preferences for each Branch
		    		branchPref[j][k] = sc.nextInt();
		    	}	
		    }
		    
		    for (int j = 1; j <= N; j++) { 							// Saving preferences for each Intern
		    	for (int k = 1; k <= M; k++) {
		    		internPref[j][k] = sc.nextInt();
		    	}
		    }
		    
		    for (int j = 1; j <= N; j++) { 							// Creating inverse preferences for Interns
		    	for (int k = 1; k <= M; k++) {
		    		inverseIntern[j][internPref[j][k]] = k;
		    	}
		    }
		    
		    findStableMarriage();
		    for (int a = 1; a <= M; a++)  {
		    	int length = branchHireLimit[a];
		    	for (int b = 0; b <= length - 1; b++) {
		    		int intern = matchedBranchesV2[a].pop();
		    		pr.printf("%1$d %2$d\n", a, intern);
		    	}
		    }
	    }
	    
	    pr.close(); // do not forget to use this

	}
	
	private static void initializeEachInternAndBranchFree() {}

	//return true iff there is a branch who is free and has not proposed anyone
	private static boolean someBranchIsFreeAndHasNotProposed()
	{
		for (int i = 1; i <= M; i++) {
			if (branchProposeCount[i] < N) {
				if (matchedBranchesV2[i].size() < branchHireLimit[i]) {
					return true;
				}
			}
		}
		return false;
	}
	
	//return the index of the Branch who is free and has not proposed anyone
	private static int chooseThatBranch()
	{
		for (int i = 1; i <= M; i++) {
			if (branchProposeCount[i] < N) {
				if (matchedBranchesV2[i].size() < branchHireLimit[i]) {
					return i;
				}
			}
		}
		return 0;
	}

	//return the index of the first intern on branch #branchIndex's list whom it has not yet proposed
	private static int firstInternOnBranchList(int branchIndex)
	{
		return branchPref[branchIndex][1 + branchProposeCount[branchIndex]];
	}

	//return true iff intern #internIndex is free
	private static boolean isFree(int internIndex)
	{
		return (matchedInterns[internIndex] == 0);
	}

	//assign branch #branchIndex and intern #internIndex to be engaged
	private static void assign(int branchIndex,int internIndex)
	{
		matchedBranchesV2[branchIndex].push(internIndex);
		matchedInterns[internIndex] = branchIndex;
		branchProposeCount[branchIndex]++;
	}

	//return true iff Woman #womanIndex prefers Man #firstManIndex to Man #secondManIndex
	private static boolean prefers(int internIndex,int firstBranchIndex,int secondBranchIndex)
	{
		if (inverseIntern[internIndex][firstBranchIndex] < inverseIntern[internIndex][secondBranchIndex]) {
			internToSetFree = internIndex;
			return true;
		}
		return false;
	}

	//set Man #manIndex to be free
	private static void setFree(int branchIndex)
	{	
		matchedBranchesV2[branchIndex].remove(new Integer(internToSetFree));
	}

	//return the index of branch who is interning of an specific intern #internIndex
	private static int interningAt(int internIndex)
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
			else if (prefers(firstIntern,freeBranch,interningAt(firstIntern)))
			{
				//assign m and w to be engaged, and m' to be free
				setFree(interningAt(firstIntern)); assign(freeBranch,firstIntern);
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