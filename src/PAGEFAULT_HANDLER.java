
public class PAGEFAULT_HANDLER {
	
	
	public static void handle(int page){
		System.out.println("page "+page);
		//if frame is empty then add directly
		
		if(isEmpty(LOADER.pageFrameTable[5])){
			LOADER.pageFrameTable[5] = new int[]{page,0,0};
			LOADER.fmtQueue.add(5);
			LOADER.loadFrameToMemory(5);
		}else if(isEmpty(LOADER.pageFrameTable[8])){
			LOADER.pageFrameTable[8] = new int[]{page,0,0};
			LOADER.fmtQueue.add(8);
			LOADER.loadFrameToMemory(8);
		}else if(isEmpty(LOADER.pageFrameTable[10])){
			LOADER.pageFrameTable[10] = new int[]{page,0,0};
			LOADER.fmtQueue.add(10);
			LOADER.loadFrameToMemory(10);
		}else if(isEmpty(LOADER.pageFrameTable[17])){
			LOADER.pageFrameTable[17] = new int[]{page,0,0};
			LOADER.fmtQueue.add(17);
			LOADER.loadFrameToMemory(17);
		}else if(isEmpty(LOADER.pageFrameTable[20])){
			LOADER.pageFrameTable[20] = new int[]{page,0,0};
			LOADER.fmtQueue.add(20);
			LOADER.loadFrameToMemory(20);
		}else if(isEmpty(LOADER.pageFrameTable[31])){
			LOADER.pageFrameTable[31] = new int[]{page,0,0};
			LOADER.fmtQueue.add(31);
			LOADER.loadFrameToMemory(31);
		}else{
			int frame = getReplacementFrame();
			
			if(LOADER.pageFrameTable[frame][2] == 1)
				LOADER.flushFrameToDisk(frame);
			
			LOADER.pageFrameTable[frame] = new int[]{page,0,0};
			LOADER.loadFrameToMemory(frame);
		}
	}
	
	
	private static boolean isEmpty(int[] frmRow){
		return (frmRow[0] == -1 && frmRow[1] == -1  && frmRow[2] == -1);
	}
	
	
	private static int getReplacementFrame(){
		
		boolean fflag = false;
		int sthead = LOADER.fmtQueue.peek();	
		
		while(true){
			int head = LOADER.fmtQueue.poll();		
			int[] frmTbEnt = LOADER.pageFrameTable[head];
			if(frmTbEnt[1]== 1){
				if(fflag == true && sthead==head){
					return head;
				}else{
					LOADER.fmtQueue.add(head);  // if ref bit is 1 then add at the bottom of the queue
				}
			}else{
				return head;
			}			
			fflag = true;
		}
		
	}
	
}
