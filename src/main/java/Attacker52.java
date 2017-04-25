import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Attacker52
{
    private final int SAMPLE_SIZE = 250;
    
    private int m_MinKeyErrors = Integer.MAX_VALUE;
    private byte[] m_CipherText;
    private byte[] m_KnownPlainText;
    private byte[] m_KnownCipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private Map<Character, Character> m_Key;
    private Map<Character, Character> m_ReversedKey;
    private Map<Character, Character> m_MinKey;
    private HashSet<String> m_EnglishDictionary;
    
    public Attacker52(String cipherTextPath, String knownPlainTextPath, String knownCipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_KnownPlainText = CommonFunctions.ReadBytesFromFile(knownPlainTextPath);
        m_KnownCipherText = CommonFunctions.ReadBytesFromFile(knownCipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
        m_Key = new HashMap<Character, Character>();
        m_ReversedKey = new HashMap<Character, Character>();
        m_EnglishDictionary = new HashSet<String>(IOUtils.readLines(ClassLoader.getSystemResourceAsStream("Dictionary")));
    }
    
    public void Attack() throws Exception
    {
        Thread t = new Thread()
        {
            public void run()
            {
                try
                {
                    BuildKeyFromKnownTexts();
                    CompleteMissingCharsInKeyBruteForce();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        System.out.println("started");
        t.join(59 * 1000);
        System.out.println("finished");
        t.stop();
        CommonFunctions.WriteKeyToFile(m_MinKey, m_OutputPath);
        System.out.println("wrote");
    }
    
    private void BuildKeyFromKnownTexts()
    {
        for (int i = 0; i < m_KnownPlainText.length; i++)
        {
            char xorResult = (char)CommonFunctions.XorByte(m_KnownPlainText[i], m_IV[i]);
            if (!((xorResult >= 'a' && xorResult <= 'z') || (xorResult >= 'A' && xorResult <= 'Z')))    // Not an English letter
                continue;
            if (!m_Key.containsKey(xorResult))
                m_Key.put(xorResult, (char)m_KnownCipherText[i]);
        }
        m_ReversedKey = MapUtils.invertMap(m_Key);
    }
    
    private void CompleteMissingCharsInKey()
    {
        int index = 0;
        while (index < m_IV.length)
            index = AnalyzeWordFrom(index);
    }
    
    private int AnalyzeWordFrom(int start)
    {
        StringBuilder sb = new StringBuilder();
        List<Integer> notConvertedIndexes = new ArrayList<Integer>();
        int lastIndexChecked;
        for (lastIndexChecked = start; lastIndexChecked < m_IV.length; lastIndexChecked++)
        {
            byte decrypted = m_CipherText[lastIndexChecked];
            if (m_ReversedKey.containsKey((char)decrypted))
                decrypted = (byte)m_ReversedKey.get((char)decrypted).charValue();
            else
                notConvertedIndexes.add(lastIndexChecked);
            decrypted = CommonFunctions.XorByte(decrypted, m_IV[lastIndexChecked]);
            char c = (char)decrypted;
            if (Pattern.matches("[\\.,\\s!;?:&\"\\[\\]]+", c + ""))
                break;
            sb.append(c);
        }
        
        if (notConvertedIndexes.size() != 1)
            return lastIndexChecked + 1;
        
        int indexToReplace = notConvertedIndexes.get(0);
        for (char c = 'A'; c <= 'z'; c++)
        {
            if (c < 'a' && c > 'Z')
                continue;
            byte afterXor = CommonFunctions.XorByte((byte)c, m_IV[indexToReplace]);
            if (m_Key.keySet().contains((char)afterXor))
                continue;
            sb.setCharAt(indexToReplace - start, (char)afterXor);
            String word = sb.toString();
            if (m_EnglishDictionary.contains(word.toLowerCase()))
            {
                m_Key.put((char)afterXor, (char)m_CipherText[indexToReplace]);
                break;
            }
        }
        return lastIndexChecked + 1;
    }
    
    private void CompleteMissingCharsInKeyBruteForce() throws IOException
    {
        String permutation = "";
        for (char c = 'A'; c <= 'z'; c++)
            if (!m_Key.values().contains(c) && (c >= 'a' || c <= 'Z'))
                permutation += c;
        BruteForceDecryption("", permutation);
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
