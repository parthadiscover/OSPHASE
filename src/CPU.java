/*
 * Global Variable : input, programCounter, flag
 * CPU calls the memory to read the word from it for executing instruction and update the memory if required.
 * cpuPROC is calling methods: execute()
 * execute() is calling checkInstc() to check the opcode type
 * increaseClock() is called in execute() to calculate for each instruction
 * BASE_CPU is the sub class where functions of zero address and one address is present
 * CPU is accessing the USER Input for calculation
 * 
 */

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CPU {
	
	static int input = 0;
	static int programCounter = 0;
	static boolean flag = true;
	
	
	public static void init(int input){
			
		BASE_CPU.addZeroInstr();
		BASE_CPU.addOneInstr();
		CPU.input = input;
	}
	
	
	//**** CPU procedure : calculating EA and ****//
	
	 public static void cpuProc(int pc,int trace){
		 int word = 1;
		 int initialPC = 0;
		 programCounter = pc;
		 try{
		 while(flag){
			  initialPC = programCounter;
			  System.out.println("*******Executing Instruction Start*****");
			  System.out.println("INITIAL PC  ==> "+ BASE_CPU.decToHex(initialPC));
			  System.out.println("BASE REG  (BR) ==> "+ BASE_CPU.decToHex(MEMORY.loadAddr));
			  System.out.println("INSTR REG  (IR) ==> "+ BASE_CPU.binToHex(MEMORY.memoryProc(0,programCounter,null)));
			  System.out.println("TOS BEFORE ==> "+ BASE_CPU.decToHex(BASE_CPU.tos) + ",  S[TOS] BEFORE ==> "+(BASE_CPU.tos>=0?BASE_CPU.stack[BASE_CPU.tos]:"Empty Stack"));
			  
			  execute();
			  if(programCounter == initialPC){
				  programCounter++;
			  }
			  System.out.println("TOS AFTER ==> "+ BASE_CPU.decToHex(BASE_CPU.tos) + ",  S[TOS] AFTER ==> "+(BASE_CPU.tos>=0?BASE_CPU.stack[BASE_CPU.tos]:"Empty Stack"));
			  System.out.println("FINAL PC ==> "+ BASE_CPU.decToHex(programCounter));	
			  System.out.println("*******Executing Instruction END*****");
			  word++;
		 }
		}catch(Exception e){
			e.printStackTrace();
			if(e.getMessage().contains("ERROR"))
				throw e;
			ERROR_HANDLER.handle(ERROR_HANDLER.ER105);
		}
	 }
	 
	//****** execute method calculating if it is a zero instruction or one instruction*****//
	//****** In if zero instruction is divided into sub strings*****//
	//****** In else if EA calculated depending upon the index bit***** // 
	private static void execute(){
		String instr = null;
		try{
			instr = MEMORY.memoryProc(0,programCounter,instr);	
		}catch(Exception e){
			ERROR_HANDLER.handle(ERROR_HANDLER.ER104,new String(BASE_CPU.decToBin(programCounter)));
		}
		 
		 if(instr.charAt(0) == '0'){
			 String opCode1 = instr.substring(instr.length()-5);
			 String opCode2 = instr.substring(instr.length()-13 ,instr.length()-8 );
			 checkInstc(opCode1,0);
			 increaseClock(0,opCode1);
			 checkInstc(opCode2,0);	
			 increaseClock(0,opCode2);
			 BASE_CPU.ZERO_ADDR_INST.get(opCode1).accept(input);			 
			 BASE_CPU.ZERO_ADDR_INST.get(opCode2).accept(input);			 
			 System.out.println("TOS - "+ BASE_CPU.tos + "VAL :: "+(BASE_CPU.tos>=0?BASE_CPU.stack[BASE_CPU.tos]:"Empty Stack"));
		 }else if(instr.charAt(0) == '1'){			 
			 String opCode = instr.substring(1,6);	
			 checkInstc(opCode,1);
			 int ea = instr.charAt(6) == '1' ? (Integer.parseInt(instr.substring(instr.length()-7), 2) + BASE_CPU.stack[BASE_CPU.tos]) : Integer.parseInt(instr.substring(instr.length()-7), 2);
			 System.out.println("EA BEFORE ==> "+ BASE_CPU.decToHex(ea) + ",  (EA) BEFORE ==> "+BASE_CPU.binToHex(MEMORY.memoryProc(0,ea,null)));
			 increaseClock(1,null);
			 BASE_CPU.ONE_ADDR_INST.get(opCode).accept(ea);
			 System.out.println("EA AFTER ==> "+ BASE_CPU.decToHex(ea) + ",  (EA) AFTER ==> "+BASE_CPU.binToHex(MEMORY.memoryProc(0,ea,null)));		
		 }
		
	}
	
	//******Checking instruction type : zero address or one address*******//
	
	private static void checkInstc(String opcode,int type){
		if(type == 0){
			if(!BASE_CPU.ZERO_ADDR_INST.containsKey(opcode)){
				ERROR_HANDLER.handle(ERROR_HANDLER.ER102,opcode);
			}
		}else{
			if(!BASE_CPU.ONE_ADDR_INST.containsKey(opcode)){
				ERROR_HANDLER.handle(ERROR_HANDLER.ER103,opcode);
			}
		}
	}
	
	//****** Calculating clock for each instruction*****//
	private static void increaseClock(int type,String opcode){
	
		if(type == 0) {			
				SYSTEM.clock = SYSTEM.clock + 1;					
		}else if (type == 1){
			SYSTEM.clock = SYSTEM.clock + 4;
		}

		//System.out.println("CLOCK VAL -> "+SYSTEM.clock);
	
	}
	
}



