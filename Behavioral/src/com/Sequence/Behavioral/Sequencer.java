package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class Sequencer {

	private int tcounter;
	private int max;
	private int dx_count;
	private int dx_max;
	private int dx_table_counter;
	private String dx_final;
	private String SQL;
	private String dx_temp;
	
	DBConn myconn;
	
	public int getmax() throws FileNotFoundException, IOException, SQLException{
		
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");
		
		SQL = "select COUNT(MCI_ID) count from dbo.temp_MCI_month_Count_min";
		
		max = myconn.execSQL_returnint(SQL);
		
		return max;
	}
	
	public void execSequence(){
		
		tcounter = 1;
		
		while(tcounter <= 1000000){
			
			dx_count = 1;
			dx_final = "";
			
			SQL = "select (DX_Count_DIST) count from dbo.temp_MCI_month_Count_min where Table_ID = " + tcounter;
			
			dx_max = myconn.execSQL_returnint(SQL);
			
			System.out.println("tcounter: " + tcounter);
			System.out.println("dx_max: " + dx_max);
			
			SQL = "select MIN(a11.Table_ID) count from dbo.AHCI_CLAIMS_temp a11 join dbo.temp_MCI_month_Count_min a12 on (a11.MCI_ID = a12.MCI_ID and a11.Min_date = a12.Min_date) where a12.Table_ID = " + tcounter;
			
			dx_table_counter = myconn.execSQL_returnint(SQL);
			
			System.out.println("dx_table_counter: " + dx_table_counter);
			
			while(dx_count <= dx_max){
				
				SQL = "select distinct a11.DX_CATEGORY code from dbo.AHCI_CLAIMS_temp a11 join dbo.temp_MCI_month_Count_min a12 on (a11.MCI_ID = a12.MCI_ID and a11.Min_date = a12.Min_date) where a12.Table_ID = " + tcounter + " and a11.Table_ID = " + dx_table_counter;
				
				System.out.println("SQL: " + SQL);
				
				dx_temp = myconn.execSQL_returnString(SQL);
				
				dx_temp = dx_temp.trim();
				
				System.out.println("dx_temp: " + dx_temp);
				
				if(dx_final == ""){
					dx_final = dx_temp;
				}
				else{
					dx_final = dx_final + "->" + dx_temp;
				}
				
				++dx_count;
				++dx_table_counter; 
			}

			System.out.println("dx_final: " + dx_final);
			SQL = "insert into dbo.AHCI_CLAIMS_MIN_DX_SEQUENCE (MCI_ID , Min_Date, DX_Sequence)	select MCI_ID, Min_Date, rtrim('" + dx_final + "') from dbo.temp_MCI_month_Count_min where Table_ID = " + tcounter;
			System.out.println(SQL);
			
			myconn.execSQL(SQL);
			
			++tcounter;
		}
	}
}
