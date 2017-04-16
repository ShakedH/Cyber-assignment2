import java.io.IOException;

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
        m_OutputPath = outputPath + "\\PlainText.txt";
    }
    
    public void Attack10()
    {
        char[] permutation = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        String decryptedCipher = BruteForceDecryption(permutation, 0, permutation.length - 1);
    }
    
    private String BruteForceDecryption(char[] permutation, int startIndex, int endIndex)
    {
        if (startIndex == endIndex)
        {
            String segment = m_CipherText.substring(0, 10);
            
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
        return null;
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
