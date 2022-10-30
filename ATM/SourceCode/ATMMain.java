package ATM;
import java.io.IOException;
import java.util.*;
class ATMMain{
	
	public static void main(String args[]) throws IOException{
	boolean condition_Atm = true; // a flag for run or stop ATM working
    String Acc_num = " ";  // stores Account number of customer  
	String Pwd; // stores Account password of customer 
	AccStatus status = AccStatus.FALSE; // stores Account status
	 Scanner input = new Scanner(System.in); // Scanner object 
	 DataBase check = new DataBase(); // an object to incorporate all details of customers
	  Transaction Obj2 = new Transaction();  // an object to utilize all transaction related operations
	  DisplayScreen Obj = new DisplayScreen(); // an object to display required information and notes
	  
	  
	  int Opt = input.nextInt(); // to store whether its admin or customer
	  if(Opt == 1) {// Admin Interface
		  boolean status_admin = false;
		  while(status_admin == false) {
			  Obj.Admin_Message();
			  String User_name = input.next();
			  if(User_name.compareToIgnoreCase("continue")==0) { // if admin wants to come out of the admin interface while he/she was unable to login
				break;
			  }
			  Admin test = new Admin();
			  if(test.check_username(User_name) == false) {
				  Obj.Admin_wrong_Message(User_name);
				  continue;
			  }
			  String Password = input.next();
			 
			  if(test.check_username(User_name)   && test.check_PassWord(Password)){
				  status_admin = true;
				  boolean admin_work = true;
				  while(admin_work) {
					  Obj.Admin_options();
					  int opt = input.nextInt();
					  switch(opt) { 
					  case 1: // Make ATM out of service 
						  condition_Atm = false;
						  Obj.make_ATM_out_of_service();
						  break;
					  case 2: // to open cash tray
						  Obj.Open_CashTray();
						  break;
					  case 3: // to open customer interface
						  admin_work = false;
						  break;
					  }
				  }
			  }
			  else {
				  Obj.Admin_wrong_Message(); // shoots out wrong username or password message
			  }
		  }
	  }
	 while(condition_Atm == true) { // working of ATM
		 Obj.Cover_screen();
    	 // An object for displaying ATM working  
	       boolean acc_check = false;  // a flag to check correct account number
	       int counter_acc = 1; // variable used to count # incorrect entries of account number
	      while(acc_check == false && counter_acc<=5) {
	    	  Acc_num = input.next(); // inputs Account number from user
	       if(check.check_acc(Acc_num)) /*checks if Account number is correct or not */{
	    	   Obj.greeting(Acc_num, check); // greets customer with his/her name
	    	    acc_check = true; // changes flags' status
	    	    status = check.get_acc_status(Acc_num); // stores customer's account status
	    	   continue;  
	       }
	       else {
	    	  Obj.InvalidAccNum();
	    	   counter_acc++; // # tries gets increased
	       }
	      }
	      if(counter_acc > 5) { // more than 5 tries asks customer to contact customer care
	    	 Obj.Acc_Prob();
	    	 continue;
	      }
	      if(acc_check == true && status != AccStatus.AUTO_BLOCKED) { // validates password
	    	  int counter_pwd = 1;  //  variable used to count # incorrect entries of passcode
	    	  boolean pwd_check = false; //  flag to check correct passcode
	    	  //boolean change_pwd = false;
	    	  while(pwd_check == false && counter_pwd <=5){ 
	    		  Obj.Ask_Pwd();
	    		  Pwd = input.next();  // inputs password from user
	    		  if(check.check_pwd(Acc_num, Pwd)== true) // validates password
	    		  {
	    			  Obj2.set_Acc_num(Acc_num);
	    			  Obj2.set_Person(check);
	    			  pwd_check = true; // changes flags' status
	    			  continue;
	    		  }
	    		  else{
	    			 Obj.InvalidPwd();
	    			  counter_pwd++; //// # tries gets increased
	    		  }
	    	  }
	    	  if(counter_pwd>5) {  // more than 5 tries blocks customer account
	    		 Obj.Pwd_Prob();
	    	      check.set_acc_status(Acc_num,AccStatus.AUTO_BLOCKED);
	    	      continue;
	    	  }
	    	  
	    	  if(check.get_acc_status(Acc_num)==AccStatus.HIBERNATE) { // asks customer to set his account live if his/her account is not used for long time(hibernate)
	    		Obj.Acc_Hib();
	    		  check.set_acc_status(Acc_num,AccStatus.INUSE);
	    	  }
	    	  
	    	  if(check.get_acc_status(Acc_num)==AccStatus.INUSE) {
	    		  status = AccStatus.INUSE; // sets local status variable to inuse type.
	    	  }
	    	  
	    	  
	      }
	      if(check.get_acc_status(Acc_num)==AccStatus.AUTO_BLOCKED) { // if a person tries to access blocked account after his account got blocked
    		  Obj.Pwd_Prob();
    	  }
	      
	      if(status == AccStatus.INUSE) {
	    	  int mode = 0;
	    	  boolean status_now = true; // flag to show menu continuously
	    	  while(status_now) {
	    	  Obj.Acc_Modes(); // displays different activities that can be done with his/her account at ATM 
	    	  mode = input.nextInt(); // stores corresponding opted mode by user
	    	  
	    	  
	    	  switch(mode) {
	    	  case 1: // to display account balance
	    		  Obj.dispBal(check,Acc_num);
	    		  Obj.menu_select();
	    		  int temp = input.nextInt(); // to ask for to exit or go back to previous menu
	    		  if(temp == 1) { 
	    			 status_now = false;
	    		  }
	    		  else  status_now = true;
	    		  break;
	    			  
	    	  case 2: // to withdraw amount
	    		 Obj.WithDraw();
	    		 double amnt = input.nextDouble();
	    		 if(amnt > Transaction.max_limit) { // max limit per transaction to withdraw is 20,000.00, if he asks more then it asks customer to ask less.
	    			 Obj.entlessmax();
	    			 continue;
	    		 }
	    		 else {
	    			 if(check.Acc_Bal(Acc_num)<amnt) { // if he asks more than his account balance then also it asks customer to ask less.
	    				 Obj.entless();
	    				 continue;
	    			 }
	    			 else {
	    				 
	    				 Obj2.CalDen(amnt);
	    				 
	    				 Obj.slip_question(); // asks for the need of receipt slip
	    				 int i = input.nextInt();
	    				 if(i==1) Obj.slip();
	    				 
	    			 }
	    		 }
	    		 
	    		 
	    		 Obj.menu_select();  // to ask for to exit or go back to previous menu
	    		 int temp1 = input.nextInt();
	    		  if(temp1 == 1) {
	    			 status_now = false;
	    		  }
	    		  else  status_now = true;
	    		 break;
	    		 
	    		 
	    	  case 3: // to deposit amount
	    	
	    		  Obj2.DepAmnt();
	    		  Obj.slip_question();
 				 int i1 = input.nextInt();
 				 if(i1==1) Obj.slip();
 				 
 				 
	    		  Obj.menu_select();  // to ask for to exit or go back to previous menu
	    		  int temp_11 = 0;
	    		  temp_11 = input.nextInt();
	    		  if(temp_11 == 1) {
	    			 status_now = false;
	    		  }
	    		  else  status_now = true;
	    		  break;
	    		  
	    		  
	    	  case 4: // to Edit personal details
	    		  Obj.EditDetails();
	    		  int op = input.nextInt();
	    		  
	    		  
	    		  switch(op) {
	    		  case 1: // for Name change 
	    			  Obj.EnterNamechange();
	    			  String Change_name = input.next();
	    			  check.changeName(Acc_num, Change_name);
	    			  Obj.Namechange();
	    			  break;
	    		  case 2:// for Phone number change
	    			  Obj.EnterPhone_number_change();
	    			  String Change_Ph = input.next();
	    			  check.changePh(Acc_num, Change_Ph);
	    			  Obj.Phone_number_change();
	    			  break;
	    		  case 3: // for Address change
	    			  Obj.EnterAddress_change();
	    			  String Change_Add = input.next();
	    			  check.changeAdd(Acc_num, Change_Add);
	    			  Obj.Address_change();
	    			  break;	  
	    		  }
	    		  
	    		  
	    		  Obj.menu_select();  // to ask for to exit or go back to previous menu
	    		  int temp_12 = 0;
	    		  temp_12 = input.nextInt();
	    		  if(temp_12 == 1) {
	    			 status_now = false;
	    		  }
	    		  else  status_now = true;
	    		  break;
	    		  
	    		  
	    		  
	    	  case 5: // summarizes all transactions
	    		  check.Transaction_list(Acc_num);
	    		  Obj.slip_question();
 				 int i2 = input.nextInt();
 				 if(i2==1) Obj.slip();
 				 
 				 
	    		  Obj.menu_select();  // to ask for to exit or go back to previous menu
	    		  int temp_13 = 0;
	    		  temp_13 = input.nextInt();
	    		  if(temp_13 == 1) {
	    			 status_now = false;
	    		  }
	    		  else  status_now = true;
	    		  break;
	    	  }
	      }
	      }
	      
      }
	 if(condition_Atm == false) {
		 Obj.Out_of_Service();
	 }
   
}
}
