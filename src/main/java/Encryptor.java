import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by user on 13/04/2017.
 */
public class Encryptor
{
    private Map<Character, Character> m_Key;
    private byte[] m_IV;
    private int m_BlockSize;
    
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
        
        // Pad with 0's:
        Arrays.fill(result, plainTextBytes.length, result.length, (byte) 0);
        
        for (int i = 0; i < plainTextBytes.length; i++)
        {
            byte toXor = i < m_BlockSize ? vector[i] : result[i - m_BlockSize];
            
            byte toAdd = CommonFunctions.XorByte(toXor, plainTextBytes[i]);
            
            if (m_Key.containsKey((char) toAdd))
                toAdd = (byte) (m_Key.get((char) toAdd).charValue());
            
            result[i] = toAdd;
        }
        return new String(result, "UTF-8");
    }
}
