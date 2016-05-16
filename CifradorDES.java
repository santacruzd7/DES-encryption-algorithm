package Hash;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Clase que implementa un Cifrador DES empleando el Proveedor Criptográfico SunJCE incluído a partir de la versión Java SDK 1.6.
 * Class that implements a DES cipher using the SunJCE chipher included in Java SDK 1.6.
 */

public class CifradorDES {
	
	public static final int MODO_CIFRADO = Cipher.ENCRYPT_MODE;
	public static final int MODO_DESCIFRADO = Cipher.DECRYPT_MODE;
	private SecretKey claveDES = null;
	private Cipher cifrador = null;
	private SecretKeyFactory factoriaClavesDES = null;
	private byte[] desKeyData = { (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09 };
	private byte[] iv = null;

	/**
	 * Instancia el Cifrador DES en modo CBC.
	 * La clave simétrica empleada es interna al cifrador.
	 * El vector de inicialización se generará internamente. Se puede recuperar invocando al método getIV()
	 * Calling the DES cipher in CBC mode.
	 * The simetric key is internal to the cipher.
	 * The IV is internal too. You can get it calling the method getIV()

	 */
	public CifradorDES() throws NoSuchAlgorithmException, NoSuchPaddingException {		
		cifrador = Cipher.getInstance("DES/CBC/NoPadding");
		factoriaClavesDES = SecretKeyFactory.getInstance("DES");
	}
	
	protected void finalize() throws Throwable {
		try
		{
			claveDES = null;
			cifrador = null;
			factoriaClavesDES = null;
		}
		finally
		{
			super.finalize();	
		}		
	}
	
	/**
	 * Método que inicializa el cifrador DES-CBC para operar en el modo indicado. 
	 * La clave empleada es interna al cifrador, así como el vector IV utilizado.
	 * @param modo Modo de operación (CifradorDES.MODO_CIFRADO ó CifradorDES.MODO_DESCIFRADO) 
	 * Method that initializes the DES cipher in CBC mode
	 * The key and the IV are internals to the cipher
	 * @param modo Operating mode (CifradorDES.MODO_CIFRADO or CifradorDES.MODO_DESCIFRADO) 
	 * @throws Exception 
	 */
	public void inicializar(int modo) throws Exception {
		if (modo != MODO_CIFRADO && modo != MODO_DESCIFRADO)
			throw new Exception("["+CifradorDES.class+"][inicializar]: Error en el modo de operación indicado");
		claveDES = factoriaClavesDES.generateSecret(new DESKeySpec(desKeyData));
		cifrador.init(modo, claveDES,new IvParameterSpec(new byte[]{-121, -99, -118, 93, -64, -85, -20, -4}));
		this.iv = cifrador.getIV();		
	}
	
	/**
	 * Metodo que cifra o descifra todo un array de bytes segun el modo CBC, 
	 * @param datos Array de bytes a cifrar/descifrar
	 * @return Resultado del cifrado/descifrado
	 * Method that encrypts or decrypts an array of bytes in CBC mode, 
	 * @param datos Array of bytes to encrypt or decrypt
	 * @return Result of encryption/decryption
	 * @throws Exception 
	 */
	public byte [] operarModoCBC(byte[] datos) throws Exception {
		if (datos.length % 8 != 0)
			throw new Exception("["+CifradorDES.class+"][inicializar]: Los datos a cifrar deben ser múltiplo de 8 bytes (64 bits)");
		return cifrador.doFinal(datos);
	}
	
	/**
	 * Método que devuelve el vector de inicialización IV empleado.
	 * @return Vector de Inicialización IV
	 * Method that gets you the IV
	 * @return Initialization Vector
	 */
	public byte[] getIV() {
		return this.iv;
	}
}
