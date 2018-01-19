/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
 
public class iTextTest extends HttpServlet {

    private static final long serialVersionUID = 1L;

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

		out.println("<h2>Build pipeline working</h2>");
		
		File file = new File(DEST);
        file.getParentFile().mkdirs();
		try {
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
        image.setAbsolutePosition(36, 400);
        PdfContentByte over = stamper.getOverContent(1);
        over.addImage(image);
        stamper.close();
        reader.close();
    }
}



