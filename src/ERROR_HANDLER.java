/*
 * In Error Handler Class all errors and warning are executed using a switch case.
 * 
 * If any error or warning occurs while executing it will be showed in output file.
 */

import java.io.PrintStream;

public class ERROR_HANDLER {
	
	
	public static final int ER101 = 101;
	public static final int ER102 = 102;
	public static final int ER103 = 103;
	public static final int ER104 = 104;
	public static final int ER105 = 105;
	public static final int ER107 = 107;
	public static final int ER108 = 108;
	public static final int ER109 = 109;
	public static final int ER110 = 110;
	public static final int ER111 = 111;
	public static final int ER112 = 112;
	
	public static final int WR201 = 201;
	public static final int WR202 = 202;
	
	
	//**** In handle() switch case is used for error and warning messages ****//
	public static void handle(int code,String ...arg){
		
		String Errmsg = "";
		String Warnmsg = "";
		
		
		switch(code){
		
			case ER101 : 
				Errmsg = "ERROR ::(LOAD TIME) Loader input is not 4 words in Intermediate job lines ";
				break;
			case ER102 : 
				Errmsg = "ERROR ::(DECODING TIME) Zero Address Instruction "+arg[0]+" Not Found ";
				break;
			case ER103 : 
				Errmsg = "ERROR ::(DECODING TIME) One Address Instruction "+arg[0]+" Not Found ";
				break;
			case ER104 : 
				Errmsg = "ERROR ::(MEMORY-REF TIME) Cannot Retrieve Value From Mem Loc  "+arg[0] ;
				break;
			case ER105 : 
				Errmsg = "ERROR ::(EXECUTION TIME) Some Fatal Error.Program terminated" ;
				break;
			case ER107 : 
				Errmsg = "ERROR ::(EXECUTION TIME) Invalid Job Id Input" ;
				break;
			case ER108 : 
				Errmsg = "ERROR ::(EXECUTION TIME) Invalid Load Address Input" ;
				break;
			case ER109 : 
				Errmsg = "ERROR ::(EXECUTION TIME) Invalid Initial PC Input" ;
				break;
			case ER110 : 
				Errmsg = "ERROR ::(EXECUTION TIME) Invalid Size Input" ;
				break;
			case ER111 : 
				Errmsg = "ERROR ::(MEMORY-REF TIME) Memory Overflow . Cannot reference size more than 256" ;
				break;
			case ER112 : 
				Errmsg = "ERROR ::(LOAD TIME) Job Params not correct. " ;
				break;
			case WR201 : 
				Warnmsg = "WARNING :: Loader input less than 4 words in Intermediate job lines ";
				break;
			case WR202 : 
				Warnmsg = "WARNING ::(EXECUTION TIME) Invalid Trace Input" ;
				break;
			default :
				Errmsg = "ERROR :: Some Fatal Error.Program terminated ";
				break;
		}
		
		if(!Errmsg.isEmpty())
			throw new RuntimeException(Errmsg);
		else if (!Warnmsg.isEmpty()){
			PrintStream current = System.out;
			SYSTEM.changeToOutput();
			System.out.println(Warnmsg);
			SYSTEM.changeToConsole();
			System.out.println(Warnmsg);
			System.setOut(current);
		}
	}
	

}
