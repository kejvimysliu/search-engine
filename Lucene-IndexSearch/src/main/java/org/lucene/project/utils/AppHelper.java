package org.lucene.project.utils;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AppHelper {

    public static String translateCommand(String command) {
        List<String> tokenizedCommand = tokenizeCommand(command);
        switch (tokenizedCommand.get(0).toUpperCase()) {
            case "INDEX":
                return AppConstants.INDEX_COMMAND;
            case "QUERY":
                return AppConstants.QUERY_COMMAND;
            default:
                return AppConstants.NOT_SUPPORTED_COMMAND;
        }
    }

    public static boolean foundNotAlphaNumeric(List<String> tokenList, String command){
        boolean foundNotAlphanumeric = false;
        for(String token : tokenList) {
            if(!isAlphaNumeric(token, command)){
                foundNotAlphanumeric = true;
                break;
            }
        }
        return foundNotAlphanumeric;
    }

    public static List<String> tokenizeCommand(String str) {
        return Collections.list(new StringTokenizer(str, " ")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    public static boolean isNumeric(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isAlphaNumeric(String txtStr, String command) {
        if (command.equalsIgnoreCase(AppConstants.QUERY_COMMAND)) {
            if((txtStr.equals("&") || txtStr.equals("|")))
                return true;
            if (txtStr.startsWith("("))
                txtStr = txtStr.substring(1);
            if(txtStr.endsWith(")"))
                txtStr = txtStr.substring(0, txtStr.length()-1);
        }
        Pattern p = Pattern.compile(AppConstants.ALPHANUMERIC_PATTERN);
        Matcher m = p.matcher(txtStr);
        return m.matches();
    }

}
