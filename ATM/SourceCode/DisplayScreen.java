package ATM;
import java.util.*;
import java.time.*;
public class DisplayScreen extends DataBase{ // a class for storing all display related ATM activities and functions
	
	   
	    DisplayScreen(){
	        System.out.println("Are you an admin or customer ?"+'\n'+"1.Admin"+'\n'+"2.customer");
	    }
	      
	  void Admin_Message() {
		  System.out.println("Please enter username and password  else continue to customer interface by typing continue ");
	  }
	  void Admin_wrong_Message() {
		  System.out.println("Please enter correct username and password else continue to customer interface by typing continue ");
	  }
	  void Admin_wrong_Message(String Username) {
		  System.out.println("Please enter correct username else continue to customer interface by typing continue "+'\n'+"Your Entered UserName "+Username + " is incorrect");
	  }
	  
	  void Admin_options() {
		  System.out.println("Hello Admin"+'\n'+"Please choose one of the options"+'\n'+"1.Make ATM out of service" +'\n'+"2.Open Cash Tray"+'\n'+"3.Open Customer Interface ");
	  }
	  void Open_CashTray() {
		  System.out.println("Cash Tray is Opened, Please close it as soon as possible"+'\n');
	  }
	  void Out_of_Service() {
		  System.out.println("ATM is out of service, please visit later");
	  }
	  void Cover_screen() {
		  System.out.println("Hello USER!! Welcome to ABC Bank ATM "+ '\n'+"Please enter your five-digit bank account number");
	  }
	  void InvalidAccNum() {
		  System.out.println("Incorrect Account Number... Please enter a valid Account Number");
	  }
	  void Acc_Prob() {
		  System.out.println("Please visit the nearest ABC Bank to ask your queires regarding your Account Number");
	  }
	  void greeting(String Ac, DataBase Person) {
		  System.out.println("Welcome "+ Person.getName(Ac)+" to ABC ATM");
	  }
	  
	  void Ask_Pwd() {
		  System.out.println("Please enter your PassWord....");
	  }
	  void InvalidPwd() {
		  System.out.println("Incorrect Password... Please enter correct Password");
	  }
	  
	  void Pwd_Prob() {
		  System.out.println("Your Account is Blocked... Please visit the nearest ABC Bank Branch to get your Account In use.");
	  }
	  void Acc_Hib() {
		  System.out.println("Your Account is not used for a long time; please reach out to ABC Bank Customer Care to set your account to LIVE");
	  }
	  void Acc_Modes() {
		  System.out.println("Please select one of the options "+ '\n'+ "1.View My Balance"+'\n'+"2.WithDrawal amount"+'\n'+"3.Deposit Funds" + '\n'+ "4.Edit Profile"+'\n'+"5.View all Transactions");
	  }
	  void dispBal(DataBase check,String Acc_num) {
		  System.out.println("Your Account Balance is :"+ check.Acc_Bal(Acc_num));
//		  System.out.println("Your Account Balance is :"+ Acc_num+'\n'+"Press 1 to quit or Press 2 to go back to previous menu");
	  }
	  void WithDraw() {
		  System.out.println("The following are the available denominations of cash FYI");
		  for(Denominations d : Denominations.values()) {
			  System.out.println("              "+d);
		  }
		  System.out.println("Please enter the amount you want to withdraw");
		  System.out.println("The max limit per transaction is 20,000"+'\n'+"Please withdraw a minimum amount of 10 rupees");
//		  System.out.println("Your Account Balance is :"+ Acc_num+'\n'+"Press 1 to quit or Press 2 to go back to previous menu");
	  }
	  void entlessmax() {
		  System.out.println("Please enter lesser amount, You've asked more than 20,000.00");
	  }
	  void entless() {
		  System.out.println("Please enter lesser amount, you've crossed your account balance");
	  }
	  void menu_select() {
		  System.out.println("Press 1 to quit or Press 2 to go back to previous menu");
	  }
	  
	   static void Thousand_num(int val) {
		  if(val!=0)
		  System.out.println("Please take "+ val+ " Thousand valued notes ");
	  }
	   static void FiveHundred_num(int val) {
		  if(val!=0)
		  System.out.println("Please take "+ val+ " FiveHundred valued notes ");
	  }
	   static void Hundred_num(int val) {
		  if(val!=0)
		  System.out.println("Please take "+ val+ " One Hundred notes ");
	  }
	   static void Fifty_num(int val) {
		  if(val!=0)
		  System.out.println("Please take "+ val+ " Fifty  notes ");
	  }
	   static void Ten_num(int val) {
		  if(val!=0)
		  System.out.println("Please take "+ val+ " Ten notes ");
	  }
	  void Out_of_cash() {
		  System.out.println("ATM is out of cash");
	  }
	  static void  Thousand_deposit() {
		  System.out.println("No of thousand Notes: ");
	  }
	  static void FiveHundred_deposit() {
		  System.out.println("No of Five HundredNotes: ");
	  }
	  static void OneHundred_deposit() {
		  System.out.println("No of One Hundred Notes: ");
	  }
	  static void Fifty_deposit() {
		  System.out.println("No of Fifty Notes: ");
	  }
	  static void Ten_deposit() {
		  System.out.println("No of Ten Notes: ");
	  }
	  static void OneRupee_coin_deposit() {
		  System.out.println("No of One Rupee coins: ");
	  }
	  static void TwoRupee_coin_deposit() {
		  System.out.println("No of Two Rupee coins: ");
	  }
	  static void FiveRupee_coin_deposit() {
		  System.out.println("No of Five Rupee coins: ");
	  }
	  static void Total_Sum(int total_sum) {
		  System.out.println("Total_Amount Deposited: "+total_sum);
	  }
	  void EditDetails() {
		  System.out.println("What do you want to Edit ? "+'\n'+"1.Name"+'\n'+"2.Phone Number"+'\n'+"3.Address");
	  }
	  void Namechange() {
		  System.out.println("Name changed successfully");
	  }
	  void Phone_number_change() {
		  System.out.println("Phone number changed successfully");
	  }
	  void Address_change() {
		  System.out.println("Address changed successfully");
	  }
	  
	  void EnterNamechange() {
		  System.out.println("Please enter the new Name ");
	  }
	  void EnterPhone_number_change() {
		  System.out.println("Please enter the new Phone number");
	  }
	  void EnterAddress_change() {
		  System.out.println("Please enter the new Address ");
	  }
	 public static void Transaction_statement(double amnt, Instant time){
		  if(amnt<0)
		  System.out.println("Amount Withdrawn on "+time+" is "+-(amnt));
		  else
			  System.out.println("Amount Deposited on "+time+" is "+amnt);  
	  }
	 public void slip() {
		 System.out.println("Please take slip");
	 }
	 public void slip_question() {
		 System.out.println("Do you need a slip ? "+"Enter 1 if yes else Enter 0");
	 }
	 public void make_ATM_out_of_service() {
		 System.out.println("ATM is made out of service"+'\n');
	 }
	 public void Cash_Tray() {
		 System.out.println("CashTray is opened");
	 }
}
