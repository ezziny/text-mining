package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Scanner;

/**
 *
 * @author ehab
 */
public class Test {

    public static void main(String args[]) throws IOException {
        
        Index5 index = new Index5();
        //|**  change it to your collection directory 
        //|**  in windows "C:\\tmp11\\rl\\collection\\"       
        //     String files = "/home/ehab/tmp11/rl/collection/";
        // String files = "C:\\tmp11\\rl\\collection\\"; 
        String files = "../../../tmp11/rl/collection/";     // in linux

        File file = new File(files);
        //|** String[] 	list()
        //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();

        fileList = index.sort(fileList);
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }

// mourad_edit1
//  perepare user interface 
        Scanner scanner = new Scanner(System.in);
        int option = 0 ;
        while(true){
            System.out.println("Welcome To Information Retrieval Program!!\nChoose a prefered option for using our program:\n1. Biword\n2. Positional Index");    
            
            option = scanner.nextInt();   
            if (option==1 || option==2) break;
            else{
                System.out.println("ERROR!!\nPlease, enter a valid optoin.\n");
            }
        }



        // case option = 1 --> biword   and  =2  --> positional index
        index.buildIndex(fileList,option);
        index.store("index");
        index.printDictionary();

        String test3 = "data  should plain greatest comif"; // data  should plain greatest comif
        System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

        String phrase = "";
        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            phrase = in.readLine();
/// -3- **** complete here ***addToIndex*
            phrase = phrase.replaceAll("\"(\\w+)\\s(\\w+)\"", "$1_$2");
            // Perform search and display results
            if (!phrase.isEmpty()) {
                String searchResult = index.find_24_01(phrase);
                System.out.println("Search result for '" + phrase + "':\n" + searchResult);
            }
        } while (!phrase.isEmpty());
        
    }
}
