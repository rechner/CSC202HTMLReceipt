import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;


/**
 * Generates HTML 3.2 markup for receipt generation in a POS system.
 * Features include inline images and barcode generation.
 * 
 * This is a "dumb" form that mostly only accepts Strings as parameters;
 * price calculation, currency formatting, and itemization must be performed 
 * elsewhere.
 *  
 * @author Zachary Sturgeon <zws258@email.vccs.edu>
 *
 */
public class ReceiptGenerator {
	
	private String html;
	private String header;
	private boolean closed = false;
	private boolean itemsOpen = false;
	private boolean paymentOpen = false;
	
	/**
	 * Requires a title parameter to satisfy HTML 3.2 standard.  Otherwise
	 * title parameter isn't important.
	 * 
	 * @param title
	 * @throws IOException
	 */
	public ReceiptGenerator(String title) throws IOException {
		
		BufferedReader br;
		br = new BufferedReader(new FileReader("html" + File.separator + "header.template"));
		try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line.replaceAll(":::TITLE:::", title));
	            sb.append('\n');
	            line = br.readLine();
	        }
	        this.header = sb.toString();
	        this.html = this.header;
	    } catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
			br = null;
	    }
		
	}
	
	/**
	 * Clears internal state.  Call before generating another recipt if reusing 
	 * an instance
	 */
	public void reset() {
		this.html = this.header;
		this.closed = false;
	}
	
	public void addLogo(String imagePath) {
		if (!this.closed) {
			//this.html += "<table width=320><tr><td>";
			this.html += "<img src=\"" + imagePath + "\"" +
					" width=320 height=100>\n";
			//this.html += "</td></tr><table>";
		}
	}
	
	
	/**
	 * Adds a standard receipt header with fields for business name,
	 * address, time, date, and website.
	 * 
	 * @param name
	 * @param address1
	 * @param address2
	 * @param date
	 * @param time
	 * @param website
	 */
	public void addTitle(String name, String address1, String address2, 
			String date, String time, String website) {
		if (!this.closed) {
			this.html += "<h1><font size=+1 face=\"verdana\"><b>:s:</b></font></h1>\n".replace(":s:", name) +
			"<table width=320>\n" +
			"<tr>\n"+
             "<td>"+address1+"<br>"+address2+"</td>\n" +
             "<td class=\"td-right\">" + date + "<br>" + time + "</td>" +
            "</tr>" +
            "<tr>" +
             "<td><b>" + website + "</b></td>" +
             "<td></td>" +
            "</tr>" +
            "</table>" +
            "<img src=\"black.gif\" width=320 height=4>";
		}
	}
	
	
	/**
	 * Adds a single quantity (1 × ) element to the itemized receipt.
	 * @param name
	 * @param price
	 */
	public void addItem(String name, String price) {
		if (!this.closed) {
			if (!this.itemsOpen) {
				html += "<table width=320>";
				this.itemsOpen = true;
			}
			
			html += "<tr><td class=\"item\"><font face=\"verdana\">1 × " + name + 
					"</font></td><td class=\"price\"><font face=\"verdana\">"+price+"</font></td></tr>";
			
		}
	}
	
	/**
	 * Adds itemized element to receipt.
	 * @param quantity
	 * @param name
	 * @param price
	 */
	public void addItem(int quantity, String name, String price) {
		if (!this.closed) {
			if (!this.itemsOpen) { 
				html += "<table width=320>";
			    this.itemsOpen = true;
			}
			
			html += "<tr><td class=\"item\"><font face=\"verdana\">" + quantity + 
					" × " + name + "</font></td><td class=\"price\">" +
					"<font face=\"verdana\">"+price+"</font></td></tr>";
			
		}
	}
	
	/**
	 * Adds itemized element to receipt with a smaller detail section under
	 * the item name.
	 * 
	 * @param quantity
	 * @param name
	 * @param price
	 * @param detail
	 */
	public void addItem(int quantity, String name, String price, String detail) {
		if (!this.closed) {
			if (!this.itemsOpen) {
				html += "<table width=320>";
				this.itemsOpen = true;
			}
			
			html += "<tr><td class=\"item\">" + quantity + 
					" × " + name + "<div class=\"detail\"><font face=\"verdana\">" + detail +
					"</font></div></td><td class=\"price\"><font face=\"verdana\">" +
					price+"</font></td></tr>";
			
		}
	}
	
	/**
	 * Adds a subtotal section to the bottom of the receipt.  If you need to
	 * list tax or a tip field, see addSubtotal(subtotal, label2, value2) below.
	 * 
	 * @param subtotal
	 */
	public void addSubtotal(String subtotal) {
		
		if (!this.closed) {
			if (this.itemsOpen) {
				html += "</table>";
				this.itemsOpen = false;
			}
			
			html += "<img src=\"black.gif\" width=320 height=1>";
            
            html += "<table width=320>" +
            		"<tr><td><font face=\"verdana\">Subtotal</font></td>" +
                    "<td class=\"price\"><font face=\"verdana\">" + subtotal + "</font></td>" + 
                    "</tr></table>";
            
            html += "<img src=\"black.gif\" width=320 height=4>";
		}
		
	}
	
	/**
	 * Adds a subtotal section with a second field such as tax or tip.
	 * 
	 * @param subtotal
	 * @param label2
	 * @param value2
	 */
	public void addSubtotal(String subtotal, String label2, String value2) {
		
		if (!this.closed) {
			if (this.itemsOpen) {
				html += "</table>";
				this.itemsOpen = false;
			}
			
			html += "<img src=\"black.gif\" width=320 height=1>";
            
            html += "<table width=320>" +
            		"<tr><td>Subtotal</td>" +
                    "<td class=\"price\">" + subtotal + "</td>" + 
                    "</tr><tr><td>" + label2 + "</td>" +
                    "<td class=\"price\">" + value2 + "</td></tr></table>";
            
		}
		
	}
	
	/**
	 * Adds grand total section.  This can be called before addPayment() 
	 * if you don't want to to defer receipt generation in your split tender
	 * code (although this isn't a very good idea).
	 * 
	 * @param total
	 */
	public void addTotal(String total) {
		if (!this.closed) {
			if (!this.paymentOpen) {
				html += "<img src=\"black.gif\" width=320 height=4>";
				html += "<table width=320>";
				paymentOpen = true;
			}
			
			html += "<tr><td>Total</td><td class=\"total\">"+ total + 
					"</td></tr>";
		}
	}
	
	/**
	 * Adds a grand total and payment section, which saves having to call the
	 * addPayment() method.  Useful for most single-tendered exact change
	 * transactions, such as debit/credit.
	 * 
	 * @param total
	 * @param method
	 * @param payment
	 */
	public void addTotal(String total, String method, String payment) {
		if (!this.closed) {
			if (!this.paymentOpen) {
				html += "<img src=\"black.gif\" width=320 height=4>";
				html += "<table width=320>";
				paymentOpen = true;
			}
			
			html += "<tr><td>Total</td><td class=\"total\">"+ total + 
					"</td></tr><tr><td>"+ method + "</td>" +
                     "<td class=\"total\">" + payment + "</td></tr>";
		}
	}
	
	
	/**
	 * Adds a payment or change segment.  While functionally similar to
	 * addTotal(), it should only be called after such.  This can be called
	 * twice to show cash tendered and change, or giftcard payment and the
	 * card's balance after the transaction.
	 * 
	 * @param method
	 * @param ammount
	 */
	public void addPayment(String method, String ammount) {
		if (!this.closed) {
			if (!this.paymentOpen)
				return; // dont add anything if there's no total yet.
			
			html += "<tr><td>"+ method + "</td>" +
                     "<td class=\"total\">" + ammount+ "</td></tr>";
		}
	}
	
	/**
	 * Adds a generic text message for noting information or surveys.
	 * 
	 * @param message Message to display
	 */
	public void addMessage(String message) {
		if (!this.closed) {
			if (this.paymentOpen){
				html += "</table>";
				this.paymentOpen = false;
			}
			
			html += "<div class=\"receipt-id\">" + message + "</div>";
		}
	}
	
	/**
	 * Adds an image to the receipt output, and despite the name is not
	 * required to be an imgage.
	 * 
	 * @param barcodeImage path to image, relative to ./html/ 
	 */
	public void addBarcodeImage(String barcodeImage) {
		if (!this.closed) {
			if (this.paymentOpen){
				html += "</table>";
				this.paymentOpen = false;
			}
			
			html += "<div width=320 style=\"width: 320px\">" + 
					"<img class=\"barcode\" src=\"" + barcodeImage + "\"" +
					"width=250 height=30></div>";
		}
	}
	
	/**
	 * Generates a Code128B barcode image and inserts it into the document.
	 * 
	 * @param data String to encode into barcode
	 */
	public void addBarcode(String data) {
		
		try {
			Barcode barcode = BarcodeFactory.createCode128B(data);
			barcode.setBarHeight(50);
			barcode.setDrawingText(false);
			
			File f = new File("html" + File.separator + "barcode.png");
			BarcodeImageHandler.savePNG(barcode, f);
			
			this.addBarcodeImage(f.getName());
			
		} catch (BarcodeException e) {
			System.err.println("ERROR: Unable to generate barcode");
			e.printStackTrace();
		} catch (OutputException e) {
			System.err.println("ERROR: Unable to save barcode image");
			e.printStackTrace();
		}
		
	}
	