class BASE_CPU {
	
	
	
	public static int[] stack= new int[7];
	public static int tos = 0;
	public static Map<String,Consumer> ZERO_ADDR_INST = new HashMap<String,Consumer>();
	public static Map<String,Consumer> ONE_ADDR_INST = new HashMap<String,Consumer>();
	
	
	 //**************Zero Instruction Operations using Functions************************//
	
	 private static Consumer<Integer> ZERO_RD = (in)-> {		 
		 tos++;
		 stack[tos] = in ;	
		 SYSTEM.clock = SYSTEM.clock + 15;
		 SYSTEM.IOClock = SYSTEM.IOClock +15;
		 System.out.println("Executed INSTRUCTION ZERO_RD ");
	 } ;
	 
	 private static Consumer<Integer> ZERO_SL = (in)-> {		 
		 stack[tos] = stack[tos] << 1 ;	
		 System.out.println("Executed INSTRUCTION ZERO_SL ");
	 } ;
	 
	 private static Consumer<Integer> ZERO_ADD = (in)-> {				
			stack[tos-1] = stack[tos] + stack[tos-1];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_ADD ");
		} ;
	
		private static Consumer<Integer> ZERO_RTN = (in)-> {				
			CPU.programCounter =  stack[tos] ;
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_RTN ");
		} ;
		
		private static Consumer<Integer> ZERO_NOT = (in)-> {				
			stack[tos] = ~stack[tos];
			System.out.println("Executed INSTRUCTION ZERO_NOT ");
		} ;
		
		private static Consumer<Integer> ZERO_WR = (in)-> {
			int out = stack[tos];
			tos--;
			SYSTEM.writeio(out);	
			SYSTEM.IOClock = SYSTEM.IOClock +15;
			System.out.println("Executed INSTRUCTION ZERO_WR ");
		} ;
		
		private static Consumer<Integer> ZERO_HLT = (in)-> {
			CPU.flag=false;		
			System.out.println("HLT Encountered - Program terminated ");
		} ;
		
		private static Consumer<Integer> ZERO_NOP = (in)-> {			
			System.out.println("NOP Encountered ");
		} ;
		
		private static Consumer<Integer> ZERO_OR = (in)-> {	
			stack[tos-1] = stack[tos] | stack[tos-1];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_OR ");
		} ;
		
