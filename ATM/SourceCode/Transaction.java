package ATM;

import java.io.IOException;
import java.time.Clock;
import java.util.*;
import java.io.*;

class Transaction implements Deposit,Withdraw{ // a class for handling all transaction related activities and functions
    private DataBase Person; // stores User's information
    private String acc_num; // stores user's account number
   private Clock time; // stores time at the instant of a transaction


      
    void set_Person(DataBase Person) { // to set a particular user's info to a local variable  
    	this.Person = Person;
    }
    void set_Acc_num(String acc_num) { // to set a particular user's account to a local variable  
    	this.acc_num = acc_num;
    }
     public void  CalDen(double amnt) { // to mimic the cash tray of a real ATM, calculates amount of money(denomination wise) that to give to the user as requested
    	
    	 
    	 time = Clock.systemUTC();
    	Person.Transaction(acc_num, -(amnt), time.instant()); // stores the transaction (withdrawal) that has occurred now
    	
    	int thousand_Den = (int)(amnt / 1000);
    	int fivehundred_den = (int)( amnt/500);
    	int hundred_den = (int)( amnt/100);
    	int fifty_den = (int)( amnt/50);
    	int ten_den = (int)( amnt/10);
    	if(thousand_Den < Person.get_denval(Denominations.Thousand)) { // if amount requested is less than the # 1000 rupee notes in the atm 
    		DisplayScreen.Thousand_num(thousand_Den);
    		Person.set_denval(Denominations.Thousand, thousand_Den);
    		Person.set_Acc_balance_WithDraw(acc_num, (double)thousand_Den*1000);
    	    amnt = (int)amnt %1000;
    		fivehundred_den = (int)( amnt/500);
    		if(fivehundred_den < Person.get_denval(Denominations.Five_Hundred)) { // if remaining amount requested is less than the # 500 rupee notes in the atm
    			Person.set_denval(Denominations.Five_Hundred, fivehundred_den);
    			Person.set_Acc_balance_WithDraw(acc_num, (double)fivehundred_den*500);
    			DisplayScreen.FiveHundred_num(fivehundred_den);
    			amnt = amnt%500;
    			hundred_den = (int)( amnt/100);
    			if(hundred_den < Person.get_denval(Denominations.One_Hundred)) { // if remaining amount requested is less than the # 100 rupee notes in the atm
    			DisplayScreen.Hundred_num(hundred_den);
    			Person.set_Acc_balance_WithDraw(acc_num, (double)hundred_den*100);
    			Person.set_denval(Denominations.One_Hundred,hundred_den);
    			amnt = amnt%100;
        			 fifty_den = (int)( amnt/50);
        			if(fifty_den < Person.get_denval(Denominations.Fifty)) { // if remaining amount requested is less than the # 50 rupee notes in the atm
        				DisplayScreen.Fifty_num(fifty_den);
        				Person.set_Acc_balance_WithDraw(acc_num, (double)fifty_den*50);
        				Person.set_denval(Denominations.Fifty, fifty_den);
            			amnt = amnt%50;
            			ten_den = (int)( amnt/10);
            			DisplayScreen.Ten_num(ten_den);
        				Person.set_denval(Denominations.Ten, ten_den);
        				Person.set_Acc_balance_WithDraw(acc_num, (double)ten_den*10);
        			}
    			}
    		}
    	}
    	else if(fivehundred_den < Person.get_denval(Denominations.Five_Hundred)){ // if amount requested is less than the # 500 rupees note in the atm
    		 fivehundred_den = (int)( amnt/500);
    		if(fivehundred_den < Person.get_denval(Denominations.Five_Hundred)) {
    			Person.set_denval(Denominations.Five_Hundred, fivehundred_den);
    			Person.set_Acc_balance_WithDraw(acc_num, (double)fivehundred_den*500);
    			DisplayScreen.FiveHundred_num(fivehundred_den);
    			amnt = amnt%500;
    			hundred_den = (int)( amnt/100);
    			if(hundred_den < Person.get_denval(Denominations.One_Hundred)) {  // if remaining amount requested is less than the # 100 rupee notes in the atm
    			DisplayScreen.Hundred_num(hundred_den);
    			Person.set_denval(Denominations.One_Hundred,hundred_den);
    			Person.set_Acc_balance_WithDraw(acc_num, (double)hundred_den*100);
    			amnt = amnt%100;
        		fifty_den = (int)( amnt/50);
        			if(fifty_den < Person.get_denval(Denominations.Fifty)) { // if remaining amount requested is less than the # 50 rupee notes in the atm
        				DisplayScreen.Fifty_num(fifty_den);
        				Person.set_denval(Denominations.Fifty, fifty_den);
        				Person.set_Acc_balance_WithDraw(acc_num, (double)fifty_den*50);
        				amnt = amnt%50;
            			ten_den = (int)( amnt/10);
            			DisplayScreen.Ten_num(ten_den);
        				Person.set_denval(Denominations.Ten, ten_den);
        				Person.set_Acc_balance_WithDraw(acc_num, (double)ten_den*10);
        			}
    			}
    		}
    	}
    	else if(hundred_den < Person.get_denval(Denominations.One_Hundred)){ // if amount requested is less than the # 100 rupee note in the atm
    		 hundred_den = (int)( amnt/100);
			if(hundred_den < Person.get_denval(Denominations.One_Hundred)) {
			DisplayScreen.Hundred_num(hundred_den);
			Person.set_Acc_balance_WithDraw(acc_num, (double)hundred_den*100);
			Person.set_denval(Denominations.One_Hundred,hundred_den);
			amnt = amnt%100;
    			fifty_den = (int)( amnt/50);
    			if(fifty_den < Person.get_denval(Denominations.Fifty)) { // if remaining amount requested is less than the # 50 rupee notes in the atm
    				DisplayScreen.Fifty_num(fifty_den);
    				Person.set_denval(Denominations.Fifty, fifty_den);
    				Person.set_Acc_balance_WithDraw(acc_num, (double)fifty_den*50);
    				amnt = amnt%50;
        		    ten_den = (int)( amnt/10);
        			DisplayScreen.Ten_num(ten_den);
    				Person.set_denval(Denominations.Ten, ten_den);
    				Person.set_Acc_balance_WithDraw(acc_num, (double)ten_den*10);
    			}
			}
    	
    	}
    	
    
    }
     public void DepAmnt() { // handles deposited amount
    	 time = Clock.systemUTC();
    	 Scanner input = new Scanner(System.in);
    	 int total_sum = 0;

    	 // Asks # notes or coins of each denomination that the user going to deposit is handled by the below code  
    	 
    	 DisplayScreen.Thousand_deposit();
		  int add_amnt_Thousand = input.nextInt();
	    	 Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_Thousand)*1000);
		  total_sum +=  add_amnt_Thousand*1000;
		  Person.set_denval(Denominations.Thousand, add_amnt_Thousand);
		  DisplayScreen.FiveHundred_deposit();
		  int add_amnt_FiveHundred = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_FiveHundred)*500);
		  total_sum += add_amnt_FiveHundred*500;
		  Person.set_denval(Denominations.Five_Hundred, add_amnt_FiveHundred);
		  DisplayScreen.OneHundred_deposit();
		  int add_amnt_OneHundred = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_OneHundred)*100);
		  total_sum += add_amnt_OneHundred*100;
		  Person.set_denval(Denominations.One_Hundred, add_amnt_OneHundred);
		  DisplayScreen.Fifty_deposit();
		  int add_amnt_Fifty = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_Fifty)*50);
		  total_sum +=  add_amnt_Fifty*50;
		  Person.set_denval(Denominations.Fifty, add_amnt_Fifty);
		  DisplayScreen.Ten_deposit();
		  int add_amnt_Ten = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_Ten)*10);
		  total_sum +=  add_amnt_Ten*10;
		  Person.set_denval(Denominations.Ten, add_amnt_Ten);
		  DisplayScreen.OneRupee_coin_deposit();
		  int add_amnt_One = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_One)*1);
		  total_sum +=  add_amnt_One*1;
		  
		  DisplayScreen.TwoRupee_coin_deposit();
		  int add_amnt_Two = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_Two)*2);
		  total_sum +=  add_amnt_Two*2;
		  
		  DisplayScreen.FiveRupee_coin_deposit();
		  int add_amnt_Five = input.nextInt();
		  Person.set_Acc_balance_WithDraw(acc_num, -((double)add_amnt_Five)*5);
		  total_sum +=  add_amnt_Five*5;
		  
		  DisplayScreen.Total_Sum(total_sum);
		  Person.Transaction(acc_num, total_sum, time.instant()); // stores the transaction (deposit) that has occurred now
     }
    
}
