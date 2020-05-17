package polyndrom.tcp_chat.server;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class SecuredDataOutputStream {

    private DataOutputStream output;
    private PublicKey publicKey;

    public SecuredDataOutputStream(DataOutputStream output, PublicKey publicKey) {
        this.output = output;
        this.publicKey = publicKey;
    }

    public void writeInt(int value) throws IOException {
        output.writeInt(value);
    }

    public void writeUTF(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {
        byte[] sourceData = data.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = Base64.getEncoder().encode(cipher.doFinal(sourceData));
        output.writeUTF(new String(encryptedData));
    }

    public void flush() throws IOException {
        output.flush();
    }

    public void close() throws IOException {
        output.close();
    }

}
