package Cifrador;

public class Cifrador_Sencillo {

	// modo  True=Cifrando False=Descifrando
	// modo  True=Encrypting False=Decrypting
	
	boolean modo;
	
	//******************** METODOS OBLIGATORIOS *************************
	//********************** MANDATORY METHODS **************************
	
	/** cifrarECB
	 * Metodo que cifra todo un array de bytes segun el modo ECB 
	 * @param bytes Array de bytes a cifrar
	 * @return Resultado del cifrado
	 * Method that encrypt one array of bytes by ECB mode
	 * @param bytes Array of byte to encrypt
	 * @return result of encryption
	 */
	public static byte [] cifrarECB(byte [] bytes, byte [] clave) throws Exception{
		
		/*Text to be encrypted multiple of block size; otherwise padding*/
		byte [] plaintext;
		if((bytes.length % 8)!=0){
			plaintext = new byte [(8*(bytes.length/8+1))];
			for(int i=0; i<bytes.length; i++){
				plaintext[i]=bytes[i];
			}
			for(int i=bytes.length; i<plaintext.length; i++){
				plaintext[i]=32;
			}
		}else{
			plaintext = bytes;
		}
		
		int blocks = plaintext.length / 8;
		byte [] ciphertext = new byte[plaintext.length];
		byte [] block;
		
		//@TODO: el numero del bloque del metodo de obtenerBloque... de 0 - 7 o de 1 - 8
		for(int i=0; i<blocks; i++){
			
			block = Implementados.obtenerBloque(plaintext, i, 8);
			byte [] left = Implementados.mitadIzquierdaBloque(block);
			byte [] right = Implementados.mitadDerechaBloque(block);
			byte [] subkey;
			byte [] blockRound = null;
			
			for(int j=1; j<=8; j++){ //Numero ronda 1 - 8
				subkey = generarSubclave(clave, j);
				blockRound = ejecutarRonda(right, left, subkey);
				left = Implementados.mitadIzquierdaBloque(blockRound);
				right = Implementados.mitadDerechaBloque(blockRound);
			}

			for(int j=0; j<blockRound.length; j++){
				ciphertext[i+j] = blockRound[j];
			}
			/*for(int j=0; j<left.length; j++){ //Also with blockRound if taken outside
				ciphertext[i+j] = left[j];
				ciphertext[i+j+left.length] = right[j];
			}*/
		}
		return ciphertext;
	}
	
	
	/** descifrarECB
	 * Metodo que descifra todo un array de bytes segun el modo ECB
	 * @param bytes Array de bytes a descifrar
	 * @return Resultado del descifrado
	 * Method that decrypt one array of bytes by ECB mode
	 * @param bytes Array of byte to decrypt
	 * @return result of decryption
	 */
	public static byte [] descifrarECB(byte [] bytes, byte [] clave) throws Exception{
		
		byte [] ciphertext;
		if((bytes.length % 8)!=0){
			ciphertext = new byte [(8*(bytes.length/8+1))];
			for(int i=0; i<bytes.length; i++){
				ciphertext[i]=bytes[i];
			}
			for(int i=bytes.length; i<ciphertext.length; i++){
				ciphertext[i]=32;
			}
		}else{
			ciphertext = bytes;
		}
		
		int blocks = ciphertext.length / 8;
		byte [] plaintext = new byte[ciphertext.length];
		byte [] block;
		
		for(int i=0; i<blocks; i++){
			block = Implementados.obtenerBloque(ciphertext, i, 8);
			byte [] left = Implementados.mitadIzquierdaBloque(block);
			byte [] right = Implementados.mitadDerechaBloque(block);
			
			for(int j=8; j>=1; j--){
				byte [] subkey = generarSubclave(clave, j);
				byte [] blockRound = ejecutarRonda(right, left, subkey);
				left = Implementados.mitadIzquierdaBloque(blockRound);
				right = Implementados.mitadDerechaBloque(blockRound);
			}
			
			for(int j=0; j<left.length; j++){
				plaintext[i+j] = left[j];
				plaintext[i+j+left.length] = right[j];
			}
		}
		
		return plaintext;
	}
	
