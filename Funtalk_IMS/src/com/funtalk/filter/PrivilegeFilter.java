/**
* <p>Title: Ȩ�޿���</p>
* <p>Description: �ж������Ƿ��ǿɷ��ʵ�ҳ���ӿɷ���ҳ�淢���û��Ƿ��½������ת�����ҳ��
* ��Ҫ���Ե�ҳ�棬��web.xml���䡣</p>
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
        	request.getSession().setAttribute("ww_locale", request.getLocale());//��Q���Z���ГQ���}
        
        //ͼƬ��html,�ȣ���������
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

   
      	 
        //�ж��Ƿ��Ǻ���ҳ�棬����������
        for(int i = 0; i<ignorePage.size(); i++){
        	if(requestURI.equalsIgnoreCase(request.getContextPath()+(String)ignorePage.get(i))){
    			logger.debug("����ҳ��,������ʣ�"+requestURI+"="+request.getContextPath()+(String)ignorePage.get(i));
            	chain.doFilter(request,response);
            	return;
        	}      		       			
        }

        //�ж��Ƿ��½��û��½ת�����ҳ��
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if(session == null){
        	request.setAttribute("errorMsg", "����û�е�¼���ߺܳ�ʱ��û�в�����!<a href=\"/index.jsp\" target=\"_top\"><font color=red> ��������¼</font></a>");
			logger.debug("û�е�½��");
			logger.debug(requestURI+"?"+request.getQueryString());
        }
        else
        {
            currentUser = (User)session.getAttribute("currentUser");
            if (currentUser == null){
            	request.setAttribute("errorMsg", "����û�е�¼���ߺܳ�ʱ��û�в�����!<br><a href=\"/index.jsp\" target=\"_top\"><font color=red>��������¼</font></a>");
    			logger.debug("û�е�½��");
    			logger.debug(requestURI+"?"+request.getQueryString());
            }
            else
            {
            	/////////////2009-5-26 liaoxb ����URL���
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
//                    	//������������
//            	        if(request.getQueryString() == null){
//            	        	if(requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("refer null �������,�޲�������,��t_role_func�����ж���:"+requestURI);
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }else{
//            	        	//������������
//            	        	String queryString = request.getQueryString();
//            	        	
//            	        	//logger.debug("xxxx_"+requestURI+"?"+queryString);
//            	        	//logger.debug("aaaa_"+request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode());
//            	        	if((requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("refer null �������,��t_role_func�����ж���==="+requestURI+"?"+request.getQueryString());
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }
//                    }
//            		request.setAttribute("errorMsg", "�Ƿ�����!<br><a href=\"/index.jsp\" target=\"_top\"><font color=red>��������¼</font></a>");
//
//            	}
//            	else
//            	{
///*            		/////////////////////
//            		//weblogic tomcat referer�ǲ�ͬ�ģ���ʵ����refererΪ"/rightmanage/login.jspa"
//            		//����Ӧ�ô��������,��Ϊ�϶����ڽ�����������
//            		//����Ͳ����ж��ˡ�
//            		if(referer.equalsIgnoreCase(basePath+"/rightmanage/login.jspa")){
//            			logger.debug("refererΪ"+basePath+"/rightmanage/login.jspa,�ǽ���������,��������:"+referer);
//            			logger.debug(requestURI+"?"+request.getQueryString());
//            			chain.doFilter(request,response);
//            			return;
//            		}
//            		if(referer.equalsIgnoreCase(basePath+"/rightmanage/login.jspa") && requestURI.equalsIgnoreCase("/opertable/OperTable!toExcel.jspa")){
//            			//toExcel location.href���
//            			logger.debug("�������:toExcel location.href���,refererΪ/rightmanage/login.jspa");
//            			logger.debug(requestURI+"?"+request.getQueryString());
//            			chain.doFilter(request,response);
//            			return;
//            		}
//            		//tomcatʱ���������
//            		if(referer.startsWith(basePath+"/opertable/OperTable.jspa") && requestURI.equalsIgnoreCase("/opertable/GenerateJsonData.jspa")){
//            			//toExcel location.href���
//            			logger.debug("�������:showmodalDialog���,refererΪ/opertable/OperTable.jspa");
//            			logger.debug(requestURI+"?"+request.getQueryString());
//            			chain.doFilter(request,response);
//            			return;
//            		}*/
//            		//�ж��Ƿ�Ϊ����������ҳ�淢�����������������
//            		
//            		for (Iterator iter = allowRequestPage.iterator(); iter.hasNext();) {
//						String allowPage = (String) iter.next();
//						if(referer.toUpperCase().startsWith((basePath+allowPage).toUpperCase())){
//							logger.debug("����������ҳ��:"+basePath+allowPage+",�ǽ���������,��������,referer"+referer);
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
//                		//logger.debug("���ж���                  ==="+basePath+func.getPagecode()+"&funccode="+func.getFunccode());
//                		//logger.debug("                 referer:"+referer);	
//                    	if(referer.equalsIgnoreCase(basePath+func.getPagecode()+"&funccode="+func.getFunccode())
//                    			//��funccode�Ͳ���funccode,���޲�����3�����
//                    			|| referer.equalsIgnoreCase(basePath+func.getPagecode())
//                    			||referer.equalsIgnoreCase(basePath+func.getPagecode()+"?funccode="+func.getFunccode())){
//                    		//���������ҳ����������ʵģ�����Է���
//                    		logger.debug("����ҳ�淢�������,�������==="+basePath+func.getPagecode()+"&funccode="+func.getFunccode());
//                    		logger.debug(requestURI+"?"+request.getQueryString());
//                    		chain.doFilter(request,response);
//        	        		return;
//                    	}
//                    	
//                    	//������������
//            	        if(request.getQueryString() == null){
//            	        	if(requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| requestURI.equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("�������,�޲�������,��t_role_func�����ж���:"+requestURI);
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }else{
//            	        	//������������
//            	        	String queryString = request.getQueryString();
//            	        	
//            	        	//logger.debug("xxxx_"+requestURI+"?"+queryString);
//            	        	//logger.debug("aaaa_"+request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode());
//            	        	if((requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"&funccode="+func.getFunccode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode())
//            	        			|| (requestURI+"?"+queryString).equalsIgnoreCase(request.getContextPath()+func.getPagecode()+"?funccode="+func.getFunccode())){
//            	        		logger.debug("�������,��t_role_func�����ж���==="+requestURI+"?"+request.getQueryString());
//            	        		chain.doFilter(request,response);
//            	        		return;
//            	        	}
//            	        }
//                    }	
//            	}

            }
            
        }
        
        logger.info("�ܾ�����==="+requestURI+"?"+request.getQueryString());
        request.getRequestDispatcher("/error.jsp").forward(request,response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		
		this.filterConfig = filterConfig;
		List ignorePageList = new ArrayList();
		List allowRequestPageList = new ArrayList();
		//ȡ��WEB.XML�к��Թ���ҳ��
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
