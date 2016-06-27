package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class Associate {

	private int carma_count;
	private int carma_max; // Number of associations
	private String ante_sql;
	private String con_sql;
	private int ante_count;
	private int con_count;
	private String SQL;
	private int pop; //mci_id count
	
	DBConn myconn;
	Carma[] mycarma;
	
	public int getmax() throws FileNotFoundException, IOException, SQLException{
		
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");
		
		SQL = "select COUNT(*) count from dbo.carma_3_pct";
				
		carma_max = myconn.execSQL_returnint(SQL);
		
		return carma_max;
	}
	
	public int ExecuteCarma(int bracket){
		
		/*
		1. Get the first ante_sql and con_sql from carma_3_pct
		2. Look for the number of MCIs from MCI_DX_Pivot with the ante and con
		3. Calculate the confidence and support
			a. Confidence: Con Count/ Ante Count
			b. Support: ConCount/ Total Population considered 
		4. Update carma table: insert these numbers (#3) back into carma_3_pct table
		*/
		
		setpop(bracket);
		
		mycarma = new Carma[carma_max];
		
		for (int i = 0; i < carma_max; ++i) 
			mycarma[i] = new Carma();
		
		for(carma_count = 1; carma_count <= carma_max; ++carma_count) //Condition should be carma_max
		{

			SQL = "select ante_sql code from dbo.carma_3_pct where TABLE_ID = " + carma_count;
			ante_sql = myconn.execSQL_returnString(SQL);
						
			SQL = "select con_sql code from dbo.carma_3_pct where TABLE_ID = " + carma_count;
			con_sql = myconn.execSQL_returnString(SQL);
			
			SQL = "select COUNT(MCI_ID) count from dbo.MCI_DX_Pivot where SU_Ind = " + bracket + " and " + ante_sql;
			System.out.println(SQL);
			ante_count = myconn.execSQL_returnint(SQL);
			System.out.println("Ante Count is: "+ ante_count);
			
			SQL = "select COUNT(MCI_ID) count from dbo.MCI_DX_Pivot where SU_Ind = " + bracket + " and " + ante_sql + " and " + con_sql;
			System.out.println(SQL);
			con_count = myconn.execSQL_returnint(SQL);
			System.out.println("Con Count is: "+ con_count);
			
			insertmyCarma();
			
		}
		
		insertCarma();
		
		return carma_count;
	}

	private void insertCarma() {
		// TODO Auto-generated method stub
		SQL = "insert into dbo.carma_10_pct (ante_sql, con_sql, Confidence_pct, Support_pct) values ";
		
		String insertvalue = "";
		int i = 0, breaker = 100;
		
		
		for (int j = 0; i < carma_max; ++i, ++j){
			if ((j == breaker) || (i == carma_max -1)) 
			{	
				insertvalue = insertvalue + "('" + mycarma[i].getante() + "','" + mycarma[i].getcon() + "'," + mycarma[i].getconfidence() + "," + mycarma[i].getsupport()+ ")";
				SQL = SQL + insertvalue;
				System.out.println(SQL);
				myconn.execSQL(SQL);
				SQL = "insert into dbo.carma_10_pct (ante_sql, con_sql, Confidence_pct, Support_pct) values ";
				insertvalue = "";
				System.out.println("i is : " + i + " and j is: " + j);
				j = 0;
			}
			else
				insertvalue = insertvalue + "('" + mycarma[i].getante() + "','" + mycarma[i].getcon() + "'," + mycarma[i].getconfidence() + "," + mycarma[i].getsupport()+ "),";
			//System.out.println(insertvalue);
		}
		
		
	}

	private void setpop(int bracket) {
		// TODO Auto-generated method stub
		SQL = "select count(MCI_ID) count from dbo.mci_rank where SU_Ind = " + bracket;
		pop = myconn.execSQL_returnint(SQL);
		System.out.println("pop is: " + pop);
	}

	private void insertmyCarma() {
		// TODO Auto-generated method stub
		mycarma[carma_count-1].setCarma(ante_count, con_count, carma_count, ante_sql, con_sql);
		mycarma[carma_count-1].setconfidence();
		mycarma[carma_count-1].setsupport(pop);
	}
	
}
