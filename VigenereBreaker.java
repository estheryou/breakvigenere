import java.util.*;
import edu.duke.*;
import java.io.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder answer=new StringBuilder();
        for(int k = whichSlice; k < message.length(); k=k+totalSlices){
          answer.append(message.charAt(k));
        }
        return answer.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for(int i=0;i<klength;i++){
         CaesarCracker slice=new CaesarCracker(mostCommon);
         String sliceofwords=sliceString(encrypted,i,klength);
         int keyforthisslice=slice.getKey(sliceofwords);
         key[i]=keyforthisslice;
        }
        return key;
    }

    public void breakVigenere () {
        FileResource fr=new FileResource();
        String f=fr.asString();
        int[] keys=tryKeyLength(f,4,'e');
        System.out.println("the keys are"+keys[0]+" "+keys[1]+" "+keys[2]+" "+keys[3]);
        VigenereCipher key=new VigenereCipher(keys);
        String decrypted=key.decrypt(f);
        //StorageResource store=new StorageResource();
        //store.add(decrypted);
        //return store;
        //String str="line 1/n line 2/n line 3/n";
        int newlineIndex=decrypted.indexOf("\n");
        System.out.println("the decrypted message is:"+decrypted.substring(0,newlineIndex));
    }
    public HashSet<String> readDictionary(FileResource fr){
      HashSet<String> dic=new HashSet<String>();
      for(String word:fr.lines()){
        word=word.toLowerCase();
        dic.add(word);       
        }
      return dic;
    }
    public int countWords(String message,HashSet<String> dictionary){
     String[] words=message.split("\\W");
     int counts=0;
     for(String word:words){
          word = word.toLowerCase();
        if(dictionary.contains(word)){
          counts=counts+1;
        }
        }
     return counts;
    }
    public String breakForLanguage(String encrypted,HashSet dictionary){
      
      int[] key_list = new int[100];
        int[] wordcount = new int[100];
        char mostcommon=mostCommonCharIn(dictionary);
        for (int k = 1; k <= 100; k++) key_list[k-1] = k;
        
        for (int k = 0; k < 100; k++) {
            
            int[] key = tryKeyLength(encrypted, key_list[k], mostcommon);
            VigenereCipher vc = new VigenereCipher(key);
            String result = vc.decrypt(encrypted);
            wordcount[k] = countWords(result, dictionary);
        }
        
        // figure out which key length has the largest word count.
        int largest = 0;
        int index = 0;
        for (int k = 0; k < 100; k++) {
            if (wordcount[k] > largest) {
                largest = wordcount[k];
                index = k;
            }
        }
        
        System.out.println("The largest count is "+largest);
        
        int truekey = key_list[index];
        int[] key = tryKeyLength(encrypted, truekey, mostcommon);
        System.out.println("The keys are "+"\t");
        //for (int k = 0; k < key.length; k++) {
          //  System.out.println(key[k]);
        //}
        System.out.println("The key length is "+key.length);
        System.out.println("the # of valid words:  "+wordcount[truekey-1]);
        VigenereCipher vc = new VigenereCipher(key);
        return vc.decrypt(encrypted);
    
    }
    public char mostCommonCharIn(HashSet<String> dictionary){
    HashMap<String,Integer> counts=new HashMap<String,Integer>();
    String letter="abcdefghijklmnopqrstuvwxyz";
    for(int i=0;i<26;i++){
     counts.put(letter.substring(i,i+1),0);
    }
    for(String word:dictionary){
        word=word.toLowerCase();
        char[] letters=word.toCharArray();
        for(char letter1:letters){
           for(String letter2:counts.keySet()){
             if(letter1==letter2.charAt(0)){
                counts.put(letter2,counts.get(letter2)+1);
                }
            }
        }
    } 
    int max=0;
    char mostcommon='a';
    for(String letter3:counts.keySet()){
      if(counts.get(letter3)>max){
        mostcommon=letter3.charAt(0);
        max=counts.get(letter3);
       }
    }
    return mostcommon;
    }
    public HashMap<String,Integer> breakForAllLanguages(String encrypted,HashMap<String,HashSet<String>> languages){
    HashMap<String,Integer> result=new HashMap<String,Integer>();    
    for(String lang:languages.keySet()){
       String decrypted=breakForLanguage(encrypted,languages.get(lang));
       int count=countWords(decrypted,languages.get(lang));
       result.put(lang,count);
        int newlineIndex=decrypted.indexOf("\n");
        System.out.println("the decrypted message is:  "+decrypted.substring(0,newlineIndex));
    }
    return result;
    }
    public void breakVigenere2 (){
        FileResource fr=new FileResource();
        FileResource Eng=new FileResource();
        String f=fr.asString();
        HashSet<String> words=readDictionary(Eng);
        String result=breakForLanguage(f,words);
        int newlineIndex=result.indexOf("\n");
        System.out.println("the decrypted message is:  "+result.substring(0,newlineIndex));
        System.out.println("try with 38");
        int[] key = tryKeyLength(f, 38, 'e');
            VigenereCipher vc = new VigenereCipher(key);
            String result1= vc.decrypt(f);
            HashSet<String> dic=readDictionary(Eng);
            int countsvalid = countWords(result1,dic);
          System.out.println("the valid words are:" + countsvalid);  
    }
    public void breakVigenere3(){
    FileResource fr=new FileResource();
    String str=fr.asString();
    
    HashMap<String,HashSet<String>> languages=new HashMap<String,HashSet<String>>();
    DirectoryResource dr= new DirectoryResource();
    
    for(File f:dr.selectedFiles()){
      FileResource fr2 = new FileResource(f.toString());
            HashSet<String> result = new HashSet<String>();
            for (String line: fr2.lines()) {
                line = line.toLowerCase();
                result.add(line);
            }
            languages.put(f.getName(), result);
            System.out.println("Finished reading "+f.getName());
    }
    HashMap<String, Integer> decrypted = breakForAllLanguages(str, languages);
        System.out.println(decrypted);
   
    }
    
}
