import javafx.util.Pair;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Attacker52
{
    private final int SAMPLE_SIZE = 250;
    
    private int m_MinKeyErrors = Integer.MAX_VALUE;
    private byte[] m_CipherText;
    private byte[] m_KnownPlainText;
    private byte[] m_KnownCipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private Map<Character, Character> m_Key;
    private Map<Character, Character> m_ReversedKey;
    private Map<Character, Character> m_MinKey;
    private HashSet<String> m_EnglishDictionary;
    
    private int m_StartOfCurrentWord;
    
    public Attacker52(String cipherTextPath, String knownPlainTextPath, String knownCipherTextPath, String ivPath, String outputPath) throws Exception
    {
        m_CipherText = CommonFunctions.ReadBytesFromFile(cipherTextPath);
        m_KnownPlainText = CommonFunctions.ReadBytesFromFile(knownPlainTextPath);
        m_KnownCipherText = CommonFunctions.ReadBytesFromFile(knownCipherTextPath);
        m_IV = CommonFunctions.ReadBytesFromFile(ivPath);
        m_OutputPath = outputPath;
        m_Key = new HashMap<Character, Character>();
        m_ReversedKey = new HashMap<Character, Character>();
        m_EnglishDictionary = new HashSet<String>(IOUtils.readLines(ClassLoader.getSystemResourceAsStream("Dictionary")));
    }
    
    public void Attack() throws Exception
    {
        Thread t = new Thread()
        {
            public void run()
            {
                try
                {
                    BuildKeyFromKnownTexts();
                    CompleteMissingCharsInKey();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        //        t.start();
        System.out.println("started");
        BuildKeyFromKnownTexts();
        CompleteMissingCharsInKey();
        // FIXME: 26/04/2017 change to 1000:
        //        t.join(59 * 10000);
        System.out.println("finished");
        t.stop();
        CommonFunctions.WriteKeyToFile(m_Key, m_OutputPath);
        System.out.println("wrote");
    }
    
    private void BuildKeyFromKnownTexts()
    {
        for (int i = 0; i < m_KnownPlainText.length; i++)
        {
            char xorResult = (char) CommonFunctions.XorByte(m_KnownPlainText[i], m_IV[i]);
            if (!((xorResult >= 'a' && xorResult <= 'z') || (xorResult >= 'A' && xorResult <= 'Z')))    // Not an English letter
                continue;
            if (!m_Key.containsKey(xorResult))
                m_Key.put(xorResult, (char) m_KnownCipherText[i]);
        }
        m_ReversedKey = MapUtils.invertMap(m_Key);
    }
    
    private void CompleteMissingCharsInKey()
    {
        while (m_Key.keySet().size() < 52)
        {
            int index = 0;
            while (index < m_CipherText.length)
            {
                m_ReversedKey = MapUtils.invertMap(m_Key);
                Pair<String, Integer> nextWord = getNextWord(index);
                if (nextWord.getValue() != -1)
                    ReplaceUnknownChar(nextWord.getKey(), nextWord.getValue(), index + nextWord.getValue());
                index += nextWord.getKey().length() + 1;
            }
        }
    }
    
    private Pair<String, Integer> getNextWord(int startFrom)
    {
        StringBuilder word = new StringBuilder();
        int unknownCharIndex = -1;
        boolean moreThanOneUnknown = false;
        for (int i = startFrom; i < m_CipherText.length; i++)
        {
            char c = (char) m_CipherText[i];
            if (m_ReversedKey.containsKey(c))
            {
                c = m_ReversedKey.get(c);
                c = XorChars(c, i);
            }
            else if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))  // c is not English letter
            {
                c = XorChars(c, i);
                if (Pattern.matches("[\\.,\\s!;?:&\"\\[\\]]+", c + ""))     // c is a delimiter
                    break;
            }
            else    // c is not in key and is English letter
            {
                if (unknownCharIndex != -1)
                    moreThanOneUnknown = true;
                unknownCharIndex = i - startFrom;
            }
            word.append(c);
        }
        if (moreThanOneUnknown)
            return new Pair<String, Integer>(word.toString(), -1);
        return new Pair<String, Integer>(word.toString(), unknownCharIndex);
    }
    
    private char XorChars(char c, int index)
    {
        if (index < m_IV.length)
            return (char) CommonFunctions.XorByte((byte) c, m_IV[index]);
        return (char) CommonFunctions.XorByte((byte) c, m_CipherText[index - m_IV.length]);
    }
    
    private void ReplaceUnknownChar(String word, int unknownCharIndex, int absoluteIndex)
    {
        StringBuilder tempWord = new StringBuilder(word);
        int matchesWithDictionary = 0;
        char c = unknownCharIndex == 0 ? 'A' : 'a';
        char matchingChar = 0;
        for (; c <= 'z'; c++)
        {
            char xored = XorChars(c, absoluteIndex);
            if (!m_Key.containsKey(xored) && (xored >= 'a' || xored <= 'Z'))
            {
                tempWord.setCharAt(unknownCharIndex, c);
                if (m_EnglishDictionary.contains(tempWord.toString().toLowerCase()))
                {
                    if (matchesWithDictionary == 1)
                        return;
                    matchesWithDictionary++;
                    matchingChar = xored;
                }
            }
        }
        
        if (matchingChar == 0)
            return;
        
        //        if (absoluteIndex < m_IV.length)
        //            matchingChar = (char) CommonFunctions.XorByte((byte) matchingChar, m_IV[absoluteIndex]);
        //        else
        //            matchingChar = (char) CommonFunctions.XorByte((byte) matchingChar, m_CipherText[absoluteIndex - m_IV.length]);
        
        m_Key.put(matchingChar, (char) m_CipherText[absoluteIndex]);
    }
    
    //region Irrelevant
    private void AnalyzeWord(String word, int index)
    {
        StringBuilder sb = new StringBuilder(word);
        
        byte byteInCipher = m_CipherText[m_StartOfCurrentWord + index];
        int indexInCipher = m_StartOfCurrentWord + index;
        
        for (char c = 'A'; c <= 'z'; c++)
        {
            if (c > 'Z' && c < 'a' || m_Key.containsKey(c))
                continue;
            
            sb.setCharAt(index, c);
            if (m_EnglishDictionary.contains(sb.toString()))
            {
                byte toAdd;
                if (m_StartOfCurrentWord + index < m_IV.length)
                    toAdd = CommonFunctions.XorByte((byte) c, m_IV[indexInCipher]);
                else
                    toAdd = CommonFunctions.XorByte((byte) c, m_CipherText[indexInCipher - m_IV.length]);
                
                m_Key.put((char) toAdd, (char) byteInCipher);
                return;
            }
        }
    }
    
    private int getUnknownIndex(String word)
    {
        for (int i = 0; i < word.length(); i++)
            if (!m_Key.containsKey(word.charAt(i)))
                return i;
        return -1;
    }
    
    private int CountUnknownIndexes(String word)
    {
        int counter = 0;
        for (int i = 0; i < word.length(); i++)
            if (!m_Key.containsKey(word.charAt(i)))
                counter++;
        return counter;
    }
    //
    //    private String getNextWord(int index)
    //    {
    //        StringBuilder sb = new StringBuilder();
    //        for (int i = index; i < m_CipherText.length; i++)
    //        {
    //            byte replace = m_CipherText[i];
    //            char c = (char) replace;
    //
    //            if (m_ReversedKey.containsKey(c))
    //                replace = (byte) m_ReversedKey.get(c).charValue();
    //
    //            if (i < m_IV.length)
    //                replace = CommonFunctions.XorByte(replace, m_IV[i]);
    //            else
    //                replace = CommonFunctions.XorByte(replace, m_CipherText[i - m_IV.length]);
    //
    //            c = (char) replace;
    //            if (Pattern.matches("[\\.,\\s!;?:&\"\\[\\]]+", c + ""))
    //                return sb.toString();
    //            sb.append(c);
    //        }
    //        return sb.toString();
    //    }
    
    private int AnalyzeWordFrom(int start)
    {
        StringBuilder sb = new StringBuilder();
        List<Integer> notConvertedIndexes = new ArrayList<Integer>();
        int lastIndexChecked;
        for (lastIndexChecked = start; lastIndexChecked < m_CipherText.length; lastIndexChecked++)
        {
            byte decrypted = m_CipherText[lastIndexChecked];
            
            if (m_ReversedKey.containsKey((char) decrypted))
                decrypted = (byte) m_ReversedKey.get((char) decrypted).charValue();
            else
                notConvertedIndexes.add(lastIndexChecked);
            
            if (lastIndexChecked < m_IV.length)
                decrypted = CommonFunctions.XorByte(decrypted, m_IV[lastIndexChecked]);
            else
                decrypted = CommonFunctions.XorByte(decrypted, m_CipherText[lastIndexChecked - m_IV.length]);
            
            char c = (char) decrypted;
            if (Pattern.matches("[\\.,\\s!;?:&\"\\[\\]]+", c + ""))
                break;
            sb.append(c);
        }
        
        if (notConvertedIndexes.size() != 1 || sb.length() <= 1)
            return lastIndexChecked + 1;
        
        int indexToReplace = notConvertedIndexes.get(0);
        for (char c = 'A'; c <= 'z'; c++)
        {
            if (c < 'a' && c > 'Z')
                continue;
            byte afterXor;
            if (indexToReplace < m_IV.length)
                afterXor = CommonFunctions.XorByte((byte) c, m_IV[indexToReplace]);
            else
                afterXor = CommonFunctions.XorByte((byte) c, m_CipherText[indexToReplace - m_IV.length]);
            
            if (m_Key.keySet().contains((char) afterXor))
                continue;
            sb.setCharAt(indexToReplace - start - 1, (char) afterXor);
            String word = sb.toString();
            if (m_EnglishDictionary.contains(word.toLowerCase()))
            {
                m_Key.put((char) afterXor, (char) m_CipherText[indexToReplace]);
                break;
            }
        }
        return lastIndexChecked + 1;
    }
    
    private void CompleteMissingCharsInKeyBruteForce() throws IOException
    {
        String permutation = "";
        for (char c = 'A'; c <= 'z'; c++)
            if (!m_Key.values().contains(c) && (c >= 'a' || c <= 'Z'))
                permutation += c;
        BruteForceDecryption("", permutation);
    }
    
    private void BruteForceDecryption(String prefix, String permutation) throws IOException
    {
        int n = permutation.length();
        if (n == 0)     // A permutation is reached
        {
            Map<Character, Character> key = new HashMap<Character, Character>(m_Key);
            int index = 0;
            for (char c = 'A'; c <= 'z'; c++)
                if (!key.containsKey(c) && (c >= 'a' || c <= 'Z'))
                {
                    key.put(c, prefix.charAt(index));
                    index++;
                }
            
            byte[] sample = new byte[SAMPLE_SIZE];
            System.arraycopy(m_CipherText, 0, sample, 0, SAMPLE_SIZE);
            Decryptor decryptor = new Decryptor(key, m_IV, 52);
            String decryptedSample = decryptor.Decrypt(sample);
            String[] splittedSample = decryptedSample.split("[\\.,\\s!;?:&\"\\[\\]]+");
            int errorCounter = 0;
            for (String word : splittedSample)
            {
                if (word.matches(".*\\d.*"))    // 'word' is a number
                    continue;
                if (!m_EnglishDictionary.contains(word.toLowerCase()))
                    errorCounter++;
                if (errorCounter > m_MinKeyErrors)
                    return;
            }
            if (errorCounter < m_MinKeyErrors)
            {
                m_MinKeyErrors = errorCounter;
                m_MinKey = key;
            }
        }
        else
        {
            for (int i = 0; i < n; i++)
                BruteForceDecryption(prefix + permutation.charAt(i), permutation.substring(0, i) + permutation.substring(i + 1, n));
        }
    }
    //endregion
}
