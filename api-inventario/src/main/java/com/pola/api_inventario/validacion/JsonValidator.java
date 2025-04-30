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

        // validar campos dentro del objeto anidado.

        validarCampo(node, "pesoPorUnidad", "number", false, errores, datos);
        validarCampo(node, "volumenPorUnidad", "number", false, errores, datos);
        validarCampo(node, "cantidadLotes", "integer", false, errores, datos);
        validarCampo(node, "pesoLote", "number", false, errores, datos);
        validarCampo(node, "unidadesPorLote", "integer", false, errores, datos);
        validarCampo(node, "longitudPorUnidad", "number", false, errores, datos);
        validarCampo(node, "contactoProveedor", "string", false, errores, datos);
        validarCampo(node, "caducidad", "string", false, errores, datos);
        validarCampo(node, "categoria", "string", false, errores, datos);
        validarCampo(node, "nombre", "string", false, errores, datos);
        validarCampo(node, "tempMin", "number", false, errores, datos);
        validarCampo(node, "tempMax", "number", false, errores, datos);
        validarCampo(node, "largo", "number", false, errores, datos);
        validarCampo(node, "ancho", "number", false, errores, datos);
        validarCampo(node, "altura", "number", false, errores, datos);
        validarCampo(node, "esFragil", "boolean", false, errores, datos);
        validarCampo(node, "ubicacion", "string", false, errores, datos);
        validarCampo(node, "cantidadMinimaLotes", "integer", false, errores, datos);

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
