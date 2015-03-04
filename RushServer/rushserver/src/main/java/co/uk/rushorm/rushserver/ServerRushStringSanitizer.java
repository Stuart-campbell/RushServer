package co.uk.rushorm.rushserver;

import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerRushStringSanitizer implements RushStringSanitizer {
    @Override
    public String sanitize(String string) {
        if(string != null) {
            return sqlEscapeString(string);
        } else {
            return "'" + string + "'";
        }
    }

    public static String sqlEscapeString(String value) {
        StringBuilder escaper = new StringBuilder();

        appendEscapedSQLString(escaper, value);

        return escaper.toString();
    }

    public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
        sb.append('\'');
        if (sqlString.indexOf('\'') != -1) {
            int length = sqlString.length();
            for (int i = 0; i < length; i++) {
                char c = sqlString.charAt(i);
                if (c == '\'') {
                    sb.append('\'');
                }
                sb.append(c);
            }
        } else
            sb.append(sqlString);
        sb.append('\'');
    }
}
