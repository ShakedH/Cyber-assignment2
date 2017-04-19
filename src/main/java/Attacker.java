import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Attacker
{
    private final int SAMPLE_SIZE = 1000;
    private final int ERROR_THRESHOLD = SAMPLE_SIZE / 200;
    
    private int m_MinKeyOccurrences = Integer.MAX_VALUE;
    private Map<Character, Character> m_MinKey;
    
    private byte[] m_CipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private HashSet<String> m_EnglishDictionary;
    
    public Attacker(String cipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
        m_EnglishDictionary = new HashSet<String>(IOUtils.readLines(ClassLoader.getSystemResourceAsStream("Dictionary")));
    }
    
    public void Attack10() throws IOException
    {
        long startTime = System.currentTimeMillis();
        BruteForceDecryption("", "cgadfbeh");
        WriteKeyToFile(m_MinKey.values());
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);
    }
    
    private void BruteForceDecryption(String prefix, String permutation) throws IOException
    {
        int n = permutation.length();
        if (n == 0)     // A permutation is reached
        {
            Map<Character, Character> key = new HashMap<Character, Character>();
            for (int i = 0; i < prefix.length(); i++)
                key.put((char)(i + 97), prefix.charAt(i));
            
            byte[] sample = new byte[SAMPLE_SIZE];
            System.arraycopy(m_CipherText, 0, sample, 0, SAMPLE_SIZE);
            Decryptor decryptor = new Decryptor(key, m_IV);
            String decryptedSample = decryptor.DecryptByte(sample);
            String[] splittedSample = decryptedSample.split("[\\.,\\s!;?:&\"\\[\\]]+");
            int errorCounter = 0;
            for (String word : splittedSample)
            {
                if (word.matches(".*\\d.*"))
                    continue;
                if (!m_EnglishDictionary.contains(word.toLowerCase()))
                    errorCounter++;
                if (errorCounter > m_MinKeyOccurrences)
                    return;
            }
            if (errorCounter < m_MinKeyOccurrences)
            {
                m_MinKeyOccurrences = errorCounter;
                m_MinKey = key;
            }
        }
        else
        {
            for (int i = 0; i < n; i++)
                BruteForceDecryption(prefix + permutation.charAt(i), permutation.substring(0, i) + permutation.substring(i + 1, n));
        }
    }
    
    private void WriteKeyToFile(Collection<Character> key) throws IOException
    {
        CommonFunctions.WriteStringToFile(m_OutputPath, key.toString() + "\n", true);
    }
    
    public void Attack52()
    {
    
    }
}
