package com.xyazh.kanake.util.xml;

import com.xyazh.kanake.util.xml.rendernode.RenderNode;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Context {
    protected static final Pattern PATTERN1 = Pattern.compile("^\\{[a-zA-Z0-9_]+\\}$");
    protected static final Pattern PATTERN2 = Pattern.compile("^-?\\d+%$");
    protected static final Pattern PATTERN3 = Pattern.compile("^0x[0-9a-fA-F]+$");
    public static final byte KEY = 0;
    public static final byte DOUBLE = 1;
    public static final byte PERCENT = 2;
    public static final byte LONG = 3;

    protected String key;
    protected double value = 0;
    protected long valueLong = 0;
    public final byte type;
    @Nullable
    protected Context parentContext;

    public Context(String input, @Nullable Context parentContext){
        this(input);
        this.parentContext = parentContext;
    }

    private Context(String input){
        this.parentContext = null;
        this.key = this.isKey(input);
        if(this.key != null){
            this.type = KEY;
            return;
        }
        if(PATTERN2.matcher(input).matches()){
            this.toPercent(input);
            this.type = PERCENT;
            return;
        }
        if(PATTERN3.matcher(input).matches()){
            this.valueLong = Long.parseLong(input.substring(2), 16);
            this.type = LONG;
            return;
        }
        this.value = Double.parseDouble(input);
        this.type = DOUBLE;
    }

    @Nullable
    protected String isKey(String input){
        Matcher matcher = PATTERN1.matcher(input);
        if(matcher.matches()) {
            String match = matcher.group();
            match = match.substring(1,match.length()-1);
            return match;
        }
        return null;
    }

    protected void toPercent(String input){
        input = input.substring(0, input.length() - 1);
        this.value = Double.parseDouble(input);
        this.value /= 100;
    }

    public double getRawDouble(Map<String,Number> map){
        double result= 0;
        switch (this.type){
            case KEY:
                if(map.containsKey(this.key)){
                    result = map.get(this.key).doubleValue();
                }
                break;
            case DOUBLE:
                result = this.value;
                break;
            case PERCENT:
                result = result * value;
                break;
            case LONG:
                result = (double) this.valueLong;
        }
        return result;
    }

    public double getDouble(Map<String,Number> map){
        double result= 0;
        if(this.parentContext != null){
            result = this.parentContext.getDouble(map);
        }
        result += this.getRawDouble(map);
        return result;
    }

    public long getRawLong(Map<String,Number> map){
        long result= 0;
        switch (this.type){
            case KEY:
                if(map.containsKey(this.key)){
                    result += map.get(this.key).longValue();
                }
                break;
            case DOUBLE:
                result = (long)this.value;
                break;
            case PERCENT:
                result = (long) (result * value);
                break;
            case LONG:
                result = this.valueLong;
        }
        return result;
    }

    public long getLong(Map<String,Number> map){
       long result= 0;
        if(this.parentContext != null){
            result = this.parentContext.getLong(map);
        }
        result += this.getRawLong(map);
        return result;
    }

    public int getRawInt(Map<String,Number> map){
        if(this.type == LONG){
            return (int) this.getLong(map);
        }
        return (int) this.getDouble(map);
    }

    public int getInt(Map<String,Number> map){
        int result= 0;
        if(this.parentContext != null){
            result = this.parentContext.getInt(map);
        }
        result += this.getRawInt(map);
        return result;
    }
}
