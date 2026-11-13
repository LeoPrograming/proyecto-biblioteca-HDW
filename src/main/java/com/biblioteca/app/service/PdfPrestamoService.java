package com.biblioteca.app.service;

import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Prestamo;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfPrestamoService {

    private final PrestamoService prestamoService;

    public PdfPrestamoService(
            PrestamoService prestamoService
    ) {
        this.prestamoService = prestamoService;
    }

    public byte[] generarComprobante(
            Integer idPrestamo
    ) {

        Prestamo prestamo =
                prestamoService.buscarPorId(
                        idPrestamo
                );

        List<DetallePrestamo> detalles =
                prestamoService.listarDetalles(
                        idPrestamo
                );

        try {

            ByteArrayOutputStream salida =
                    new ByteArrayOutputStream();

            Document documento =
                    new Document();

            PdfWriter.getInstance(
                    documento,
                    salida
            );

            documento.open();

            Font tituloFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD
                    );

            Font subtituloFont =
                    new Font(
                            Font.HELVETICA,
                            12,
                            Font.BOLD
                    );

            Paragraph titulo =
                    new Paragraph(
                            "Biblioteca Virtual General",
                            tituloFont
                    );

            titulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(titulo);

            Paragraph subtitulo =
                    new Paragraph(
                            "Comprobante de Reserva",
                            subtituloFont
                    );

            subtitulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            subtitulo.setSpacingAfter(20);

            documento.add(subtitulo);


            documento.add(
                    new Paragraph(
                            "Préstamo N°: "
                                    + prestamo.getIdPrestamo()
                    )
            );

            documento.add(
                    new Paragraph(
                            "Fecha: "
                                    + prestamo.getFechaPrestamo()
                    )
            );

            documento.add(
                    new Paragraph(
                            "Comprobante: "
                                    + prestamo.getSerie()
                                    + " - "
                                    + prestamo.getNumero()
                    )
            );

            documento.add(
                    new Paragraph(
                            "Estado: "
                                    + prestamo.getEstado()
                    )
            );

            documento.add(
                    new Paragraph(
                            "Total: S/ "
                                    + prestamo.getMontoTotal()
                    )
            );

            documento.add(
                    new Paragraph(" ")
            );


            PdfPTable tabla =
                    new PdfPTable(5);

            tabla.setWidthPercentage(100);

            agregarCabecera(
                    tabla,
                    "Libro"
            );

            agregarCabecera(
                    tabla,
                    "Días"
            );

            agregarCabecera(
                    tabla,
                    "Precio/Día"
            );

            agregarCabecera(
                    tabla,
                    "Fecha Compromiso"
            );

            agregarCabecera(
                    tabla,
                    "Subtotal"
            );


            for (
                    DetallePrestamo detalle
                    : detalles
            ) {

                tabla.addCell(
                        detalle.getLibro() != null
                                ? detalle.getLibro()
                                  .getTitulo()
                                : ""
                );

                tabla.addCell(
                        detalle.getTiempo() != null
                                ? detalle.getTiempo()
                                  .toString()
                                : ""
                );

                tabla.addCell(
                        detalle.getPrecioTiempo() != null
                                ? "S/ "
                                  + detalle.getPrecioTiempo()
                                : "S/ 0.00"
                );

                tabla.addCell(
                        detalle.getFechaCompromiso() != null
                                ? detalle
                                  .getFechaCompromiso()
                                  .toString()
                                : ""
                );

                tabla.addCell(
                        detalle.getSubtotal() != null
                                ? "S/ "
                                  + detalle.getSubtotal()
                                : "S/ 0.00"
                );
            }

            documento.add(tabla);

            Paragraph mensaje =
                    new Paragraph(
                            "\nGracias por utilizar Biblioteca Virtual General."
                    );

            mensaje.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(mensaje);

            documento.close();

            return salida.toByteArray();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "No se pudo generar el comprobante PDF.",
                    exception
            );
        }
    }


    private void agregarCabecera(
            PdfPTable tabla,
            String texto
    ) {

        Font fuente =
                new Font(
                        Font.HELVETICA,
                        10,
                        Font.BOLD,
                        Color.WHITE
                );

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setBackgroundColor(
                new Color(
                        13,
                        110,
                        253
                )
        );

        celda.setPadding(8);

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        tabla.addCell(celda);
    }
}