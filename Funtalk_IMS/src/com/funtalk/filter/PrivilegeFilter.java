/**
* <p>Title: 权限控制</p>
* <p>Description: 判断请求是否是可访问的页面或从可访问页面发起，用户是否登陆。否，则转向错误页面
* 需要忽略的页面，在web.xml里配。</p>
* <p>Company: funtalk</p>
* @author wangxp
* 2016-1-25
*/
package com.funtalk.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.funtalk.pojo.rightmanage.Func;
import com.funtalk.pojo.rightmanage.User;


public class PrivilegeFilter implements Filter {

	protected FilterConfig filterConfig;
	
	protected List ignorePage;
	
	protected List allowRequestPage;

	private Log logger = LogFactory.getLog(PrivilegeFilter.class);
	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response 	= (HttpServletResponse)res;
        HttpServletRequest  request 	= (HttpServletRequest)req; 
        String requestURI 				= request.getRequestURI();
        //////////////logger.info("in doFIlter, requestURI:"+requestURI);//////////requestURI:/opertable/list.jsp
        
        if (request.getSession().getAttribute("ww_locale") == null)
        	request.getSession().setAttribute("ww_locale", request.getLocale());//解決多語言切換問題
        
        //图片，html,等，处理请求
        if(requestURI.length() > 3){
            String pageType = requestURI.substring(requestURI.length()-3,requestURI.length());

            String ignoreType = "htm,tml,css,.js,gif,jpg,dwr,png,xls,csv,cab";
            String[] ignoreTypes = ignoreType.split(",");
            for(int i = 0; i < ignoreTypes.length; i++){ 
            	if(ignoreTypes[i].equalsIgnoreCase(pageType)){
            		chain.doFilter(request,response);
            		return;
            	}
            }
        }

   
      	 
        //判断是否是忽略页面，是则处理请求
        for(int i = 0; i<ignorePage.size(); i++){
        	if(requestURI.equalsIgnoreCase(request.getContextPath()+(String)ignorePage.get(i))){
    			logger.debug("忽略页面,允许访问："+requestURI+"="+request.getContextPath()+(String)ignorePage.get(i));
            	chain.doFilter(request,response);
            	return;
        	}      		       			
        }

