package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class MainClass {

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub

		//Create a class  to do the sequencing of multiple occurrences of min date
	
		//Sequencer();
		
		Association();
		
	}

	private static void Association() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		int max, count, bracket;
		
		Associate myassociate = new Associate();
		
		bracket = 20;
		
		max = myassociate.getmax();
		count = myassociate.ExecuteCarma(bracket);
		
	}

	private static void Sequencer() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		Sequencer myseq = new Sequencer();
		
		
		int max;
		
		max = myseq.getmax();
		
		System.out.println(max);
		
		max = myseq.execSequence_DX1();
		System.out.println("Inserted records count: " + max);
		
		max = myseq.execSequence_DXGT1();
		System.out.println("Inserted records count: " + max);
	}
	
	

}
