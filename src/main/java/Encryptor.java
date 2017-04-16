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
        
        for (int i = 0; i < plainText.length(); i++)
        {
            String segment = plainText.substring(i * m_BlockSize, i * m_BlockSize + m_BlockSize);
            segment = EncryptSegment(CommonFunctions.XOR(previousSegment, segment));
            result += segment;
            previousSegment = segment;
        }
        return result;
    }
    
    private String EncryptSegment(String segment)
    {
        String result = segment;
        for (Character c : m_Key.keySet())
            result = result.replace(c, m_Key.get(c));
        return result;
    }
    
    
}
