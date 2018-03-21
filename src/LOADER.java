/*
 * 	In loader class one line is fetched from input file.
 * 	Each line split in 4 words.
 * 	Each word is stored in a buffer and from the buffer it is sent to the main memory.
 * 	Buffer size is 4( 4 for Hex).
 * trim function is used to split the words in loader.
 */

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LOADER {
	
	
	
	public static int[][]segmentTable = new int[10][3];  //max segment 10 (arbitrary)
	
	public static int[][]pageFrameTable = new int[32][3];  
	
	public static Queue<Integer> fmtQueue = new LinkedList<>();
	
	
	/*
	 * 
	 * 0 -> Page Num
	 * 1 -> Reference bit
	 * 2 -> Dirty bit
	 */
	
	public static int discLoc = 0;
	
	
	
	
	public static void load(Supplier<Stream<String>> streamSupplier){
		
		Arrays.fill(segmentTable, new int[]{-1,-1,-1});   //initialize
		Arrays.fill(pageFrameTable, new int[]{-1,-1,-1});  //initialize
		
		vaidateJob(streamSupplier);
		
		
		String jobDetails =  streamSupplier.get()
										.skip(1)
										.findFirst()
										.get();
		MEMORY.addJobParams(jobDetails);
		
		addToDisc(streamSupplier);
		
		DISC.printContent();
		
		
		/*	
		
		String jobDetails =  streamSupplier.get().findFirst().get();
		MEMORY.addJobParams(jobDetails);
	
		
		
		//catch exception from memory -- map to errr code.
		streamSupplier.get()
					  .skip(1)
					  .map(String::trim)
					  .map( s -> {
						  String ts=null;
						  if(s.length() < 16){
							  ERROR_HANDLER.handle(ERROR_HANDLER.WR201);
							  ts = (s+"0000000000000000");
							  return ts.substring(0,16);
						  }
						  return s;
					  })
					  .forEach(n-> {
						  char [] buffer = new char [16];
						  buffer = n.toCharArray();
						  MEMORY.memoryProc(1,MEMORY.__memoryLoc, new String (buffer));
						  });
	
		*/
		
		
		
	 //	Adding to Disc
		
	}
	
	
	private static void vaidateJob(Supplier<Stream<String>> streamSupplier){
		
		
		
	}
	
	private static void addSegmentTable(int index , int base , int length){
		
		segmentTable[index][0] = index;
		segmentTable[index][1] = base;
		segmentTable[index][2] = length;
		
	}
	
	private static void addPageTable(Supplier<Stream<String>> streamSupplier){
		
		
		
	}
	
	private static void addToDisc(Supplier<Stream<String>> streamSupplier){
		
		boolean input = false;
		int discLoc = 0;
		
		Iterator<String> it = streamSupplier.get()
						.skip(2)
						.iterator();
		
		
		while(it.hasNext()){
			String item =  it.next();
			if(item.contains("**INPUT")){
				input = true;				
				continue;
			}else if(item.contains("**FIN")){
				break;
			}
			
			if(input){
				int page =   (int) Math.ceil(((discLoc/8) * 1000000)/1000000);
				addSegmentTable(1,page,8);	
				addInputToDisc(item);
				CPU.init(Integer.parseInt(item, 16));
			}else{
				addWordsToDisc(item);
			}			
			
		}
		
		
	}
	
	
	private static void addInputToDisc(String word){
		if(word.length()!=4){
			//throw excep
		}else{			
			int page = discLoc + (discLoc%8);
			DISC.addToDisk(hexToBinary(word), page);
		}
		
	}
	
	
	private static void addWordsToDisc(String line){
				if(line.length()!=16){					//error
					ERROR_HANDLER.handle(ERROR_HANDLER.ER101);
				}
				for(int i=0;i<16;i=i+4){
					String word = line.substring(i,i+4);
					DISC.addToDisk(hexToBinary(word), discLoc);
					discLoc++;
				}
	}
	
	
	public static void loadFrameToMemory(int frame){
		System.out.println("Frame No "+frame);
		int page = pageFrameTable[frame][0];
		int dscpos = page * 8;
		int mempos = frame * 8;
		for(int i=0 ;i<8 ;i++){
			 char[] discVal = DISC.readFromDisk(dscpos + i);
			 MEMORY.memoryProc(1, (mempos + i), new String(discVal));
		}		
	}
	
	public static void flushFrameToDisk(int frame){
		int page = pageFrameTable[frame][0];
		int dscpos = page * 8;
		int mempos = frame * 8;
		for(int i=0 ;i<8 ;i++){
			
			String bin = MEMORY.memoryProc(0,(mempos + i),null);
			DISC.addToDisk(bin, dscpos);
		}	
		
	}
	
	private static String hexToBinary(String hex) {
	    int i = Integer.parseInt(hex, 16);
	    String bin = Integer.toBinaryString(i);
	    return bin;
	}

}
