//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.io.*;
//import java.util.Map;
//import java.util.ResourceBundle;
import java.util.*;
import java.lang.Math.*;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.codec.Base64;
 
public class FormServlet extends HttpServlet {

	public static final String SRC_PDF = "webapps/voteinit/resources/pdfs/florida.voting.amend.pdf";

    public static final String DEST_DIR = "voteinitresults/";
	public static final String DEST_PREFIX = "fl.vra18.";
	public static final String DEST_EXT = ".pdf";
	
	public class FormData {
		String name;
		String address;
		String city;
		String zip;
		String county;
		boolean changeAddress;
		String voterReg;
		String dateOfBirth;
		String signatureDate;
		String sigBase64;
		
		@Override
		public int hashCode() {
			return name.hashCode() 
				^ city.hashCode()
				^ zip.hashCode()
				^ county.hashCode()
				^ (changeAddress ? 0 : 1)
				^ voterReg.hashCode()
				^ dateOfBirth.hashCode()
				^ signatureDate.hashCode()
				^ sigBase64.hashCode();
		}
		
	};
	
	public FormData getValidFormData(
			HttpServletRequest request,
			PrintWriter out
			) throws ServletException {
		FormData data = new FormData();
					
		data.name = request.getParameter("name");
		data.address = request.getParameter("address");
		data.city = request.getParameter("city");
		data.zip = request.getParameter("zip");
		data.county = request.getParameter("county");
		data.changeAddress = request.getParameter("changeAddress") != null;
		data.voterReg = request.getParameter("voterReg");
		data.dateOfBirth = request.getParameter("dateOfBirth");
		data.signatureDate = request.getParameter("signatureDate");
		data.sigBase64 = request.getParameter("signature");

		if(data.name == null || data.address == null || data.city == null || data.zip == null || data.county == null || data.voterReg == null || data.dateOfBirth == null || data.signatureDate == null || data.sigBase64 == null) {
			String report = String.format(
				"name:%s address:%s city:%s zip:%s county:%s change:%d voterReg:%s dateOfBirth:%s signatureDate:%s",
				data.name, data.address, data.city, data.zip, data.county, data.voterReg, data.dateOfBirth, data.signatureDate);
			throw new ServletException("invalid form data:" + report);
		}
	
		return data;
	}
	
	public String getResultFileName(FormData person) {
		String result = DEST_DIR + DEST_PREFIX 
			+ Math.abs(person.hashCode()) + "."
			+ Math.abs(System.currentTimeMillis())
			//+ "." + Math.abs((new Long(System.currentTimeMillis())).hashCode())
			+ DEST_EXT;
		return result;
	}
	
	@Override
	public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException {
		response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html><html>");
        out.println("<head>");
        out.println("<body bgcolor=\"white\">");

		out.println("<h2>wow:" + request.getParameter("name") + "!</h2>");
		
		FormData personData;
		try {
			personData = getValidFormData(request, out);
		} catch (ServletException ex) {
			out.println(
				"<h2>Exception was: " + ex.getMessage() + "</h2>");
			throw(ex);
		}

		out.println("<h2>From POST form servlet</h2>");
		
		String resultName = getResultFileName(personData);
		File file = new File(resultName);
        file.getParentFile().mkdirs();
		try {			
			writeNewForm(SRC_PDF, resultName, personData);
		} catch (Exception ex) {
			out.println("<h4>Exception was: " + ex.getMessage() + "</h4>");
		}
		
		out.println("Signature recorded and petition created successfully!");
		
		out.println("<h4>Wrote to " + resultName + "</h4>");

	    out.println("</body>");
        out.println("</html>");
	}
	
    public void writeNewForm(
			String srcFile, 
			String destFile, 
			FormData personData
			) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(srcFile);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFile));
        PdfContentByte over = stamper.getOverContent(1);

		BaseFont bf = BaseFont.createFont(
			BaseFont.HELVETICA, "Cp1252", false);
		
		boolean drawDebugCoords = false;
		if(drawDebugCoords) {
			float xMax = 840;
			float yMax = 800;
			int xSteps = 10;
			int ySteps = 10;
			for(int i = 0; i < xSteps; i++) {
				for(int j = 0; j < ySteps; j++) {
					over.beginText();
					over.setFontAndSize(bf, 10);
					int xPos = (int)(((float)i) / ((float)xSteps) * xMax);
					int yPos = (int)(((float)j) / ((float)ySteps) * yMax);
					String text = "" + xPos + "," + yPos;
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
						text, xPos, yPos, 0 /*rotation*/);
					over.endText();
				}
			}
		}
		
		// Hardcoded numbers correspond to florida voting restoration amendment petition form
		over.beginText();
		over.setFontAndSize(bf, 10);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.name, 92, 658, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.address, 100, 630, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.city, 64, 608, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.zip, 266, 608, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.county, 408, 608, 0);
		if(personData.changeAddress) {
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
				"X", 44, 586, 0);
		}
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.voterReg, 162, 566, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.dateOfBirth, 404, 566, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			personData.signatureDate, 40, 168, 0);
		//over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
		//	"Signature", 222, 168, 0);
		over.endText();
		
		// should be formatted like 
		// data:image/png;base64,iVBORw0KGgoAAAAN...
		// so get rid of the header
		String justData = personData.sigBase64.split(",")[1]; 
		byte [] imageData = Base64.decode(justData);
		Image image = Image.getInstance(imageData);
		image.scaleAbsolute(280, 30);
		
        PdfImage stream = new PdfImage(image, "", null);
        stream.put(new PdfName("ITXT_SpecialId"),
			new PdfName("SignatureStream"));
        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
        image.setDirectReference(ref.getIndirectReference());
		image.setAbsolutePosition(222, 168);
		over.addImage(image);
		
        stamper.close();
        reader.close();
    }
}



