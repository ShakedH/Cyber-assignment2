import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Attacker52
{
    private byte[] m_CipherText;
    private byte[] m_KnownPlainText;
    private byte[] m_KnownCipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private Map<Character, Character> m_Key;
    
    public Attacker52(String cipherTextPath, String knownPlainTextPath, String knownCipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_KnownPlainText = CommonFunctions.ReadBytesFromFile(knownPlainTextPath);
        m_KnownCipherText = CommonFunctions.ReadBytesFromFile(knownCipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
        m_Key = new HashMap<Character, Character>();
    }
    
    public void Attack() throws Exception
    {
        BuildKeyFromKnownTexts();
        CompleteMissingCharsInKey();
        DecryptCipherText();
        CommonFunctions.WriteKeyToFile(m_Key, m_OutputPath);
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
        // FIXME: 19/04/2017 remove this
        try
        {
            CommonFunctions.WriteKeyToFile(m_Key, m_OutputPath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void CompleteMissingCharsInKey()
    {
    
    }
    
    private void DecryptCipherText() throws Exception
    {
        int SAMPLE_SIZE = 1000;
        byte[] sample = new byte[SAMPLE_SIZE];
        System.arraycopy(m_CipherText, 0, sample, 0, SAMPLE_SIZE);
        Decryptor decryptor = new Decryptor(m_Key, m_IV, 52);
        String decryptedSample = decryptor.Decrypt(sample);
        System.out.println(decryptedSample);
    }
}
