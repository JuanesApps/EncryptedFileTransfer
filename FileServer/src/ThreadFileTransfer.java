import java.io.*;
import java.math.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

public class ThreadFileTransfer extends Thread {

	private ObjectInputStream in;
	private ObjectOutputStream out;

	Socket socket;

	private DiffieHellmanKex dh = new DiffieHellmanKex();

	public ThreadFileTransfer(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {

			//Negociacion de la clave simetrica
			DHPublicKey publicKey;
			KeyPair keyPair;
			byte[] secret;

			InputStream is = socket.getInputStream();
			in = new ObjectInputStream(is);
			out = new ObjectOutputStream(socket.getOutputStream());

			//Recepcion de lon valores del cliente
			publicKey = dh.getServerPublic(in);

			keyPair = dh.generateKeyPair();

			//Envio de la clave publica al cliente
			dh.passPublicToServer((DHPublicKey) keyPair.getPublic(), out);

			//Se genera la clave privada
			secret = dh.computeDHSecretKey((DHPrivateKey) keyPair.getPrivate(), publicKey);

			secret = MessageDigest.getInstance("MD5").digest(secret);
			byte[] symmetricKey = Arrays.copyOf(secret, 16);


			//Se inicializan los parametros para hacer la desencripcion
			Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec ky = new SecretKeySpec(symmetricKey, "AES");

			ci.init(Cipher.DECRYPT_MODE, ky, new IvParameterSpec(new byte[16]));


			//Recepcion del archivo encriptado
			byte[] mybytearray = new byte[100000000];
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			int bytesRead;
			int current = 0;
			
			bytesRead = is.read(mybytearray, 0, mybytearray.length);
			current = bytesRead;
			mybytearray = Arrays.copyOf(mybytearray, bytesRead);
			
			// Se imprime el archivo encriptado recibido 
			fos = new FileOutputStream("./docs/recievedFileEncrypted.txt");
			bos = new BufferedOutputStream(fos);
			bos.write(mybytearray, 0, mybytearray.length);
			bos.flush();

			//Desencripcion del archivo
			mybytearray = ci.doFinal(mybytearray);


			//Calculo del hash MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mybytearray);
			byte[] md5Hash = md.digest();
			System.out.println("El MD5 es "+new String(md5Hash));

			// Se imprime el archivo desencriptado recibido 
			fos = new FileOutputStream("./docs/recievedFilePlain.txt");
			bos = new BufferedOutputStream(fos);
			bos.write(mybytearray, 0, mybytearray.length);
			bos.flush();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
