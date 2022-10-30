package ATM;

public class Admin {
   private enum UserName{
	   ABC_12345
   }
   private enum PassWord{
	   ABC_123
   }
  public boolean check_username(String User_name) {
	  if(User_name.compareToIgnoreCase(UserName.ABC_12345.name())==0)  return true;
	  else return false;
   }
   public boolean check_PassWord(String Pass_word) {
	   if(Pass_word.compareToIgnoreCase(PassWord.ABC_123.name())==0) return true;
	   else return false;
   }
}
