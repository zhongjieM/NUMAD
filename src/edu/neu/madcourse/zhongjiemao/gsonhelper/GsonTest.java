package edu.neu.madcourse.zhongjiemao.gsonhelper;

import com.google.gson.*;

import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.UserInfo;

public class GsonTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UserInfo ui = new UserInfo("1", "Kevin", "0001", 100);
		Gson gSon = new Gson();
		String gString = gSon.toJson(ui);
		System.out.println("Hello");
		//System.out.println(gString);

	}

}
