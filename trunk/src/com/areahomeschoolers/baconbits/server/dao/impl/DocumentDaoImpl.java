package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;

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
			// update("delete from documentsMapping where documentId = ?", documentId);
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
		String sql = "select id, fileName, fileType, fileExtension, addedById, startDate, endDate, addedDate, description from documents";
		ArrayList<Document> data = query(sql, new DocumentMapper());

		return data;
	}

	@Override
	public Document save(Document document) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(document);

		if (document.isSaved()) {
			String sql = "update documents set description = :description, startDate = :startDate, endDate = :endDate where id = :id";
			update(sql, namedParams);
		} else {
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
		}

		return getById(document.getId());
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

}
