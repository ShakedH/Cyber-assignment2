import org.apache.commons.lang3.StringUtils;

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
            String decrypted = CommonFunctions.EncryptSegmentByKey(segment, m_ReversedKey);
            String xor = CommonFunctions.XOR(previousCipher, decrypted);
            text += xor;
            previousCipher = segment;
        }
        String toRemove = "";
        toRemove += (char) 0;
        return StringUtils.strip(text, toRemove);
    }
}
