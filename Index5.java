/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.io.PrintWriter;

/**
 *
 * @author ehab
 */

//? comment methods
/**
 * implementing inverted index, providing methods for building and manipulating inverted index
 * searching and storing the index  
 */


public class Index5 {

    //--------------------------------------------
    int N = 0;  // total number of documents
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }

    //---------------------------------------------
//? comment methods
    /**
     * print the posting list for terms.
     *  p -> posting list to print
     */

    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            // Print the document ID followed by a comma
            System.out.print("" + p.docId);
            // Move to the next posting
            p = p.next;
            // Check if there's another posting to print
            if (p != null) {
                // If so, print a comma
                System.out.print(",");
            }
        }
        System.out.println("]");
    }
    //---------------------------------------------
    /**
     * print the dictionary.
     *iterates through the index map and prints each term along with its posting list.
     */
    public void printDictionary() {
        Map<String, DictEntry> sortedIndex = new TreeMap<>(index); // TreeMap sorts entries by keys

        Iterator<Map.Entry<String, DictEntry>> it = sortedIndex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DictEntry> pair = it.next();
            String term = pair.getKey();
            DictEntry dictEntry = pair.getValue();

            System.out.print(" [" + term + "," + dictEntry.doc_freq + "] =--> ");
            printPostingList(dictEntry.pList);
        }

        System.out.println("------------------------------------------------------");
        System.out.println("* Number of terms = " + index.size());
    }

    //-----------------------------------------------
 /**
     * build the inverted index from a set of files.
     */    


    //  mourad_edit1
    // option case:   1 --> biword      and  2  --> positional index
    
    public void buildIndex(String[] files, int option) {  // from disk not from the internet
    int fid = 0;
    for (String fileName : files) {
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            if (!sources.containsKey(fileName)) {
                sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
            }
            String ln;
            int flen = 0;

            // mourad_edit1
            // case option 1 -> biword
            if (option==1){
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);                
                // Update document length with the length of each line
                // indexOneLine -> ??
                
                flen += doBiword(ln, fid); // Call indexOneLine method to process each line
            }
            }
            // case option 2 -> positioanl index 
            else{
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);                
                // Update document length with the length of each line
                // indexOneLine -> ??
                
                flen += doPositionalIndex(ln, fid); // Call indexOneLine method to process each line
            }
            }
            
            sources.get(fid).length = flen;

        } catch (IOException e) {
            System.out.println("File " + fileName + " not found. Skip it");
        }
        fid++;
    }

    printDictionary();
}

    //----------------------------------------------------------------------------  
//? comment
    /**
     * process a single line of text to update the inverted index.
     * parameter "ln" The line of text
     * parameter "fid" The document id
     * and it return The length of the line
     */

 //  mourad_edit1 
//  biword function 
// indexOneLine  --> doBiword
   public int doBiword(String ln, int fid) {
       int flen = 0;

       String[] words = ln.split("\\W+");
       flen += words.length;

       // Iterate through the words in the line
       for (int i = 0; i < words.length; i++) {
           String word = words[i].toLowerCase();

           // Check if the word is a stop word
           if (stopWord(word)) {
               continue;
           }

           // Stem the word
           word = stemWord(word);

           // Add the single word to the index
           addToIndex(word, fid);

           // Create and add word combinations with underscores
           if (i < words.length - 1) {
               String nextWord = words[i + 1].toLowerCase();
               if (!stopWord(nextWord)) {
                   String combinedWord = word + "_" + stemWord(nextWord);
                   addToIndex(combinedWord, fid);
               }
           }
       }
       return flen;
   }
//    mourad_edit1
//    postional index function
//   indexOneLine  --> doPositionalIndex
    public int doPositionalIndex(String ln, int fid) {
        int flen = 0;
        int position = 0; // Track the position of words in the document

        String[] words = ln.split("\\W+");
        flen += words.length;

        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // Check if the word is not in the dictionary
            // If not, add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // Add document ID and position to the posting list
            if (!index.get(word).postingListContains(fid)) {
                // mourad_edit1
                // addPosting -> addPosting_PositionalIndex
                index.get(word).addPosting_PositionalIndex(fid, position); // Add position along with document ID
            } else {
                index.get(word).incrementTermFreq(); // Increment term frequency
            }
            // Increment position for the next word
            position++;
        }
        return flen;
    }


