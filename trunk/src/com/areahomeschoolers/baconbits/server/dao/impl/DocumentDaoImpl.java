package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.DocumentDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

@Repository
public class DocumentDaoImpl extends SpringWrapper implements DocumentDao {

	private final class DocumentMapper implements RowMapper<Document> {
		@Override
		public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
			return createDocument(rs);
		}
	}

	private final class FullDocumentMapper implements RowMapper<Document> {
		@Override
		public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
			Document d = createDocument(rs);
			d.setData(rs.getBytes("document"));
			d.setFileSize(d.getData().length);
			return d;
		}
	}

	@Autowired
	public DocumentDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void delete(int documentId) {
		if (documentId > 0) {
			update("delete from documentArticleMapping where documentId = ?", documentId);
			update("delete from documentEventMapping where documentId = ?", documentId);
			update("delete from documents where id = ?", documentId);
		}
	}

	@Override
	public Document getById(int documentId) {
		String sql = "select * from documents where id = ?";

		return queryForObject(sql, new FullDocumentMapper(), documentId);
	}

	@Override
	public ArrayList<Document> list(ArgMap<DocumentArg> args) {
		int articleId = args.getInt(DocumentArg.ARTICLE_ID);
		int eventId = args.getInt(DocumentArg.EVENT_ID);
		List<Object> sqlArgs = new ArrayList<Object>();

		String sql = "select d.* from documents d ";
		if (articleId > 0) {
			sql += "join documentArticleMapping da on da.documentId = d.id and da.articleId = ? ";
			sqlArgs.add(articleId);
		}

		if (eventId > 0) {
			sql += "join documentEventMapping da on da.documentId = d.id and da.eventId = ? ";
			sqlArgs.add(eventId);
		}
		sql += "order by d.description";

		ArrayList<Document> data = query(sql, new DocumentMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Document save(Document document) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(document);

		if (document.isSaved()) {
			String sql = "update documents set description = :description, startDate = :startDate, endDate = :endDate where id = :id";
			update(sql, namedParams);
		} else {
			// scale if needed
			if (document.getLinkType() != null) {
				if (document.getLinkType() == DocumentLinkType.HTML_IMAGE_INSERT) {
					scaleImageToMaximumSize(document, 600, 2000);
				} else if (document.getLinkType() == DocumentLinkType.BOOK) {
					scaleImageToMaximumSize(document, 300, 300);
				} else if (document.getLinkType() == DocumentLinkType.PROFILE) {
					cropToSquare(document);
					scaleImageToMaximumSize(document, 200, 200);
				}
			}

			if (document.getStartDate() == null) {
				document.setStartDate(new Date());
			}

			if (ServerContext.isAuthenticated()) {
				document.setAddedById(ServerContext.getCurrentUser().getId());
			}

			if (document.getStringData() != null) {
				try {
					document.setData(document.getStringData().getBytes("UTF8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				document.setFileSize(document.getData().length);
				document.setStringData(null);
			}

			String sql = "insert into documents (addedById, startDate, endDate, addedDate, description, document, fileName, fileType, fileExtension) values ";
			sql += "(:addedById, :startDate, :endDate, now(), :description, :data, :fileName, :fileType, :fileExtension)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			document.setId(ServerUtils.getIdFromKeys(keys));

			if (document.getLinkType() != null && document.getLinkId() > 0) {
				if (document.getLinkType() == DocumentLinkType.BOOK) {
					String newsql = "update books set imageId = ? where id = ?";
					update(newsql, document.getId(), document.getLinkId());

					scaleImageToMaximumSize(document, 80, 80);
					update(sql, namedParams, keys);

					sql = "update books set smallImageId = ? where id = ?";
					update(sql, ServerUtils.getIdFromKeys(keys), document.getLinkId());
				} else if (document.getLinkType() == DocumentLinkType.PROFILE) {
					String newsql = "update users set imageId = ? where id = ?";
					update(newsql, document.getId(), document.getLinkId());

					scaleImageToMaximumSize(document, 80, 80);
					update(sql, namedParams, keys);

					sql = "update users set smallImageId = ? where id = ?";
					update(sql, ServerUtils.getIdFromKeys(keys), document.getLinkId());
				} else {
					link(document);
				}
			}
		}

		return getById(document.getId());
	}

	public void scaleImageToMaximumSize(int docId, int maxWidth, int maxHeight) {
		Document d = getById(docId);
		d = scaleImageToMaximumSize(d, maxWidth, maxHeight);
		save(d);
	}

	private Document createDocument(ResultSet rs) throws SQLException {
		Document document = new Document();
		document.setId(rs.getInt("id"));
		document.setAddedById(rs.getInt("addedById"));
		document.setFileExtension(rs.getString("fileExtension"));
		document.setFileType(rs.getString("fileType"));
		document.setFileName(rs.getString("fileName"));
		document.setStartDate(rs.getTimestamp("startDate"));
		document.setEndDate(rs.getTimestamp("endDate"));
		document.setAddedDate(rs.getTimestamp("addedDate"));
		document.setDescription(rs.getString("description"));
		return document;
	}

	private Document cropToSquare(Document d) {
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		Image image = ImagesServiceFactory.makeImage(d.getData());

		int width = image.getWidth();
		int height = image.getHeight();
		if (width == height) {
			return d;
		}

		float leftX = 0;
		float topY = 0;
		float rightX = 1;
		float bottomY = 1;
		if (height > width) {
			// we need to change the Y coordinates
			float ratio = ((height - width) / 2) / height;
			topY = ratio;
			bottomY = 1 - ratio;
		} else {
			// we need to change the X coordinates
			float ratio = ((width - height) / 2) / width;
			leftX = ratio;
			rightX = 1 - ratio;
		}

		Transform crop = ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);

		Image newImage = imagesService.applyTransform(crop, image);

		d.setData(newImage.getImageData());

		return d;
	}

	private Document link(Document document) {
		String entityType = document.getLinkType().getEntityType();

		String sql = "insert into document" + Common.ucWords(entityType) + "Mapping (documentId, " + entityType + "Id) values (?, ?)";
		update(sql, document.getId(), document.getLinkId());

		return document;
	}

	/**
	 * This function will reduce the size of the image of the provided document ID to the maximum dimensions provided. The image will have the same proportions
	 * as the original image.
	 * 
	 * @param docId
	 * @param response
	 * @return
	 */
	private Document scaleImageToMaximumSize(Document d, int maxWidth, int maxHeight) {
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		Image image = ImagesServiceFactory.makeImage(d.getData());

		int width = image.getWidth();
		int height = image.getHeight();

		if (width > maxWidth || height > maxHeight) {
			// We need to find a new height and width that's within the maximum and still to scale with the original image.
			double tooWideRatio = (double) width / (double) maxWidth;
			double tooTallRatio = (double) height / (double) maxHeight;
			double reductionDivisor = tooWideRatio > tooTallRatio ? tooWideRatio : tooTallRatio;

			int newWidth = (int) (width / reductionDivisor);
			int newHeight = (int) (height / reductionDivisor);

			Transform resize = ImagesServiceFactory.makeResize(newWidth, newHeight);

			Image newImage = imagesService.applyTransform(resize, image);

			d.setData(newImage.getImageData());
		}

		return d;
	}

}
