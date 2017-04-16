import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ron Michaeli on 16-Apr-17.
 */
public class Attacker
{
    private final int m_BlockSize = 10;
    
    private String m_CipherText;
    private String m_IV;
    private String m_OutputPath;
    
    public Attacker(String cipherTextPath, String ivPath, String outputPath) throws IOException
    {
        m_CipherText = CommonFunctions.ReadFromFile(cipherTextPath);
        m_IV = CommonFunctions.ReadFromFile(ivPath);
        m_OutputPath = outputPath + "\\DecryptedCipher.txt";
    }
    
    public void Attack10() throws IOException
    {
        char[] permutation = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        String decryptedCipher = "";
        BruteForceDecryption(permutation, 0, permutation.length - 1, decryptedCipher);
    }
    
    private void BruteForceDecryption(char[] permutation, int startIndex, int endIndex, String decryptedCipher) throws IOException
    {
        if (startIndex == endIndex)
        {
            Map<Character, Character> key = new HashMap<Character, Character>();
            for (int i = 0; i < permutation.length; i++)
                key.put((char)(i + 97), permutation[i]);
            String previousSegment = m_IV;
            for (int i = 0; i < m_CipherText.length(); i += m_BlockSize)
            {
                String segment = m_CipherText.substring(i, i + m_BlockSize);
                String decryptedSegment = CommonFunctions.EncryptDecryptSegmentByKey(segment, key);
                decryptedSegment = CommonFunctions.XOR(decryptedSegment, previousSegment);
                decryptedCipher += decryptedSegment;
                previousSegment = segment;
            }
            CommonFunctions.WriteToFile(m_OutputPath, "\n" + new String(permutation) + "\n" + decryptedCipher + "\n", true);
            decryptedCipher = "";
        }
        else
        {
            for (int i = startIndex; i < endIndex; i++)
            {
                swap(permutation, startIndex, i);
                BruteForceDecryption(permutation, startIndex + 1, endIndex, decryptedCipher);
                swap(permutation, startIndex, i);
            }
        }
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
