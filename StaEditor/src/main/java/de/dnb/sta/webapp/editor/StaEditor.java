package de.dnb.sta.webapp.editor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dnb.sta.storage.PropsHelper;
import de.dnb.sta.storage.StaProps;
import de.dnb.sta.storage.confluence.StaStorageConfluence;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.wikibase.StaStorageWikibase;

public class StaEditor extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final Logger LOGGER = System.getLogger(StaEditor.class.getName());

	private StaProps staProps;

	private StaStorageWikibase staStorageWikibase;

	private StaStorageConfluence staStorageConfluence;

	private StaStorageWikibase staStorageWikibaseTest;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		PrintWriter pw = res.getWriter();
		try {
			String operation = req.getParameter("operation");
			if (operation != null) {
				switch (operation) {
				case "show-confluence":
					pw.write(getConfluenceContent(req, res));
					break;
				case "show-wikibase":
					pw.write(getWikibaseContent(req, res));
					break;
				case "update-from-confluence-preview":
					pw.write(updateFromConfluencePreview(req, res));
					break;
				case "write-to-confluence":
					writeToConfluence(req, res);
					break;
				case "update-from-confluence":
					updateFromConfluence(req, res);
					break;
				}
			}
		} catch (Throwable e) {
			LOGGER.log(Level.ERROR, e.getMessage(), e);

			pw.println("{\"Error\":\"" + e.getMessage() + "\"}");
		} finally {
			pw.close();// close the stream
		}

	}

	private String updateFromConfluencePreview(HttpServletRequest req, HttpServletResponse res) {
		String ret = "";
		String confluenceId = req.getParameter("page-id");

		if (confluenceId != null) {
			StaContent staContent = staStorageWikibase.modify4Save(staStorageConfluence.load(confluenceId));
			if (staContent != null) {
				ret = GSON.toJson(staContent);
			} else {
				ret = "Konnte die Confluence-Seite nicht laden. Page-Id: " + confluenceId;
			}
		}
		return ret;
	}

	private String updateFromConfluence(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String ret = "";
		String confluenceId = req.getParameter("page-id");
		String wbStage = req.getParameter("wb-stage");
//		String paramEntityId = req.getParameter("entity-id");

		StaStorageWikibase wbStorage = staStorageWikibaseTest;
		if (wbStage != null && wbStage.equals("prod")) {
			wbStorage = staStorageWikibase;
		}
		if (confluenceId != null) {
			String entityId = wbStorage.save(staStorageConfluence.load(confluenceId));
			res.sendRedirect(wbStorage.getEntityUrl(entityId));
		}
		return ret;
	}

	private String getWikibaseContent(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String ret = "";
		String entityId = req.getParameter("entity-id");
		StaContent staContent = staStorageWikibase.load(entityId);
		if (staContent != null) {
			ret = GSON.toJson(staContent);
		} else {
			ret = "{\"Error\": \"Konnte die Wikibase-Entität nicht laden. Page-Id: " + entityId + "\"}";
		}
		return ret;
	}

	private void writeToConfluence(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String confluenceId = req.getParameter("page-id");
		String entityId = req.getParameter("entity-id");

		StaContent staContent = staStorageWikibase.load(entityId);
		if (staContent != null) {
			staContent.setId(StaStorageConfluence.CONFLUENCE_KEY, confluenceId);
			staStorageConfluence.save(staContent);
		}
		res.sendRedirect(staStorageConfluence.getPageUrl(confluenceId));
	}

	public String getConfluenceContent(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String ret = "";
		String confluenceId = req.getParameter("page-id");
		if (confluenceId != null) {
			StaContent staContent = staStorageConfluence.load(confluenceId);
			if (staContent != null) {
				ret = GSON.toJson(staContent);
			} else {
				ret = "Konnte die Confluence-Seite nicht laden. Page-Id: " + confluenceId;
			}

		}
		return ret;
	}

	@Override
	public void init() throws ServletException {
		try {
			staProps = PropsHelper.getProps();
			staStorageWikibase = new StaStorageWikibase(staProps.wbProps);
			staStorageWikibaseTest = new StaStorageWikibase(staProps.wbTestProps);
			staStorageConfluence = new StaStorageConfluence(staProps, staStorageWikibase, staStorageWikibase);
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, e);
			throw new ServletException();
		}
		super.init();
	}

}