        //判断是否登陆，没登陆转向错误页面
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if(session == null){
        	request.setAttribute("errorMsg", "您还没有登录或者很长时间没有操作了!<a href=\"/index.jsp\" target=\"_top\"><font color=red> 点击这里登录</font></a>");
			logger.debug("没有登陆：");
			logger.debug(requestURI+"?"+request.getQueryString());
        }
        else
        {
            currentUser = (User)session.getAttribute("currentUser");
            if (currentUser == null){
            	request.setAttribute("errorMsg", "您还没有登录或者很长时间没有操作了!<br><a href=\"/index.jsp\" target=\"_top\"><font color=red>点击这里登录</font></a>");
    			logger.debug("没有登陆：");
    			logger.debug(requestURI+"?"+request.getQueryString());
            }
            else
            {
            	/////////////2009-5-26 liaoxb 跳过URL检测
            	chain.doFilter(request,response);
        		return;
            	
//            	
//            	
//            	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
//            	String referer = request.getHeader("Referer");
//            	logger.debug("Referer:"+request.getHeader("Referer"));
//            	
//            	if(referer == null)
//            	{
//            		List userFunc = (List)session.getAttribute("userFunc");
//                    for(int i = 0; i < userFunc.size(); i++){
//                    	Func func = (Func)userFunc.get(i);
//                    	//不带参数请求
//            	        if(request.getQueryString() == null){
//            	        	if(requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("refer null 允许访问,无参数请求,在t_role_func表中有定义:"+requestURI);
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }else{
//            	        	//带参数的请求
//            	        	String queryString = request.getQueryString();
//            	        	
//            	        	//logger.debug("xxxx_"+requestURI+"?"+queryString);
//            	        	//logger.debug("aaaa_"+request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode());
//            	        	if((requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("refer null 允许访问,在t_role_func表中有定义==="+requestURI+"?"+request.getQueryString());
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }
//                    }
//            		request.setAttribute("errorMsg", "非法操作!<br><a href=\"/index.jsp\" target=\"_top\"><font color=red>点击这里登录</font></a>");
//
//            	}
//            	else
//            	{
///*            		/////////////////////
//            		//weblogic tomcat referer是不同的，其实所有referer为"/rightmanage/login.jspa"
//            		//都是应该处理请求的,因为肯定是在界面点击操作。
//            		//下面就不用判断了。
//            		if(referer.equalsIgnoreCase(basePath+"/rightmanage/login.jspa")){
//            			logger.debug("referer为"+basePath+"/rightmanage/login.jspa,是界面点击操作,处理请求:"+referer);
//            			logger.debug(requestURI+"?"+request.getQueryString());
//            			chain.doFilter(request,response);
//            			return;
//            		}
//            		if(referer.equalsIgnoreCase(basePath+"/rightmanage/login.jspa") && requestURI.equalsIgnoreCase("/opertable/OperTable!toExcel.jspa")){
//            			//toExcel location.href情况
//            			logger.debug("允许访问:toExcel location.href情况,referer为/rightmanage/login.jspa");
//            			logger.debug(requestURI+"?"+request.getQueryString());
//            			chain.doFilter(request,response);
//            			return;
//            		}
//            		//tomcat时有这种情况
//            		if(referer.startsWith(basePath+"/opertable/OperTable.jspa") && requestURI.equalsIgnoreCase("/opertable/GenerateJsonData.jspa")){
//            			//toExcel location.href情况
//            			logger.debug("允许访问:showmodalDialog情况,referer为/opertable/OperTable.jspa");
//            			logger.debug(requestURI+"?"+request.getQueryString());
//            			chain.doFilter(request,response);
//            			return;
//            		}*/
//            		//判断是否为允许发起请求页面发起的请求，是则处理请求
//            		
//            		for (Iterator iter = allowRequestPage.iterator(); iter.hasNext();) {
//						String allowPage = (String) iter.next();
//						if(referer.toUpperCase().startsWith((basePath+allowPage).toUpperCase())){
//							logger.debug("允许发起请求页面:"+basePath+allowPage+",是界面点击操作,处理请求,referer"+referer);
//							logger.debug(requestURI+"?"+request.getQueryString());
//							chain.doFilter(request,response);
//        	        		return;
//						}
//					}
//            		
//                	List userFunc = (List)session.getAttribute("userFunc");
//                	logger.debug(requestURI+"?"+request.getQueryString());
//                    for(int i = 0; i < userFunc.size(); i++){
//                    	Func func = (Func)userFunc.get(i);
//                		//logger.debug("表中定义                  ==="+basePath+func.getPagecode()+"&funccode="+func.getFunccode());
//                		//logger.debug("                 referer:"+referer);	
//                    	if(referer.equalsIgnoreCase(basePath+func.getPagecode()+"&funccode="+func.getFunccode())
//                    			//带funccode和不带funccode,或无参数　3种情况
//                    			|| referer.equalsIgnoreCase(basePath+func.getPagecode())
//                    			||referer.equalsIgnoreCase(basePath+func.getPagecode()+"?funccode="+func.getFunccode())){
//                    		//发起请求的页面是允许访问的，则可以访问
//                    		logger.debug("允许页面发起的请求,允许访问==="+basePath+func.getPagecode()+"&funccode="+func.getFunccode());
//                    		logger.debug(requestURI+"?"+request.getQueryString());
//                    		chain.doFilter(request,response);
//        	        		return;
//                    	}
//                    	
//                    	//不带参数请求
//            	        if(request.getQueryString() == null){
//            	        	if(requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("允许访问,无参数请求,在t_role_func表中有定义:"+requestURI);
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }else{
//            	        	//带参数的请求
//            	        	String queryString = request.getQueryString();
//            	        	
//            	        	//logger.debug("xxxx_"+requestURI+"?"+queryString);
//            	        	//logger.debug("aaaa_"+request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode());
//            	        	if((requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("允许访问,在t_role_func表中有定义==="+requestURI+"?"+request.getQueryString());
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }
//                    }	
//            	}

            }
            
        }
        
        logger.info("拒绝访问==="+requestURI+"?"+request.getQueryString());
        request.getRequestDispatcher("/error.jsp").forward(request,response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		
		this.filterConfig = filterConfig;
		List ignorePageList = new ArrayList();
		List allowRequestPageList = new ArrayList();
		//取出WEB.XML中忽略过滤页面
		Enumeration enu = filterConfig.getInitParameterNames();
		while(enu.hasMoreElements())
		{
			String paramName = (String)enu.nextElement();
					
			String page = filterConfig.getInitParameter(paramName);
			if(paramName.startsWith("ignorePage")){
				ignorePageList.add(page);
			}else if(paramName.startsWith("allowRequestPage")){
				allowRequestPageList.add(page);
			}
		}
		
		logger.debug("ignorePageList:");
		for (Iterator iter = ignorePageList.iterator(); iter
				.hasNext();) {
			String element = (String) iter.next();
			logger.debug(element);
			
		}
		
		
		logger.debug("allowRequestPageList:");
		for (Iterator iter = allowRequestPageList.iterator(); iter
				.hasNext();) {
			String element = (String) iter.next();
			logger.debug(element);
		}
		
		ignorePage = ignorePageList;
		allowRequestPage = allowRequestPageList;
		
	}

}
