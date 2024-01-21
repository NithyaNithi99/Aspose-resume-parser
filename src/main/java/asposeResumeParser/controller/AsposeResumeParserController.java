package asposeResumeParser.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.aspose.email.Attachment;
//import com.aspose.email.AttachmentCollection;
//import com.aspose.email.ImapClient;
//import com.aspose.email.ImapMessageInfo;
//import com.aspose.email.ImapMessageInfoCollection;
//import com.aspose.email.ImapQueryBuilder;
//import com.aspose.email.MailMessage;
//import com.aspose.email.MailQuery;
//import com.aspose.email.SaveOptions;
//import com.aspose.email.SecurityOptions;
import com.aspose.pdf.Document;
import com.aspose.pdf.TextAbsorber;
import com.aspose.words.SaveFormat;
import com.aspose.words.TxtSaveOptions;

import com.aspose.ocr.RecognitionSettings;
import com.aspose.email.Attachment;
import com.aspose.email.AttachmentCollection;
import com.aspose.email.ImapClient;
import com.aspose.email.ImapMessageInfo;
import com.aspose.email.ImapMessageInfoCollection;
import com.aspose.email.ImapQueryBuilder;
import com.aspose.email.MailMessage;
import com.aspose.email.MailQuery;
import com.aspose.email.SaveOptions;
import com.aspose.email.SecurityOptions;
import com.aspose.ocr.AsposeOCR;
import com.aspose.ocr.Format;
import com.aspose.ocr.InputType;
import com.aspose.ocr.OcrInput;
import com.aspose.ocr.RecognitionResult;
import java.util.ArrayList;


@RestController
public class AsposeResumeParserController {
	 
	private ImapClient getClientInformation(ImapClient client) {
		client.setHost("outlook.office365.com");
		client.setPort(993);
		client.setUsername("sample@outlook.com");
		client.setPassword("saddds");
		client.setSecurityOptions(SecurityOptions.Auto);
		return client;
	}
	
	@GetMapping("/email-parse")
	public void retrieveNNumberOfMessagesFromServer() throws Exception {
		
		ImapClient client = getClientInformation(new ImapClient());

		ImapQueryBuilder builder = new ImapQueryBuilder();
		builder.getFrom().contains("nithya.p@sedintechnologies.com");
		//builder.getSubject().contains("nithya resume.docx");
		//builder.getInternalDate().on(null));
		MailQuery query = builder.getQuery();
		ImapMessageInfoCollection coll = client.listMessages(query);

		//List 5 messages from the server
		System.out.println(coll.size());
		
		for (ImapMessageInfo msgInfo : coll) {
			if(!msgInfo.isRead()) {
			
				MailMessage eml = client.fetchMessage(msgInfo.getUniqueId()); //use the sequence number to fetch messages
				eml.save(eml.getSubject() + ".eml", SaveOptions.getDefaultEml()); //save as EML
				MailMessage message = MailMessage.load(eml.getSubject() + ".eml");
				AttachmentCollection attachments = message.getAttachments();

				boolean isValidAttachment = getAttachmentDetail(attachments);
			
				if(!isValidAttachment) {
					message.save("HtmlOutput.html", SaveOptions.getDefaultHtml());
					com.aspose.words.Document document = new com.aspose.words.Document("HtmlOutput.html");
				    File myObj = new File("HtmlOutput.html"); 
				    myObj.delete();
					//document.save("output_email.text", SaveFormat.TEXT);   
					System.out.println(document.toString(SaveFormat.TEXT));
				}
			}

		}
	}
	
	
	private String getFileExtension(String fullName) {
	    String fileName = new File(fullName).getName();
	    int dotIndex = fileName.lastIndexOf('.');
	    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}
	
