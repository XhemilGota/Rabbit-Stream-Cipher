import java.math.BigInteger;

public class RabbitStreamCipher {

	// State variables
	private static boolean[][] X = new boolean[8][32];
	
	// Counter variables
	private static boolean[][] C = new boolean[8][32];
	
	private static boolean carryBit; // Counter carry bit
	

	public static void main(String[] args) {
		
		String key = "00000000000000000000000000000000";
		//String key = "912813292E3D36FE3BFC62F1DC51C3AC";
		//String iv = "C373F575C1267E59";
		String iv = "0000000000000000";
		String m = "007014A78459657841254586945554444444755900000000000111111112254487841654A";
		
		String[] c = encrypt(m,key,iv);
		
		System.out.println("Plaintext:   " + m + "\n");
		System.out.println("Ciphertext: " + c[0] + "\n");
		System.out.println("Algorithm output: \n");
		System.out.println(c[1]);
				
	}
	
	public static String[] encrypt(String message, String k, String initV)
	{
		boolean[] plaintext = new boolean[message.length() * 4];
		boolean[] key = new boolean[128];
		boolean[] IV = new boolean[64];
		
		key = StringToBoolean(k); 
		
		IV = StringToBoolean(initV);
		
		plaintext = StringToBoolean(message);
		
		String ciphertext = "";
		
		carryBit = false;
				
		KeySetup(key);
		IVsetup(IV);
		
		boolean[] S = extract();
		
		boolean[] c = new boolean[plaintext.length];
		
		String Si = "S1: " + BooleanToString(S);
		
		int i = 0;
		int j = 0;
		int l = 2;
		while(i < plaintext.length)
		{
			c[i] = plaintext[i++] ^ S[j++];
			if(j == 127)
			{
				S = extract();
				j = 0;
				Si += "\nS" + l++ + ": " + BooleanToString(S);
			}
		}
		
		ciphertext = BooleanToString(c);
		
		String[] rez = {ciphertext, Si};
		
		return rez;
	}
	
	public static String[] encrypt(String message, String k)
	{
		boolean[] plaintext = new boolean[message.length() * 4];
		boolean[] key = new boolean[128];
		
		key = StringToBoolean(k);
		
		plaintext = StringToBoolean(message);
		
		String ciphertext = "";
		
		carryBit = false;
				
		KeySetup(key);
		
		boolean[] S = extract();
		
		boolean[] c = new boolean[plaintext.length];
		
		String Si = "S1: " + BooleanToString(S);
		
		int i = 0;
		int j = 0;
		int l = 2;
		while(i < plaintext.length)
		{
			c[i] = plaintext[i++] ^ S[j++];
			if(j == 127)
			{
				S = extract();
				j = 0;
				Si += "\nS" + l++ + ": " + BooleanToString(S);
			}
		}
		
		ciphertext = BooleanToString(c);
		
		String[] rez = {ciphertext, Si};
		
		return rez;
	}
	
	
	
	private static void KeySetup(boolean[] key)
	{
		boolean[][] k = new boolean[8][16];
		
		// Divide key into 8 sub-keys 
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 16; j++)
				k[i][j] = key[j + i * 16];
		
		int l = 7;
		// Initializing initial state
		for(int j = 0; j < 8; j++)
		{
			if(j % 2 == 0)
			{
				X[l - j] = concatenate(k[l - ((j + 1) % 8)], k[l - j]);
				C[l - j] = concatenate(k[l - ((j + 4) % 8)], k[l - ((j + 5) % 8)]);
			}
			
			else
			{
				X[l - j] = concatenate(k[l - ((j + 5) % 8)], k[l - ((j + 4) % 8)]);
				C[l - j] = concatenate(k[l - j], k[l - ((j + 1) % 8)]);
			}
			
		}
	
		// Iterating system four times
		for(int j = 0; j < 4; j++)
			nextState();
		
