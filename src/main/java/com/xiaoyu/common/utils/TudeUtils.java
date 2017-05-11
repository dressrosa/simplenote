/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.utils;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * googleMap经纬度算距离
 * 
 * @author xiaoyu 2016年3月28日
 */
public class TudeUtils {

	private static double EARTH_RADIUS = 6378137.0;// 地球半径m

	/**
	 * 纬度转换为弧度
	 * 
	 * @param d
	 * @return 弧度
	 */
	public static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据经纬度计算俩地距离
	 * 
	 * @param longiA
	 *            经度1
	 * @param latiA
	 *            纬度1
	 * @param longiB
	 *            经度2
	 * @param latiB
	 *            纬度2
	 * @return 俩地距离(整数字符串，单位 km)
	 */
	public static String getDistanceBetween2Place(String longiA, String latiA, String longiB, String latiB) {
		double radLatiA = rad(str2Double(latiA));
		double radLatiB = rad(str2Double(latiB));
		double a = radLatiA - radLatiB;
		double b = rad(str2Double(longiA)) - rad(str2Double(longiB));

		double distance = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLatiA) * Math.cos(radLatiB) * Math.pow(Math.sin(b / 2), 2)));
		distance *= EARTH_RADIUS;
		distance = Math.round(distance * 10000) / 10000;
		String distanceStr = double2Str(distance);
		distanceStr = distanceStr.substring(0, distanceStr.indexOf("."));

		return (Integer.parseInt(distanceStr) / 1000) + "";

	}

	/**
	 * 返回半径的外切正方形的四个点的经纬度
	 * 
	 * @author xiaoyu
	 * @param longitude
	 * @param latitude
	 * @param round
	 * @return Map
	 * @time 2015年11月23日上午10:27:40
	 */
	public static Map<String, Double> squrle4Points(String longitude, String latitude, String round) {
		double longi = str2Double(longitude);
		double lati = str2Double(latitude);
		double r = str2Double(round);
		// 计算经度弧度,从弧度转换为角度
		double dLongitude = 2 * (Math.asin(Math.sin(r / (2 * EARTH_RADIUS)) / Math.cos(Math.toRadians(lati))));
		dLongitude = Math.toDegrees(dLongitude);
		// 计算纬度角度
		double dLatitude = r / EARTH_RADIUS;
		dLatitude = Math.toDegrees(dLatitude);
		// 正方形
		Map<String, Double> map = Maps.newHashMap();
		map.put("TopY", lati + dLatitude);// 上边的经度
		map.put("BottomY", lati - dLatitude);// 底边的经度
		map.put("leftX", longi - dLongitude);// 左边的纬度
		map.put("rightX", longi + dLongitude);// 右边的纬度
		return map;
	}

	/**
	 * 比较俩地的距离是否在一定范围内
	 * 
	 * @param str1
	 * @param round
	 * @return
	 */
	public static boolean compareDisInRound(String str1, String round) {
		return Integer.parseInt(str1) - Integer.parseInt(round) <= 0 ? true : false;
	}

	private static double str2Double(String str) {
		if (null == str || "" == str) {
			return 0;
		}
		return Double.parseDouble(str);
	}

	private static String double2Str(double d) {
		return String.valueOf(d);
	}

}