	private boolean getAttachmentDetail(AttachmentCollection attachments) throws Exception {
		
		for (Attachment attachment : attachments) {
		    String fileType = getFileExtension(attachment.getName());
		    if(fileType.equals("docx")) {
		    	convertDocxToText(attachment);
		    	return true;
		    } else if(fileType.equals("pdf")) {
		    	convertPdfToText(attachment);
		    	return true;
		    } else {
		    	System.out.println("Docx and pdf file formats only supported");
		    	return false;
		    }
		}
		return false;
	}
	
	
		private String  convertDocxToText(Attachment attachment) throws Exception {
			com.aspose.words.Document doc = new com.aspose.words.Document(attachment.getContentStream());
			TxtSaveOptions txtOpts = new TxtSaveOptions();
	        txtOpts.setMaxCharactersPerLine(100);
	        txtOpts.setSaveFormat(SaveFormat.TEXT);
	        txtOpts.setPrettyFormat(true);
	        //doc.save("/Users/nithyap/Downloads/" + "Exxtracted_DOCX_Text.txt", txtOpts);
			System.out.println(doc.toString(SaveFormat.TEXT));
	        return doc.toString(SaveFormat.TEXT);
		}
		
		private String convertPdfToText(Attachment attachment) throws IOException {
	        Document pdfDocument = new Document(attachment.getContentStream());
	         TextAbsorber ta = new TextAbsorber();
	         ta.visit(pdfDocument);
	         String txtFileName = Paths.get("/Users/nithyap/Downloads/", "PDFToTXT_out.txt").toString();
	         BufferedWriter writer = new BufferedWriter(new FileWriter(txtFileName));
	         writer.write(ta.getText());
	         writer.close();
			 System.out.println(ta.getText());
	         return ta.getText();
		}
		

		@GetMapping("/ocr")
			public void getTextFromPdfOCR() throws IOException {
				// ExStart:1
				// The path to the documents directory.
				String dataDir ="/Users/nithyap/Downloads/";
						// Utils.getSharedDataDir(OCRRecognizePdf.class);

				// The image path
				String file = dataDir + "Moodle.pdf";

				// Create api instance
				AsposeOCR api = new AsposeOCR();

				RecognitionSettings set = new RecognitionSettings();
				OcrInput input = new OcrInput(InputType.PDF);
				input.add(file);
				ArrayList<RecognitionResult> res =  api.Recognize(input, set);
					
				System.out.println("TEXT:\n" + res.get(0).recognitionText);
				System.out.println(res);
				AsposeOCR.SaveMultipageDocument("java.txt", Format.Json, res);
			}
		
		@GetMapping("/pdfToText")
		public void PdfToTextConverter() throws IOException {

		        // Specify the input PDF file path
	        String inputPdfPath = "/Users/nithyap/Downloads/sample-pdf-file.pdf";

	        String pdfFileName = Paths.get(inputPdfPath).toString();
	        String txtFileName = Paths.get("/Users/nithyap/Downloads/", "PDFToTXT_out.txt").toString();

	        // Load PDF document
	        Document pdfDocument = new Document(pdfFileName);
	        TextAbsorber ta = new TextAbsorber();
	        
	        pdfDocument.getPages().accept(ta);
	        ta.visit(pdfDocument);
	        BufferedWriter writer = new BufferedWriter(new FileWriter(txtFileName));
	        writer.write(ta.getText());
	        
	        System.out.println(ta.getText());
	        writer.close();

//					System.out.println(pdfDocument.getPages().size());
//					
//					int size = pdfDocument.getPages().size();
//					
//					for (int i = 1; i <= size; i++) {
//						textAbsorber.visit(pdfDocument.getPages().get_Item(i));
//						System.out.println(pdfDocument.getPages().toString());
//					}
//					// Extract text from the PDF document
//					//pdfDocument.getPages().accept(textAbsorber);
	//
//					// Get the extracted text
//					String extractedText = textAbsorber.getText();
	//
//					// Print the extracted text
//					System.out.println("Extracted Text:\n" + extractedText);
		}

	}
	