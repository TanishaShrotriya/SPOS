package Pass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Pass1 {

	public static final String input = "/home/tanishashrotriya/Documents/3476/Assign1/src/input";
	public static final String fileMnem ="/home/tanishashrotriya/Documents/3476/Assign1/src/mnemonics";
	public static final String fileIC="/home/tanishashrotriya/Documents/3476/Assign1/src/IC";
	public static final String fileSYM="/home/tanishashrotriya/Documents/3476/Assign1/src/SYMTAB";
	public static final String fileLIT="/home/tanishashrotriya/Documents/3476/Assign1/src/LITTAB";
	public static final String filePOOL="/home/tanishashrotriya/Documents/3476/Assign1/src/POOLTAB";
	
	public static void main(String[] args) throws Exception {
		
	 /*reading instructions from code */
		String data = readFileAsString(input);
		String[] s = data.split("[\\s,]");
		//split based on space
		 
		ArrayList<String> str = new ArrayList<String>();
		for(String e : s) {
		    if(!e.isEmpty()) {    
			 str.add(e);
		    }
		    
	    }
		
		System.out.println(str.toString());
		
		//read the mnemonics file
		
	    String mnemo = readFileAsString(fileMnem);
	    String[] mnem = mnemo.split("\\s");
	    
	    ArrayList<String> mnemonics = new ArrayList<String>();
		
	    for(String e : mnem){
		    
	    	if(!e.isEmpty()){
		    	 mnemonics.add(e);
		    }
		    
	    }
		
		System.out.println(mnemonics.toString());
		
		// wrapping the created ref array around object of class mnemonics
		
		Mnemonics m;
		ArrayList<Mnemonics> ref = new ArrayList<Mnemonics>();	    
		String n=null,op=null,t=null;
	    int l=0;
	    for(int i=0;i<mnemonics.size();i++) {
			if(i%4==0) {
				n=mnemonics.get(i);
				
			}
			else if(i%4==1) {
				t=mnemonics.get(i);
			
			}
			else if(i%4==2) {
				op=mnemonics.get(i);
				
			}
			else if(i%4==3) {
				l=Integer.parseInt(mnemonics.get(i));
				m=new Mnemonics();
				m.name=n;
				m.len=l;
				m.opcode=op;
				m.type=t;
				ref.add(m);
			
			}
		}
	
	    System.out.println(ref.toString());
	//Variables for pass 1 ==============================================================================		
	    String opIn=null;
	    int lc=0;
	    int litc=1; //counter for literals 
        int litFlag=0; //flag for identifying literal pool 
        int clearArray=0;
	    int flag=0; // used for the final condition for finding symbols
	    boolean litAtEnd=false;
	    ArrayList<Literal> litArray = new ArrayList<Literal>();
	    ArrayList<Symbols> SymArray = new ArrayList<Symbols>();
        
	//=====================================================================================================	
	    //stage one of parse one - compare with regs,start and so on..
	    
	    
	    for(int i=0;i<str.size();i++) {
		    flag=0;
		  
		    // check if operand belongs to set of instructions by cross verifying against ref arraylist
	        for(int j=0;j<ref.size();j++) {
	    		
	    	    String one=str.get(i);
	    	    String two=ref.get(j).name;
	    	    
	    		if(one.equals(two)){
	    			
	    			litFlag=0; // to reset pool flag when first instruction after last literal in pool is seen
	    			
	    			if(clearArray==1) {
	    				
	    				write(fileLIT," " +litArray.toString().replaceAll("[\\[,\\]]", ""),true); 
		    			litArray.clear();
		    			clearArray=0;
	    			}
	    			
	    			if(lc!=0) {
	    				opIn=lc+" ("+ref.get(j).type+","+ref.get(j).opcode+")";
	    				if(ref.get(j).name.equals("STOP")) {
	    					opIn=lc+" ("+ref.get(j).type+","+ref.get(j).opcode+")\n";
	    				}
	    			    lc=lc+ref.get(j).len;
	    			}
	    			
	    			else {	
		    			opIn="("+ref.get(j).type+","+ref.get(j).opcode+")";
		    		}
	    			
	    			write(fileIC,opIn,true);
	    			
	    			flag=1;
	    			
	    			//writing specific conditions for special instructions like LTORG
	    			if(two.equals("LTORG") || two.equals("END")) {
	    				litFlag=1;
	    				
	    				int v=litArray.get(0).number;
	    				// updating pool file
	    				write(filePOOL,"#"+v+"\n",true);
	    				
	    			}
	    			
	    			// calculations for ORIGIN instruction
	    			if(str.get(i).equals("ORIGIN")) {
	    				
	    	    	    for(int k=0;k<SymArray.size();k++) {
	    	    	      
	    	    	    	//if ORIGIN is encountered then the next value in str is checked across SymArray
	    	    	    	
	    	    	    	if(SymArray.get(k).name.equals(str.get(i+1).replaceAll("\\W\\d*",""))) {
	    				         lc=SymArray.get(k).addr+Integer.parseInt(str.get(i+1).split("\\+")[1]);
	    	    	    	}
	    	    	    	
	    	    	    	//once found we overwrite LC with address of label+specified number
	    	    	    }
	    			}
	    			
	    			//for declaration statements 
	    			if(str.get(i).equals("DS")) {
	    				lc=lc+Integer.parseInt(str.get(i+1));
	    			}
	    			
	    			if(str.get(i).equals("END")) {
	    				litAtEnd=true;
	    			}
	    			break;			
	    		}
	    	}
            //check if operand is numeric 
	    	if(str.get(i).matches("\\d*")){
		   			
	    		opIn=" "+str.get(i)+"\n";
	    		
	    		if(!str.get(i-1).equals("DS")) {
	    			
		   			lc=Integer.parseInt(str.get(i));
	    		}
	   			flag=1;
	   			write(fileIC,opIn,true);

	    	}
	    	
	    	//check if operand matches a register
	    	if(str.get(i).matches(".REG")){
		   			
	    		flag=1;	
	    		opIn=" "+str.get(i)+",";
		   	    write(fileIC,opIn,true);
		    					
			}
            
	    	// check if operand matches a literal
	    	if(str.get(i).matches("=.*")){
		   			
	    		flag=1;
	    		if(litFlag==0) {
	    			//no LTORG condition put yet
	    			if(litArray.isEmpty()==true) {
	    				Literal e = new Literal();
						e.number=litc;
						e.value=str.get(i).replaceAll("\\W","");
						litArray.add(e);
		      		   	opIn="(L,"+litc+")\n";
		      		    litc++;
				   	    write(fileIC,opIn,true);
	    			}
	    			else {
	    				
	    				boolean repeat=false;                                                               
	    				for(Literal lit : litArray) {
	    					if(lit.value.equals(str.get(i).replaceAll("\\W",""))) {
							   repeat=true;
							   break;
	    					}
	    				}
	    				if(repeat==false) {
	    					Literal e = new Literal();
							e.number=litc;
							e.value=str.get(i).replaceAll("\\W","");
							litArray.add(e);
							litc++;
			      		   	opIn="(L,"+litc+")\n";
					   	    write(fileIC,opIn,true);
					   	    
	    				}
		    		}
			   	      // all the literals before the literal pool are counted
	    		}
	    		
	    		else {
	    			//copy literal as is to IC file when LTORG is detected
	    		    if(litFlag==2) {
	    				opIn=lc+" "+str.get(i).replaceAll("\\W","")+"\n";
		    			write(fileIC,opIn,true);
		    			
	    			}
	    		
	    		    else if(litFlag==1) {
		    			
	    				opIn=" "+str.get(i).replaceAll("\\W","")+"\n";
		    			write(fileIC,opIn,true);	
	    			    litFlag=2; 
	    			    //set flag for all literals after first
	    			    // As we have to also write the address for second literal onwards
	    			}
	    		    
	    		    // update the addresses when LTORG or END is seen
	    		    for(Literal lit : litArray) {
	    				
	    		    	if(str.get(i).replaceAll("\\W","").equals(lit.value) && lit.addr==0) {	
	    		    		lit.addr=lc;
	    					break;
	    				}
	    			}
	    		    
	    			lc++;
	    			clearArray=1;
	    			//increment lc for next literal in pool/instruction
	    		
	       		}
	    					
		    }
	 
	    	//check if operand is something else, that is variable
	    	else if(flag==0) {
	    		
	    		// Reading SYMTAB file to compare already existing symbols
	    		String three = readFileAsString(fileSYM);
	    	    String[] trois=null;
	    	    trois=three.split("\\s");
	    	    
	    	    int f=0;
	    	    // to avoid repeats
	    	    for(String s1 : trois ){
	    	    	
	    	    	// get the newest symbol, and strip of all digits and non-word characters
	    	    	
	    	    	String s2=str.get(i).replaceAll("[\\W\\d*]","");
	    	    	
	    	    	if(s1.equals(s2)){
	    	    			
	    	    		f=1;
	    	    	
	    	    		//writing the variables which are repeated only to IC
			    		if(!str.get(i).matches(".*:")){
			    			 //if DS instruction found after then write ADDR
			    			if(str.get(i+1).equals("DS")) {
			    			
			    				for(Symbols Syms:SymArray) {
				    				if(Syms.name.equals(str.get(i))) {
				    					Syms.addr=lc;
				    				}
				    			}
					   		    write(fileSYM,SymArray.toString().replaceAll("[\\[,\\]]", ""),false);
			    			}
			    			
			    			// if found op1 EQU op2 and so - 
			    			
			    			else if(str.get(i+1).equals("EQU")) {
			    				int addr=0;
			    				//Copying op2.ADDR to ADDR
			    				for(Symbols Syms:SymArray) {
				    				if(Syms.name.equals(str.get(i+2))) {
				    				    addr=Syms.addr;
				    			        break;
				    				}
				    			}
			    				//finding in SymArray op1 and writing ADDR value
			    				for(Symbols Syms:SymArray) {
				    				if(Syms.name.equals(str.get(i))) {
				    				    Syms.addr=addr;
				    				    break;
				    				}
				    			
				    			}
			    				
					   		    write(fileSYM,SymArray.toString().replaceAll("[\\[,\\]]", ""),false);
			    			}
			    			else {
				    			opIn=" "+str.get(i).replaceAll(":", "")+"\n";
					   		    write(fileIC,opIn,true);
			    			}
				   		}
			    		// for finding duplicate LABEL error
			    		if(str.get(i).matches(".*:")) {
			    			
			    			boolean multOcc =false;
			    			for(Symbols Syms : SymArray) {
			    				
			    				if(Syms.name.equals(str.get(i).replaceAll("\\W",""))) {
			    					// this means the label is seen for the second time
			    					if(Syms.addr!=0) {
			    						multOcc=true;
			    						break;
			    					}
			    				}
			    			}
			    			
			    			if(multOcc==false) {
			    		   		//writing the label encountered after it has already
			    				//been registered but without the address	
					    		
			    				for(Symbols Sym : SymArray) {
					    			if(Sym.name.equals(s1)) {
					    				Sym.addr=lc;
					   				}
					   			}
						   		write(fileSYM,SymArray.toString().replaceAll("[\\[,\\]]", ""),false);
			    			}
			    			else {
			    				System.out.println("ERROR : DUPLICATE USE OF LABEL [" + str.get(i)+"]");
			    			}
			    		}
	    	    		break;
	    	    	}
	    	    }
	    	    
	    	    // symbol seen for the first time 
                if(f!=1){
                	
		    		// for labels, written only to SYMTAB, labels encountered for the first time
		    		
		    		if(str.get(i).matches(".*:")){
		    			
	    				// check variable across reference file of mnemonics
	    				boolean error = false;
	    				for (Mnemonics Ref : ref) {
	    					if(str.get(i).replaceAll(":", "").equals(Ref.name)) {
	    						error=true;
	    						break;
	    					}
	    				}
	    				
	    				if(error==false) {
		    		
	    					opIn=" "+str.get(i).replaceAll(":", "")+" "+ lc + "\n";
			    			//since : implies we have found a label we can also write the value of address
			    		    Symbols sym=new Symbols();
				   		    sym.name=str.get(i).replaceAll(":", "");
				   		    sym.addr=lc;
				   		    SymArray.add(sym);
				   		   
				   		    write(fileSYM,SymArray.toString().replaceAll("[\\[,\\]]", ""),false);
		    			
	    				}
	    				else {
	    					System.out.println("ERROR : MNEMONIC ["+str.get(i)+"] USED AS A LABEL!!");
	    				}
		      		}
		    		
		    		// writing variables for the first time to IC as well as SYMTAB
		    		
		    		else{
		    				opIn=" "+str.get(i)+"\n";
			    			
			    			Symbols sym=new Symbols();
				   		    sym.name=str.get(i);
				   		    SymArray.add(sym);
			    			
				   		    write(fileSYM,SymArray.toString().replaceAll("[\\[,\\]]", " "),false);
			    			write(fileIC,opIn,true);
		    	
		    		}
                    f=0;
                }    
            }
		}

	    
	    SymArray.get(0).name=" "+SymArray.get(0).name;
	    write(fileSYM,SymArray.toString().replaceAll("[\\[,\\]]", " "),false);
	    
	    if(litAtEnd==true) {			
			write(fileLIT," " +litArray.toString().replaceAll("[\\[,\\]]", ""),true); 
			litArray.clear();
			clearArray=0;
	
	    }
	     
	    
	    //================================================================================================
	    
	    //Handling errors
	    
	    ///=================================================
	     boolean addrFound=false;
	     boolean opcodeFound=false;
	     boolean regFound=false;
	     boolean mnemPresent=true; //assuming no symbols are undefined mnemonics
	     boolean symOkay=true;//assuming all symbols to be correct initially
	     //=============
	    String IC = readFileAsString(fileIC);
		String[] ic = IC.split("\\s");
		
		ArrayList<String> icValues=new ArrayList<String>();
		
		for(String i : ic) {
			icValues.add(i);
		}
	
		for(int x=0;x<icValues.size();x++) {
			
		
			if(icValues.get(x).matches("\\(.*\\)")) {
				opcodeFound=true;
			}
			if(icValues.get(x).matches(".*REG")) {
				regFound=true;
			}
			else {
				String errValue=null;
				// if symbol found then check for - 
				for(Symbols sym : SymArray) {
					
					if(sym.name.equals(icValues.get(x))) {
						// ADDR op1, SYM 
						if(icValues.get(x-1).matches(".*REG") || icValues.get(x-1).matches(".*")) {
							if(icValues.get(x-2).matches("\\(.*\\)")) {
								mnemPresent=false;
								// implies mnemonic is present
								
							}
						}
						// ADDR SYM, op2 
						else if(icValues.get(x+1).matches(".*REG") || icValues.get(x+1).matches(".*")) {
							if(icValues.get(x-1).matches("\\(.*\\)")) {
								mnemPresent=false;
							    System.out.println("here for 2"+icValues.get(x));
							}
						}
						// ADDR SYM 
						else if(icValues.get(x-1).matches("\\(.*\\)")) {
							mnemPresent=false;
							System.out.println("here for 3"+icValues.get(x));
						}
						
						if(sym.addr==0) {
							symOkay=false;
							
						}
						errValue=sym.name;
						break;
					}
				}
				
				if(mnemPresent==true && symOkay==false) {
					System.out.println("ERROR : MNEMONIC "+errValue+" NOT DEFINED");
					symOkay=true;
				}
				if(mnemPresent==false && symOkay==false) {
					System.out.println("ERROR : SYMBOL "+errValue+ " NOT DEFINED");
					symOkay=true;
					mnemPresent=true;
				}
			}

			
			if(opcodeFound=true) {
				
				if(icValues.get(x).matches("\\(.*\\)\\d+")) {
					String errValue=null;
					String opcode=icValues.get(x).split("[,)]")[1].replaceAll("\\W","");
					String instrType=icValues.get(x).split("[,)]")[0].replaceAll("\\W","");
					
					for(Mnemonics m1 : ref) {
						if(m1.opcode.equals(opcode)&&m1.type.equals(instrType)) {
							errValue=m1.name;
							break;
						}
					}
					System.out.println("ERROR : INCOMPLETE INSTRUCTION!! ["+errValue+"]");
				}
				opcodeFound=false;
				
			}
			
		}

		int last = icValues.size()-1;
		if(!icValues.get(last).equals("(AD,02)")) {
			System.out.println("ERROR : MISSING END OF FILE!!");
		}
	}

	public static String readFileAsString(String fileName)throws Exception
	{
	 String data = "";
	 data = new String(Files.readAllBytes(Paths.get(fileName)));
	 return data;
	}
	
	public static void write(String path,String update,boolean val){
		try {
			FileWriter file = new FileWriter(path,val);
			file.write(update);
			file.close();
		} catch (IOException e) {
	
			e.printStackTrace();
		}

	}

}
