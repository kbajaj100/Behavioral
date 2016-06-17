package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class MainClass {

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub

		//Create a class  to do the sequencing of multiple occurrences of min date
		
		Sequencer myseq = new Sequencer();
		
		int max;
		
		max = myseq.getmax();
		
		System.out.println(max);
		
		myseq.execSequence();
	}

}
