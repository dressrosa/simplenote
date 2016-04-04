/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.test.controller;


import java.sql.DriverManager;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**测试回滚
 * @author xiaoyu
 *2016年3月17日
 */
public class Test {

	/**
	 *@author xiaoyu
	 *@param args
	 *@time 2016年3月17日下午7:51:22
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Connection conn = null;
	       Statement stmt = null;
	       try {
	           // 动态导入数据库的驱动
	           Class.forName("com.mysql.jdbc.Driver");
	           conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/xiaoyu", "root", "root");
	           // 开启事务
	           conn.setAutoCommit( false );
	           // 创造SQL语句
	           String sql = "INSERT INTO org_user ( id, name ) VALUES ( '7', '123' )";
	           String sql1 =  "INSERT INTO org_user ( id, name ) VALUES ( '7', '12343534' )";
	           // 执行SQL语句
	           stmt = (Statement) conn.createStatement();
	           stmt.executeUpdate(sql);
	           stmt.executeUpdate(sql1);
	           // 提交事务
	           conn.commit();
	           System.out.println( "OK!" );
	       } catch (Exception e) {
	           e.printStackTrace();
	           // 回滚事务
	           try {
	              conn.rollback();
	           } catch ( Exception e2 ) {}
	       } finally {
	           // 关闭Statement
	           try {
	              stmt.close();
	           } catch (Exception e) {}
	           // 关闭Connection
	           try {
	              conn.close();
	           } catch (Exception e) {}
	       }
	    }
}
