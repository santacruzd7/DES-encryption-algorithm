package Cifrador;

/**
 * 
 */
import java.io.*;

/**
 * PRACTICA 2 - IMPLEMENTACION DE CIFRADOR SEGURIDAD EN LAS TECNOLOGIAS DE LA
 * INFORMACION 2011-2012. GRADO EN INGENIERIA INFORMATICA UNIVERSIDAD CARLOS III
 * DE MADRID
 * 
 */
public class Cifrador {

	/**
	 * 
	 */
	public Cifrador() {
		// TODO Auto-generated constructor stub
	}

	static File entrada;
	static File salida;
	static byte[] clave;
	static boolean cifrar;
	// En BYTES
	static int tamBloque = 8;
	static int tamBloqueV2 = 4;
	static final int RONDAS = 8;

	// ******************** METODOS OBLIGATORIOS (ES NECESARIO IMPLEMENTARLOS
	// PARA APROBAR LA PRACTICA) *************************

	/**
	 * Metodo que cifra todo un array de bytes segun el modo ECB (mínimo
	 * OBLIGATORIO)
	 * 
	 * @param bytes
	 *            Array de bytes a cifrar
	 * @return Resultado del cifrado
	 */
	static byte[] cifrarECB(byte[] bytes) {

		byte[] resultado = new byte[bytes.length];
		System.out.println("[CifrarECB] Comienza el metodo");

		try {

			for (int i = 0; i < (bytes.length / tamBloque); i++) { // For each
																	// block

				// Initialization (getting necessary block in order to begin the
				// rounds.
				byte[] currentBlock = obtenerBloque(bytes, i * tamBloque,
						(i + 1) * tamBloque);
				byte[] l = mitadIzquierdaBloque(currentBlock);
				byte[] r = mitadDerechaBloque(currentBlock);

				// Performing the 8 rounds
				for (int j = 0; j < 8; j++) {
					currentBlock = ejecutarRonda(r, l,
							generarSubclave(clave, j + 1));

					// The block is already swapped with the method
					// "ejecutarRonda".
					l = mitadIzquierdaBloque(currentBlock);
					r = mitadDerechaBloque(currentBlock);
				}

				// copying the block to the variable resultado.
				for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
					resultado[j] = currentBlock[j % tamBloque];
				}
			}

		} catch (Exception e) {
			System.out.println("[CifrarECB] Problema. Cerrando...");
			e.printStackTrace();
		}
		System.out.println("[CifrarECB] Resultado:" + new String(resultado));
		return resultado;
	}

	/**
	 * Metodo que descifra todo un array de bytes segun el modo ECB (minimo
	 * OBLIGATORIO)
	 * 
	 * @param bytes
	 *            Array de bytes a descifrar
	 * @return Resultado del descifrado
	 */
	static byte[] descifrarECB(byte[] bytes) {

		byte[] resultado = new byte[bytes.length];
		System.out.println("[DescifrarECB] Comienza el metodo");

		try {
			for (int i = 0; i < (bytes.length / tamBloque); i++) { // For each
																	// block
				// Initialization (getting necessary block in order to begin the
				// rounds.
				byte[] currentBlock = obtenerBloque(bytes, i * tamBloque,
						(i + 1) * tamBloque);

				// First difference with the encryption: Since in the encryption
				// we invert the blocks at the end, we have to reverse them here
				// again.
				byte[] r = mitadIzquierdaBloque(currentBlock);
				byte[] l = mitadDerechaBloque(currentBlock);

				// Performing the 8 rounds
				for (int j = 0; j < 8; j++) {
					currentBlock = ejecutarRonda(r, l,
							generarSubclave(clave, 8 - j));
					l = mitadIzquierdaBloque(currentBlock);
					r = mitadDerechaBloque(currentBlock);
				}

				// Second difference, since we used the permutation in
				// "ejecutarRonda", we have to - again - reverse it here.
				// byte[] currentBlockCopy = currentBlock.clone();
				for (int j = 0; j < r.length; j++) {
					currentBlock[j] = r[j];
					currentBlock[j + currentBlock.length / 2] = l[j];
				}

				// copying the block to the variable resultado.
				for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
					resultado[j] = currentBlock[j % tamBloque];
				}
			}

		} catch (Exception e) {
			System.out.println("Problema. Cerrando...");
			e.printStackTrace();
		}
		System.out.println("[DescifrarECB] Resultado descifrado ECB:"
				+ new String(resultado));
		return resultado;

	}

	/**
	 * Metodo que ejecuta una ronda de CIFRADO O DESCIFRADO
	 * 
	 * @param r
	 *            Parte DERECHA del bloque que se va a cifrar/descifrar
	 * @param l
	 *            Parte IZQUIERDA del bloque que se va a cifrar/descifrar
	 * @param subclave
	 *            Subclave que se utilizara en esta ronda
	 * @return Array de bytes con el resultado del cifrado/descifrado
	 */
	static byte[] ejecutarRonda(byte[] r, byte[] l, byte[] subclave) {

		byte[] resultado = new byte[r.length + l.length];

		// Perform the XOR operation with the encrypted right part and the
		// left part.
		l = ejecutarXOR(ejecutarFuncionF(r, subclave), l);

		// Invert halves.
		for (int i = 0; i < r.length; i++) {
			resultado[i] = r[i];
			resultado[i + r.length] = l[i];
		}

		// returns the result in the variable "resultado"
		return resultado;
	}

	/**
	 * Desplazar a la izquierda tantos grupos de 4 bits de clave como indique
	 * vez
	 * 
	 * @param clave
	 *            Array de bytes a desplazar
	 * @param vez
	 *            Numero de grupos de 4 bits a desplazar
	 * @return Array de bytes conteniendo los mismos bytes que la clave,
	 *         desplazando a la izquierda los correspondientes bits
	 */
	static byte[] generarSubclave(byte[] clave, int vez) {

		byte[] resultado = new byte[clave.length];

		if (vez % 2 == 0 && vez != 0) { // If vez is even

			for (int i = 0; i < 4; i++) {
				resultado[i] = clave[(i + vez / 2) % 4];
			}

		} else { // If vez is odd

			for (int i = 0; i < 4; i++) {
				resultado[i] = clave[(i + (vez - 1) / 2) % 4];
			}

			byte[] buffer = (byte[]) resultado.clone();
			for (int j = 0; j < 4; j++) {
				resultado[j] = (byte) (buffer[j] << 4 | buffer[(j + 1) % 4] >> 4);
			}
		}
		return resultado;
	}

	/**
	 * Devuelve el resultado de aplicar la función F sobre bloque usando la
	 * subclave, teniendo en cuenta las simplificaciones que se han indicado.
	 * 
	 * @param bloque
	 *            Array de bytes sobre el que se aplicara la funcion F. RECUERDE
	 *            QUE LA FUNCION F SE APLICA SOBRE LA MITAD DEL BLOQUE QUE SE
	 *            CONSIDERA EN LA RONDA
	 * @param subclave
	 *            Subclave que se va a utilizar para ejecutar la funcion F
	 * @return Resultado de aplicar la funcion F sobre el bloque indicado
	 */
	static byte[] ejecutarFuncionF(byte[] bloque, byte[] subclave) {

		// It will store in "resultado" the result of applying substitution to
		// the result of the XOR operation with bloque and subclave
		byte[] resultado = sustituir(ejecutarXOR(bloque, subclave));

		// Returns that result
		return resultado;
	}

	/**
	 * Metodo que implementa la fase de sustitucion de la funcion F teniendo en
	 * cuenta las simplificaciones que se han indicado.
	 * 
	 * @param bloque
	 * @return Devuelve el resultado de la fase de sustitución (dentro de la
	 *         función F)
	 */
	static byte[] sustituir(byte[] bloque) {

		byte[] resultado = new byte[bloque.length];

		// With the next for loop it adds 9 to each byte of the block ("bloque")
		// and stores it in "resultado"
		for (int i = 0; i < bloque.length; i++)
			resultado[i] = (byte) (bloque[i] + 9); // % 128 or %255 ? (one bit
													// for the sign)
		return resultado;
	}

	/**
	 * Metodo que deshace la fase de sustitucion de la funcion F teniendo en
	 * cuenta las simplificaciones que se han indicado.
	 * 
	 * @param bloque
	 * @return Devuelve el resultado de la fase de sustitución (dentro de la
	 *         función F)
	 */
	static byte[] deshacerSustituir(byte[] bloque) {

		byte[] resultado = new byte[bloque.length];

		// With the next for loop it 9 to each byte of the block ("bloque")
		// and stores it in "resultado"
		for (int i = 0; i < bloque.length; i++)
			resultado[i] = (byte) (bloque[i] - 9);

		return resultado;
	}

	/**
	 * Metodo que implementa la fase de permutacion de la funcion F. Permuta los
	 * bytes del bloque en un orden determinado.
	 * 
	 * @param bloque
	 *            Array de bytes a permutar
	 * @return Devuelve el resultado de permutar los bytes del bloque en un
	 *         orden determinado.
	 */
	static byte[] ejecutarPermutacionF(byte[] bloque) {

		// No permutation have been implemented because in the compulsory part,
		// no permutation has been specified.. The result is exactly the same
		// than the input.
		return bloque;
	}

	// ******************** METODOS OPCIONALES (NO ES NECESARIO IMPLEMENTARLOS
	// PARA SUPERAR EL CRITERIO 1) *************************

	/*
	 * Here are all the v1, in other words, the block ciphers with the F
	 * function simplified (No P-BOX nor expansions)
	 */

	/* CBC - part */

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a cifrar
	 * @return resultado cifrado
	 */
	static byte[] cifrarCBCv1(byte[] bytes) {

		/*
		 * In order to implement that block cipher, we'll keep quite similar
		 * caracteritics than the ECB Cipher Block size: 8 bytes. Encryption
		 * method: F. Subkeys: same than ECB. In ordero to perform that block
		 * cipher, we'll need to "invent" a initialization vector.
		 */

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

		// currentBlock will get, in each round, the corresponding block from
		// the plaintext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloque);

		// result will be used to add the result got for each block
		byte[] result = new byte[bytes.length];
		int numberOfBlock = bytes.length / tamBloque; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// cipherText will store, at each round, the result of encrypting each
		// block of plaintext
		byte[] cipherText = null;

		// We perform the fist round here:
		if (numberOfBlock > 0) {

			// Get the ciphertext of the first block
			cipherText = performRoundEncryptionCBCv1(iv, currentBlock,
					generarSubclave(clave, 1));

			// Add it to the result
			for (int i = 0; i < cipherText.length; i++)
				result[i] = cipherText[i];
		}

		// And now that we solved the particular case of the initilzation, we
		// perform all the remaining rounds
		for (int i = 1; i < numberOfBlock; i++) {

			// Here it takes the corresponding block
			currentBlock = obtenerBloque(bytes, i * tamBloque, (i + 1)
					* tamBloque);

			// Get the ciphertext of that block
			cipherText = performRoundEncryptionCBCv1(cipherText, currentBlock,
					generarSubclave(clave, i + 1));

			// And add it to the result
			for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
				result[j] = cipherText[j % tamBloque];
			}
		}
		System.out.println("[CifrarCBC] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a descifrar
	 * @return resultado del descifrado
	 */
	static byte[] descifrarCBCv1(byte[] bytes) {

		// This method is almost the same than the encryption one, except that
		// we have to execute each step at a reverse order (as well as the F
		// function).

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

		// currentBlock will get, in each round, the corresponding block from
		// the ciphertext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloque);

		// result will be used to add the result got for each block
		byte[] result = new byte[bytes.length];
		int numberOfBlock = bytes.length / tamBloque; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// plainText will store, at each round, the result of decrypting each
		// block of ciphertext
		byte[] plainText = null;

		// We perform the fist round here:
		if (numberOfBlock > 0) {

			// Get the plaintext from the first block of the ciphertext
			plainText = performRoundDecryptionCBCv1(iv, currentBlock,
					generarSubclave(clave, 1));

			// Add it to the result
			for (int i = 0; i < plainText.length; i++)
				result[i] = plainText[i];
			plainText = (byte[]) currentBlock.clone();
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {

			// Get the next block of ciphertext
			currentBlock = obtenerBloque(bytes, i * tamBloque, (i + 1)
					* tamBloque);

			// Get the corresponding plaintext
			plainText = performRoundDecryptionCBCv1(plainText, currentBlock,
					generarSubclave(clave, i + 1));

			// Add it to the result
			for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
				result[j] = plainText[j % tamBloque];
			}
			plainText = (byte[]) currentBlock.clone();
		}
		System.out.println("[DcCifrarCBC] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param a
	 *            array of bits
	 * @param b
	 *            array of bits
	 * @param subkey
	 *            the key that is going to be used for the encryption
	 * @return resultado del descifrado
	 */
	static byte[] performRoundEncryptionCBCv1(byte[] a, byte[] b, byte[] subkey) {

		// Executes the function F declared for the compulsory
		// part with the result of a XOR b and the subkey
		byte[] result = ejecutarFuncionF(ejecutarXOR(a, b), subkey);

		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param a
	 *            array of bits
	 * @param b
	 *            array of bits
	 * @param subkey
	 *            the key that is going to be used for the decryption
	 * @return resultado del descifrado
	 */
	static byte[] performRoundDecryptionCBCv1(byte[] a, byte[] b, byte[] subkey) {

		// It executes XOR with the decryption function with b and subkey, and a
		return ejecutarXOR(executionDecryptionFunctionFv1(b, subkey), a);
	}

	/* OFB - part */

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a cifrar
	 * @return resultado cifrado
	 */
	static byte[] cifrarOFBv1(byte[] bytes) {

		/*
		 * Once again, we'll keep the same characteristics than the ECB block
		 * cipher
		 */

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

		// currentBlock will get, in each round, the corresponding block from
		// the plaintext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloque);

		// result will be used to add the result got for each block
		byte[] result = new byte[bytes.length];
		int numberOfBlock = bytes.length / tamBloque; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// cipherText will store, at each round, ciphertext corresponding to
		// each block of plaintext
		byte[] cipherText = null;

		// tempText will store, at each round, the result of applying the
		// function to the block of plaintext
		byte[] tempText = null;

		// We perform the fist round here
		if (numberOfBlock > 0) {

			// Execute the function and store it into tempText
			tempText = ejecutarFuncionF(iv, generarSubclave(clave, 1));

			// Get the corresponding ciphertext
			cipherText = ejecutarXOR(tempText, currentBlock);

			// Add it to the report
			for (int i = 0; i < cipherText.length; i++)
				result[i] = cipherText[i];
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {

			// Get the current block of plaintext
			currentBlock = obtenerBloque(bytes, i * tamBloque, (i + 1)
					* tamBloque);

			// Execute the function to the block, storing it into tempText
			tempText = ejecutarFuncionF(tempText, generarSubclave(clave, i + 1));

			// Get the corresponding ciphertext
			cipherText = ejecutarXOR(tempText, currentBlock);

			// Add it to the result
			for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
				result[j] = cipherText[j % tamBloque];
			}
		}
		System.out.println("[CifrarOFBv1] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a descifrar
	 * @return resultado del descifrado
	 */
	static byte[] descifrarOFBv1(byte[] bytes) {

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

		// currentBlock will get, in each round, the corresponding block from
		// the ciphertext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloque);

		// result will be used to add the result got for each block
		byte[] result = new byte[bytes.length];
		int numberOfBlock = bytes.length / tamBloque; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// plainText will store, at each round, plaintext corresponding to
		// each block of ciphertext
		byte[] plainText = null;

		// tempText will store, at each round, the result of applying the
		// function to the block of ciphertext
		byte[] tempText = null;

		// We perform the fist round here
		if (numberOfBlock > 0) {

			// Apply the function to the first block
			tempText = ejecutarFuncionF(iv, generarSubclave(clave, 1));

			// Get the corresponding plaintext
			plainText = ejecutarXOR(tempText, currentBlock);

			// Add it to the result
			for (int i = 0; i < plainText.length; i++)
				result[i] = plainText[i];
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {

			// Get the next block of ciphertext
			currentBlock = obtenerBloque(bytes, i * tamBloque, (i + 1)
					* tamBloque);

			// Apply the function to it
			tempText = ejecutarFuncionF(tempText, generarSubclave(clave, i + 1));

			// Get the corresponding plaintext
			plainText = ejecutarXOR(tempText, currentBlock);

			// Add to the result
			for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
				result[j] = plainText[j % tamBloque];
			}
		}
		System.out.println("[DescifrarOFBv1] Resultado:" + new String(result));
		return result;
	}

	/* Here are the global methods for the v1 part */

	/**
	 * Método OPCIONAL
	 * 
	 * @param bloque
	 *            is the block for which decryption function will be apply
	 * @param subclave
	 *            is the key regarding which the decryption function will be
	 *            applied
	 * @return the result of applying the decryption function
	 */
	static byte[] executionDecryptionFunctionFv1(byte[] bloque, byte[] subclave) {

		byte[] resultado = (byte[]) bloque.clone();

		// Doing back the substitution.
		resultado = deshacerSustituir(resultado);

		// Doing back the XOR.
		return ejecutarXOR(resultado, subclave);

	}

	/*
	 * 
	 * Here are all the v2, in other words, the block cipher with the F function
	 * extanded (With P-BOX and expansions)
	 */

	/* CBC - part */

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a cifrar
	 * @return resultado cifrado
	 */
	static byte[] cifrarCBCv2(byte[] bytes) {
		/*
		 * In order to implement that second version of the block cipher,we
		 * change a bit the specification of the first version Block size: 4
		 * bytes. Encryption method: F. Subkeys: same than ECB. In ordero to
		 * perform that block cipher, we'll need to "invent" a initialization
		 * vector.
		 */

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03 };

		// currentBlock will get, in each round, the corresponding block from
		// the plaintext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloqueV2);

		// result will be used to add the result got for each block
		byte[] result = new byte[2 * bytes.length];
		int numberOfBlock = bytes.length / tamBloqueV2; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// cipherText will store, at each round, ciphertext corresponding to
		// each block of plaintext
		byte[] cipherText = null;

		// We perform the first round here
		if (numberOfBlock > 0) {

			// Get the ciphertext of the first block
			cipherText = performRoundEncryptionCBCv2(iv, currentBlock,
					generarSubclave(clave, 1));

			// Add it to the result
			for (int i = 0; i < cipherText.length; i++)
				result[i] = cipherText[i];
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {

			// Get the current block (size tamBloqueV2)
			currentBlock = obtenerBloque(bytes, i * tamBloqueV2, (i + 1)
					* tamBloqueV2);

			// Get its corresponding ciphertext
			cipherText = performRoundEncryptionCBCv2(reducirBloque(cipherText),
					currentBlock, generarSubclave(clave, i + 1));

			// Add it to the result
			for (int j = i * tamBloque; j < (i + 1) * tamBloque; j++) {
				result[j] = cipherText[j % tamBloque];
			}
		}
		System.out.println("[CifrarCBC] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a descifrar
	 * @return resultado del descifrado
	 */
	static byte[] descifrarCBCv2(byte[] bytes) {

		// This method is almost the same than the encryption one, except that
		// we have to execute each step at a reverse order (as well as the F
		// function).

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03 };

		// currentBlock will get, in each round, the corresponding block from
		// the ciphertext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloque);

		// result will be used to add the result got for each block
		byte[] result = new byte[bytes.length / 2];
		int numberOfBlock = bytes.length / tamBloque; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// plaintext will store, at each round, plaintext corresponding to
		// each block of ciphertext
		byte[] plainText = null;

		// We perform the fist round here
		if (numberOfBlock > 0) {

			// Get the plaintext corresponding to the first block
			plainText = performRoundDecryptionCBCv2(iv, currentBlock,
					generarSubclave(clave, 1));

			// Add it to the result
			for (int i = 0; i < plainText.length; i++)
				result[i] = plainText[i];
			plainText = (byte[]) currentBlock.clone();
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {

			// Get the next block of ciphertext
			currentBlock = obtenerBloque(bytes, i * tamBloque, (i + 1)
					* tamBloque);

			// Get the corresponding plaintext
			plainText = performRoundDecryptionCBCv2(reducirBloque(plainText),
					currentBlock, generarSubclave(clave, i + 1));

			// Add it to the result
			for (int j = i * tamBloqueV2; j < (i + 1) * tamBloqueV2; j++) {
				result[j] = plainText[j % tamBloqueV2];
			}
			plainText = (byte[]) currentBlock.clone();
		}
		System.out.println("[DcCifrarCBC] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param a
	 *            Array of bytes
	 * @param b
	 *            Array of bytes
	 * @param subkey
	 *            Array of bytes used as key
	 * @return result of the round
	 */
	static byte[] performRoundEncryptionCBCv2(byte[] a, byte[] b, byte[] subkey) {

		// First, expand the result of a XOR b
		byte[] result = expandirBloque(ejecutarXOR(a, b));

		// After that, XOR that result with the subkey
		result = ejecutarXOR(result, subkey);

		// Execute substitution
		result = sustituir(result);

		// And permutation
		result = executePermutationFv2(result);

		// return the result
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param a
	 *            Array of bytes
	 * @param b
	 *            Array of bytes
	 * @param subkey
	 *            Array of bytes used as key
	 * @return result of the round
	 */
	static byte[] performRoundDecryptionCBCv2(byte[] a, byte[] b, byte[] subkey) {

		return ejecutarXOR(executionDecryptionFunctionFv2(b, subkey), a);
	}

	/* OFB - part */

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a cifrar
	 * @return resultado cifrado
	 */
	static byte[] cifrarOFBv2(byte[] bytes) {
		/*
		 * Once again, we'll keep the same characteristics than the ECB block
		 * cipher
		 */

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03 };

		// currentBlock will get, in each round, the corresponding block from
		// the plaintext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloqueV2);

		// result will be used to add the result got for each block
		byte[] result = new byte[2 * bytes.length];
		int numberOfBlock = bytes.length / tamBloqueV2; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// cipherText will store, at each round, ciphertext corresponding to
		// each block of plaintext
		byte[] cipherText = null;

		// tempText will store, at each round, the result of applying the
		// encryption to the corresponding block
		byte[] tempText = null;

		// We perform the fist round here
		if (numberOfBlock > 0) {
			
			// Apply the encryption to the first block
			tempText = performRoundEncryptionOFBv2(iv,
					generarSubclave(clave, 1));
			
			// Get the corresponding ciphertext
			cipherText = ejecutarXOR(tempText, currentBlock);
			
			// Add it to the result
			for (int i = 0; i < cipherText.length; i++)
				result[i] = cipherText[i];
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {
			
			// Get next block
			currentBlock = obtenerBloque(bytes, i * tamBloqueV2, (i + 1)
					* tamBloqueV2);
			
			// Apply the encryption
			tempText = performRoundEncryptionOFBv2(tempText,
					generarSubclave(clave, i + 1));
			
			// Get the corresponding cipherText
			cipherText = ejecutarXOR(tempText, currentBlock);
			
			// Add it to the result
			for (int j = i * tamBloqueV2; j < (i + 1) * tamBloqueV2; j++) {
				result[j] = cipherText[j % tamBloqueV2];
			}
		}
		System.out.println("[CifrarOFBv2] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bytes
	 *            Array de bytes a descifrar
	 * @return resultado del descifrado
	 */
	static byte[] descifrarOFBv2(byte[] bytes) {

		/*
		 * Once again, we'll keep the same characteristics than the ECB block
		 * cipher
		 */

		// iv will be the initialization vector used in the first round
		byte[] iv = { 0x00, 0x01, 0x02, 0x03 };

		// currentBlock will get, in each round, the corresponding block from
		// the ciphertext
		byte[] currentBlock = obtenerBloque(bytes, 0, tamBloqueV2);

		// result will be used to add the result got for each block
		byte[] result = new byte[2 * bytes.length];
		int numberOfBlock = bytes.length / tamBloque; // Since the length of
														// bytes is a multiple
														// of the block size, we
														// don't have any
														// problem here.

		// plainText will store, at each round, plaintext corresponding to
		// each block of ciphertext
		byte[] plainText = null;

		// tempText will store, at each round, the result of applying the
		// encryption to the corresponding block
		byte[] tempText = null;

		// We perform the fist round here
		if (numberOfBlock > 0) {
			
			// Apply the encryption to the first block
			tempText = performRoundEncryptionOFBv2(iv,
					generarSubclave(clave, 1));
			
			// Get the corresponding plaintext
			plainText = ejecutarXOR(tempText, currentBlock);
			
			// Add it to the result
			for (int i = 0; i < plainText.length; i++)
				result[i] = plainText[i];
		}

		// And now that we solved the particular case of the initilzation
		for (int i = 1; i < numberOfBlock; i++) {
			
			// Get the next block
			currentBlock = obtenerBloque(bytes, i * tamBloqueV2, (i + 1)
					* tamBloqueV2);
			
			// Get the result of the encryption round
			tempText = performRoundEncryptionOFBv2(tempText,
					generarSubclave(clave, i + 1));
			
			// Get the corresponding plaintext
			plainText = ejecutarXOR(tempText, currentBlock);
			
			//Add it to the result
			for (int j = i * tamBloqueV2; j < (i + 1) * tamBloqueV2; j++) {
				result[j] = plainText[j % tamBloqueV2];
			}
		}
		System.out.println("[DescifrarOFBv2] Resultado:" + new String(result));
		return result;
	}

	/**
	 * Metodo OPCIONAL
	 * 
	 * @param a
	 *            Array of bytes to apply the function
	 * @param subkey
	 *            Array of bytes containing the key to be used in the function
	 * @return result of the encryption
	 */
	static byte[] performRoundEncryptionOFBv2(byte[] a, byte[] subkey) {

		// First we expand the block
		byte[] result = expandirBloque(a);

		// Then XOR it with the subkey storing the result into "result"
		result = ejecutarXOR(result, subkey);

		// Then apply substitution to result
		result = sustituir(result);

		// Finally permutate result
		result = executePermutationFv2(result);

		// And return it
		return reducirBloque(result);
	}

	/* Here are the global methods for the v2 part */

	/**
	 * Metodo OPCIONAL
	 * 
	 * @param bloque
	 *            Array of bytes to apply the function
	 * @param subclave
	 *            Array of bytes containing the key to be used in the function
	 * @return result of the function
	 */
	static byte[] executionDecryptionFunctionFv2(byte[] bloque, byte[] subclave) {

		byte[] resultado = (byte[]) bloque.clone();

		// Doing back the permutation
		resultado = restorePermutationFv2(resultado);

		// Doing back the substitution
		resultado = deshacerSustituir(resultado);

		// Doing back the XOR
		return reducirBloque(ejecutarXOR(resultado, subclave));

	}

	/**
	 * Metodo OPCIONAL
	 * 
	 * @param bloque
	 *            Array de bytes a expandir
	 * @return Resultado de la expansion
	 */
	static byte[] expandirBloque(byte[] bloque) {

		// We assume it has length 4
		if (bloque.length == 4) {
			byte[] result = new byte[8];

			/*
			 * If we consider the bloque as: 0|1|2|3 Then after the expansion,
			 * the block will be: 1|3|0|3|0|1|2|3
			 */

			// E-BOX
			result[0] = bloque[1];
			result[1] = bloque[3];
			result[2] = bloque[0];
			result[3] = bloque[3];
			result[4] = bloque[0];
			result[5] = bloque[1];
			result[6] = bloque[2];
			result[7] = bloque[3];

			return result;
		}

		// If the length of the block is not 4, we return it the same
		return bloque;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bloque
	 *            is the expanded block that will be reduced
	 * @return result of the reduction
	 */
	static byte[] reducirBloque(byte[] bloque) {

		// We assume it has length of 8
		if (bloque.length == 8) {
			byte[] result = new byte[4];

			// We take the last 4 bytes, that are in order
			result[0] = bloque[4];
			result[1] = bloque[5];
			result[2] = bloque[6];
			result[3] = bloque[7];

			return result;
		}

		return bloque;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bloque
	 *            is the block that will be permutated
	 * @return result of the permutation
	 */
	static byte[] executePermutationFv2(byte[] bloque) {

		// We suppose that the bloque is of size 8 blocks.
		if (bloque.length == 8) {
			byte[] result = new byte[bloque.length];

			/*
			 * If we consider the bloque as: 0|1|2|3|4|5|6|7 Then after the
			 * permutation, the block will be: 4|1|0|2|5|7|3|6
			 */

			// P-BOX
			result[0] = bloque[4];
			result[1] = bloque[1];
			result[2] = bloque[0];
			result[3] = bloque[2];
			result[4] = bloque[5];
			result[5] = bloque[7];
			result[6] = bloque[3];
			result[7] = bloque[6];

			return result;
		}

		// If the bloque is not as expected, we return bloque without any
		// modification.
		return bloque;
	}

	/**
	 * Método OPCIONAL
	 * 
	 * @param bloque
	 *            is the block that will be restored
	 * @return result of restoring the permutation
	 */
	static byte[] restorePermutationFv2(byte[] bloque) {

		// We suppose that the bloque is of size 8 blocks.
		if (bloque.length == 8) {
			byte[] result = new byte[bloque.length];

			/*
			 * If we consider the bloque as: 0|1|2|3|4|5|6|7 Then after the
			 * permutation restore, the block will be: 2|1|3|6|0|4|7|5
			 */

			// P-BOX
			result[0] = bloque[2];
			result[1] = bloque[1];
			result[2] = bloque[3];
			result[3] = bloque[6];
			result[4] = bloque[0];
			result[5] = bloque[4];
			result[6] = bloque[7];
			result[7] = bloque[5];

			return result;
		}

		// If the bloque is not as expected, we return bloque without any
		// modification.
		return bloque;
	}

	// ******************** METODOS YA IMPLEMENTADOS (NO REQUIEREN ATENCION POR
	// PARTE DEL ALUMNO) *******************************

	/**
	 * Metodo principal de la clase
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			// USO: java Cifrador entrada salida contraseña [cifrar-ECB |
			// cifrar-CBC | descifrar-ECB | descifrar-CBC]
			salida = new File(args[1]);
			clave = args[2].getBytes();

			byte[] bytes = ficheroArray(args[0]);
			byte[] resultado = null;
			if (args[3].equalsIgnoreCase("cifrar-ECB")) {
				resultado = cifrarECB(bytes);
				cifrar = true;
			} else if (args[3].equalsIgnoreCase("cifrar-CBC")) {
				resultado = cifrarCBCv1(bytes);
				cifrar = true;
			} else if (args[3].equalsIgnoreCase("descifrar-CBC")) {
				resultado = descifrarCBCv1(bytes);
				cifrar = false;
			} else if (args[3].equalsIgnoreCase("cifrar-CBCv2")) {
				resultado = cifrarCBCv2(bytes);
				cifrar = true;
			} else if (args[3].equalsIgnoreCase("descifrar-CBCv2")) {
				resultado = descifrarCBCv2(bytes);
				cifrar = false;
			} else if (args[3].equalsIgnoreCase("descifrar-ECB")) {
				resultado = descifrarECB(bytes);
				cifrar = false;
			}
			// OFB added.
			else if (args[3].equalsIgnoreCase("cifrar-OFB")) {
				resultado = cifrarOFBv1(bytes);
				cifrar = true;
			} else if (args[3].equalsIgnoreCase("descifrar-OFB")) {
				resultado = descifrarOFBv1(bytes);
				cifrar = false;
			} else if (args[3].equalsIgnoreCase("cifrar-OFBv2")) {
				resultado = cifrarOFBv2(bytes);
				cifrar = true;
			} else if (args[3].equalsIgnoreCase("descifrar-OFBv2")) {
				resultado = descifrarOFBv2(bytes);
				cifrar = false;
			} else {
				// descifrar-CBC
				resultado = descifrarCBCv1(bytes);
				cifrar = false;
			}
			arrayFichero(resultado);
		} catch (Exception e) {
			System.out
					.println("USO: java Cifrador FicheroEntrada FicheroSalida clave [cifrar-ECB |  cifrar-CBC | cifrar-CBCv2 | cifrar-OFB | cifrar-OFBv2 | descifrar-ECB |  descifrar-CBC | descifrar-CBCv2 | descifrar-OFB | descifrar-OFBv2]");
			System.out.println("Cerrando...");
			e.printStackTrace();
		}

	}

	/**
	 * Método YA IMPLEMENTADO
	 * 
	 * @param bytes
	 *            Array de bytes a escribir en el fichero 'salida'
	 * 
	 */
	static void arrayFichero(byte[] bytes) {
		try {
			OutputStream os = new FileOutputStream(salida);
			os.write(bytes);
			os.close();
		} catch (Exception e) {
			System.out.println("Problema. Cerrando...");
			e.printStackTrace();
		}
	}

	/**
	 * Método YA IMPLEMENTADO
	 * 
	 * @param nombreFichero
	 *            Nombre del fichero que se quiere leer
	 * @return array de bytes conteniendo el texto de dicho fichero, o null en
	 *         caso de excepcion
	 */
	static byte[] ficheroArray(String nombreFichero) {
		byte[] bytes = null;
		entrada = new File(nombreFichero);
		try {
			InputStream is = new FileInputStream(entrada);

			// Get the size of the file
			long length = entrada.length();

			// Create the byte array to hold the data
			bytes = new byte[(int) length];
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ entrada.getName());
			}

			// Close the input stream and return bytes
			is.close();
			String res = new String(bytes);
			System.out.println("[DEBUG] bytes:" + res);

		} catch (IOException e) {
			System.out.println("Problema. Cerrando...");
			e.printStackTrace();
		}
		return bytes;

	}

	/**
	 * Metodo YA IMPLEMENTADO
	 * 
	 * @param a
	 * @param b
	 * @return Resultado de la operacion a XOR b, ejecutando BYTE A BYTE la
	 *         operacion XOR
	 */
	static byte[] ejecutarXOR(byte[] a, byte[] b) {
		byte[] resultado = new byte[a.length];
		for (int i = 0; i < resultado.length; i++) {
			resultado[i] = (byte) (a[i] ^ b[i]);
		}
		System.out.println("XOR:" + new String(a) + " ^ " + new String(b)
				+ " = " + new String(resultado));
		System.out.print("");
		return resultado;
	}

	/**
	 * Metodo YA IMPLEMENTADO
	 * 
	 * @param texto
	 *            Array de bytes del que se quiere obtener un bloque
	 * @param inicio
	 *            Indice de inicio del bloque a obtener (debe ser menor que la
	 *            longitud de texto)
	 * @param fin
	 *            Indice del byte final del trozo a obtener (debe ser, como
	 *            mucho, la longitud de texto menos uno)
	 * @return array de bytes del bloque
	 */
	static byte[] obtenerBloque(byte[] texto, int inicio, int fin) {
		int longitud = fin - inicio;
		byte[] resultado = new byte[longitud];
		for (int i = 0; i < longitud; i++) {
			resultado[i] = texto[inicio + i];
		}
		return resultado;
	}

	/**
	 * Metodo YA IMPLEMENTADO. Devuelve la mitad izquierda del bloque que se
	 * pasa por parametro
	 * 
	 * @param bloque
	 * @return la mitad izquierda del bloque indicado
	 */
	static byte[] mitadIzquierdaBloque(byte[] bloque) {
		int mitad = bloque.length / 2;
		byte[] resultado = new byte[mitad];
		for (int i = 0; i < mitad; i++) {
			resultado[i] = bloque[i];
		}
		System.out.println("MITAD IZQ BLOQUE:" + new String(bloque) + ": ES: "
				+ new String(resultado));
		return resultado;

	}

	/**
	 * Metodo YA IMPLEMENTADO. Devuelve la mitad derecha del bloque que se pasa
	 * por parametro
	 * 
	 * @param bloque
	 * @return la mitad derecha del bloque indicado
	 */
	static byte[] mitadDerechaBloque(byte[] bloque) {
		int mitad = bloque.length / 2;
		byte[] resultado = new byte[mitad];
		for (int i = 0; i < mitad; i++) {
			resultado[i] = bloque[mitad + i];
		}
		System.out.println("MITAD DER BLOQUE:" + new String(bloque) + ": ES: "
				+ new String(resultado));
		return resultado;

	}
}
