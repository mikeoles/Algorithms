/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;      			// number of input chars
    private static int W = 9;         				// codeword width
    private static int L = (int)Math.pow(2,W);      // number of codewords = 2^W	
	
    public static void compress(String mode) {		
		int modeNum = 0;	
        if(mode.equals("r")) modeNum = 1;
        if(mode.equals("m")) modeNum = 2;	
        BinaryStdOut.write(modeNum, W);	
		
		int t = 0;
		String input = BinaryStdIn.readString();
		int outputSize = 2*W, inputSize = 0;		
		
		while(input.length() > 0) {
			float resetRatio = 0, newRatio = 0,oldRatio = 0;			
			boolean oldRatioSet = false;	
			boolean monitor = false;			
			
			TST<Integer> st = new TST<Integer>();						
			for (int i = 0; i < R; i++)
				st.put("" + (char) i, i);
			int code = R+2;  // R is codeword for EOF R+1 is codeword to reset when monitoring
			while (input.length() > 0) {
				if(mode.equals("m")){
					if(newRatio!=0)	resetRatio = oldRatio/newRatio;				
					if(resetRatio > 1.1){ 
					monitor = true;
					}else{
						monitor = false;
					}
				}
				
				String s = st.longestPrefixOf(input);  // Find max prefix match s.				
				BinaryStdOut.write(st.get(s), W);      // Print s's encoding.	
				outputSize+=W;
				t = s.length();
				
				if (t < input.length() && code+1 < L)    // Add s to symbol table.
					st.put(input.substring(0, t + 1), code++);
				else if(t < input.length() && W < 16) {						
					W++;
					L = (int)Math.pow(2,W);
					st.put(input.substring(0, t+1), code++);
				}else if(W == 16&&mode.equals("r")||(mode.equals("m")&&monitor==true)){  							
					if(monitor==true){				
						BinaryStdOut.write(R+1, W);
						outputSize+=W;
					}
					W = 9;
					L = (int)Math.pow(2,W);					
					break;
				}else if(W == 16&&mode.equals("m")&&oldRatioSet == false){
					oldRatio = (float)inputSize/(float)outputSize;
					oldRatioSet = true;	
				}				
				newRatio = (float)inputSize/(float)outputSize;					
				input = input.substring(t);            // Scan past s in input.
				inputSize += 8*s.length();					
			}	
		}
        BinaryStdOut.write(R, W);		
        BinaryStdOut.close();
    } 


    public static void expand() {
		int mode = 0;
		mode = BinaryStdIn.readInt(W);		
		while(true){			
			String[] st = new String[L];
			int i = 0; // next available codeword value
			
			for (i = 0; i < R; i++)			// initialize symbol table with all 1-character strings
				st[i] = "" + (char) i;
			st[i++] = "";                        // (unused) lookahead for EOF
			st[i++] = "";                        // (unused) lookahead for Monitor			
						
			int codeword = BinaryStdIn.readInt(W);	
			if (codeword == R) return;           		// expanded message is empty string			
			String val = st[codeword];					
			while (true) {			
				boolean monitor = false;
				BinaryStdOut.write(val);
				if(i+1 >= L && W!=16){						//if codebook is full, increase size
					if(W==16&&mode==1||(mode==2&&codeword==R+1)) break;	
					W++;			
					L = (int)Math.pow(2,W);
					String[] temp = new String[L];
					for(int j = 0; j < st.length; j++)   
						temp [j] = st[j];
					st = temp;  				
				}			
				codeword = BinaryStdIn.readInt(W);				
				if (codeword == R) break;
				String s = st[codeword];			
				if (i == codeword) s = val + val.charAt(0);   // special case hack 				
				if (i < L) st[i++] = val + s.charAt(0);				
				val = s;
			}
			W = 9;
			L = (int)Math.pow(2,W);
			if (codeword == R) break;
		}
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
