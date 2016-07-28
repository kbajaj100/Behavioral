package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class MainClass {

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub

		//Create a class  to do the sequencing of multiple occurrences of min date
	
		//Sequencer();
		
		//Association();
		
		//Predictor();

		//Lifter();
		
		//SU_Month_Identifier();
		
		//EndCombination();
		
		MonthCombination();
	}

	private static void MonthCombination() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		int count;
		MonthCom myMonthCom = new MonthCom();
		
		count = myMonthCom.getmax();
		count = myMonthCom.setMCIandnumMonth();
		count = myMonthCom.setMCIContinuum();
		myMonthCom.setMonthEndComb();
		
	}

	private static void EndCombination() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		int count;
		EndCom myEndCom = new EndCom();
		
		count = myEndCom.getmax();
		count = myEndCom.setMCIandDXNum();
		myEndCom.setEndComb();
		
	}

	private static void SU_Month_Identifier() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		int count;
		SUMonth mySUmonth = new SUMonth();
		
		count = mySUmonth.getmax();
		
		System.out.println("max is: " + count);

		count = mySUmonth.getMCIfromDB();
		count = mySUmonth.getPAfromDB();
		count = mySUmonth.updateMonth();
		
	}

	private static void Lifter() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
			int max, count, bracket;
			
			bracket = 12;

			Lifter myLifter = new Lifter();
			max = myLifter.getmax(bracket);

			count = myLifter.ExecuteLifter(bracket);

	}

	private static void Predictor() throws FileNotFoundException, IOException, SQLException {
					
		int max, count, bracket;
		
		bracket = 6;
		
		Predictor myPredictor = new Predictor();
		max = myPredictor.getmax(bracket);
		
		count = myPredictor.ExecutePredictor(bracket);
	}

	private static void Association() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		int max, count, bracket, period;
		
		Associate myassociate = new Associate();
		
		bracket = 0;
		period = 12;
		
		max = myassociate.getmax();
		count = myassociate.ExecuteCarma(bracket, period);
		
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
