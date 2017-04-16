import org.junit.Assert;
import org.junit.Test;

/**
 * Created by user on 16/04/2017.
 */
public class PartATests
{
    @Test
    public void testEncryptionFirstSample()
    {
        String directory = "C:\\Users\\user\\Dropbox\\לימודים\\שנה ג' סמסטר ב'\\אבטחה\\מטלה 2\\Files\\";
        String keyFilePath = directory + "key_example.txt";
        String PlainTextFilePath = directory + "plainMsg_example.txt";
        String vectorFilePath = directory + "IV_example.txt";
        String ExpectedResultFilePath = directory + "cipherMsg_example.txt";
        try
        {
            Encryptor encryptor = new Encryptor(keyFilePath, CommonFunctions.ReadFromFile(vectorFilePath));
            String plainText = CommonFunctions.ReadFromFile(PlainTextFilePath);
            String cipher = encryptor.Encrypt(plainText);
            String expected = CommonFunctions.ReadFromFile(ExpectedResultFilePath);
            Assert.assertEquals(expected, cipher);
        } catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testDecryptionFirstSample()
    {
        String directory = "C:\\Users\\user\\Dropbox\\לימודים\\שנה ג' סמסטר ב'\\אבטחה\\מטלה 2\\Files\\";
        String keyFilePath = directory + "key_example.txt";
        String ExpectedFilePath = directory + "plainMsg_example.txt";
        String vectorFilePath = directory + "IV_example.txt";
        String cipherFilePath = directory + "cipherMsg_example.txt";
        try
        {
            Decryptor decryptor = new Decryptor(keyFilePath, CommonFunctions.ReadFromFile(vectorFilePath));
            String cipher = CommonFunctions.ReadFromFile(cipherFilePath);
            String plainText = decryptor.Decrypt(cipher);
            String expected = CommonFunctions.ReadFromFile(ExpectedFilePath);
            Assert.assertEquals(expected, plainText);
        } catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
}