package commentcounter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
*
* @author Selcan Narin selcan.narin@ogr.sakarya.edu.tr
* @since 16.04.2023
* <p>
* This class detects single-line, multi-line and javadoc comment 
* lines of funtions in a given java file.
* </p>
*/
public class CommentCounter {
    public static void main(String[] args) throws IOException {

        // Specify the file path of the Java file to read
        String filename = args[0];

        // Read the contents of the Java file
        String javaCode = ReadFile.readFile(filename);

        // Define the regular expression to match function names
        Pattern functionPattern = Pattern.compile(
                "\\b(public|private|protected|final|static|synchronized|abstract|native)?\\s*(class)?\\s*(\\w+\\s+)*(\\w+)\\s*\\(([\\s\\S]*?)\\)\\s*(throws [\\s\\S]+)?\\s*\\{([\\s\\S]*?)\\}");

        // Define the regular expression to match for above the function JavaDoc comments
        Pattern javadocPattern = Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL);

        // Define the output file to write single-line comments
        File singleLineFile = new File("teksatir.txt");
        // Define the output file to write multi-line comments
        File multiLineFile = new File("coksatir.txt");
        // Define the output file to write Javadoc comments
        File javadocFile = new File("javadoc.txt");
        // Create the file if it does not exist
        if (!singleLineFile.exists()) {
            singleLineFile.createNewFile();
        }
        if (!multiLineFile.exists()) {
            multiLineFile.createNewFile();
        }
        if (!javadocFile.exists()) {
            javadocFile.createNewFile();
        }

        // Create writers and buffered writers for each output file
        FileWriter writerjavadoc = new FileWriter(javadocFile);
        FileWriter writersingleLine = new FileWriter(singleLineFile);
        FileWriter writermultiLine = new FileWriter(multiLineFile);
        BufferedWriter bufferjavadoc = new BufferedWriter(writerjavadoc);
        BufferedWriter buffersingleLine = new BufferedWriter(writersingleLine);
        BufferedWriter buffermultiLine = new BufferedWriter(writermultiLine);

        
        // Match all function names in the Java code
        Matcher functionMatcher = functionPattern.matcher(javaCode);

        // Initialize variables to keep track of the previous function end index and comment counts
        int previousFunctionEnd = 0;
        int javadocCounter = 0;
        int singleLineCounter = 0;
        int multiLineCounter = 0;

