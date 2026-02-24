package es.codeurjc.scam_g18.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.OrderItem;

@Service
public class InvoicePdfService {

    private static final double TAX_RATE = 0.21;

    public byte[] generateInvoicePdf(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime issuedAt = order.getPaidAt() != null ? order.getPaidAt() : LocalDateTime.now();

        document.add(new Paragraph("Factura SCAM")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Pedido #" + order.getId()));
        document.add(new Paragraph("Fecha: " + issuedAt.format(formatter)));
        document.add(new Paragraph("Cliente: " + safeValue(order.getBillingFullName())));
        document.add(new Paragraph("Email: " + safeValue(order.getBillingEmail())));
        document.add(new Paragraph("Referencia pago: " + safeValue(order.getPaymentReference())));
        document.add(new Paragraph(" "));

        Table table = new Table(UnitValue.createPercentArray(new float[] { 6, 2, 2 }));
        table.useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Concepto").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tipo").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Importe").setBold()));

        int subtotalCents = 0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                int price = item.getPriceAtPurchaseCents() == null ? 0 : item.getPriceAtPurchaseCents();
                subtotalCents += price;

                table.addCell(new Cell().add(new Paragraph(resolveItemName(item))));
                table.addCell(new Cell().add(new Paragraph(resolveItemType(item))));
                table.addCell(new Cell().add(new Paragraph(formatEuros(price))).setTextAlignment(TextAlignment.RIGHT));
            }
        }

        document.add(table);
        document.add(new Paragraph(" "));

        int taxCents = (int) (subtotalCents * TAX_RATE);
        int totalCents = subtotalCents + taxCents;

        document.add(new Paragraph("Subtotal: " + formatEuros(subtotalCents)).setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("IVA (21%): " + formatEuros(taxCents)).setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("Total: " + formatEuros(totalCents))
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));

        document.close();
        return outputStream.toByteArray();
    }

    private String resolveItemName(OrderItem item) {
        if (item.isSubscription()) {
            return "Suscripción Premium (30 días)";
        }
        if (item.getCourse() != null) {
            return item.getCourse().getTitle();
        }
        if (item.getEvent() != null) {
            return item.getEvent().getTitle();
        }
        return "Producto";
    }

    private String resolveItemType(OrderItem item) {
        if (item.isSubscription()) {
            return "Suscripción";
        }
        if (item.getCourse() != null) {
            return "Curso";
        }
        if (item.getEvent() != null) {
            return "Evento";
        }
        return "Otro";
    }

    private String formatEuros(int cents) {
        return String.format("%.2f €", cents / 100.0);
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
