package com.Sequence.Behavioral;

public class ComList {

	private int MCI;
	private String EndComb;
	private int numDX;
	private int numMonth;
	private int[] Continuum;
	
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
	
	void setnumMonth(int numMo){
		numMonth = numMo;
		Continuum = new int[numMonth];
		
	}

	void setContinuum(int Cont, int pos){
		Continuum[pos] = Cont;
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
	
	int getnumMonth(){
		return numMonth;
	}
	
	int getContinuum(int pos){
		return Continuum[pos];
	}
}


