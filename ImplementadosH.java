package Hash;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class Implementados {
	
	public static byte [] ejecutarXOR (byte[] a, byte[] b) throws Exception {		
		if (a.length != b.length)
			throw new Exception("Los tamaños de los arrays no coinciden");
		byte[] resultado = Arrays.copyOf(a, a.length);
		for(int i=0; i<resultado.length; i++) {
			resultado[i] ^= (byte)b[i];
		}
		return resultado;
	}
	
	public static byte [] obtenerBloque(byte [] texto, int nbloque, int longitud){
		byte[] resultado =new byte[longitud];
		for(int i=0;i<longitud;i++){
			resultado[i]=texto[nbloque*longitud+i];
		}
		return resultado;
	}
	
	public static String stringToHex(String base) {
		StringBuffer buffer = new StringBuffer();
     	int intValue;
		for(int x = 0; x < base.length(); x++) {
			int cursor = 0;
			intValue = base.charAt(x);
			String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
			for(int i = 0; i < binaryChar.length(); i++) if(binaryChar.charAt(i) == '1') cursor += 1;
			if((cursor % 2) > 0) intValue += 128;
         	if (x == base.length() -1) buffer.append(Integer.toHexString(intValue).toUpperCase());
			else buffer.append(Integer.toHexString(intValue).toUpperCase() + ":");
		}
		return buffer.toString();
	}

	public static String byteToHex(byte in) {
		String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		byte ch = 0x00;
		StringBuffer out = new StringBuffer(2);		    
		ch = (byte) (in & 0xF0);
		ch = (byte) (ch >>> 4);
		ch = (byte) (ch & 0x0F);
		out.append(pseudo[ (int) ch]);
		ch = (byte) (in & 0x0F);
		out.append(pseudo[ (int) ch]);
		String rslt = new String(out);
		return rslt;
	} 
 
	public static String bytesToHex(byte[] in) {
		String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		byte ch = 0x00;
		int i = 0; 
		if (in == null || in.length <= 0) return null;				    
		StringBuffer out = new StringBuffer(in.length * 2);		    
		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0);
			ch = (byte) (ch >>> 4);
			ch = (byte) (ch & 0x0F);
			out.append(pseudo[ (int) ch]);
			ch = (byte) (in[i] & 0x0F);
			out.append(pseudo[ (int) ch]);
			if (i < in.length -1 ) out.append(":");
			i++;
		}
		String rslt = new String(out);
		return rslt;
	} 
	
	public static void array2Fichero(byte [] bytes, String fichero) throws IOException {		
		OutputStream os = new FileOutputStream(fichero);
		os.write(bytes);
		os.close();
	}
	
	public static byte[] fichero2Array (String fichero) throws IOException {		
		byte[] buff = new byte[1024];
		InputStream is = Implementados.class.getResourceAsStream("/"+fichero);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int r = -1;
		while ((r = is.read(buff)) > 0) baos.write(buff, 0, r);
		is.close();
		baos.close();
		return baos.toByteArray();
	}

}
