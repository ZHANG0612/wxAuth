package com.wx.auth.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.wx.auth.util.AuthUtil;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class CallBackServlet
 */
//@WebServlet("/callBack")
public class CallBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private String dbUrl="";
	private String driverName="";
	private String userName="";
	private String passWord="";
	private Connection connection=null;
	private PreparedStatement pStatement=null;
	private ResultSet rSet=null;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		this.dbUrl=config.getInitParameter("dbUrl");
		this.driverName=config.getInitParameter("driverName");
		this.userName=config.getInitParameter("userName");
		this.passWord=config.getInitParameter("passWord");
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CallBackServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String code=request.getParameter("code");
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                + "appid=" + AuthUtil.APPID
                + "&secret=" + AuthUtil.APPSECRET
                + "&code=" + code
                + "&grant_type=authorization_code";
		JSONObject jsonObject = AuthUtil.doGetJson(url);
        String openid = jsonObject.getString("openid");
        String token = jsonObject.getString("access_token");
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?"
                + "access_token=" + token
                + "&openid=" + openid
                + "&lang=zh_CN";
        JSONObject userInfo = AuthUtil.doGetJson(infoUrl);
        System.out.println(userInfo);
        
        //1.使用微信用户信息直接登录，无需注册和绑定
//        request.setAttribute("info", userInfo);
//        request.getRequestDispatcher("/index1.jsp").forward(request, response);
        //2.将微信与当前系统的账号进行绑定
        try {
			String nickName=getNickNme(openid);
			if(!"".equals(nickName)){
				//绑定成功
				request.setAttribute("nickName", nickName);
				request.getRequestDispatcher("/index2.jsp").forward(request, response);
			}else{
				//未绑定
				request.setAttribute("openid", openid);
				request.setAttribute("nickname", userInfo.getString("nickname"));
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String account=request.getParameter("account");
		String password=request.getParameter("password");
		String openid=request.getParameter("openid");
		String nickName=request.getParameter("nickname");
		try {
			int temp=insertUser(openid, nickName, account, password);
			if(temp>0){
				System.out.println("账号绑定成功");
			}else{
				System.out.println("账号绑定失败");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getNickNme(String openId) throws SQLException{
		String nickName="";
		connection=DriverManager.getConnection(dbUrl, userName, passWord);
		String sql="select nickname from auth_user where openid=?";
		pStatement=connection.prepareStatement(sql);
		pStatement.setString(1, openId);
		rSet=pStatement.executeQuery();
		while(rSet.next()){
			nickName=rSet.getString("NICKNAME");
		}
		if(rSet!=null){
			rSet.close();
		}
		if(pStatement!=null){
			pStatement.close();
		}
		if(connection!=null){
			connection.close();
		}
		return nickName;
	}
	
	public int updateUser(String openId,String account,String password) throws SQLException{
		connection=DriverManager.getConnection(dbUrl, userName, passWord);
		String sql="update auth_user set openid=? where account=? and password=?";
		pStatement=connection.prepareStatement(sql);
		pStatement.setString(1, openId);
		pStatement.setString(2, account);
		pStatement.setString(3, password);
		int temp=pStatement.executeUpdate();
		if(rSet!=null){
			rSet.close();
		}
		if(pStatement!=null){
			pStatement.close();
		}
		if(connection!=null){
			connection.close();
		}
		return temp;
	}
	
	public int insertUser(String openId,String nickName,String account,String password) throws SQLException{
		connection=DriverManager.getConnection(dbUrl, userName, passWord);
		String sql="insert into auth_user(ACCOUNT,PASSWORD,NICKNAME,OPENID) values(?,?,?,?)";
		pStatement=connection.prepareStatement(sql);
		pStatement.setString(1, account);
		pStatement.setString(2, password);
		pStatement.setString(3, nickName);
		pStatement.setString(4, openId);
		int temp=pStatement.executeUpdate();
		if(rSet!=null){
			rSet.close();
		}
		if(pStatement!=null){
			pStatement.close();
		}
		if(connection!=null){
			connection.close();
		}
		return temp;
	}

}
