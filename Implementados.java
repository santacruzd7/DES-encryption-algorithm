package Cifrador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Implementados {
	
	//******************** METODOS YA IMPLEMENTADOS *******************************
	//*********************** IMPLEMENTED METHODS **********************************

	/** 
	 * @param bytes Array de bytes a escribir en el fichero
	 * @param nombreFichero
	 * @param bytes Array of bytes to write in a file
	 * @param nombreFichero file name

	 */
	public static void arrayFichero(byte [] bytes, String nombreFichero){
		try{
			if(bytes!=null){
				File salida=new File(nombreFichero);
				OutputStream os = new FileOutputStream(salida);
				os.write(bytes);
				os.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** 
	 * @param nombreFichero Nombre del fichero que se quiere leer
	 * @return array de bytes conteniendo el texto de dicho fichero
	 * @param nombreFichero File name that you want to read
	 * @return array of bytes with the file content
	 */
	public static byte [] ficheroArray (String nombreFichero){
		byte[] bytes=null;
		File entrada=new File(nombreFichero);
		try{
			InputStream is = new FileInputStream(entrada);
	        bytes = new byte[(int)entrada.length()];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "+entrada.getName());
	        }
	        is.close();
		}catch(IOException e){
			e.printStackTrace();
	    }
		return bytes;
	}

	/**
	 * @param a
	 * @param b
	 * @return Resultado de la operacion a XOR b
	 * @return Result of a XOR b operation 
	 */
	public static byte [] ejecutarXOR (byte [] a, byte [] b){
		if(a.length!=b.length) return null;
		byte [] resultado = new byte [a.length];
		for(int i=0;i<resultado.length;i++){
			resultado[i]=(byte)( a[i] ^ b[i]);
		}
		return resultado;
	}

	/**
	 * @param texto Array de bytes del que se quiere obtener un bloque
	 * @param nbloque Numero del bloque a obtener
	 * @param longitud tamaño del bloque a obtener
	 * @return array de bytes del bloque
	 * @param texto Array of bytes you want to get a block
	 * @param nbloque number of block 
	 * @param longitud block size
	 * @return array of bytes of the block
	 */
	public static byte [] obtenerBloque(byte [] texto, int nbloque, int longitud){
		byte[] resultado =new byte[longitud];
		for(int i=0;i<longitud;i++){
			resultado[i]=texto[nbloque*longitud+i];
		}
		return resultado;
	}

	/**
	 * @param bloque
	 * @return la mitad izquierda del bloque indicado
	 * @param bloque Block
	 * @return left half of the block

	 */
	public static byte [] mitadIzquierdaBloque(byte[] bloque){ 
		int mitad = bloque.length/2;
		byte [] resultado = new byte[mitad];
		for(int i=0;i<mitad;i++){
			resultado[i]=bloque[i];
		}
		return resultado;
	}

	/**
	 * @param bloque
	 * @return la mitad derecha del bloque indicado
	 * @param bloque Block
	 * @return right half of the block
	 */
	public static byte [] mitadDerechaBloque(byte[] bloque){ 
		int mitad = bloque.length/2;
		byte [] resultado = new byte[mitad];
		for(int i=0;i<mitad;i++){
			resultado[i]=bloque[mitad+i];
		}
		return resultado;
	}
}