		private static Consumer<Integer> ZERO_AND = (in)-> {
			stack[tos-1] = stack[tos] & stack[tos-1];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_AND ");
		} ;
		
		private static Consumer<Integer> ZERO_XOR = (in)-> {	
			stack[tos-1] = stack[tos] ^ stack[tos-1];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_XOR ");
		} ;
		
		private static Consumer<Integer> ZERO_SUB = (in)-> {	
			stack[tos-1] = stack[tos-1] - stack[tos];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_SUB ");
		} ;
		
		private static Consumer<Integer> ZERO_MUL = (in)-> {
			stack[tos-1] = stack[tos] * stack[tos-1];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_MUL ");
		} ;
		
		private static Consumer<Integer> ZERO_DIV = (in)-> {
			stack[tos-1] = stack[tos-1] / stack[tos];
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_DIV ");
		} ;
		
		private static Consumer<Integer> ZERO_MOD = (in)-> {
			stack[tos-1] = stack[tos-1] % stack[tos];			
			tos--;
			System.out.println("Executed INSTRUCTION ZERO_MOD ");
		} ;
		
		private static Consumer<Integer> ZERO_SR = (in)-> {
			stack[tos] = stack[tos] >> 1	;	
			 System.out.println("Executed INSTRUCTION ZERO_SR ");
		 } ;
		 
		 private static Consumer<Integer> ZERO_CPG = (in)-> {
				stack[tos+1] = stack[tos-1] > stack[tos] ? 1 : 0;
				tos++;
				System.out.println("Executed INSTRUCTION ZERO_CPG ");
			} ;
			
		private static Consumer<Integer> ZERO_CPL = (in)-> {
				stack[tos+1] = stack[tos-1] < stack[tos] ? 1 : 0;
				tos++;
				System.out.println("Executed INSTRUCTION ZERO_CPL ");
			} ;
			
		private static Consumer<Integer> ZERO_CPE = (in)-> {
				stack[tos+1] = stack[tos-1] == stack[tos] ? 1 : 0;
				tos++;
				System.out.println("Executed INSTRUCTION ZERO_CPE ");
			} ;
	 
	 //**************One Instruction Operations using Functions************************//
	 
	 private static Consumer<Integer> ONE_POP = (ea)-> {		 
	//	 MEMORY.MainMemory[ea] = decToBin(stack[tos]);
		 MEMORY.reswrite(ea,new String(decToBin(stack[tos])));
	//	 MEMORY.memoryProc(1,ea,new String(decToBin(stack[tos])));
		 tos--;
		 System.out.println("Executed INSTRUCTION ONE_POP ");
	 } ;
	 
	 private static Consumer<Integer> ONE_PUSH = (ea)-> {	
		 tos++;
		 stack[tos] = binToDec(MEMORY.MainMemory[ea]);		
		 System.out.println("Executed INSTRUCTION ONE_PUSH ");
	 } ;
	 
	 private static Consumer<Integer> ONE_CPG = (ea)-> {		 
		 stack[tos+1] = stack[tos] > binToDec(MEMORY.memoryProc(0,ea,null)) ? 1 : 0 ;
		 tos++;		 
		 System.out.println("Executed INSTRUCTION ONE_CPG ");
	 } ;

	private static Consumer<Integer> ONE_CPL = (ea)-> {			 
		 stack[tos+1] = stack[tos] < binToDec(MEMORY.memoryProc(0,ea,null)) ? 1 : 0 ;
		 tos++;		 
		 System.out.println("Executed INSTRUCTION ONE_CPL ");
	} ;
	private static Consumer<Integer> ONE_CPE = (ea)-> {				
		 stack[tos+1] = stack[tos] == binToDec(MEMORY.memoryProc(0,ea,null)) ? 1 : 0 ;
		 tos++;		 
		 System.out.println("Executed INSTRUCTION ONE_CPE ");
	} ;

	private static Consumer<Integer> ONE_BRF = (ea)-> {			 
		 if(stack[tos] == 0)
			 CPU.programCounter = ea;		
		 tos--;		 
		 System.out.println("Executed INSTRUCTION ONE_BRF ");
	} ;
	 
