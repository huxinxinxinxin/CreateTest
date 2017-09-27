package redis;

import java.util.Random;
import java.util.StringTokenizer;

/**
 * 
 * @author Jason Li 2014-6-3
 * StringTokenizer和Spilt速度比较
 *
 */
public class StringTest {

	public static void main(String[] args) {
        String str = buildString(1_000_000); //1.7新特性, 1000000
		long start;
		long end;
		
		System.out.println("-----------StringTokenizer start-----------");		
		start = System.currentTimeMillis();
		StringTokenizer st = new StringTokenizer(str);
		StringBuilder sb = new StringBuilder();
		while(st.hasMoreTokens()){
			sb.append(st.nextToken());
		}
		end = System.currentTimeMillis();
		System.out.println("StringTokenizer time use:" + (end-start));
		
		System.out.println("-----------StringSpilt start-----------");		
		start = System.currentTimeMillis();
		StringBuilder sb2 = new StringBuilder();
		String[] strs = str.split("\\s");
		for(String s: strs){
			sb2.append(s);
		}
		end = System.currentTimeMillis();
		System.out.println("StringSpilt time use:" + (end-start));		
		
	}
	
	//建立一个长字符串，
	//其中有空格，以便拆分成length长度的n个字符串
	private static String buildString(int length) {
		StringBuilder sb = new StringBuilder();
		Random r =new Random();
		for (int i = 0; i <length;i++ ){
			for (int j = r.nextInt(10); j>0 ;j--){
				sb.append((char)('a' + r.nextInt(26)));
			}
			sb.append(" ");
		}		
		return sb.toString();
	}
}