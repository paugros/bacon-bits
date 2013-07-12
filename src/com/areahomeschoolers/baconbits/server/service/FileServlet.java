package com.areahomeschoolers.baconbits.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.areahomeschoolers.baconbits.server.dao.DocumentDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Servlet that receives all requests to "/baconbits/service/file": POSTs are expected to be file upload HTML forms, GETs are expected to be requests for
 * documents from the database.
 */
@Component
@RequestMapping("/file")
public class FileServlet extends RemoteServiceServlet implements ServletContextAware, Controller {
	private final Logger logger = Logger.getLogger(this.getClass().toString());
	private static final long serialVersionUID = 1L;
	private final DocumentDao documentDao;
	protected ServletContext servletContext;

	@Autowired
	public FileServlet(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ServerContext.loadContext(request, response, servletContext);

		try {
			String method = request.getMethod();
			if ("POST".equals(method)) {
				handlePost(request, response);
			} else if ("GET".equals(method)) {
				handleGet(request, response);
			}
		} finally {
			ServerContext.unloadContext();
		}
		return null;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		String delete = request.getParameter("deleteAfterServing");
		String inline = request.getParameter("inline");

		Document document = documentDao.getById(id);

		if (document == null) {
			return;
		}

		response.reset();
		response.setBufferSize(10240);
		response.setContentLength(document.getFileSize());
		response.setContentType(document.getFileType());

		// header
		String disp = "attachment";
		if (inline != null) {
			disp = "inline";
		}
		response.setHeader("Content-Disposition", disp + "; filename=\"" + document.getFileName() + "\"");

		ServletOutputStream out = response.getOutputStream();

		out.write(document.getData());

		out.flush();
		out.close();

		if (delete != null) {
			documentDao.delete(id);
		}
	}

	private void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletFileUpload upload = new ServletFileUpload();

		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				// loop through multipart form initializing document dto
				FileItemIterator iterator = upload.getItemIterator(request);

				String responseMsg;

				responseMsg = processDocumentUploadRequest(iterator);
				response.getWriter().write(responseMsg);
			}
		} catch (Exception e) {
			logger.warning("Unknown file upload error.");
			response.getWriter().write("Unknown file upload error.");
		}
	}

	private String processDocumentUploadRequest(FileItemIterator iterator) throws NumberFormatException, FileUploadException, IOException {
		Document document = new Document();

		while (iterator.hasNext()) {
			FileItemStream item = iterator.next();
			InputStream stream = item.openStream();

			if (item.isFormField()) {
				String name = item.getFieldName();
				StringWriter writer = new StringWriter();
				IOUtils.copy(stream, writer);
				String value = writer.toString();

				if ("description".equals(name)) {
					document.setDescription(value);
				} else if ("userId".equals(name)) {
					document.setAddedById(Integer.parseInt(value));
				} else if ("linkType".equals(name)) {
					if (!Common.isNullOrBlank(value)) {
						document.setLinkType(DocumentLinkType.valueOf(value));
					}
				} else if ("linkId".equals(name)) {
					document.setLinkId(Integer.parseInt(value));
				} else if ("fileName".equals(name)) {
					if (!Common.isNullOrBlank(value)) {
						document.setFileName(value);
					}
				}

			} else {
				document.setFileType(item.getContentType());
				if (document.getFileName() == null) {
					String name = FilenameUtils.getName(item.getName());
					if (name.length() > 100) {
						name = name.substring(0, 99);
					}
					document.setFileName(name);
				}

				String fn = document.getFileName().toLowerCase();
				String ext = Common.getFileExtension(fn);
				document.setFileExtension(ext);
				document.setData(IOUtils.toByteArray(stream));
			}
		}

		document = documentDao.save(document);

		if (document == null) {
			return "Unknown document upload error.";
		}

		return Integer.toString(document.getId());
	}

	@Override
	protected void checkPermutationStrongName() throws SecurityException {
		return;
	}
}
