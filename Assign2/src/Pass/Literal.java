package Pass;

public class Literal {

	int number;
	String value;
	int addr;
	
   
	public Literal(){

		number=0;
		value=null;
		addr=0;
		
	}
	
	public String toString(){
		return number + " " + value +" " + addr + "\n";
		
	}
}
