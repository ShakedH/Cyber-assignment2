import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
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
        m_Key = ReadKeyFromFile(keyFilePath);
        m_IV = IV;
    }
    
    public String Encrypt(String plainText)
    {
        String result = "";
        String previousSegment = m_IV;
        if(plainText.length() %10 !=0)
        {
//            plainText += String.join();
        }
        for (int i = 0; i < plainText.length() / 10; i++)
        {
            String segment = plainText.substring(i * m_BlockSize, i * m_BlockSize + m_BlockSize);
            segment = EncryptSegment(XOR(previousSegment, segment));
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
    
    private Map<Character, Character> ReadKeyFromFile(String keyFilePath) throws Exception
    {
        Map<Character, Character> key = new HashMap<Character, Character>();
        File file = new File(keyFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null)
        {
            String[] split = st.split(" ");
            key.put(split[0].charAt(0), split[1].charAt(0));
        }
        return key;
    }
    
    public String XOR(String a, String b)
    {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < a.length(); k++)
            sb.append((a.charAt(k) ^ b.charAt(k + (Math.abs(a.length() - b.length())))));
        return sb.toString();
    }
}
