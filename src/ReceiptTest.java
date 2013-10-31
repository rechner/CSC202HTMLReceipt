import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptTest {

	public static void main(String[] args) throws IOException {

		PrinterJob job = PrinterJob.getPrinterJob();	
		
		// create a receipt.
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
		
		final ReceiptGenerator receipt = new ReceiptGenerator("Your Receipt");
		receipt.addLogo("logo.jpg");
		receipt.addTitle("The Baker's Dozen", "555 Main Street", 
						 "San Francsico, CA 94103", sdf.format(date), 
						 stf.format(date), "www.bakersdozen.com");
		receipt.addItem(1, "Ham and Turkey", "$6.00", "Swiss, sourdough");
		receipt.addItem("Apple", "$1.50");
		receipt.addItem(3, "Peanut Butter", "$6.75", "(@ $2.25 each)<br>single");
		receipt.addItem(1, "Iced Coffee", "$1.75");
		receipt.addSubtotal("$16.00", "Tip", "$2.00");
		receipt.addTotal("$18.00");
		receipt.addPayment("Cash", "-$20.00");
		receipt.addPayment("Change", "$2.00");
		receipt.addMessage("Thank You<br>Receipt ID: 123456789359");
		receipt.addBarcode("123456789");
		receipt.close();	
		
		// Since JEditorPane doesn't like being outside of a SWING thread, we
		// write the file out for this example and then pass JEditorPane an URL
		PrintWriter writer = new PrintWriter("html" + File.separator + "tmp.html", "UTF-8");
		writer.println(receipt.getHTML());
		writer.close();

		
		try {
			//final JEditorPane pane = new JEditorPane("file:///");
	    	//pane.setContentType("text/html");
			//pane.setText(receipt.getHTML());
			//HTMLReceiptPrinter printer = new HTMLReceiptPrinter("text/html", receipt.getHTML());
			
			/* For some reason, the above method of setting the html directly
			 * work, so we just pass it as a file instead. */
			HTMLReceiptPrinter printer = new HTMLReceiptPrinter(
					"file://" + System.getProperty("user.dir") + "/html/tmp.html");
			
			job.setPrintable(printer);
			
			printer.showPreview(null);
			/* This is the easy way to use it */
			//printer.fastPrint();
			
			
			/* or you can use this to get more control over the options */
			boolean doPrint = job.printDialog();
			
			if (doPrint) {
				job.print();
			}
						
		} catch (PrinterException e) {
			e.printStackTrace();
		}
			
			
	}

}
