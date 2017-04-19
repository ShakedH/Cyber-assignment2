import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Attacker10
{
    private final int SAMPLE_SIZE = 1000;
    
    private int m_MinKeyErrors = Integer.MAX_VALUE;
    private Map<Character, Character> m_MinKey;
    private byte[] m_CipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private HashSet<String> m_EnglishDictionary;
    
    public Attacker10(String cipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
        m_EnglishDictionary = new HashSet<String>(IOUtils.readLines(ClassLoader.getSystemResourceAsStream("Dictionary")));
    }
    
    public void Attack() throws IOException
    {
        BruteForceDecryption("", "abcdefgh");
        CommonFunctions.WriteKeyToFile(m_MinKey, m_OutputPath);
    }
    
    private void BruteForceDecryption(String prefix, String permutation) throws IOException
    {
        int n = permutation.length();
        if (n == 0)     // A permutation is reached
        {
            Map<Character, Character> key = new HashMap<Character, Character>();
            for (int i = 0; i < prefix.length(); i++)
                key.put((char) (i + 97), prefix.charAt(i));
            
            byte[] sample = new byte[SAMPLE_SIZE];
            System.arraycopy(m_CipherText, 0, sample, 0, SAMPLE_SIZE);
            Decryptor decryptor = new Decryptor(key, m_IV, 10);
            String decryptedSample = decryptor.Decrypt(sample);
            String[] splittedSample = decryptedSample.split("[\\.,\\s!;?:&\"\\[\\]]+");
            int errorCounter = 0;
            for (String word : splittedSample)
            {
                if (word.matches(".*\\d.*"))
                    continue;
                if (!m_EnglishDictionary.contains(word.toLowerCase()))
                    errorCounter++;
                if (errorCounter > m_MinKeyErrors)
                    return;
            }
            if (errorCounter < m_MinKeyErrors)
            {
                m_MinKeyErrors = errorCounter;
                m_MinKey = key;
            }
        }
        else
        {
            for (int i = 0; i < n; i++)
                BruteForceDecryption(prefix + permutation.charAt(i), permutation.substring(0, i) + permutation.substring(i + 1, n));
        }
    }
}
