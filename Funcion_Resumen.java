package Hash;

public class Funcion_Resumen {
	
	static final int PADDING_0x0n = 0;
	static final int PADDING_0x1n = 1;
	static final int PADDING_0x10n = 2; 

	/////////////////////// METODOS A IMPLEMENTAR POR EL ALUMNO ///////////////////////////
	//////////////////////////// METHODS TO BE IMPLEMENTED ////////////////////////////////

	/**
	 * Método que devuelve el hash según la Función Resumen XORBasica 
	 * @param contenidoFichero Datos a los que hay que hacer el Hash
	 * @return Los bytes del hash
	 * Method that implements the XORBasica Hash
	 * @param contenidoFichero Input data to obtain the hash
	 * @return Bytes of the Hash
	 * @throws Exception 
	 */
	public static byte[] XORBasica(byte[] contenidoFichero) throws Exception{
		//Supongo tipo de Padding 0, es correct;
		byte [] content = anyadirPadding(contenidoFichero, 8, 0);
		//@TODO: including message length?
		byte [] hash = new byte [8];
		for(int i = 0; i<8; i++){
			hash[i]=content[i];
		}
		
		for(int i = 8; i<content.length; i++){
			hash[i%8] = (byte) (hash[i%8] ^ content[i]);
		}
		
		return hash;
	}

	/**
	 * Método que devuelve el hash según la Función Resumen XORDesplazamiento
	 * @param contenidoFichero Datos a los que hay que hacer el Hash
	 * @return Los bytes del hash
	 * Method that implements the XORDesplazamiento Hash
	 * @param contenidoFichero Input data to obtain the hash
	 * @return Bytes of the Hash
	 * @throws Exception
	 */
	public static byte[] XORDesplazamiento(byte[] contenidoFichero) throws Exception{
		byte [] content = anyadirPadding(contenidoFichero, 8, 1);
		//@TODO: including message length?
		byte [] hash = new byte [8];
		for(int i = 0; i<8; i++){
			hash[i]=0;
		}
		
		/*for(int i = 0; i<(content.length/8); i++){
			hash = rotarIzquierdaBit(hash);
			for(int j = 0; j<8; j++){
				hash[j] = (byte) (hash[j] ^ content[i+j]);
			}
			//@TODO: update the hash value to previous one... no se hace automaticamente?
		}*/
		
		
		for(int i=0; i<(content.length/8); i++){
			//Obtener bloque
			byte [] block = new byte[8];
			for(int j=0;j<8;j++){
				block[j]=content[i*8+j];
			}
			
			hash = rotarIzquierdaBit(hash);
			for(int j = 0; j<8; j++){
				hash[j] = (byte) (hash[j] ^ block[j]);
			}

		}
		
		return hash;
	}

	/**
	 * Método que devuelve el hash según la Función Resumen Encadenada
	 * @param contenidoFichero Datos a los que hay que hacer el Hash
	 * @return Los bytes del hash
	 * Method that implements the Encadenada Hash
	 * @param contenidoFichero Input data to obtain the hash
	 * @return Bytes of the Hash
	 * @throws Exception
	 */
	public static byte[] Encadenada(byte[] contenidoFichero) throws Exception{
		byte [] output = new byte []{1,2,3,4,5,6,7,8}; //initialized as IV
		byte [] message = anyadirLongitud(contenidoFichero);
		message = anyadirPadding(message,8,2); //@TODO: debería hacerlo en otra array?
		
		CifradorDES cipher = new CifradorDES();
		cipher.inicializar(CifradorDES.MODO_CIFRADO);
		
		for(int i=0; i<(message.length/8); i++){
			//Obtener bloque
			byte [] block = new byte[8];
			for(int j=0;j<8;j++){
				block[j]=message[i*8+j];
			}
			
			//Aplicar funcion compresion
			byte [] result = new byte [8];
			for(int j=0;j<8;j++){
				result[j]= (byte) (block[j] ^ output[j]);
			}

			output = cipher.operarModoCBC(result);
		}
		
		return output;
	}

	
	/////////////////////// METODOS DE APOYO A IMPLEMENTAR POR EL ALUMNO ///////////////////////////
	///////////////////////////// HELPER METHODS TO BE IMPLEMENTED /////////////////////////////////
	
	/**
	 * Método que añade el tipo de padding indicado a los datos al final de los mismos.
	 * @param datos Datos a los que añadir el padding
	 * @param tamanyoBloque Tamaño de bloque de referencia para el padding
	 * @param tipoPadding Tipo de padding a añadir. 
	 * @return Los datos con el padding añadido en los bits más significativos
	 * Method that includes the indicated padding 
	 * @param datos array of bytes to include the padding
	 * @param tamanyoBloque block size to the padding
	 * @param tipoPadding Type of padding
	 * @return Array of bytes with the padding included
	 * @throws Exception
	 */
	public static byte[] anyadirPadding(byte[] datos, int tamanyoBloque, int tipoPadding) throws Exception	{	
		byte [] data;
		if((datos.length%tamanyoBloque)!=0){
			data = new byte[(tamanyoBloque*(datos.length/tamanyoBloque+1))];
			for(int i=0; i<datos.length; i++){
				data[i]=datos[i];
			}
		}else{
			return datos;
		}
		
		//@TODO: el tipo de padding es int...
		/*Considero:
		 * 0 = PADDING_0x0n
		 * 1 = PADDING_0x1n
		 * 2 = PADDING_0x10n
		 * */
		switch(tipoPadding){
			case 0:
				for(int i=datos.length; i<data.length; i++){
					data[i]=0;
				}
				break;
			case 1:
				for(int i=datos.length; i<data.length; i++){
					data[i]=-1;
				}
				break;
			case 2: //@TODO: si hay tres nuevos bytes: 0x00 0x00 0x80 ó 0x80 0x00 0x00??
				for(int i=datos.length; i<data.length-1; i++){
					data[i]=0;
				}
				data[data.length-1]=-128;
				/*data[datos.length]=-128;
				for(int i=datos.length+1; i<data.length; i++){
					data[i]=0;
				}*/
				
				break;
		}
		return data;  
	}
	
	/**
	 * Método que añade la longitud de los datos al final de los mismos.
	 * @param datos Datos a los cuales se debe incorporar su longitud
	 * @return Datos con la longitud añadida en los MSB
	 * Method that implements the inclusion of the message length
	 * @param datos Array of bytes to include the length
	 * @return Array of bytes with the length included
	 * @throws Exception
	 */
	public static byte[] anyadirLongitud(byte[] datos) throws Exception {
		byte [] data = new byte [datos.length + 1];
		for(int i = 0; i<datos.length; i++){
			data[i] = datos[i];
		}
		data[datos.length] = (byte) datos.length;
		return data;
	}
	
	/**
	 * Método que rota un bit (desplazamiento circular) en un array de bytes.
	 * @param a Array de bytes
	 * @return El array de bytes de entrada con 1 bit rotado 
	 * Method that implements the shifting the byte array in one bit
	 * @param a Array of bytes
	 * @return The shifting array of bytes
	 * @throws Exception
	 */
	public static byte[] rotarIzquierdaBit (byte[] a) throws Exception {		
		byte [] result = new byte [a.length];
		for(int i = 0; i<a.length; i++){
			
			byte temp1 = (byte) (0x7F & a[i]);
			temp1 = (byte) (temp1<<1);
			
			byte temp2 = (byte) (0x80 & a[(i+1)%a.length]);
			temp2 = (byte) (temp2>>7);
			
			result[i] = (byte) (temp1 | temp2);
		}
		return result;
	}		

}
