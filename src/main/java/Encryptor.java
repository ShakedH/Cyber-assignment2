import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by user on 13/04/2017.
 */
public class Encryptor
{
    private Map<Character, Character> m_Key;
    private byte[] m_IV;
    private int m_BlockSize = 10;
    
    public Encryptor(String keyFilePath, byte[] IV, int blockSize) throws Exception
    {
        m_Key = CommonFunctions.ReadKeyFromFile(keyFilePath);
        m_IV = IV;
        m_BlockSize = blockSize;
    }
    
    public String Encrypt(byte[] plainTextBytes) throws UnsupportedEncodingException
    {
        int missingChars = m_BlockSize - plainTextBytes.length % m_BlockSize;
        byte[] result = new byte[plainTextBytes.length + missingChars];
        byte[] vector = m_IV;
        
        for (int i = 0; i < plainTextBytes.length; i++)
        {
            byte toXor = i < m_BlockSize ? vector[i] : result[i - m_BlockSize];
            
            byte toAdd = CommonFunctions.XorByte(toXor, plainTextBytes[i]);
            
            if (m_Key.containsKey((char)toAdd))
                toAdd = (byte)(m_Key.get((char)toAdd).charValue());
            
            result[i] = toAdd;
        }
        
        // Add 0 padding:
        for (int i = plainTextBytes.length; i < plainTextBytes.length + missingChars; i++)
            result[i] = CommonFunctions.XorByte((byte)0, result[i - m_BlockSize]);
        
        return new String(result, "UTF-8");
    }
}
