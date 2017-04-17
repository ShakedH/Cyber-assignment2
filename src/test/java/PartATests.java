import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileReader;

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
            Encryptor encryptor = new Encryptor(keyFilePath, CommonFunctions.ReadBytesFromFile(vectorFilePath));
            byte[] plainText = org.apache.commons.io.IOUtils.toByteArray(new FileReader(PlainTextFilePath));
            String cipher = encryptor.Encrypt(plainText);
            String expected = CommonFunctions.ReadStringFromFile(ExpectedResultFilePath);
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
            Decryptor decryptor = new Decryptor(keyFilePath, CommonFunctions.ReadBytesFromFile(vectorFilePath));
            byte[] cipher = IOUtils.toByteArray(new FileReader(cipherFilePath));
            String plainText = decryptor.DecryptByte(cipher);
            String expected = CommonFunctions.ReadStringFromFile(ExpectedFilePath);
            Assert.assertEquals(expected, plainText);
        } catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void TestEncryptAndDecrypt()
    {
        String directory = "C:\\Users\\user\\Dropbox\\לימודים\\שנה ג' סמסטר ב'\\אבטחה\\מטלה 2\\Files\\Corpus\\";
        String keyFilePath = directory + "key.txt";
        String fileName = "Alice";
        String plainTextFilePath = directory + fileName + ".txt";
        String vectorFilePath = directory + "vector.txt";
        String cipherOutput = directory + fileName + "_cipher.txt";
        String decrypted = directory + fileName + "_dec.txt";
        try
        {
            byte[] givenPlainText = IOUtils.toByteArray(new FileReader(plainTextFilePath));
            Encryptor encryptor = new Encryptor(keyFilePath, CommonFunctions.ReadBytesFromFile(vectorFilePath));
            Decryptor decryptor = new Decryptor(keyFilePath, CommonFunctions.ReadBytesFromFile(vectorFilePath));
            
            String cipher = encryptor.Encrypt(givenPlainText);
            CommonFunctions.WriteStringToFile(cipherOutput, cipher, false);
            String decrypt = decryptor.DecryptByte(IOUtils.toByteArray(new FileReader(cipherOutput)));
            CommonFunctions.WriteStringToFile(decrypted, decrypt, false);
            
            Assert.assertEquals(new String(givenPlainText), decrypt);
        } catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void TestByteDecryption()
    {
        String directory = "C:\\Users\\user\\Dropbox\\לימודים\\שנה ג' סמסטר ב'\\אבטחה\\מטלה 2\\additional_examples\\PartB\\";
        String keyFilePath = directory + "key_short.txt";
        String fileName = "cipher";
        String plainTextFilePath = directory + fileName + ".txt";
        String vectorFilePath = directory + "IV_short.txt";
        String decrypted = directory + fileName + "_dec.txt";
        try
        {
            //String givenCipher = CommonFunctions.ReadStringFromFile(plainTextFilePath);
            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(plainTextFilePath));
            Decryptor decryptor = new Decryptor(keyFilePath, CommonFunctions.ReadBytesFromFile(vectorFilePath));
            
            String decrypt = decryptor.DecryptByte(bytes);
            CommonFunctions.WriteStringToFile(decrypted, decrypt, false);
            
            Assert.assertTrue(true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}