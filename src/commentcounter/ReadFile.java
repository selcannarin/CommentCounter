package commentcounter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
*
* @author Selcan Narin selcan.narin@ogr.sakarya.edu.tr
* @since 16.04.2023
* <p>
* This class read lines in a given java file.
* </p>
*/
public class ReadFile {

    public static String readFile(String filename) throws IOException {
        StringBuilder javaCode = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                javaCode.append(line).append("\n");
            }
        }
        return javaCode.toString();
    }

}