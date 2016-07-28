package com.Sequence.Behavioral;

public class ComList {

	private int MCI;
	private String EndComb;
	private int numDX;
	
	void setMCI(int MCI_ID){
		MCI = MCI_ID;
		System.out.println("MCI is: " + MCI);
	}
	
	void setComb(String Comb){
		EndComb = Comb;
		System.out.println("EndComb is: " + EndComb);
	}
	
	void setnumDX(int num){
		numDX = num;
		System.out.println("numDX is: " + numDX);
	}
	
	int getMCI(){
		return MCI;
	}
	
	String getEndComb(){
		return EndComb;
	}
	
	int getNumDX(){
		return numDX;
	}
	
}