// mourad_edit1
//  addToIndex function called in doBiword function

   private void addToIndex(String word, int fid) {
       if (!index.containsKey(word)) {
           index.put(word, new DictEntry());
       }
       // Add document id to the posting list
       if (!index.get(word).postingListContains(fid)) {
           // Add document id to the posting list
           index.get(word).addPosting_Biword(fid);
       } else {
           // Update document frequency
           index.get(word).doc_freq += 1;
       }
       // Update term frequency
       index.get(word).term_freq += 1;
   }


//----------------------------------------------------------------------------
//? comment
 /**
     * Check if a word is a stop word.
     */
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
//----------------------------------------------------------------------------  
 /**
     * stemming on a word (the inf of it)
     */
    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    //---------------------------------------------------------------------------- 
/*
returns a new posting list that contains the intersection of the document IDs present in both lists.
iterates through both lists on the same time
comparing the document IDs at each step
if the document IDs are the same it adds the ID to the intersection list
else it make the pointer of the posting list with the smaller document ID
it returns the resulting intersection list.
*/

    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
        
    // initialize an empty posting list for the intersection
        Posting answer = null;
        Posting last = null;
//      2 while p1  != NIL and p2  != NIL
    
    // loop until (reach null the end)
        while (pL1 != null && pL2 != null) {
//          3 do if docID ( p 1 ) = docID ( p2 )
        // if the document ids of both postings are the same
            if (pL1.docId == pL2.docId) {
//          4   then ADD ( answer, docID ( p1 ))
            // add the document ID to the intersection list
                if (answer == null) {
                answer = new Posting(pL1.docId);
                last = answer;
                } else {
                last.next = new Posting(pL1.docId);
                last = last.next;
                }
//          5       p1 ← next ( p1 )
//          6       p2 ← next ( p2 )            
            // Move to the next postings in both lists
                pL1 = pL1.next;
                pL2 = pL2.next;
//          7   else if docId ( p1 ) < docId ( p2 )
            } else if (pL1.docId < pL2.docId) {
//          8        then p1 ← next ( p1 )
            // if the document id of the first posting is smaller -> move to the next posting in the first list
                pL1 = pL1.next;
            } else {
//          9        else p2 ← next ( p2 )
            // if the document id of the second posting is smaller -> move to the next posting in the second list
                pL2 = pL2.next;
            }
        }
    // Return the intersection of the two posting lists
        return answer;
    }

 //? comment
/**
     * find documents containing all terms in a given phrase.
     * it return a string containing the document IDs and associated data
     */
     Posting posting;
     public String find_24_01(String phrase) { // any number of terms non-optimized search 
         String result = "";
         String[] words = phrase.split("\\W+");
         int len = words.length;

         for (int i = 0; i < len; i++) {
             String word = words[i].toLowerCase();
             // Check if the word exists in the index
             if (index.containsKey(word)) {
                 Posting wordPosting = index.get(word).pList;
                 if (i == 0) {
                     // Initialize posting with the first word's posting list
                     posting = wordPosting;
                 } else {
                     // Intersect the current posting with the posting of the current word
                     posting = intersect(posting, wordPosting);
                 }
             } else {
                 // Handle the case where the word is not found in the index
                 System.out.println("Word '" + word + "' not found in the index.");
                 // You can choose to skip or handle this case based on your application's requirements
             }
         }

         while (posting != null) {
             result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
             posting = posting.next;
         }

         return result;
 }

//? comment    
/**
     * perform sorting of an array of words using bubble sort.
     * its return The sorted array of words
     */    
    //---------------------------------

    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

     //---------------------------------
//? comment 
/**
     * store the index and associated metadata to disk.
     */

    public void store(String storageName) {
        try {
            String pathToStorage = "C:\\tmp11\\rl\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================  
//? comment
/**
     * Check if a storage file exists.
     */  
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("/home/ehab/tmp11/rl/"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------   
//?comment
/**
     * Create an empty storage file.
     */ 
    public void createStore(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------      
     //load index from hard disk into memory
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/rl/"+storageName;         
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
