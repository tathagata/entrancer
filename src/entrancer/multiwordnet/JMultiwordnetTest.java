package entrancer.multiwordnet;

import org.itc.mwn.*;
import java.util.Date;
import java.io.*;
import java.nio.charset.Charset;

public class JMultiwordnetTest {
 public static void main (String[] args) {
     // Load Dictionary
     Date day = new Date();
     long start = day.getTime();
     long end,time;

     String lemma = null;
     //String lemma = "home";
     //String lemma = "equipa";
     try {
         InputStreamReader in = new InputStreamReader(new FileInputStream(args[0]), Charset.forName("UTF8"));
         BufferedReader filereader = new BufferedReader(in);

         //  String language = "english";
         //String language = "portuguese";
         String language = "italian";
         // Look up word by substring
         DictionaryDatabase dictionary = new MysqlDictionary();

         while ((lemma = filereader.readLine()) != null) {
             //lemma ="requisi̤̣o";

             IndexWord[] searchwords = dictionary.searchIndexWords(POS.NOUN, lemma, language);
             if (searchwords != null) {
                 System.out.println("Found " + searchwords.length + " synsets with synonym \"" + lemma + "\"");

                 for (int i=0; i < searchwords.length; i++) {
                     System.out.println("Lemma " + searchwords[i].getLemma() + " has " +  searchwords[i].getTaggedSenseCount() + " senses");
                 }
             }
             day = new Date();
             end = day.getTime();
             time = end-start;
             System.err.println("---- Execution time: " + time + "\n");



             // Look up word and the relations of its synset
             IndexWord word = dictionary.lookupIndexWord(POS.NOUN, lemma, language);

             int taggedCount = word.getTaggedSenseCount();
             System.out.println("The lemma " + lemma + " has " + taggedCount + " senses:");
             Synset[] senses = word.getSenses();


             // Explore related relations.
             if (senses != null) {
                 for (int i=0; i < senses.length; i++) {
                     Synset sense = senses[i];         // Print Synset Description
                     System.out.println((i+1) + ". " + sense.getLongDescription());     // Print Synset Description

                     //Pointer[] pointers = sense.getPointers(PointerType.HYPERNYM);
                     Pointer[] pointers = sense.getPointers();
                     for (int p=0; p < pointers.length; p++) {
                         PointerType ptrType = pointers[p].getType();
                         PointerTarget target = pointers[p].getTarget();
                         System.out.println(ptrType.getLabel() + " => " + target.toString());
                     }

                 } // end-outer-for
             }
         }
         filereader.close();
         in.close();
     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
     day = new Date();
     end = day.getTime();
     time = end-start;
     System.err.println("--- Execution time: " + time);

 }
} // end-class
