/*
 *  Name : Ipsita Ghosh
 *  
 *  Course Number : CS 5323
 *  
 *  Assignment Title : A Simple Batch System ( Phase 1)
 *  
 *  Date : 02/27/2918
 *  
 *  Description of Global Variable :
 *  in - USER Input 
 *  clock - calculating the clock time 
 *  out - Output in console
 *  IOClock - calculating the IO Clock time
 * 
 * Reading the input loader format
 * For IO additional clock is 15
 * printing in output file
 * SYSTEM is calling CPU, MEMORY and LOADER to access those
 * 
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SYSTEM {
	
	private static List<Integer> out = new ArrayList<Integer>();
	public static int clock = 0;
	public static PrintStream console = System.out;
	public static PrintStream outfile ;
	public static int IOClock = 0;
	public static String[] param;
	
	public static void main(String[] args){
		
		param = args;
		console = System.out;
		enableOutput();
		
		try{
			
	//		System.out.println("Input Integer Number To Encrypt (in Dec):: ");
	//		Scanner sc = new Scanner(System.in);
	//		int in = sc.nextInt();
			
			
			
			LOADER.load(loadLoaderFormat());	
			
//			MEMORY.printMemorySnapshot();
			
//			CPU.init(in);
			CPU.cpuProc(MEMORY.initialPc, MEMORY.trace);
			
			System.out.println("===============================END EXECUTION===============================");
			
			
			changeToConsole();
			
			System.out.println("===============================OUTPUT===============================");
//			System.out.println("INPUT "+in +" :");
			System.out.println("INPUT : "+new String(BASE_CPU.decToBin(out.get(0))));
			System.out.println("RESULT : "+new String(BASE_CPU.decToBin(out.get(1))));
			System.out.println("CLOCL in vtu  : "+BASE_CPU.decToHex(clock));
			
			changeToOutput();
				System.out.println("===============================OUTPUT===============================");
				System.out.println("Job ID :" + MEMORY.jobId);
//				System.out.println("INPUT "+in +" :");
				System.out.println("INPUT : "+new String(BASE_CPU.decToBin(out.get(0))));
				System.out.println("RESULT : "+new String(BASE_CPU.decToBin(out.get(1))));
				System.out.println("CLOCL in vtu  : "+BASE_CPU.decToHex(clock));
				System.out.println("IO CLOCK :" + IOClock);
				System.out.println("Execution Time :" + (clock - IOClock));
				
			
		}catch(Exception e){
			e.printStackTrace();
			if(e.getMessage().contains("ERROR") || e.getMessage().contains("WARNING")){
				changeToConsole();
				System.out.println(e.getMessage());
				changeToOutput();
				System.out.println(e.getMessage());
			}
			else{
				ERROR_HANDLER.handle(0);
			}
		}
	}
	
	public static void writeio(Integer val){
		out.add(val);
		clock = clock +15;
	}
	
	
	//****** Reading the input loader format *****//
	public static Supplier<Stream<String>> loadLoaderFormat(){
		Supplier<Stream<String>> streamSupplier = () -> {
			try {
				return Files.lines(Paths.get(ClassLoader.getSystemResource(param[0]).toURI()));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
			return null;
		};
		
		return streamSupplier;
	}
	
	//****** writing the output in output file ****//
	public static void enableOutput(){	
		try {
			outfile = (new PrintStream(new FileOutputStream("output.txt")));
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		}
	}
	
	public static void changeToOutput(){	
		System.setOut(outfile);
	}
	public static void changeToConsole(){	
		System.setOut(console);
	}

}
