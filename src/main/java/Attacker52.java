import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Attacker52
{
    private final int SAMPLE_SIZE = 1000;
    
    private int m_MinKeyErrors = Integer.MAX_VALUE;
    private Map<Character, Character> m_MinKey;
    private byte[] m_CipherText;
    private byte[] m_KnownPlainText;
    private byte[] m_KnownCipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private Map<Character, Character> m_Key;
    private HashSet<String> m_EnglishDictionary;
    
    public Attacker52(String cipherTextPath, String knownPlainTextPath, String knownCipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_KnownPlainText = CommonFunctions.ReadBytesFromFile(knownPlainTextPath);
        m_KnownCipherText = CommonFunctions.ReadBytesFromFile(knownCipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
        m_Key = new HashMap<Character, Character>();
        m_EnglishDictionary = new HashSet<String>(IOUtils.readLines(ClassLoader.getSystemResourceAsStream("Dictionary")));
    }
    
    public void Attack() throws Exception
    {
        BuildKeyFromKnownTexts();
        CompleteMissingCharsInKey();
        CommonFunctions.WriteKeyToFile(m_Key, m_OutputPath);
    }
    
    private void BuildKeyFromKnownTexts()
    {
        for (int i = 0; i < m_KnownPlainText.length; i++)
        {
            char xorResult = (char) CommonFunctions.XorByte(m_KnownPlainText[i], m_IV[i]);
            if (!((xorResult >= 'a' && xorResult <= 'z') || (xorResult >= 'A' && xorResult <= 'Z')))    // Not an English letter
                continue;
            if (!m_Key.containsKey(xorResult))
                m_Key.put(xorResult, (char) m_KnownCipherText[i]);
        }
    }
    
    private void CompleteMissingCharsInKey() throws IOException
    {
        String permutation = "";
        for (char c = 'A'; c <= 'z'; c++)
            if (!m_Key.values().contains(c) && (c >= 'a' || c <= 'Z'))
                permutation += c;
        BruteForceDecryption("", permutation);
        m_Key = m_MinKey;
    }
    
    private void BruteForceDecryption(String prefix, String permutation) throws IOException
    {
        int n = permutation.length();
        if (n == 0)     // A permutation is reached
        {
            Map<Character, Character> key = new HashMap<Character, Character>(m_Key);
            int index = 0;
            for (char c = 'A'; c <= 'z'; c++)
                if (!key.containsKey(c) && (c >= 'a' || c <= 'Z'))
                {
                    key.put(c, prefix.charAt(index));
                    index++;
                }
            
            byte[] sample = new byte[SAMPLE_SIZE];
            System.arraycopy(m_CipherText, 0, sample, 0, SAMPLE_SIZE);
            Decryptor decryptor = new Decryptor(key, m_IV, 52);
            String decryptedSample = decryptor.Decrypt(sample);
            String[] splittedSample = decryptedSample.split("[\\.,\\s!;?:&\"\\[\\]]+");
            int errorCounter = 0;
            for (String word : splittedSample)
            {
                if (word.matches(".*\\d.*"))    // 'word' is a number
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
