package com.Sequence.Behavioral;

public class Carma {

	private int ante_count;
	private int con_count;
	private int TableID;
	
	private String ante;
	private String con;
	
	private float confidence;
	private float support;
	
	void setCarma(int a, int c, int t, String an, String co){
		ante_count = a;
		con_count = c;
		TableID = t;
		
		ante = an;
		con = co;
	}
	
	void setconfidence(){
		
		confidence = (float) con_count/ante_count;
		System.out.println("Confidence is : " + confidence);
	}
	
	void setsupport(float pop){
		
		support = (float) con_count/pop;
		System.out.println("Support is : " + support);
	}
	
	int getantecount(){
		return ante_count;
	}
	
	int getconcount(){
		return con_count;		
	}
	
	private int getTableID(){
		return TableID;
	}
	
	String getante(){
		return ante;
	}
	
	String getcon(){
		return con;
	}
	
	float getconfidence(){
		return confidence;
	}
	
	float getsupport(){
		return support;
	}
	
}
