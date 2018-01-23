package Pass;

public class Mnemonics {

	String name;
	String opcode;
	String type;
	int len;
    
	
	public Mnemonics(){

		name=null;
		opcode=null;
		type=null;
		len=0;
		
	}
	
	public String toString(){
		return name + " " + opcode +" " + len + " " + type+ "\n";
		
	}
}
