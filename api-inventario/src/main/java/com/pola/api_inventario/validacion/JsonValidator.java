package com.pola.api_inventario.validacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.pola.api_inventario.rest.service.ProveedorService;
import com.pola.api_inventario.rest.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class JsonValidator {
    @Autowired
    private UserService userService;

    public static class ValidationResult {
        private final boolean valid;
        private final Map<String, String> errores;
        private final Map<String, Object> datos;

        public ValidationResult(boolean valid, Map<String, String> errores, Map<String, Object> datos) {
            this.valid = valid;
            this.errores = errores;
            this.datos = datos;
        }

        public boolean isValid() {
            return valid;
        }

        public Map<String, String> getErrores() {
            return errores;
        }

        public Map<String, Object> getDatos() {
            return datos;
        }
    }

    public ValidationResult validar(JsonNode node) {
        Map<String, String> errores = new HashMap<>();
        Map<String, Object> datos = new HashMap<>();

        // Validar campos del nodo raíz
        validarCampoRaiz(node, "remitente", "string", true, errores, datos);
        validarCampoRaiz(node, "estado", "string", true, errores, datos);
        validarCampoRaiz(node, "ubicacion", "string", false, errores, datos);
        // validar campos dentro del objeto anidado.

        JsonNode contenidoNode = node.path("contenido");
        validarCampo(contenidoNode, "pesoPorUnidad", "number", true, errores, datos);
        validarCampo(contenidoNode, "volumenPorUnidad", "number", false, errores, datos);
        validarCampo(contenidoNode, "cantidadLotes", "integer", true, errores, datos);
        validarCampo(contenidoNode, "pesoLote", "number", true, errores, datos);
        validarCampo(contenidoNode, "unidadesPorLote", "integer", false, errores, datos);
        validarCampo(contenidoNode, "longitudPorUnidad", "number", false, errores, datos);
        validarCampo(contenidoNode, "caducidad", "string", false, errores, datos);
        validarCampo(contenidoNode, "categoria", "string", true, errores, datos);
        validarCampo(contenidoNode, "nombre", "string", true, errores, datos);
        validarCampo(contenidoNode, "tempMin", "number", true, errores, datos);
        validarCampo(contenidoNode, "tempMax", "number", true, errores, datos);
        validarCampo(contenidoNode, "largo", "number", true, errores, datos);
        validarCampo(contenidoNode, "ancho", "number", true, errores, datos);
        validarCampo(contenidoNode, "altura", "number", true, errores, datos);
        validarCampo(contenidoNode, "esFragil", "boolean", false, errores, datos);
        validarCampo(contenidoNode, "cantidadMinimaLotes", "integer", true, errores, datos);

        return new ValidationResult(errores.isEmpty(), errores, datos);
    }

    private void validarCampoRaiz(JsonNode node, String campo, String tipoDato, boolean esRequerido,
            Map<String, String> errores, Map<String, Object> datos) {
        JsonNode valorNode = node.get(campo);

        if (esRequerido && (valorNode == null || valorNode.isNull() || valorNode.asText().trim().isEmpty())) {
            errores.put(campo, "El campo es requerido");
            return;
        }

        if (!esRequerido && (valorNode == null || valorNode.isNull())) {
            return;
        }

        switch (tipoDato.toLowerCase()) {
            case "string":
                if (!valorNode.isTextual()) {
                    errores.put(campo, "Debe ser una cadena de texto");
                } else {
                    datos.put(campo, valorNode.asText());
                }
                break;
            default:
                errores.put(campo, "Tipo de dato no soportado");
        }
    }

    private void validarCampo(JsonNode node, String campo, String tipoDato, boolean esRequerido,
            Map<String, String> errores, Map<String, Object> datos) {
        JsonNode valorNode = node.get(campo);

        // verifica si el campo esta persente
        if (esRequerido && (valorNode == null || valorNode.isNull() || valorNode.asText().trim().isEmpty())) {
            errores.put(campo, "El campo es requerido");
            return;
        }
        // verifica si el campo no es nulo
        if (!esRequerido && (valorNode == null || valorNode.isNull())) {
            return;
        }
        // verifica el tipo de dato esperado.
        switch (tipoDato.toLowerCase()) {
            case "string":
                if (!valorNode.isTextual()) {
                    errores.put(campo, "Debe ser una cadena de texto");
                } else {
                    datos.put(campo, valorNode.asText());
                }
                break;
            case "number":
                if (!valorNode.isNumber()) {
                    errores.put(campo, "Debe ser un número");
                } else {
                    datos.put(campo, valorNode.asDouble());
                }
                break;
            case "integer":
                if (!valorNode.isInt()) {
                    errores.put(campo, "Debe ser un número entero");
                } else {
                    datos.put(campo, valorNode.asInt());
                }
                break;
            case "boolean":
                if (!valorNode.isBoolean()) {
                    errores.put(campo, "Debe ser un valor booleano (true/false)");
                } else {
                    datos.put(campo, valorNode.asBoolean());
                }
                break;
            default:
                errores.put(campo, "Tipo de dato no soportado");
        }
    }
}
