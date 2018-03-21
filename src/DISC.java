import java.util.Arrays;

public class DISC {
	
	
	private static  char disk[][] = new char[2048][16];
	
	// create segment table while adding to disk
	
	
	public static void addToDisk(String bin,int pos){	
		String pad = "0000000000000000" + bin;         // padding
		bin = pad.substring(pad.length()-16);					
		disk[pos] = bin.toCharArray();		
	}
	
	
	public static char[] readFromDisk(int pos){
		System.out.println(pos);
		return disk[pos];		
	}
	
	
	
	
	public static void printContent(){
		
		Arrays.stream(disk)
		.map(n->new String(n))
		.forEach( n -> System.out.println(n));
	}

}
