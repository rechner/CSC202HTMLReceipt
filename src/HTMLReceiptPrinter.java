import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

/**
 * Prepares an HTML file using JEditorPane for preview or printing.
 * 
 * @author Zachary Sturgeon <zws258@email.vccs.edu>
 *
 */
public class HTMLReceiptPrinter implements Printable {
	
	private JEditorPane htmlPane;
	
	/**
	 * Opens the HTML file specified by the URL.  Use file:/// for local files.
	 * @param url
	 * @throws IOException
	 */
	public HTMLReceiptPrinter(String url) throws IOException {
		
		this.htmlPane = new JEditorPane(url);
		this.htmlPane.setEditable(false);		
				
	}
	
	/**
	 * @param contentType Usually "text/html"
	 * @param contents The HTML document
	 * @throws IOException
	 */
	public HTMLReceiptPrinter(String contentType, String contents) throws IOException {
		this.htmlPane = new JEditorPane(contentType, contents);
		this.htmlPane.setEditable(false);
	}
	
	public HTMLReceiptPrinter(JEditorPane editor) {
		this.htmlPane = editor;
		this.htmlPane.setEditable(false);
	}
	
	public int showPreview(Component parent) {
		return JOptionPane.showConfirmDialog(parent, new JScrollPane(this.htmlPane));
	}

	/**
	 * Calling this should satisfy most.  If it's too big, then use with 
	 * PrinterJob.
	 * @return
	 * @throws PrinterException
	 */
	public boolean fastPrint() throws PrinterException {
		return this.htmlPane.print();
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int page)
			throws PrinterException {
		
		if (page > 0) 
			return Printable.NO_SUCH_PAGE;
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2d.scale(0.5, 0.5);
		
		/* This is necessary to set the <img> loading to syncronous,
		 * since the images render in JOptionPane after the pane is loaded;
		 * Syncronous makes sure the images are rendered before submitting
		 * to the printer.	 
		 * 
		 * UPDATE:  Seems to work fine without this.
		this.htmlPane.setEditorKit(new HTMLEditorKit() {

			private static final long serialVersionUID = -1395518469603466191L;

			@Override
            public ViewFactory getViewFactory() {
                return new HTMLFactory() {

                    @Override
                    public View create(Element elem) {
                        View view = super.create(elem);
                        if (view instanceof ImageView) {
                            ((ImageView) view).setLoadsSynchronously(true);
                        }
                        return view;
                    }
                };
            }
        });*/
		
		htmlPane.print(g);
		
		return Printable.PAGE_EXISTS;
	}

}
