import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by user on 16/04/2017.
 */
public class Decryptor
{
    private Map<Character, Character> m_ReversedKey;
    private byte[] m_IV;
    private final int m_BlockSize = 10;
    
    public Decryptor(Map<Character, Character> reversedKey, byte[] IV)
    {
        m_ReversedKey = reversedKey;
        m_IV = IV;
    }
    
    public Decryptor(String keyFilePath, byte[] IV) throws Exception
    {
        m_ReversedKey = CommonFunctions.ReadKeyReversedFromFile(keyFilePath);
        m_IV = IV;
    }
    
    public String DecryptByte(byte[] cipherBytes) throws UnsupportedEncodingException
    {
        byte[] result = new byte[cipherBytes.length];
        byte[] vectorBytes = m_IV;
        for (int i = 0; i < cipherBytes.length; i++)
        {
            char cipherChar = (char) cipherBytes[i];
            
            byte decrypted = m_ReversedKey.containsKey(cipherChar) ?
                    (byte) m_ReversedKey.get(cipherChar).charValue() :
                    cipherBytes[i];
            
            result[i] = i < m_BlockSize ?
                    CommonFunctions.XorByte(decrypted, vectorBytes[i]) :
                    CommonFunctions.XorByte(decrypted, cipherBytes[i - m_BlockSize]);
        }
        
        // Remove 0 padding:
        String toRemove = "";
        toRemove += (char) 0;
        return StringUtils.strip(new String(result, "UTF-8"), toRemove);
    }
}
