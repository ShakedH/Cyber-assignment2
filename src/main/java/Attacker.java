import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Attacker
{
    private final int SAMPLE_SIZE = 1000;
    private final int ERROR_THRESHOLD = SAMPLE_SIZE / 65;
    
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
        // The following char array represents the HashMap values of the cipher key. The HashMap keys of the cipher key are constant a->h sorted
        char[] permutation = {'c', 'g', 'e', 'd', 'f', 'h', 'a', 'b'};
        BruteForceDecryption(permutation, 0, permutation.length - 1);
    }
    
    private void BruteForceDecryption(char[] permutation, int startIndex, int endIndex) throws IOException
    {
        if (startIndex == endIndex)     // A permutation is reached
        {
            Map<Character, Character> key = new HashMap<Character, Character>();
            for (int i = 0; i < permutation.length; i++)
                key.put((char)(i + 97), permutation[i]);
            
            byte[] sample = new byte[SAMPLE_SIZE];
            System.arraycopy(m_CipherText, 0, sample, 0, SAMPLE_SIZE);
            Decryptor decryptor = new Decryptor(key, m_IV);
            String decryptedSample = decryptor.DecryptByte(sample);
            String[] splittedSample = decryptedSample.split("\\W+");
            int errorCounter = 0;
            for (String word : splittedSample)
            {
                if (!m_EnglishDictionary.contains(word.toLowerCase()))
                    errorCounter++;
                if (errorCounter > ERROR_THRESHOLD)
                    return;
            }
            WriteKeyToFile(key.values());
        }
        else
        {
            for (int i = startIndex; i < endIndex; i++)
            {
                swap(permutation, startIndex, i);
                BruteForceDecryption(permutation, startIndex + 1, endIndex);
                swap(permutation, startIndex, i);
            }
        }
    }
    
    private void WriteKeyToFile(Collection<Character> key) throws IOException
    {
        CommonFunctions.WriteStringToFile(m_OutputPath, key.toString() + "\n", true);
    }
    
    private void swap(char[] array, int a, int b)
    {
        char temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }
    
    public void Attack52()
    {
    
    }
}
