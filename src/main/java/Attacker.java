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
    
    }
    
    public void Attack52()
    {
    
    }
}
