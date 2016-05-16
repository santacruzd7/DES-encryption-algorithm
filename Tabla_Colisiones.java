package Hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Tabla_Colisiones {

	public static void main(String[] args) {
		if (args.length < 3)
		{
			System.out.println("USO: java Hash.Tabla_Colisiones funcion_resumen nombre_fichero_entrada tamaño_buffer_lectura [nombre_fichero_salida]");
			System.out.println("");
			System.out.println("  funcion_resumen: nombre de la funcion Resumen (p. ej.: XORBasica)");
			System.out.println("  nombre_fichero_entrada: nombre del fichero al que realizar las pruebas");
			System.out.println("  tamaño_buffer_lectura: tamaño del buffer empleado para leer el fichero de entrada (bytes)");
			System.out.println("  nombre_fichero_salida: nombre del informe generado. Opcional");			
			System.out.println("");
			System.exit(-1);
		}
		try {
			String funcionResumen = args[0];
			String nombreFicheroEntrada = args[1];
			String tamayoBufferLectura = args[2];
			String nombreFicheroSalida = (args.length == 4)?args[3]:null;
			if (new Integer(tamayoBufferLectura).intValue() > 255) {
				System.err.println("El tamaño del buffer de lectura debe ser como máximo de 255 bytes");
				System.exit(-1);
			}
			calcularTablaColisiones(nombreFicheroEntrada, funcionResumen, new Integer(tamayoBufferLectura).intValue(),nombreFicheroSalida);
		}
		catch (Exception e) {
			System.err.println("Ha ocurrido un error...");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/////////////////////// METODO A IMPLEMENTAR POR EL ALUMNO ///////////////////////////
	////////////////////////// METHOD TO BE IMPLEMENTED///////////////////////////////////

	/**
	 * Método que genera una tabla de colisiones donde se debe poder conocer los hashes generados y el número de colisiones. 
	 * @param nombreFichero Nombre del fichero que contiene la información
	 * @param funcionResumen nombre de la función resumen a probar
	 * @param tamayoBloqueLeido Longitud del segmento
	 * @param salida Nombre del fichero de salida, en caso de querer volcar el resultado a disco (Opcional)
	 * Method that generates a collisions table where it is possible to know the hashes and the number of collisions.
	 * @param nombreFichero File name that contains the information
	 * @param funcionResumen Hash name to probe
	 * @param tamayoBloqueLeido segment size
	 * @param salida File name in order to save the generated table (Optional)
	 */
	public static void calcularTablaColisiones(String nombreFichero, String funcionResumen, int tamayoBloqueLeido, String salida) throws Exception{
		int numberOfHash = 0;
		int numberOfHashWithCollision = 0;
		int numberOfCollisions = 0;
		ArrayList<byte []> hashes = new ArrayList<byte []>();
		
		//byte [] file = Implementados.fichero2Array(nombreFichero); //@TODO: leer message from nombreFichero;
		
		byte[] file=null;
		File entrada=new File(nombreFichero);
		try{
			InputStream is = new FileInputStream(entrada);
	        file = new byte[(int)entrada.length()];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < file.length && (numRead=is.read(file, offset, file.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        if (offset < file.length) {
	            throw new IOException("Could not completely read file "+entrada.getName());
	        }
	        is.close();
		}catch(IOException e){
			e.printStackTrace();
	    }
		

		for(int i = 0; i<file.length/tamayoBloqueLeido; i++){ //Decompose in messages size tamañobufferlecture
			byte [] message = new byte[tamayoBloqueLeido];
			for(int j=0;j<tamayoBloqueLeido;j++){
				message[j]=file[i*8+j];
			}
			
			byte [] hash;
			byte [] tempHash;
			boolean coll;
			boolean equal;
			switch(funcionResumen){
				case "XORBasica":
					hash = Funcion_Resumen.XORBasica(message);
					hashes.add(hash);
					numberOfHash++;
					coll = false;
					for(int j=0; j<hashes.size()-1; j++){ //@TODO: creo que hash.size -1 para no comparar con actual
						tempHash = hashes.get(j);
						equal = true;
						for(int k = 0; k<hash.length; k++){
							if(hash[k] != tempHash[k]){
								equal =  false;
							}
						}
						if(equal){
							numberOfCollisions++;
							coll = true;
						}
					}
					if(coll){
						numberOfHashWithCollision++;
					}
					break;
				case "XORDesplazamiento":
					hash = Funcion_Resumen.XORDesplazamiento(message);
					hashes.add(hash);
					numberOfHash++;
					coll = false;
					for(int j=0; j<hashes.size()-1; j++){ //@TODO: creo que hash.size -1 para no comparar con actual
						tempHash = hashes.get(j);
						equal = true;
						for(int k = 0; k<hash.length; k++){
							if(hash[k] != tempHash[k]){
								equal =  false;
							}
						}
						if(equal){
							numberOfCollisions++;
							coll = true;
						}
					}
					if(coll){
						numberOfHashWithCollision++;
					}
					break;
				case "Encadenada":
					hash = Funcion_Resumen.Encadenada(message);
					hashes.add(hash);
					numberOfHash++;
					coll = false;
					for(int j=0; j<hashes.size()-1; j++){ //@TODO: creo que hash.size -1 para no comparar con actual
						tempHash = hashes.get(j);
						equal = true;
						for(int k = 0; k<hash.length; k++){
							if(hash[k] != tempHash[k]){
								equal =  false;
							}
						}
						if(equal){
							numberOfCollisions++;
							coll = true;
						}
					}
					if(coll){
						numberOfHashWithCollision++;
					}
					break;
			}
		}
		System.out.println("Number of hashes calculated: " + numberOfHash);
		System.out.println("Number of hashes with collision calculated: " + numberOfHashWithCollision);
		System.out.println("Number of collisions calculated: " + numberOfCollisions);
		System.out.println("Proportion of collisions: " + (double) (numberOfHashWithCollision/numberOfHash));
	}

}
