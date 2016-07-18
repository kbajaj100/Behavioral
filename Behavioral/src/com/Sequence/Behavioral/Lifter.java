package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class Lifter {

	private int carma_count;
	private int carma_max; // Number of associations
	private String ante_sql;
	private String con_sql;
	private int ante_count;
	private int con_count;
	private int con_count_no_ante;
	private float prob_con_count_no_ante;
	private String SQL;
	private int pop; //mci_id count
	private String bracketTable;
	private String pivotTable;
	private int limit = 10;
	private int insert_start_counter = 1;
	
	DBConn myconn;
	Carma[] mycarma;
	
	public int getmax(int bracket) throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");
		
		setbracketTable(bracket);
		setpivotTable(bracket);
		SQL = "select COUNT(*) count from " + bracketTable;
		
		carma_max = myconn.execSQL_returnint(SQL);
		
		
		//++carma_max;
		return carma_max;
				
	}

	private void setpivotTable(int bracket) {
		// TODO Auto-generated method stub
		
		pivotTable = "dbo.MCI_DX_Pivot_" + bracket + "M";
		
	}

	private void setbracketTable(int bracket) {
		// TODO Auto-generated method stub
		bracketTable = "dbo.carma_all_" + bracket + "M";
		
	}

	public int ExecuteLifter(int bracket) {
		// TODO Auto-generated method stub
		
		setpop(bracket);
		
		//Get distinct consequents and calculate their probability
		//Write each of these back to the table
		for (int i = 1; i <=carma_max; ++i){
			SQL = "select con_sql code from " + bracketTable + " where Table_ID = " + i + " and Confidence_pct > 0";
			con_sql = myconn.execSQL_returnString(SQL);
					
			//If there is a value for con_sql, then get the number of MCIs with that consequent
			if(con_sql != ""){
				SQL = "select count(MCI_ID) count from " + pivotTable + " where " + con_sql;
				con_count_no_ante = myconn.execSQL_returnint(SQL);
			
				prob_con_count_no_ante = (float)con_count_no_ante/pop;
				
				SQL = "update " + bracketTable + " set prob_consequent = " + prob_con_count_no_ante + " where TABLE_ID = " + i;
				myconn.execSQL(SQL);
				
				SQL = "update " + bracketTable + " set lift = Confidence_pct/" + prob_con_count_no_ante + " where TABLE_ID = " + i;
				myconn.execSQL(SQL);
				
				System.out.println("i is : " + i);
			}
			
			con_sql = "";
		}
		
		return 0;
	}

	
	private void setpop(int bracket) {
		// TODO Auto-generated method stub
		
		SQL = "select count(MCI_ID) count from " + pivotTable;

		pop = myconn.execSQL_returnint(SQL);
		System.out.println("pop is: " + pop);
	}

}
