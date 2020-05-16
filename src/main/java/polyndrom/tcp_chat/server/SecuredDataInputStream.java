package polyndrom.tcp_chat.server;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

public class SecuredDataInputStream {

    private DataInputStream input;
    private PrivateKey privateKey;

    public SecuredDataInputStream(DataInputStream input, PrivateKey privateKey) {
        this.input = input;
        this.privateKey = privateKey;
    }

    public int available() throws IOException {
        return input.available();
    }

    public int readInt() throws IOException {
        return input.readInt();
    }

    public String readUTF() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, Base64DecodingException {
        String data = input.readUTF();
        byte[] encryptedData = Base64.getDecoder().decode(data);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] sourceData = cipher.doFinal((encryptedData));
        return new String(sourceData);
    }

    public void close() throws IOException {
        input.close();
    }

}
