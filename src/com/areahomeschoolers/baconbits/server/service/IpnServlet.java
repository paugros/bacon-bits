package com.areahomeschoolers.baconbits.server.service;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@Component
@RequestMapping("/ipn")
public class IpnServlet extends RemoteServiceServlet implements ServletContextAware, Controller {

	private static final long serialVersionUID = 1L;

	protected ServletContext servletContext;

	public IpnServlet() {
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// public class PaypalListenerServlet extends HttpServlet {
		//  
		// protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// HttpClient client = new DefaultHttpClient();
		// HttpPost post = new HttpPost(Constants.PAYPAL_URL);
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("cmd", "_notify-validate")); //You need to add this parameter to tell PayPal to verify
		// for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
		// String name = e.nextElement();
		// String value = request.getParameter(name);
		// params.add(new BasicNameValuePair(name, value));
		// }
		// post.setEntity(new UrlEncodedFormEntity(params));
		// String rc = getRC(client.execute(post)).trim();
		// if ("VERIFIED".equals(rc)) {
		// //Your business code comes here
		// }
		// }
		//  
		// private String getRC(HttpResponse response) throws IOException, IllegalStateException {
		// InputStream is = response.getEntity().getContent();
		// BufferedReader br = new BufferedReader(new InputStreamReader(is));
		// String result = "";
		// String line = null;
		// while ((line = br.readLine()) != null) {
		// result += line;
		// }
		// return result;
		// }
		//  
		// }
		//

		try {
			String method = request.getMethod();
			if ("POST".equals(method)) {
				handlePost(request, response);
			} else if ("GET".equals(method)) {
				handleGet(request, response);
			}
		} finally {
		}
		return null;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	}

	private void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	}

	@Override
	protected void checkPermutationStrongName() throws SecurityException {
		return;
	}
}
