import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by user on 13/04/2017.
 */
public class Encryptor
{
    private Map<Character, Character> m_Key;
    private String m_IV;
    private final int m_BlockSize = 10;
    
    public Encryptor(Map<Character, Character> key, String IV)
    {
        m_Key = key;
        m_IV = IV;
    }
    
    public Encryptor(String keyFilePath, String IV) throws Exception
    {
        m_Key = CommonFunctions.ReadKeyFromFile(keyFilePath);
        m_IV = IV;
    }
    
    public String Encrypt(String plainText)
    {
        String result = "";
        String previousSegment = m_IV;
        int missingChars = m_BlockSize - plainText.length() % m_BlockSize;
        if (missingChars != 0)
            plainText += StringUtils.repeat((char) 0, missingChars);
        
        for (int i = 0; i < plainText.length(); i += m_BlockSize)
        {
            String segment = plainText.substring(i, i + m_BlockSize);
            String xor = CommonFunctions.XOR(previousSegment, segment);
            String cipheredSegment = CommonFunctions.EncryptDecryptSegmentByKey(xor, m_Key);
            result += cipheredSegment;
            previousSegment = cipheredSegment;
        }
        return result;
    }
}
