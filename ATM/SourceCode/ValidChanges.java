package ATM;

public interface ValidChanges {  // as changes allowed in user info is specific to bank, only below changes are allowed (eg: Change of Account Type is not allowed)
	    void set_Name(String Name);
     	void set_Phone_number(String Name); 
	    void set_address(String Name); 
	    void set_Acc_Num(String Name); 
	    void set_Acc_status(AccStatus Name); 
	    void set_Acc_balance(Double Amnt); 
	    void set_Acc_pwd(String Name); 
	    void set_AccType(AccType Acc_Type); 
	    String get_Name(); 
	    String get_Phone_number(); 
	    String get_address(); 
	    String get_Acc_Num(); 
	    AccStatus get_Acc_status(); 
	    Double get_Acc_balance(); 
	    String get_Acc_pwd(); 
}