/**
 * Generates a Code128B barcode image and inserts it into the document.
 * 
 * @param data String to encode into barcode
 * @param width Width of resulting barcode image
 * @param height Height of barcode image
 * @param displayText Set to true to include the data string under barcode
 */
public void addBarcode(String data, int width, int height, boolean displayText) {
		
		try {
			Barcode barcode = BarcodeFactory.createCode128B(data);
			barcode.setBarWidth(width);
			barcode.setBarHeight(height);
			barcode.setDrawingText(displayText);
			
			File f = new File("html" + File.separator + "barcode.png");
			BarcodeImageHandler.savePNG(barcode, f);
			
			this.addBarcodeImage(f.getName());
			
		} catch (BarcodeException e) {
			System.err.println("ERROR: Unable to generate barcode");
			e.printStackTrace();
		} catch (OutputException e) {
			System.err.println("ERROR: Unable to save barcode image");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Closes the html document.  Call this before using generated code.
	 */
	public void close() {
		if (!this.closed) {
			html += "</body></html>";
			this.closed = true;
		}
	}
	
	/**
	 * Returns the generated HTML.
	 * @return HTML document
	 */
	public String getHTML() {
		if (!this.closed) {
			this.close();
			this.closed = true;
		}
		
		return this.html;
	}

}
