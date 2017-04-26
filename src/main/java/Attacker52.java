import javafx.util.Pair;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class Attacker52
{
    private byte[] m_CipherText;
    private byte[] m_KnownPlainText;
    private byte[] m_KnownCipherText;
    private byte[] m_IV;
    private String m_OutputPath;
    private Map<Character, Character> m_Key;
    private Map<Character, Character> m_ReversedKey;
    private HashSet<String> m_EnglishDictionary;
    
    
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
        t.start();
        t.join(59 * 1000);
        t.stop();
        CommonFunctions.WriteKeyToFile(m_Key, m_OutputPath);
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
        
        if (matchingChar == 0 ||
                (matchingChar < 'a' || matchingChar > 'z') && (matchingChar < 'A' || matchingChar > 'Z')) // matchingChar is not english letter
            return;
        
        m_Key.put(matchingChar, (char) m_CipherText[absoluteIndex]);
        m_ReversedKey = MapUtils.invertMap(m_Key);
    }
    
    private char XorChars(char c, int index)
    {
        if (index < m_IV.length)
            return (char) CommonFunctions.XorByte((byte) c, m_IV[index]);
        return (char) CommonFunctions.XorByte((byte) c, m_CipherText[index - m_IV.length]);
    }
}
