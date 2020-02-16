public class Test {

	public static void main(String[] args) {
		
		RabbitStreamCipher rsc = new RabbitStreamCipher();
		
		String key = "00000000000000000000000000000000";
		//String key = "912813292E3D36FE3BFC62F1DC51C3AC";
		//String iv = "C373F575C1267E59";
		String iv = "0000000000000000";
		String m = "007014A78459657841254586945554444444755900000000000111111112254487841654A";
		
		String[] c = rsc.encrypt(m,key,iv);
		
		System.out.println("Plaintext:   " + m + "\n");
		System.out.println("Ciphertext: " + c[0] + "\n");
		System.out.println("Algorithm output: \n");
		System.out.println(c[1]);
				
	}
	
}
