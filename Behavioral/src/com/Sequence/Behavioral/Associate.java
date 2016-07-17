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
	private String bracketTable;
	private String pivotTable;
	private int limit = 10;
	private int insert_start_counter = 1;
	
	DBConn myconn;
	Carma[] mycarma;
	
	public int getmax() throws FileNotFoundException, IOException, SQLException{
		
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");
		
		SQL = "select COUNT(*) count from dbo.carma_3_pct";
		
		carma_max = myconn.execSQL_returnint(SQL);
		
		++carma_max;
		return carma_max;
	}
	
	public int ExecuteCarma(int bracket, int period){
		
		/*
		1. Get the first ante_sql and con_sql from carma_3_pct
		2. Look for the number of MCIs from MCI_DX_Pivot with the ante and con
		3. Calculate the confidence and support
			a. Confidence: Con Count/ Ante Count
			b. Support: ConCount/ Total Population considered 
		4. Store these in an array
		5. When the array size hits a limit, update carma table: insert these numbers (#3) into carma table according to the bracket
		6. Reset the array and Loop
		*/
		
		setpop(bracket);
		setbrackettable(period);
		setpivotTable(period);
		
		mycarma = new Carma[carma_max];
		
		
		for (int i = 0; i < carma_max; ++i) 
			mycarma[i] = new Carma();
		
		int breaker = 0, checker = 0;
		
		for(carma_count = 1; carma_count <= carma_max; ++carma_count) //Condition should be carma_max
		{

			SQL = "select ante_sql code from dbo.carma_3_pct where TABLE_ID = " + carma_count;
			ante_sql = myconn.execSQL_returnString(SQL);
						
			SQL = "select con_sql code from dbo.carma_3_pct where TABLE_ID = " + carma_count;
			con_sql = myconn.execSQL_returnString(SQL);
			
			if (bracket != 0)
				SQL = "select COUNT(MCI_ID) count from " + pivotTable + " where SU_Ind = " + bracket + " and " + ante_sql;	
			else 
				SQL = "select COUNT(MCI_ID) count from " + pivotTable + " where " + ante_sql;
				
			System.out.println(SQL);
			ante_count = myconn.execSQL_returnint(SQL);
			System.out.println("Ante Count is: "+ ante_count);
			
			if (bracket != 0)
				SQL = "select COUNT(MCI_ID) count from " + pivotTable + " where SU_Ind = " + bracket + " and " + ante_sql + " and " + con_sql;
			else 
				SQL = "select COUNT(MCI_ID) count from " + pivotTable + " where " + ante_sql + " and " + con_sql;
			
			System.out.println(SQL);
			con_count = myconn.execSQL_returnint(SQL);
			System.out.println("Con Count is: "+ con_count);
			
			if ((ante_count > 0) || (con_count > 0))
			{
				insertmyCarma();
				++breaker;
				System.out.println("carma_count is: "+ carma_count);
				System.out.println("breaker is: "+ breaker);
			}
			
			if(breaker == limit)
			{
				checker = insertCarma(breaker);
				
				if (checker ==0)
					break;
					
				breaker = 0;

			}
		}
		
		insertCarma(breaker);
		
		return carma_count;
	}

	
	private void setpivotTable(int period) {
		// TODO Auto-generated method stub
		
		pivotTable = "dbo.MCI_DX_Pivot_" + period + "M";
		System.out.println(pivotTable);
	}

	//Carma_count goes from 1 - 474170
	//limit = 1000
	//breaker is incremented every time a value is inserted into mycarma array
	//when breaker = limit, records are inserted into table
	//breaker is reset to 0
	//challenge is to only insert valid records
	//the array contains many nulls
	
	
	private int insertCarma(int breaker) {
		// TODO Auto-generated method stub
		SQL = "insert into " + bracketTable + " (ante_sql, con_sql, Confidence_pct, Support_pct, ante_count, con_count) values ";
		String insertvalue = "";
		
		for(int i = insert_start_counter, j = 1; i <= carma_count; ++i)
		{		
			ante_sql = mycarma[i].getante();
			
			if (ante_sql != null)
			{
				insertvalue = insertvalue + "('" + mycarma[i].getante() + "','" + mycarma[i].getcon() + "'," + mycarma[i].getconfidence() + "," + mycarma[i].getsupport() + "," + mycarma[i].getantecount() + "," + mycarma[i].getconcount() + ")";
				System.out.println(insertvalue);
				System.out.println("j is : " + j);
				System.out.println("i is : " + i);
				if (j == breaker)  
				{	
					SQL = SQL + insertvalue;
					System.out.println(SQL);
					myconn.execSQL(SQL);
					System.out.println("i is : " + i);
				}
				else
					insertvalue = insertvalue + ",";
				
				++j;
			}
		}
		
		insert_start_counter = carma_count+1;
		return 1;
		
	}

	
	private void insertmyCarma() {
		// TODO Auto-generated method stub
		mycarma[carma_count].setCarma(ante_count, con_count, carma_count, ante_sql, con_sql);
		mycarma[carma_count].setconfidence();
		mycarma[carma_count].setsupport(pop);
	}
	
	private void setpop(int bracket) {
		// TODO Auto-generated method stub
		if (bracket == 0)
			SQL = "select count(MCI_ID) count from dbo.mci_rank where SU_Ind is not null";
		else SQL = "select count(MCI_ID) count from dbo.mci_rank where SU_Ind = " + bracket;

		pop = myconn.execSQL_returnint(SQL);
		System.out.println("pop is: " + pop);
	}

	private void setbrackettable(int bracket) {
		// TODO Auto-generated method stub
		
		bracketTable = "dbo.carma_All_" + bracket + "M";
		System.out.println("insert table is: " + bracketTable);
	}

}
