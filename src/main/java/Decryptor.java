import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by user on 16/04/2017.
 */
public class Decryptor
{
    private Map<Character, Character> m_ReversedKey;
    private String m_IV;
    private final int m_BlockSize = 10;
    
    public Decryptor(Map<Character, Character> reversedKey, String IV)
    {
        m_ReversedKey = reversedKey;
        m_IV = IV;
    }
    
    public Decryptor(String keyFilePath, String IV) throws Exception
    {
        m_ReversedKey = CommonFunctions.ReadKeyReversedFromFile(keyFilePath);
        m_IV = IV;
    }
    
    public String Decrypt(String cipher)
    {
        String text = "";
        String previousCipher = m_IV;
        for (int i = 0; i < cipher.length(); i += m_BlockSize)
        {
            String segment = cipher.substring(i, i + m_BlockSize);
            String decrypted = CommonFunctions.EncryptDecryptSegmentByKey(segment, m_ReversedKey);
            String xor = CommonFunctions.XOR(previousCipher, decrypted);
            text += xor;
            previousCipher = segment;
        }
        String toRemove = "";
        toRemove += (char) 0;
        return StringUtils.strip(text, toRemove);
    }
    
    public String DecryptByte(byte[] cipherBytes) throws UnsupportedEncodingException
    {
        byte[] result = new byte[cipherBytes.length];
        byte[] vectorBytes = m_IV.getBytes();
        for (int i = 0; i < cipherBytes.length; i++)
        {
            char cipherChar = (char) cipherBytes[i];
            byte decrypted;
            
            if (m_ReversedKey.containsKey(cipherChar))
                decrypted = (byte) m_ReversedKey.get(cipherChar).charValue();
            else
                decrypted = cipherBytes[i];
            
            if (i < m_BlockSize)
                result[i] = CommonFunctions.XorByte(decrypted, vectorBytes[i]);
            else
                result[i] = CommonFunctions.XorByte(decrypted, cipherBytes[i - m_BlockSize]);
        }
        String toRemove = "";
        toRemove += (char) 0;
        return StringUtils.strip(new String(result), toRemove);
    }
}
