package Hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Inicio {


	public static void main(String[] args) {
		if (args.length != 2)
		{
			System.out.println("USO: java Hash.Inicio funcion_resumen nombre_fichero_entrada");
			System.out.println("");
			System.out.println("  funcion_resumen: nombre de la funcion Resumen (p. ej.: XORBasica)");
			System.out.println("  nombre_fichero_entrada: nombre del fichero al que calcular el resumen");
			System.out.println("");
			System.exit(-1);
		}
		try {
			String funcionResumen = args[0];
			String nombreFicheroEntrada = args[1];
			byte[] hash=calcularHash(funcionResumen, nombreFicheroEntrada);					
			System.out.println("El hash calculado mediante la funcion resumen " + funcionResumen + " es: " + Implementados.bytesToHex(hash));
		}
		catch (Exception e) {
			System.err.println("Ha ocurrido un error...");
			e.printStackTrace();
			System.out.println("");
			System.exit(-1);
		}
	}
	
	public static byte[] calcularHash( String funcionResumen, String nombreFichero) throws Exception { 
		//byte[] contenidoFichero = Implementados.fichero2Array(nombreFichero);
		
		byte[] contenidoFichero=null;
		File entrada=new File(nombreFichero);
		try{
			InputStream is = new FileInputStream(entrada);
	        contenidoFichero = new byte[(int)entrada.length()];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < contenidoFichero.length && (numRead=is.read(contenidoFichero, offset, contenidoFichero.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        if (offset < contenidoFichero.length) {
	            throw new IOException("Could not completely read contenidoFichero "+entrada.getName());
	        }
	        is.close();
		}catch(IOException e){
			e.printStackTrace();
	    }
		
		/*byte[] contenidoFichero = new byte[]{72, 79, 76, 65, 81, 85, 69, 84, 65, 76, 69,83, 84,65,83, 63};*/
		switch (funcionResumen){
		case "XORBasica":
			return Funcion_Resumen.XORBasica(contenidoFichero);
		case "XORDesplazamiento":
			return Funcion_Resumen.XORDesplazamiento(contenidoFichero);
		case "Encadenada":
			return Funcion_Resumen.Encadenada(contenidoFichero);
		}
		return null;
		
		/*return Funcion_Resumen.Encadenada(contenidoFichero);*/
		
		/*if(funcionResumen == "XORBasica"){
			return Funcion_Resumen.XORBasica(contenidoFichero);
		}else if(funcionResumen == "XORDesplazamiento"){
			return Funcion_Resumen.XORDesplazamiento(contenidoFichero);
		}else if(funcionResumen == "Encadenada"){
			return Funcion_Resumen.Encadenada(contenidoFichero);
		}else{
			return null;
		}*/
	}
		
}
