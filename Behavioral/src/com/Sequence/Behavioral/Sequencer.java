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
	
	public int execSequence_DXGT1(){
		
		tcounter = 1;
		
		SQL = "select max(Table_ID) count from dbo.temp_MCI_month_Count_min where DX_Count_Dist > 1 and Processed = 0";
		max = myconn.execSQL_returnint(SQL);	
		
		while(tcounter <= max){
			
			SQL = "select MIN(Table_ID) count from dbo.temp_MCI_month_Count_min where DX_Count_Dist > 1 and Processed = 0";
			tcounter = myconn.execSQL_returnint(SQL);	
			
			dx_count = 1;
			dx_final = "";
			
			SQL = "select (DX_Count_DIST) count from dbo.temp_MCI_month_Count_min where DX_Count_Dist > 1 and Table_ID = " + tcounter;
			
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
			
			SQL = "update dbo.temp_MCI_month_Count_min set Processed = 1 where Table_ID = " + tcounter;
			myconn.execSQL(SQL);
			
			++tcounter;
		}
		
		getCount();
		
		return tcounter;
	}
	
	public int execSequence_DX1(){
		
		SQL = "insert into dbo.AHCI_CLAIMS_MIN_DX_SEQUENCE (MCI_ID , Min_Date, DX_Sequence) select distinct a11.MCI_ID, a11.Min_date, a11.DX_Category from dbo.AHCI_CLAIMS_temp a11 join dbo.temp_MCI_month_Count_min a12 on (a11.MCI_ID = a12.mci_id and a11.Min_date = a12.min_date) where a12.DX_Count_Dist = 1";

		myconn.execSQL(SQL);
		
		SQL = "update dbo.temp_MCI_month_Count_min set Processed = 1 where DX_Count_Dist = 1";
		myconn.execSQL(SQL);
		
		getCount();
		
		return tcounter;
	}
	
	
	public void getCount(){
		
		SQL = "select count(*) count from dbo.AHCI_CLAIMS_MIN_DX_SEQUENCE";
		
		tcounter = myconn.execSQL_returnint(SQL);
		
	}
	
}
