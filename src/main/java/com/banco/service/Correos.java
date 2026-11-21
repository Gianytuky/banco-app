package com.banco.service;

import org.springframework.web.util.HtmlUtils;

import java.util.Map;

/** Arma el HTML de los correos de confirmacion. */
public final class Correos {

    private Correos() {
    }

    public static String html(String titulo, String saludo, Map<String, String> datos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        sb.append("<h2>Banco Demo</h2>");
        sb.append("<h3>").append(HtmlUtils.htmlEscape(titulo)).append("</h3>");
        sb.append("<p>").append(HtmlUtils.htmlEscape(saludo)).append("</p>");
        sb.append("<table border=\"1\" cellpadding=\"8\" cellspacing=\"0\">");
        datos.forEach((k, v) -> sb.append("<tr><td><b>").append(HtmlUtils.htmlEscape(k)).append("</b></td><td>")
                .append(HtmlUtils.htmlEscape(v == null ? "" : v)).append("</td></tr>"));
        sb.append("</table>");
        sb.append("<p>Gracias por confiar en nosotros.</p>");
        sb.append("<p><small>Este es un mensaje automatico de un proyecto academico.</small></p>");
        sb.append("</div>");
        return sb.toString();
    }
}
