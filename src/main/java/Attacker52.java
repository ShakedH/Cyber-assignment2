/**
 * Created by Ron Michaeli on 19-Apr-17.
 */
public class Attacker52
{
    private byte[] m_CipherText;
    private byte[] m_KnownPlainText;
    private byte[] m_KnownCipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    
    public Attacker52(String cipherTextPath, String knownPlainTextPath, String knownCipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_KnownPlainText = CommonFunctions.ReadBytesFromFile(knownPlainTextPath);
        m_KnownCipherText = CommonFunctions.ReadBytesFromFile(knownCipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
    }
    
    public void Attack()
    {
        for (int i = 0; i < m_KnownPlainText.length; i++)
        {
        
        }
    }
}