		l = 7;
		// Re-initializing Counters
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 32; j++)
				C[l - i][j] = C[l - i][j] ^ X[l - ((i + 4) % 8)][j];
		
	}
	
	
	private static void IVsetup(boolean[] IV)
	{
		boolean[] temp1 = new boolean[32];
		boolean[] temp2 = new boolean[32];
		
		for(int i = 0; i < 16; i++)
		{
			temp1[i] = IV[i];
			temp2[i] = IV[i + 16];
		}
		
		for(int i = 16; i < 32; i++)
		{
			temp1[i] = IV[i + 16];
			temp2[i] = IV[i + 32];
		}
		
		int l = 7;
		// Modifying counter variables
		for(int j = 0; j < 8; j++)
		{
			for(int i = 0; i < 32; i++)
			{
				if(j % 4 == 0)
					C[l - j][i] = C[l - j][i] ^ IV[i + 32];
				
				else if(j % 4 == 1)
					C[l - j][i] = C[l - j][i] ^ temp1[i];
				
				else if(j % 4 == 2)
					C[l - j][i] = C[l - j][i] ^ IV[i];
				
				else if(j % 4 == 3)
					C[l - j][i] = C[l - j][i] ^ temp2[i];
			}
		}
		
		// Iterating system four times
		for(int i = 0; i < 4; i++)
			nextState();
		
	}
	
	private static void updateCounters()
	{
		BigInteger[] C_int = new BigInteger[8];
		
		for(int i = 0; i < 8; i++)
			C_int[i] = binTobigDec(C[i]);
		
		long[] A = new long[8];
		
		// Constants A1,...,A7
		A[0] = A[3] = A[6] = 1295307597;  // 0x4D34D34D
		A[1] = A[4] = A[7] = 3545052371L; // 0xD34D34D3
		A[2] = A[5]        = 886263092;   // 0x34D34D34
		
		BigInteger cb = BigInteger.ZERO;
		if(carryBit)
			cb = BigInteger.valueOf(1);
		
		BigInteger temp = BigInteger.ZERO;
		int j = 7;
		for(int i = 0; i < 8; i++)
		{
			temp = C_int[j].add((BigInteger.valueOf(A[i])).add(cb));
			
			cb = temp.divide(BigInteger.valueOf((long)Math.pow(2, 32)));
			
			C[j] = convert(temp.mod(BigInteger.valueOf((long)(Math.pow(2, 32)))),32);
			
			j--;
		}
		
		if(cb.compareTo(BigInteger.ONE) == 0)
			carryBit = true;
		else
			carryBit = false;
		
	}
	
	private static boolean[] g(boolean[] X, boolean[] C)
	{
		boolean[] g = new boolean[32];
		
		BigInteger temp = (binTobigDec(X).add(binTobigDec(C))).mod(BigInteger.valueOf(4294967296L));
		
		BigInteger sqtemp = temp.multiply(temp);
		
		boolean[] t = new boolean[64];
		                                         		
		t = convert(sqtemp, 64);
		
		boolean[] LSW = new boolean[32];
		boolean[] MSW = new boolean[32];
		
		for(int i = 0; i < 32; i++)
		{
			LSW[i] = t[i + 32];
			MSW[i] = t[i];
		}
		
		for(int i = 0; i < 32; i++)
			g[i] = LSW[i] ^ MSW[i];
		
		return g;
	}
	
	private static void nextState()
	{
		updateCounters();
		
		boolean[][] G = new boolean[8][32];
		
		for(int i = 0; i < 8; i++)
			G[i] = g(X[i], C[i]);
		
		X[7] = convert((binToDec(G[7]) + binToDec(leftRotation(G[0],16)) + binToDec(leftRotation(G[1],16)) % 4294967296L), 32);
		X[6] = convert((binToDec(G[6]) + binToDec(leftRotation(G[7],8))  + binToDec(G[0]) % 4294967296L), 32);
		X[5] = convert((binToDec(G[5]) + binToDec(leftRotation(G[6],16)) + binToDec(leftRotation(G[7],16)) % 4294967296L), 32);
		X[4] = convert((binToDec(G[4]) + binToDec(leftRotation(G[5],8))  + binToDec(G[6]) % 4294967296L), 32);
		X[3] = convert((binToDec(G[3]) + binToDec(leftRotation(G[4],16)) + binToDec(leftRotation(G[5],16)) % 4294967296L), 32);
		X[2] = convert((binToDec(G[2]) + binToDec(leftRotation(G[3],8))  + binToDec(G[4]) % 4294967296L), 32);
		X[1] = convert((binToDec(G[1]) + binToDec(leftRotation(G[2],16)) + binToDec(leftRotation(G[3],16)) % 4294967296L), 32);
		X[0] = convert((binToDec(G[0]) + binToDec(leftRotation(G[1],8))  + binToDec(G[2]) % 4294967296L), 32);
		
	}
	
	private static boolean[] extract()
	{
		boolean[] S = new boolean[128];
		
		nextState();
		
		int j = 15;
		for(int i = 0; i < 16; i++)
		{
			S[127 - (j + 16 * 0)] = X[7][i + 16] ^ X[2][i];
			S[127 - (j + 16 * 1)] = X[7][i]      ^ X[4][i + 16];
			S[127 - (j + 16 * 2)] = X[5][i + 16] ^ X[0][i];
			S[127 - (j + 16 * 3)] = X[5][i]      ^ X[2][i + 16];
			S[127 - (j + 16 * 4)] = X[3][i + 16] ^ X[6][i];
			S[127 - (j + 16 * 5)] = X[3][i]      ^ X[0][i + 16];
			S[127 - (j + 16 * 6)] = X[1][i + 16] ^ X[4][i];
			S[127 - (j + 16 * 7)] = X[1][i]      ^ X[6][i + 16];
			j--;
		}
		
		return S;
	}
	
	public static boolean[] concatenate(boolean[] b1, boolean[] b2)
	{
		boolean[] rez = new boolean[b1.length + b2.length];
		
		int i = 0;
		for(; i < b1.length; i++)
			rez[i] = b1[i];
		
		for(int j = 0; j < b2.length; j++)
		{
			rez[i] = b2[j];
			i++;
		}
		
		return rez;
	}
	
	public static boolean[] leftRotation(boolean[] b, int n)
	{
		boolean[] rez = new boolean[b.length];
		
		for(int i = n; i < b.length; i++)
			rez[i - n] = b[i];
		
		for(int i = b.length - n; i < b.length; i++)
			rez[i] = b[i - (b.length - n)];
		
		return rez;
	}
	
	public static boolean[] StringToBoolean(String s)
	{
		int l = 0;
		boolean[] rez = new boolean[s.length() * 4];
		
		for(int i = 0; i < s.length(); i++)
		{
			boolean[] temp = hexToBin(s.charAt(i), 4);
			for(int j = 0; j < 4; j++)
				rez[l++] = temp[j];
		}
		return rez;
	}
	
	public static String BooleanToString(boolean[] b)
	{
		int f = 0;
		String rez = "";
		for(int i = 0; i < b.length / 4; i++)
		{
				boolean[] temp = new boolean[4];
				for(int j = 0; j < 4; j++)
					temp[j] = b[f++];
				
				rez += binToHex(temp);
		}
		
		return rez;
	}
	
	public static char binToHex(boolean[] b)
	{
		String s = "";
		
		char c = '.';
		
		for(int i = 0; i < b.length; i++)
		{
			if(b[i])
				s += "1";
			else
				s += "0";
		}
		
		if(s.equals("0000"))
			c = '0';
		
		else if(s.equals("0001"))
			c = '1';
		
		else if(s.equals("0010"))
			c = '2';
		
		else if(s.equals("0011"))
			c = '3';
		
		else if(s.equals("0100"))
			c = '4';
		
		else if(s.equals("0101"))
			c = '5';
		
		else if(s.equals("0110"))
			c = '6';
		
		else if(s.equals("0111"))
			c = '7';
		
		else if(s.equals("1000"))
			c = '8';
		
		else if(s.equals("1001"))
			c = '9';
		
		else if(s.equals("1010"))
			c = 'A';
		
		else if(s.equals("1011"))
			c = 'B';
		
		else if(s.equals("1100"))
			c = 'C';
		
		else if(s.equals("1101"))
			c = 'D';
		
		else if(s.equals("1110"))
			c = 'E';
		
		else if(s.equals("1111"))
			c = 'F';
		
		return c;
	}
	
	public static boolean[] hexToBin(char c, int n)
	 {
		int dec = hexToDec(c);		
		
		 boolean[] rez = new boolean[n];
		 
			String s = "";
			while(dec != 0)
			{
				s = dec % 2 + s;
				dec /= 2;
			}
			
			
			for(int i = s.length() - 1; i >= 0; i--)
			{
				if(s.charAt(i) == '1')
					rez[n-1] = true;
				n--;
			}
			 
			 return rez;
	 }
	
	public static int hexToDec(char c)
	{
		int rez = 0;
		
		if((int)c >= 48 && (int)c <= 57)
			rez = (int)c - 48;
		
		else if(c == 'A')
			rez = 10;
		
		else if(c == 'B')
			rez = 11;
		
		else if(c == 'C')
			rez = 12;
		
		else if(c == 'D')
			rez = 13;
		
		else if(c == 'E')
			rez = 14;
		
		else if(c == 'F')
			rez = 15;
		
		return rez;
	}
	
	public static long binToDec(boolean[] a, boolean b)
	{
		long rez = 0;
		int j = 0;
		for(int i = a.length - 1; i >= 0; i--)
		{
			if(a[i])
				rez += Math.pow(2, j);
			j++;
		}
		
		if(b && a.length == 32)
			rez = rez % (long)Math.pow(2, 32);
		
		return rez;
	}
	
	public static BigInteger binTobigDec(boolean[] a)
	{
		BigInteger rez = BigInteger.ZERO;
		int j = 0;
		BigInteger temp = BigInteger.ONE;
		for(int i = a.length - 1; i >= 0; i--)
		{
			if(a[i])
				rez = rez.add(temp);
			j++;
			temp = temp.multiply(BigInteger.valueOf(2));
		}
		
		return rez;
	}

	public static long binToDec(boolean[] a)
	{
		long rez = 0;
		int j = 0;
		for(int i = a.length - 1; i >= 0; i--)
		{
			if(a[i])
				rez += Math.pow(2, j);
			j++;
		}
		
		if(a.length == 32)
			rez = rez % (long)Math.pow(2, 32);
		
		return rez;
	}
	
	public static boolean[] convert(BigInteger dec, int n)
	 {
		 boolean[] rez = new boolean[n];
		 
			String s = "";
			while(!(dec.compareTo(BigInteger.ZERO) == 0))
			{
				s = dec.mod(BigInteger.valueOf(2)) + s;
				dec = dec.divide(BigInteger.valueOf(2));
			}
			
			
			for(int i = s.length() - 1; i >= 0; i--)
			{
				if(s.charAt(i) == '1')
					rez[n-1] = true;
				n--;
			}
			 
			 return rez;
	 }
	
	public static boolean[] convert(long dec, int n)
	 {
		 boolean[] rez = new boolean[n];
		 
		 if(n == 32)
			 dec = dec % 4294967296L;
		 
			String s = "";
			while(dec != 0)
			{
				s = dec % 2 + s;
				dec /= 2;
			}
			
			
			for(int i = s.length() - 1; i >= 0; i--)
			{
				if(s.charAt(i) == '1')
					rez[n-1] = true;
				n--;
			}
			 
			 return rez;
	 }	

}
