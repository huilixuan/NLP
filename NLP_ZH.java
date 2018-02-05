package nlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NLP_ZH {


	public static boolean isChinese(char c) {
        return c >= '\u4e00' && c <= '\u9fa5';
    }
	

	public static boolean isEnglish(char c) {
        if (c > 'z' && c < 'Ａ') {
            return false;
        }
        if (c < 'A') {
            return false;
        }
        if (c > 'Z' && c < 'a') {
            return false;
        }
        if (c > 'Ｚ' && c < 'ａ') {
            return false;
        }
        if (c > 'ｚ') {
            return false;
        }
        return true;
    }
	

	 public static boolean isNumber(char c) {
	        //大部分字符在这个范围
	        if (c > '9' && c < '０') {
	            return false;
	        }
	        if (c < '0') {
	            return false;
	        }
	        if (c > '９') {
	            return false;
	        }
	        return true;
	    }
	 

    public static boolean isAllNonChinese(String text) {
        for (char c : text.toCharArray()) {
            if (isChinese(c)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isAllChinese(String text) {
        boolean chinese = true;
        for (char c : text.toCharArray()) {
            if (!isChinese(c)) {
                chinese = false;
            }
        }
        return chinese;
    }
	
    public static boolean begins0Chinese(String text){
    	char[] cs = text.toCharArray();
    	if(cs.length>0)
    		return isChinese(cs[0]);
    	else
    		return false;
    }
    
    public static boolean begins01Chinese(String text){
    	char[] cs = text.toCharArray();
    	if(cs.length>1){
    		return isChinese(cs[1])||isChinese(cs[0]);
    	}else if(cs.length>0){
    		return isChinese(cs[0]);
    	}else{
    		return false;
    	}
    }
    
    public static boolean begins0Number(String text){
    	char[] cs = text.toCharArray();
    	if(cs.length>0)
    		return isNumber(cs[0]);
    	else
    		return false;
    }
    
    public static boolean begins01Number(String text){
    	char[] cs = text.toCharArray();
    	if(cs.length>1){
    		return isNumber(cs[1])||isNumber(cs[0]);
    	}else if(cs.length>0){
    		return isNumber(cs[0]);
    	}else{
    		return false;
    	}
    }
    

	public static String extractChinese(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isChinese(c)) {
                result.append(c);
            }
        }
        return result.toString().toLowerCase();
    }
	
	public static String removeFirstChar(char c, String str){
		StringBuilder result  = new StringBuilder();
		int i = 0;
		for(char c0:str.toCharArray()){
			if(i==0){
				if(c!=c0){
					result.append(c0);
				}
				i++;
			}else{
				result.append(c0);
			}
		}
		return result.toString();
	}
	
	public static String removeFirstChar(String src, String str){
		StringBuilder result  = new StringBuilder();
		int i = 0;
		char c = src.charAt(0);
		for(char c0:str.toCharArray()){
			if(i==0){
				if(c!=c0){
					result.append(c0);
				}
				i++;
			}else{
				result.append(c0);
			}
		}
		return result.toString();
	}
	
	public static String[] getZhBeginsAndLeft(String str){//截取开头的中文
		String reg = "^.{0,1}[\\u4e00-\\u9fa5]{2,55}";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()){
			String m = matcher.group(0);
			if(begins0Chinese(m)){
				return new String[]{m, str.replaceAll(m, "").replaceAll("^\\s+", "")};
			}else{
				str = removeFirstChar(m,str);
				return new String[]{m.substring(1), str.replaceAll(m.substring(1), "").substring(1).replaceAll("^\\s+", "")};
			}
		}
		return null;
	}
	
	public static String[] getDateBeginsAndLeft(String str){//截取开头的中文
		String reg = "^.{0,1}(\\d{4}\\.\\d{2}|\\d{4}\\.\\d|"
				+ "\\d{4}\\/\\d{2}|\\d{4}\\/\\d|\\d{4}\\-\\d{2}|\\d{4}\\-\\d|\\d{6})"
				+".{1,5}(\\d{4}\\.\\d{2}|\\d{4}\\.\\d|"
				+ "\\d{4}\\/\\d{2}|\\d{4}\\/\\d|\\d{4}\\-\\d{2}|\\d{4}\\-\\d|\\d{6})";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()){
			String m = matcher.group(0);
			String s = matcher.group(1);
			String e = matcher.group(2);
			if(m.matches("^\\d")){
				return new String[]{s,e, str.replaceAll(m, "").replaceAll("^\\s+", "")};
			}else{
				str = removeFirstChar(m,str);
				return new String[]{s, e, str.replaceAll(m.substring(1), "").substring(1).replaceAll("^\\s+", "")};
			}
		}
		return null;
	}
	
	public static String[] split(String str){
		return str.split("\r\n|\\||\t|：|\\:|\\|");
	}
	
	public static String clearChar(String str){
		String[] splitStr = str.split("\\s");
		for(String s:splitStr){
			if(begins01Chinese(s)){
				break;
			}else{
				for(char c:s.toCharArray()){
					str = removeFirstChar(c, str);
				}
			}
		}
		str=str.replaceAll("^\\s+", "");
		return str;
	}
	
	
}
