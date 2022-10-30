package ATM;
import java.util.HashMap;
import java.io.*;
import java.util.*;
//import java.sql.*;
import java.time.*;
public class DataBase {   // Contains DataBase of all Customers
	
         protected static HashMap<Denominations,Integer> DenVal = new HashMap<Denominations,Integer>(); // Stores  Number of notes with Certain Denomination
		 protected static HashMap<String,User_Info> All_Info = new HashMap<String,User_Info>(); // Stores User Information
		 DataBase(){
		    	    try {
		    	    	File myObj = new File("input_ATM.txt");  // open or creates and then opens the input file which contains all the information of customers.
		    	    	Scanner input_reader = new Scanner(myObj);  
		    	    	int i =1;
		    	    	
		    	    	while(input_reader.hasNextLine()) {
		    	    		
		    	    		User_Info test = new User_Info();	
		    	    		String Acc_num = input_reader.nextLine();
		    	    
		    	    		test.set_Acc_Num(Acc_num);
		    	    		test.set_Acc_pwd(input_reader.nextLine());
		    	    		test.set_Name(input_reader.nextLine());
		    	    		test.set_Phone_number(input_reader.nextLine());
		    	    		test.set_Acc_balance(10000.00);
		    	    		if(i!=14 || i!=15) {
		    	    		test.set_Acc_status(AccStatus.INUSE);
		    	    		test.set_AccType(AccType.Current_Account);
		    	    		}
		    	    		else {
		    	    	    test.set_Acc_status(AccStatus.HIBERNATE);
		    	    	    test.set_AccType(AccType.Savings_Account);
		    	    		}
		    	    		All_Info.put(Acc_num, test);
		    	    		i++;
		    	    	}
		    	    }
		    	    catch(FileNotFoundException e) {
		    	    	System.out.println("IO Exception");
		    	    }
		    		

		    		// setting all notes' denominations with their quantity present in ATM.
		    		DenVal.put(Denominations.Thousand, 50);
		    		DenVal.put(Denominations.Five_Hundred, 30);
		    		DenVal.put(Denominations.One_Hundred, 100);
		    		DenVal.put(Denominations.Fifty, 100);
		    		DenVal.put(Denominations.Ten, 250);
		    	}
		 
		    public boolean check_acc(String Ac) { // method to check if given account is present or not
		    	return All_Info.containsKey(Ac);
		    }
		    
		    public boolean check_pwd(String Ac,String Pwd) { // method to check if given password is correct or not
		    	User_Info temp = All_Info.get(Ac);
		    	String this_pwd = temp.get_Acc_pwd();
		    	if(this_pwd.compareTo(Pwd)==0) return true;
		    	else return false;
		    }
		    
		    void set_acc_status(String Ac, AccStatus set) { // to change a person's account status
		    	User_Info temp = All_Info.get(Ac);
		    	temp.set_Acc_status(set);
		    }
		    AccStatus get_acc_status(String Ac) { // to get status of a person's account
		    	User_Info temp = All_Info.get(Ac);
		    	return temp.get_Acc_status();
		    }
		    double Acc_Bal(String Acc) { // to get account status 
		    	User_Info temp = All_Info.get(Acc);
		    	return temp.get_Acc_balance();
		    }
		    int get_denval(Denominations den) { // to get different denominations available
		    	return DenVal.get(den);
		    }
		    
		   void set_denval(Denominations den, int val) { // to set different denominations available
		    	int cr_val = DenVal.get(den);
			   DenVal.put(den,cr_val - val); 
		    }
		   void set_Acc_balance_WithDraw(String Ac,Double Amnt) { // to update account balance
				User_Info temp = All_Info.get(Ac);
			  double temp1 = temp.get_Acc_balance();
			   temp.set_Acc_balance(temp1 - Amnt);
		   }
		   
//		   User_Info create_new_userInfo() {
//			   User_Info obj = new User_Info();
//			   return obj;
//		   }
		   
		   String getName(String Ac) { // to get name of the user linked with his/her bank account
			   User_Info temp = All_Info.get(Ac);
			   return temp.get_Name();
		   }
		   void changeName(String Ac,String S) { // to update name of the user linked with his/her bank account
			   User_Info temp = All_Info.get(Ac);
			   temp.set_Name(S);
		   }
		   void changePh(String Ac,String S) { // to update phone number of the user linked with his/her bank account
			   User_Info temp = All_Info.get(Ac);
			   temp.set_Phone_number(S);
		   }
		   void changeAdd(String Ac,String S) { // to update address of the user linked with his/her bank account
			   User_Info temp = All_Info.get(Ac);
			   temp.set_address(S);
		   }
		   void Transaction(String Acc_num, double amnt, Instant time) { // to note down a transaction
			   User_Info temp = All_Info.get(Acc_num);
			   temp.set_Acc_Statement(amnt, time);
		   }
		   void Transaction_list(String Acc_num) { // to get transactions list
			   User_Info temp = All_Info.get(Acc_num);
			   temp.get_Acc_statement();
		   }
	



}