	/** ejecutarRonda
	 * Metodo que ejecuta una ronda de CIFRADO O DESCIFRADO
	 * @param r Parte derecha  del bloque 
	 * @param l Parte izquierda del bloque
	 * @param subclave Subclave que se utilizara en esta ronda
	 * @return Array de bytes con el resultado del cifrado/descifrado
	 * Method that runs one round of encryption/decryption
	 * @param r Right half of the block
	 * @param l Left half of the block
	 * @param subclave Subkey of this round
	 * @return Array of bytes of the result
	 */
	public static byte [] ejecutarRonda(byte [] r, byte[] l, byte [] subclave) throws Exception{
		byte [] leftRound;
		byte [] rightRound;
		byte [] function = ejecutarFuncionF(r, subclave);
		leftRound = r;
		rightRound = Implementados.ejecutarXOR(l, function);
		byte [] result = new byte [2*leftRound.length];
		for(int i = 0; i<leftRound.length; i++){
			result[i] = leftRound[i];
			result[i+leftRound.length] = rightRound[i];
		}
		return result;
	}
	
	/** generarSubclave
	 * Metodo que genera la subclave de la ronda especificada
	 * @param clave Array de bytes que contiene la clave
	 * @param ronda de la subclave a generar
	 * @return Array de bytes conteniendo la subclave
	 * Method that gets the subkey of the round
	 * @param clave Array of bytes that contains the key
	 * @param ronda round of the subkey to be generated
	 * @return Array of bytes that contains the subkey
	 */
	public static byte [] generarSubclave(byte [] clave, int ronda) throws Exception{
		
		/*@TODO: Is it right? Key length 4 bytes long*/
		byte [] key;
		
		if(clave.length>=4){
			key = new byte [4];
			for(int i=0; i<key.length; i++){
				key[i]=clave[i];
			}
		}else if(clave.length==4){
			key = clave;
		}else{
			System.out.println("ERROR: The key is not long enought!");
			return null;
		}
		
		byte [] subkey = new byte[4];
		// If round is odd, which actually is 1st, 3rd, 5th, 7th
		if (ronda%2 != 0){
			for(int i = 0; i<key.length; i++){
				byte keyRight = key[(i + (ronda/2)) % key.length];
				byte keyLeft = key[(i+(ronda/2)+1) % key.length];
				//keyLeft = (byte) (keyLeft >> 4);
				
				byte left = (byte) (0X0F & keyRight);
				left = (byte) (left<<4);
				byte right = (byte) (0XF0 & keyLeft);
				right = (byte) (right>>4);
				
				byte temp = (byte) (left | right);
				subkey[i] = temp;
			}
			// If round is even, which actually is 2nd, 4th, 6th, 8th
		} else {
			for(int i = 0; i<key.length; i++){
				subkey[i] = key[(i+(ronda/2)) % key.length];
			}
		}
		return subkey;
	}
	
	
	/** ejecutarFuncionF
	 * Metodo que aplica la función F
	 * @param bloque Array de bytes que contiene el bloque
	 * @param subclave Subclave que se va a utilizar para ejecutar la funcion F
	 * @return Resultado de aplicar la funcion F sobre el bloque indicado
	 * Method that runs the F function
	 * @param bloque Array of bytes of the block
	 * @param subclave subkey
	 * @return Result of the aplication of the F function
	 */
	public static byte [] ejecutarFuncionF (byte [] bloque, byte [] subclave) throws Exception{ 
		byte [] mix = Implementados.ejecutarXOR(bloque, subclave);
		byte [] substitution = sustituir(mix);
		byte [] permutation = permutar(substitution);
		return permutation;
		
	}
	
	/** sustituir
	 * Metodo que implementa la fase de sustitucion de la funcion F.
	 * @param bloque Array de bytes que contiene el bloque 
	 * @return Devuelve el resultado de la fase de sustitución
	 * Method that implements the substitution fase of the F function
	 * @param bloque Array of bytes of the block
	 * @return Result of the substitution fase
	 */
	public static byte [] sustituir(byte [] bloque) throws Exception{ 
		byte [] result = new byte [bloque.length];
		for(int i=0; i<bloque.length; i++){
			result[i] = (byte) (bloque[i]+i+1); /*@TODO: I'm not sure whether adding one is this or not*/
		}
		return result;
	}	
	
	/**
	 * @param bloque Array de bytes a permutar
	 * @return Devuelve el resultado de permutar los bytes del bloque
 	 * Method that implements the permutation fase of the F function
	 * @param bloque Array of bytes of the block
	 * @return Result of the permutation fase
	 */
	public static byte [] permutar(byte [] bloque) throws Exception{ 
		/*Permutation done byte by byte*/
		int permutation [] = new int []{0,1,2,3,4,5,6,7};
		byte [] result = new byte [bloque.length];
		for(int i = 0; i<bloque.length; i++){
			result[i] = bloque[permutation[i]];
		}
		return result;
	}
	
}
