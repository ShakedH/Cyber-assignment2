import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Ron Michaeli on 16-Apr-17.
 */
public class Attacker
{
    private final int BLOCK_SIZE = 10;
    private final int SAMPLE_SIZE = 100;
    
    private byte[] m_CipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private HashSet<String> m_EnglishDictionary;
    
    public Attacker(String cipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath + "\\DecryptedCipher.txt";
        m_EnglishDictionary = new HashSet<String>(IOUtils.readLines(ClassLoader.getSystemResourceAsStream("Dictionary")));
    }
    
    public void Attack10() throws IOException
    {
        // The following char array represents the HashMap values of the cipher key. The HashMap keys of the cipher key are constant a->h sorted
        char[] permutation = {'g', 'h', 'a', 'd', 'c', 'e', 'b', 'f'};
        byte[] decryptedCipher = new byte[SAMPLE_SIZE];
        BruteForceDecryption(permutation, 0, permutation.length - 1, decryptedCipher);
    }
    
    private void BruteForceDecryption(char[] permutation, int startIndex, int endIndex, byte[] decryptedCipher) throws IOException
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
            System.out.println();
            //            byte previousByte = m_IV[0];
            //            for (int i = 0; i < m_CipherText.length; i++)
            //            {
            //                byte currentByte = m_CipherText[i];
            //                if (key.containsKey(currentByte))
            //
            //                String decryptedSegment = CommonFunctions.EncryptDecryptSegmentByKey(segment, key);
            //                decryptedSegment = CommonFunctions.XOR(decryptedSegment, previousByte);
            //                decryptedCipher.append(decryptedSegment);
            //                previousByte = segment;
            //            }
            //            CommonFunctions.WriteStringToFile(m_OutputPath, "\n" + new String(permutation) + "\n" + decryptedCipher + "\n", true);
            //            decryptedCipher = new StringBuilder();
            //            System.exit(1);
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
