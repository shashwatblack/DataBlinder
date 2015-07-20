package np.com.shashwatblack.datablinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shashwat on 12/16/14.
 */
public class DataBlinder {

    protected final Set<String> STRINGMANIPULATORTOKENS = new HashSet<String>(Arrays.asList(
            new String[]{"rotate", "encrypt"}
    ));
    protected final Set<String> CCWTOKENS = new HashSet<String>(Arrays.asList(
            new String[]{"ccw", "counterclockwise", "anticlockwise"}
    ));
    protected final Set<String> INCTOKENS = new HashSet<String>(Arrays.asList(
            new String[]{"increase", "inc", "add"}
    ));
    protected final Set<String> DECTOKENS = new HashSet<String>(Arrays.asList(
            new String[]{"decrease", "dec", "subtract", "sub", "reduce"}
    ));
    public enum DateUnits {
        DAY, MONTH, WEEK, YEAR
    }

    // constructor
    public void DataBlinder() {
        // init args here
    }

    public String blind(String data, String cue) {
        // data can be a string, date, or even int/float as strings
        // To-do implement functionality here

        cue = cue.toLowerCase();
        String dataFormat = "\\d\\d[-\\/:,\\\\]\\d\\d[-\\/:,\\\\]\\d\\d\\d\\d";
        for (String s: STRINGMANIPULATORTOKENS) {
            if (cue.contains(s)) return blindString(data, cue);
            else if (data.matches(dataFormat)) return blindDate(data, cue);
            else {
                try {
                    return (blind(Integer.parseInt(data), cue)) + "";
                } catch (NumberFormatException ex) {
                    try {
                        return (blind(Double.parseDouble(data), cue)) + "";
                    } catch (NumberFormatException exf) {
                        // why do anything?
                    }
                }
            }
        }
        return null;
    }

    public int blind(int data, String cue) {
        int amt = 0;
        boolean dec = false;
        String[] cues = cue.toLowerCase().split("[ ,./*]");
        for (String s: cues) {
            if (s.equals("")) continue;
            try {
                amt = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                // do nothing
            }
            if (DECTOKENS.contains(s))
                dec = true;
        }
        return incInt(data, dec ? -amt : amt);
    }

    public int incInt(int data, int amt) {
        return data + amt;
    }

    public int decInt(int data, int amt) {
        return data - amt;
    }

    public float blind(double data, String cue) {
        return blind((float) data, cue);
    }

    public float blind(float data, String cue) {
        float amt = 0;
        boolean dec = false;
        String[] cues = cue.toLowerCase().split("[ ,./*]");
        for (String s: cues) {
            if (s.equals("")) continue;
            try {
                amt = Float.parseFloat(s);
            } catch (NumberFormatException ex) {
                // do nothing
            }
            if (DECTOKENS.contains(s))
                dec = true;
        }
        return incFloat(data, dec ? -amt : amt);
    }

    public float incFloat(float data, float amt) {
        return data + amt;
    }

    public float decFloat(float data, float amt) {
        return data - amt;
    }

    public String blindDate(String data, String cue) {
        int amt = 0;
        DateUnits units = DateUnits.DAY;
        boolean dec = false;
        String[] cues = cue.toLowerCase().split("[ ,./*]");
        for (String s: cues) {
            if (s.equals("")) continue;
            s = s.toLowerCase();
            try {
                amt = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                // do nothing
            }
            if (DECTOKENS.contains(s))
                dec = true;
            if (s.equals("week") || s.equals("weeks")) units=DateUnits.WEEK;
            else if (s.equals("month") || s.equals("months")) units=DateUnits.MONTH;
            else if (s.equals("year") || s.equals("years")) units=DateUnits.YEAR;
        }
        return incDate(data, dec ? -amt : amt, units);
    }

    public String incDate(String data, int amt, DateUnits units) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(data));
            if (units == DateUnits.WEEK)        c.add(Calendar.WEEK_OF_YEAR, amt);
            else if (units == DateUnits.MONTH)  c.add(Calendar.MONTH, amt);
            else if (units == DateUnits.YEAR)   c.add(Calendar.YEAR, amt);
            else    c.add(Calendar.DATE, amt);
            return sdf.format(c.getTime());  // dt is now the new date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String blindString(String data, String cue) {
        // if cue contains 'Rotate'
        if (cue.toLowerCase().contains("rotate")) {
            int amt = 0;
            boolean ccw = false;
            String[] cues = cue.toLowerCase().split("[ ,./*]");
            for (String s: cues) {
                if (s.equals("")) continue;
                try {
                    amt = Integer.parseInt(s);
                } catch (NumberFormatException ex) {
                    // do nothing
                }
                if (CCWTOKENS.contains(s))
                    ccw = true;
            }
            return rotateString(data, ccw ? -amt : amt);
        }

        // if cue contains 'Encrypt'

        return null;
    }

    public String rotateString(String data, int amt) {
        // rotate every character by amt
        StringBuilder stringBuilder = new StringBuilder();
        for (char c: data.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                c += amt;
                if (c > 'z') c-= 26;
                else if (c < 'a') c += 26;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }


    public static void main(String[] args) {

        DataBlinder dataBlinder = new DataBlinder();

        System.out.println(dataBlinder.blind(dataBlinder.blind("hello world", "rotate by 5"), "rotate ccw 5"));
        //System.out.println(dataBlinder.rotateString("hello world", 5));
        System.out.println(dataBlinder.blind(5, "increase 5"));
        System.out.println(dataBlinder.blind(5.5, "decrease 5"));
        System.out.println(dataBlinder.blind("15-06-2014", "add 16 days"));


    }
}