        // Iterate over all function matches
        while (functionMatcher.find()) {
            // Get the function name and start/end indices
            String functionName = functionMatcher.group(4);
            int functionStartIndex = functionMatcher.start();
            int functionEndIndex = functionMatcher.end();

            // Find all JavaDoc comments above the function
            // Iterate over all JavaDoc matches and add them to the list
            // Match all Javadoc comments above the function
            Matcher javadocMatcher = javadocPattern.matcher(javaCode.substring(previousFunctionEnd + 2, functionStartIndex));
            List<String> javadocComments = new ArrayList<>();
            while (javadocMatcher.find()) {
                javadocComments.add(javadocMatcher.group().trim());
                javadocCounter++;
            }

            // Extract the code block of the function (excluding the function signature and parameter list)
            String functionCodeBlock = functionMatcher.group(5) + functionMatcher.group(7);

            // Match all Javadoc comments inside the function
            javadocMatcher = javadocPattern.matcher(functionCodeBlock);

            List<String> singleLineComments = new ArrayList<>();
            List<String> multiLineComments = new ArrayList<>();

            // Initialize boolean flags to keep track of which type of comment the parser is currently inside.
            boolean insideSingleLineComment = false; // Flag to indicate if inside a single-line comment.
            boolean insideMultiLineComment = false; // Flag to indicate if inside a multi-line comment.
            boolean insideJavadocComment = false; // Flag to indicate if inside a Javadoc comment.
            boolean insideNestedComment = false; // Flag to indicate if inside a nested comment.
            StringBuilder sb = new StringBuilder(); // StringBuilder to store the contents of the comment being parsed.

            for (int i = 0; i < functionCodeBlock.length(); i++) {
                char c = functionCodeBlock.charAt(i);
                // check if inside a nested comment
                if (insideNestedComment) {
                    if (functionCodeBlock.startsWith("*/", i)) {
                        insideNestedComment = false;
                        i++;
                    }
                }
                // check if inside a Javadoc comment
                else if (insideJavadocComment) {
                    if (functionCodeBlock.startsWith("*/", i)) {
                        insideJavadocComment = false;
                        javadocComments.add(sb.append("*/").toString());
                        javadocCounter++;
                        sb.setLength(0);
                        i++;
                    } else {
                        sb.append(c);
                    }
                } 
                // check if inside a multi-line comment
                else if (insideMultiLineComment) {
                    if (functionCodeBlock.startsWith("*/", i)) {
                        insideMultiLineComment = false;
                        multiLineComments.add(sb.append("*/").toString());
                        multiLineCounter++;
                        sb.setLength(0);
                        i++;
                    }
                    // Add this condition to recognize "/**/" as a multiline comment
                    else if (functionCodeBlock.startsWith("*/", i)) {
                        insideMultiLineComment = false;
                        multiLineComments.add(sb.append("/**/").toString());
                        multiLineCounter++;
                        sb.setLength(0);
                        i++;
                    } else if (functionCodeBlock.startsWith("/*", i)) {
                        insideNestedComment = true;
                        sb.append("/*");
                        i++;
                    } else {
                        sb.append(c);
                    }
                }
                // check if inside a single line comment 
                else if (insideSingleLineComment) {
                    if (c == '\n') {
                        insideSingleLineComment = false;
                        singleLineComments.add(sb.toString());
                        singleLineCounter++;
                        sb.setLength(0);
                    } else {
                        sb.append(c);
                    }
                
                }
                // handle the start of a new comment 
                else {
                    // Add this condition to recognize "/**/" as a multiline comment
                    if (functionCodeBlock.startsWith("/**/", i)) {
                        insideMultiLineComment = true;
                        sb.append("/*");
                        i++;
                    } else if (functionCodeBlock.startsWith("/**", i)) {
                        insideJavadocComment = true;
                        sb.append("/**");
                        i++;

                    } else if (functionCodeBlock.startsWith("/*", i)) {
                        insideMultiLineComment = true;
                        sb.append("/*");
                        i++;
                    } else if (functionCodeBlock.startsWith("//", i)) {
                        insideSingleLineComment = true;
                        sb.append("//");
                        i++;
                        // check for white space characters after "//"
                        while (i < functionCodeBlock.length() && Character.isWhitespace(functionCodeBlock.charAt(i))) {
                            sb.append(functionCodeBlock.charAt(i));
                            i++;
                        }

                    } else if (functionCodeBlock.startsWith("*/", i)) {
                        // handle case of multi-line comment that only contains "*/"
                        if (sb.length() > 0) {
                            multiLineComments.add(sb.toString());
                            multiLineCounter++;
                            sb.setLength(0);
                        }
                        i++;
                    }
                }
            }
            // Check if there are any open comment blocks and add them to their lists 
            if (insideSingleLineComment) {
                singleLineComments.add(sb.toString());
                singleLineCounter++;
            }
            if (insideMultiLineComment) {
                multiLineComments.add(sb.toString());
                multiLineCounter++;
            }
            if (insideJavadocComment) {
                javadocComments.add(sb.toString());
                javadocCounter++;
            }


            System.out.println("Function: " + functionName);
            System.out.println("Number of single line comments: " + singleLineCounter);
            System.out.println("Number of multi-line comments: " + multiLineCounter);
            System.out.println("Number of Javadoc comments: " + javadocCounter);
            System.out.println("------------------------------------------");

            // write javadocsComments list
            bufferjavadoc.write("Function: " + functionName);
            bufferjavadoc.newLine();
            bufferjavadoc.write("Javadoc Comments: ");
            bufferjavadoc.newLine();
            writeCommentsToFile(javadocComments, bufferjavadoc);
            bufferjavadoc.write("------------------------------------------");
            bufferjavadoc.newLine();

            // write singleLineComments list
            buffersingleLine.write("Function: " + functionName);
            buffersingleLine.newLine();
            buffersingleLine.write("Single Line Comments: ");
            buffersingleLine.newLine();
            writeCommentsToFile(singleLineComments, buffersingleLine);
            buffersingleLine.write("------------------------------------------");
            buffersingleLine.newLine();

            // write multiLineComments list
            buffermultiLine.write("Function: " + functionName);
            buffermultiLine.newLine();
            buffermultiLine.write("Multi Line Comments: ");
            buffermultiLine.newLine();
            writeCommentsToFile(multiLineComments, buffermultiLine);
            buffermultiLine.write("------------------------------------------");
            buffermultiLine.newLine();

            // Reset the counters for the next function
            javadocCounter = 0;
            singleLineCounter = 0;
            multiLineCounter = 0;
            previousFunctionEnd = functionEndIndex;

        }
        closeWriters(bufferjavadoc, buffersingleLine, buffermultiLine);
    }
    private static void writeCommentsToFile(List<String> comments, BufferedWriter writer) throws IOException {
        for (String comment : comments) {
            writer.write(comment);
            writer.newLine();
        }
    }
    private static void closeWriters(BufferedWriter bufferjavadoc, BufferedWriter buffersingleLine,
    BufferedWriter buffermultiLine) throws IOException {
        bufferjavadoc.close();
        buffersingleLine.close();
        buffermultiLine.close();
    }

}