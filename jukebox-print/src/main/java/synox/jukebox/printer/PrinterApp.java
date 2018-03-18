package synox.jukebox.printer;

import synox.jukebox.printer.layout.Printer;
import synox.jukebox.printer.model.PrintBox;
import synox.jukebox.printer.parser.InputReader;

import java.io.File;
import java.util.List;

public class PrinterApp {
    public static void main(String[] args) throws Exception {
        Printer p = new Printer();
        InputReader r = new InputReader(p.getDocument());
        List<PrintBox> boxes = r.read(new File("jukebox-print-input"));
        p.print(boxes, "jukebox-print-output.pdf");
    }
}
