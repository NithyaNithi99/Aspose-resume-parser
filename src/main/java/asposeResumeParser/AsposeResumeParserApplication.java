package asposeResumeParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsposeResumeParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsposeResumeParserApplication.class, args);

			//ExStart: ApplyLicenseFromFile
		// Initialize the licensing component
		com.aspose.ocr.License license = new com.aspose.ocr.License();

		// Load the license from file
		//license.SetLicense("Aspose_OCR_License_File.lic");
			license.setLicense("Aspose.TotalforJava.lic");
			// Validate the license
			if(license.isValid()) {
			    System.out.println("The license is valid!");
			} else {
			    System.out.println("The license is invalid!");
			}
			//ExEnd: ApplyLicenseFromFile
	}

}