	private static Consumer<Integer> ONE_BRT = (ea)-> {			 
		 if(stack[tos] == 1)
			 CPU.programCounter = ea;
		 tos--;		 
		 System.out.println("Executed INSTRUCTION ONE_BRT ");
	} ;
	
	private static Consumer<Integer> ONE_AND = (ea)-> {				
		stack[tos]  = stack[tos] & binToDec(MEMORY.memoryProc(0,ea,null)) ;
		 System.out.println("Executed INSTRUCTION ONE_AND ");
	} ;
	
	private static Consumer<Integer> ONE_CALL = (ea)-> {				
		tos++;
		stack[tos]  = CPU.programCounter +1;
		CPU.programCounter = ea;
		 System.out.println("Executed INSTRUCTION ONE_CALL ");
	} ;
	
	private static Consumer<Integer> ONE_BR = (ea)-> {				
		 CPU.programCounter = ea;
		 System.out.println("Executed INSTRUCTION ONE_BR ");
	} ;
	
	private static Consumer<Integer> ONE_ADD = (ea)-> {				
		stack[tos] = stack[tos] + binToDec(MEMORY.memoryProc(0,ea,null));
		System.out.println("Executed INSTRUCTION ONE_ADD ");
	} ;
	
	private static Consumer<Integer> ONE_OR = (ea)-> {				
		stack[tos]  = stack[tos] | binToDec(MEMORY.memoryProc(0,ea,null)) ;
		 System.out.println("Executed INSTRUCTION ONE_OR ");
	} ;
	
	private static Consumer<Integer> ONE_XOR = (ea)-> {				
		stack[tos]  = stack[tos] ^ binToDec(MEMORY.MainMemory[ea]) ;
		 System.out.println("Executed INSTRUCTION ONE_XOR ");
	} ;
	
	private static Consumer<Integer> ONE_SUB = (ea)-> {				
		stack[tos] = stack[tos] - binToDec(MEMORY.MainMemory[ea]);
		System.out.println("Executed INSTRUCTION ONE_SUB ");
	} ;
	
	private static Consumer<Integer> ONE_MUL = (ea)-> {				
		stack[tos] = stack[tos] * binToDec(MEMORY.memoryProc(0,ea,null));
		System.out.println("Executed INSTRUCTION ONE_MUL ");
	} ;
	
	private static Consumer<Integer> ONE_DIV = (ea)-> {				
		stack[tos] = stack[tos] / binToDec(MEMORY.memoryProc(0,ea,null));
		System.out.println("Executed INSTRUCTION ONE_DIV ");
	} ;
	
	private static Consumer<Integer> ONE_MOD = (ea)-> {				
		stack[tos] = stack[tos] % binToDec(MEMORY.memoryProc(0,ea,null));		
		System.out.println("Executed INSTRUCTION ONE_MOD ");
	} ;
	
	//**************Zero Instruction Opcodes************************//
	/*
	 * Using Put checking which opcode is matching and executing the corresponding zero instruction
	 */
	 public static void addZeroInstr(){
		 ZERO_ADDR_INST.put("10011",ZERO_RD);
		 ZERO_ADDR_INST.put("01010",ZERO_SL);
		 ZERO_ADDR_INST.put("00101",ZERO_ADD);
		 ZERO_ADDR_INST.put("10101",ZERO_RTN);
		 ZERO_ADDR_INST.put("00011",ZERO_NOT);
		 ZERO_ADDR_INST.put("10100",ZERO_WR);
		 ZERO_ADDR_INST.put("11000",ZERO_HLT);
		 ZERO_ADDR_INST.put("00000",ZERO_NOP);
		 ZERO_ADDR_INST.put("00001",ZERO_OR);
		 ZERO_ADDR_INST.put("00010",ZERO_AND);
		 ZERO_ADDR_INST.put("00100",ZERO_XOR);
		 ZERO_ADDR_INST.put("00110",ZERO_SUB);
		 ZERO_ADDR_INST.put("00111",ZERO_MUL);
		 ZERO_ADDR_INST.put("01000",ZERO_DIV);
		 ZERO_ADDR_INST.put("01001",ZERO_MOD);
		 ZERO_ADDR_INST.put("01100",ZERO_CPG);
		 ZERO_ADDR_INST.put("01101",ZERO_CPL);
		 ZERO_ADDR_INST.put("01110",ZERO_CPE);
	 }
	 
