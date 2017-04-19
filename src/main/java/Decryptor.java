import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class Decryptor
{
    private Map<Character, Character> m_ReversedKey;
    private byte[] m_IV;
    private int m_BlockSize;
    
    public Decryptor(Map<Character, Character> key, byte[] IV, int blockSize)
    {
        m_ReversedKey = ReverseKey(key);
        m_IV = IV;
        m_BlockSize = blockSize;
    }
    
    public Decryptor(String keyFilePath, byte[] IV, int blockSize) throws Exception
    {
        m_ReversedKey = ReverseKey(CommonFunctions.ReadKeyFromFile(keyFilePath));
        m_IV = IV;
        m_BlockSize = blockSize;
    }
    
    public String Decrypt(byte[] cipherBytes) throws UnsupportedEncodingException
    {
        byte[] result = new byte[cipherBytes.length];
        byte[] vectorBytes = m_IV;
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
        
        // Remove 0 padding:
        String toRemove = "";
        toRemove += (char) 0;
        return StringUtils.strip(new String(result, "UTF-8"), toRemove);
    }
    
    private Map<Character, Character> ReverseKey(Map<Character, Character> key)
    {
        return MapUtils.invertMap(key);
    }
}
