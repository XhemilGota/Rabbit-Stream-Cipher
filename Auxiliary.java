import java.math.BigInteger;

	public class Auxiliary
	{
	
		public boolean[] concatenate(boolean[] b1, boolean[] b2)
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
		
		public boolean[] leftRotation(boolean[] b, int n)
		{
			boolean[] rez = new boolean[b.length];
			
			for(int i = n; i < b.length; i++)
				rez[i - n] = b[i];
			
			for(int i = b.length - n; i < b.length; i++)
				rez[i] = b[i - (b.length - n)];
			
			return rez;
		}
		
		public boolean[] StringToBoolean(String s)
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
		
		public String BooleanToString(boolean[] b)
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
		
		public char binToHex(boolean[] b)
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
		
		public boolean[] hexToBin(char c, int n)
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
		
		public int hexToDec(char c)
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
		
		public long binToDec(boolean[] a, boolean b)
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
		
		public BigInteger binTobigDec(boolean[] a)
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
		
		public long binToDec(boolean[] a)
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
		
		public boolean[] convert(BigInteger dec, int n)
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
		
		public boolean[] convert(long dec, int n)
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