	//**************One Instruction Opcodes************************//
	 /*
	  * Using Put checking which opcode is matching and executing the corresponding one instruction
	  */
	 
	 public static void addOneInstr(){
		 ONE_ADDR_INST.put("10111",ONE_POP);
		 ONE_ADDR_INST.put("10110",ONE_PUSH);
		 ONE_ADDR_INST.put("10001",ONE_BRF);
		 ONE_ADDR_INST.put("10000",ONE_BRT);
		 ONE_ADDR_INST.put("01100",ONE_CPG);
		 ONE_ADDR_INST.put("01101",ONE_CPL);
		 ONE_ADDR_INST.put("01110",ONE_CPE);
		 ONE_ADDR_INST.put("00010",ONE_AND);
		 ONE_ADDR_INST.put("10010",ONE_CALL);
		 ONE_ADDR_INST.put("01111",ONE_BR);
		 ONE_ADDR_INST.put("00101",ONE_ADD);
		 ONE_ADDR_INST.put("00001",ONE_OR);
		 ONE_ADDR_INST.put("00100",ONE_XOR);
		 ONE_ADDR_INST.put("00110",ONE_SUB);
		 ONE_ADDR_INST.put("00111",ONE_MUL);
		 ONE_ADDR_INST.put("01000",ONE_DIV);
		 ONE_ADDR_INST.put("01001",ONE_MOD);
		 
		 
	 }
	 //****** decimal to binary *****//
	 public static char[] decToBin(int dec){
		 String pad = "0000000000000000"+Integer.toBinaryString(dec);
		
		 return pad.substring(pad.length()-16,pad.length()).toCharArray();				 
	 }
	 
	 //***** binary to decimal *****//
	 // two's Complement operation *****//
	 
	 public static int binToDec(String bin){
		 return binToDec(bin.toCharArray());
	 }
	 
	 public static int binToDec(char[] bin){
		 
		 			
		 int retval=0;
		 if(bin[0] == '1'){
			 bin = twosCompliment(new String(bin)).toCharArray();	
	
			 retval = -1*Integer.parseInt(new String(bin), 2);
		 }else{
			 retval = Integer.parseInt(new String(bin), 2);
		 }
		
		 return retval; 
	 }
	
	 //****** decimal to hex ****//
	 
	 public static String decToHex(int dec){
		 String temp = ("0000" + Integer.toHexString(dec));
		 return temp.substring(temp.length()-4);
		 				 
	 }
	 
	 //***** binary to hex ****//
	 
	 public static String binToHex(String bin){
		 return Integer.toHexString(Integer.parseInt(bin, 2));				 
	 }
	 
	 //***** calling twosCompliment to calculate the actual two's compliment *****//
	 
	 public static String twosCompliment(String bin) {
	        String twos = "", ones = "";

	        for (int i = 0; i < bin.length(); i++) {
	            ones += flip(bin.charAt(i));
	        }
	        int number0 = Integer.parseInt(ones, 2);
	        StringBuilder builder = new StringBuilder(ones);
	        boolean b = false;
	        for (int i = ones.length() - 1; i > 0; i--) {
	            if (ones.charAt(i) == '1') {
	                builder.setCharAt(i, '0');
	            } else {
	                builder.setCharAt(i, '1');
	                b = true;
	                break;
	            }
	        }
	        if (!b)
	            builder.append("1", 0, 7);

	        twos = builder.toString();

	        return twos;
	    }

	// Returns '0' for '1' and '1' for '0'
	    public static char flip(char c) {
	        return (c == '0') ? '1' : '0';
	    }
	    
	   
}
