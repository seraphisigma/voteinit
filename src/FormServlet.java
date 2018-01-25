import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.ResourceBundle;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
 
public class FormServlet extends HttpServlet {


	public static final String SRC = "webapps/voteinit/resources/pdfs/florida.voting.amend.pdf";
    public static final String DEST = "voteinitresults/hello_with_image_id.pdf";
	public static final String WEB_DEST = "../../results/stamper/hello_with_image_id.pdf";
	
    public static final String IMG = "webapps/voteinit/resources/images/logo.jpg";
	
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        ResourceBundle rb =
            ResourceBundle.getBundle("LocalStrings",request.getLocale());
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\" />");
        String title =  "Hello socially conscious voter!";
        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body bgcolor=\"white\">");

        out.println("<h1>" + title + "</h1>");
		out.println("<h3>Bundle gave:" + rb.getString("voteinit.title") + "</h3>");

		out.println("<h2>From form  servlet</h2>");
		
		File file = new File(DEST);
        file.getParentFile().mkdirs();
		try {
			PdfReader reader = new PdfReader(SRC);
			out.println("<h4>about to print fields" + reader.getAcroFields().getFields().entrySet().size());
			for(Map.Entry<String, AcroFields.Item> entry : reader.getAcroFields().getFields().entrySet()) {
				out.println("<h4>!! " + entry.getKey() + ", !!! " + entry.getValue());
			}
			reader.close();
			
			manipulatePdf(SRC, DEST);
		} catch (Exception ex) {
			out.println("<h4>Exception was: " + ex.getMessage() + "</h4>");
		}

		out.println("<h2>Made a pdf!</h2>");
        out.println("<a href=\"" + WEB_DEST + "\">Try it?</a>");
		
        out.println("</body>");
        out.println("</html>");
    }
	
    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        Image image = Image.getInstance(IMG);
        PdfImage stream = new PdfImage(image, "", null);
        stream.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
        image.setDirectReference(ref.getIndirectReference());
        PdfContentByte over = stamper.getOverContent(1);

		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
		
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
					//over.showText("here we go wow");
					// 136, 180 name
					//over.moveText(136, 180); 
					over.endText();
				}
			}
		}
		
		over.beginText();
		over.setFontAndSize(bf, 10);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Your name", 92, 658, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Your address", 100, 630, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"City", 66, 608, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Zip", 266, 608, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"County", 404, 608, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"X", 44, 586, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Voter Registration Number", 162, 566, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Date of Birth", 402, 566, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Date of Signature", 40, 168, 0);
		over.showTextAligned(PdfContentByte.ALIGN_LEFT, 
			"Signature", 222, 168, 0);
		over.endText();
		
		image.setAbsolutePosition(36, 400);
		//over.addImage(image);
		
        stamper.close();
        reader.close();
    }
}



