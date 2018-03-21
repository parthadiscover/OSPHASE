/*
 *  Global Variables
 *  1. jobId - It is in two digit HEX form and it gives the job id of the input loader format
 *  2. loadAddr - It is in two digit HEX form and it gives the base address of the input loader format
 *  3. initialPC - It is in two digit HEX  form and it shows the initial program counter from where loader input starts executing
 *  4. size - It is in two digit HEX form and it is the length of the job
 *  5. trace - It is in one digit HEX form and if it is enable it will create trace_file, where PC, IR, BR, TOS before and after, S[TOS] before and after is 
 *  
 *  -> The words from loader called to Memory.
 *  -> Converting it from HEX to Binary.
 *  -> Storing Binary converted words in MainMemory.
 *  
 *  
 *  
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class MEMORY {
	
	
	public static char MainMemory[][] = new char[256][16];
	

	
	
	public static int __memoryLoc = 0;
	public static byte jobId;
	public static byte loadAddr;
	public static byte initialPc;
	private static byte size;
	public static byte trace;
	
	//For reading disk address
	//For writing actual Memory address
	
	public static String memoryProc(int rw,int pos,String var){
		if(pos>255){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER111);
		}
		if(rw==1){		
			if(var.length()!=16){
				ERROR_HANDLER.handle(ERROR_HANDLER.ER101);
			}
			
			write(pos,var);
		/*	
			for(int i=0;i<16;i=i+4){
				String word = var.substring(i,i+4);
				write(ea,word);
				ea++;
				__memoryLoc=ea;
			}*/
		}else{
			var = read(pos);
		}
		return var;
	}
	
	//****** Adding words to MainMemory (write) ******//


	private static void write(int pos,String bin){
		
	//	String bin = hexToBinary(hex);
	
//		addToMemory(bin, MainMemory, pos);
		
		MainMemory[pos] = bin.toCharArray();
		
	}
	
	
	public static void reswrite(int dskAddress,String bin){
		
				if(!read(dskAddress).isEmpty()){
					int page = pageNumFromDskAddrs(dskAddress);
					int frame = frameFromPageNum(page);
					int posinmem = (frame * 8) + (dskAddress % 8);
					MainMemory[posinmem] = bin.toCharArray();								
					LOADER.pageFrameTable[frame][2] = 1;			//update dirty bit
					
					//should reference bit be updated in case of write
				}
	
			
		}
		
	
	private static String read(int dskAddr){	
		
		int page = pageNumFromDskAddrs(dskAddr);
		int frame = frameFromPageNum(page);
		int posinmem = 0;

		if(frame == -1){
			PAGEFAULT_HANDLER.handle(page);
//			read(dskAddr);
			frame = frameFromPageNum(page);
			posinmem = (frame * 8) + (dskAddr % 8);
		}else{			
			 posinmem = (frame * 8) + (dskAddr % 8);
			 LOADER.pageFrameTable[frame][1] = 1;				//update reference bit
		}
		return new String(MainMemory[posinmem]);	
	}
	
	//******* Storing one word in each location (16 bits) ******//
	
	private static void addToMemory(String bin,char[][] memory,int memoryLoc){
		
		 
		memory[memoryLoc] = bin.toCharArray();
		
	
/*		Modified logic
		
			for(int i=0;i<16;i++){

				if(i>=(16 - bin.length())){
					
	
					memory[memoryLoc][i]=binArr[i-(16-bin.length())];
				}else{
					memory[memoryLoc][i]='0';
				}		
		  }
		  
*/
		
		
	}
	
	
	
	///////////////////////////////////PHASE 2 ////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	
	
	
	//********** Fetched HEX word from the loader converted in Binary******//

	private static String hexToBinary(String hex) {
	    int i = Integer.parseInt(hex, 16);
	    String bin = Integer.toBinaryString(i);
	    return bin;
	}
	
	
	
	
	
	public static void addJobParams(String hex){
		
		try{
			System.out.println("JOB_ID :: "+ hex.substring(0, 2));
			System.out.println("LOAD ADDR :: " +hex.substring(3, 5));
			System.out.println("INITIAL_PC :: "+hex.substring(6, 8));
			System.out.println("SIZE :: "+hex.substring(9, 11));
			System.out.println("TRACE FLAG :: "+hex.substring(12, 13));
		}catch(Exception e){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER112);
		}
		
		
		if((!hex.substring(12, 13).trim().equals("0")) && (!hex.substring(12, 13).trim().equals("1"))){
			ERROR_HANDLER.handle(ERROR_HANDLER.WR202);
		}	
		
		try{
			jobId = (byte) Integer.parseInt(hex.substring(0, 2), 16);
		}catch(NumberFormatException e){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER107);
		}
		
		try{
			loadAddr = (byte) Integer.parseInt(hex.substring(3, 5), 16);
		}catch(NumberFormatException e){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER108);
		}
		
		try{
			initialPc = (byte) Integer.parseInt(hex.substring(6, 8), 16);
		}catch(NumberFormatException e){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER109);
		}
		
		try{
			size = (byte) Integer.parseInt(hex.substring(9, 11), 16);
		}catch(NumberFormatException e){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER110);
		}
	
		
			
		trace = (byte) Integer.parseInt(hex.substring(12, 13), 16);
		
		File f = new File("trace_file.txt");
		if (f.exists()) {
			f.delete();
		}
		if(trace == 1){
			enableTrace();
		}else{
//			System.setOut(new PrintStream(new ByteArrayOutputStream()));
		}
		
		
		
	}
	
	//*******Printing MainMemory in Binary*******//
	
	public static void printMemorySnapshot(){	
		
		System.out.println("===================MEMORY SNAPSHOT=========================");
		for(int i = 0; i<__memoryLoc; i++)
		{
		    for(int j = 0; j<16; j++)		    
		        System.out.print(MEMORY.MainMemory[i][j]);		    
		    System.out.println();
		}		
		System.out.println("========================================================");
		
	}
	
	//******* Printing trace_file******//
	public static void enableTrace(){	
		try {
			System.setOut(new PrintStream(new FileOutputStream("trace_file.txt")));
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		}
	}
	
	
	
	public static int pageNumFromDskAddrs(int dskAddress){
		if(dskAddress < 8)
			return 0;
		else
			return (dskAddress / 8);
	}
	
	public static int frameFromPageNum(int pageNum){
		int frameNum = -1;
		
		for(int i=0 ; i<32 ;i++){
			int temp[] = LOADER.pageFrameTable[i];
			if(temp[0]==pageNum){
				frameNum = i;
			}
		}
		return frameNum;
	}

}
