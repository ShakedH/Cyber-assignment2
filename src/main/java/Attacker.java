/**
 * Created by Ron Michaeli on 16-Apr-17.
 */
public class Attacker
{
    private int m_Algorithm;
    private String m_CipherText;
    private String m_IV;
    private String m_OutputPath;
    
    public Attacker(String algorithm, String cipherTextPath, String ivPath, String outputPath)
    {
        try
        {
            m_Algorithm = algorithm.endsWith("10") ? 10 : 52;
            
        }
        catch (Exception ex)
        {
        
        }
        
    }
}
