package Pass;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Pass2 {
	
	public static final String input = "/home/tanishashrotriya/Documents/SPOS/Assign2/src/input";
	public static final String fileMnem ="/home/tanishashrotriya/Documents/SPOS/Assign2/src/mnemonics";
	public static final String fileIC="/home/tanishashrotriya/Documents/SPOS/Assign2/src/IC";
	public static final String fileSYM="/home/tanishashrotriya/Documents/SPOS/Assign2/src/SYMTAB";
	public static final String fileLIT="/home/tanishashrotriya/Documents/SPOS/Assign2/src/LITTAB";
	public static final String filePOOL="/home/tanishashrotriya/Documents/SPOS/Assign2/src/POOLTAB";
	public static final String fileTC="/home/tanishashrotriya/Documents/SPOS/Assign2/src/TC";
	
	
	public static void main(String[] args) throws Exception {
		
			ArrayList<String> icValues=new ArrayList<String>();	
			createArray(fileIC,icValues);
			System.out.println(icValues.toString());
			

			//=========================================================================
			// reading from symbols file into arrayList of SYmbols class to wrap around object of Symbols
			
			ArrayList<String> symbols =new ArrayList<String>();
			createArray(fileSYM,symbols);
			System.out.println(symbols.toString());
			
			
			ArrayList<Symbols> SYM =new ArrayList<Symbols>();
			
			int addr=0;
			String name=null;
			
			for(int i=0;i<symbols.size();i++) {
			   
			   if(i%2==0) {
				   name=symbols.get(i);
			   }
			   
			   if(i%2==1) {
				   
				    addr=Integer.parseInt(symbols.get(i));
				    Symbols s = new Symbols();
				    s.addr=addr;
					s.name=name;
					SYM.add(s);
			   }
			}
		
			System.out.println(SYM.toString());
			
			//=================================================================================
			// Reading LITTAB into ArrayList to wrap around lIterals object
			
			ArrayList<String> literal =new ArrayList<String>();
			createArray(fileLIT,literal);
			System.out.println(literal.toString());
			
			
			ArrayList<Literal> LIT =new ArrayList<Literal>();
			 int num=0;
		
			for(int i=0;i<literal.size();i++) {
			   
			  
		   	   if(i%3==0) {
				   num=Integer.parseInt(literal.get(i));
			   }
			   if(i%3==1) {
				   name=literal.get(i);
			   }
			   
			   if(i%3==2) {
				   
				    addr=Integer.parseInt(literal.get(i));
				    Literal l = new Literal();
				    l.number=num;
				    l.addr=addr;
					l.value=name;
					LIT.add(l);
			   }
			}
		
			System.out.println(LIT.toString());
			
			//=============================================================
			for(int i=0;i<icValues.size();i++) {
				
				// conditions for Ad, DL instructions
				if(icValues.get(i).matches("\\(A.*")||icValues.get(i).matches("\\(D.*")) {
					if(icValues.get(i).equals("(AD,03)")) {
						int address=0;
						for(Symbols s : SYM) {

							if(icValues.get(i+1).split("\\+")[0].equals(s.name)) {
								address=s.addr;
								break;
							}
						}
						address=address+Integer.parseInt(icValues.get(i+1).split("\\+")[1]);
						write(fileTC," "+ address+"\n",true);
					}
					else if(icValues.get(i).equals("(AD,02)")||icValues.get(i).equals("(DL,02)")||icValues.get(i).equals("(AD,04)")) {
						if(i<icValues.size()-2) {
							i=i+2;
							
						}
						else {
							break;
						}
						write(fileTC,"\n",true);
					}
					else if(icValues.get(i).equals("(AD,01)")) {
						i=i+2;
					}
				}
				
				// conditions for numeric instruction -  includes literals after LTORG 
				// and addresses.
				if(icValues.get(i).matches("\\d*")) {
					write(fileTC," "+icValues.get(i),true);
					for(Literal l : LIT) {
						if(icValues.get(i).equals(l.value)) {
							write(fileTC,"\n",true);
							break;
						}
					}
				}
				
				// condition for IS instructions
				
				if(icValues.get(i).matches("\\(I.*")) {
					System.out.println(icValues.get(i));
					if(icValues.get(i).equals("(IS,00)")) {
						write(fileTC," + "+icValues.get(i).split(",")[1].replaceAll("\\)","")+" \n",true);
					}
					else {
						write(fileTC," + "+icValues.get(i).split(",")[1].replaceAll("\\)",""),true);
					}
				}
				
			   //Conditions for REGs
				if(icValues.get(i).matches("AREG.*")) {
					write(fileTC," 1",true);
				}
				if(icValues.get(i).matches("BREG.*")) {
					write(fileTC," 2",true);
				}
				if(icValues.get(i).matches("CREG.*")) {
					write(fileTC," 3",true);
				}
				if(icValues.get(i).matches("DREG.*")) {
					write(fileTC," 4",true);
				}
				
				// Condition for for Literals
				if(icValues.get(i).matches(".*\\(L.*")) {
					
					for(Literal l : LIT) {
						int check=Integer.parseInt(icValues.get(i).split("\\(L")[1].replaceAll("\\W",""));
						System.out.println(check);
						if(check==l.number) {
							write(fileTC," "+l.addr+"\n",true);	
							break;
						}
					}
				}
				
				// Condition for all other symbols
				else {
					for(Symbols s : SYM) {

						if(icValues.get(i).replaceAll("\\W","").matches(s.name)) {
							write(fileTC," "+s.addr+"\n",true);		
						}
					}
				}
				
				
				
				
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

	public static void createArray(String file,ArrayList<String> name) throws Exception {
		
	    	String fileData = readFileAsString(file);
			String[] data = fileData.split("\\s");
			
			for(String i : data) {
			   if(i.isEmpty()==false) {
				name.add(i);
			   }
			}
	}
}
