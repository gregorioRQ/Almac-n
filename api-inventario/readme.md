# Sistema de manejo de logística

## Descripción

Esta app gestiona la logistica de un inventario, permite a los usuarios realizar diversas operaciones relacionadas con la creación, almacenamiento y manipulación de items. La aplicación incluye funcionalidades avanzadas como gestión de roles, notificaciones en tiempo real y manejo de eventos mediante WebSockets y RabbitMQ.

## Roles y permisos

- **Logística**:
  - Incluye usuarios con roles relacionados (repositor,despachador,transporte)

## Funcionalidades principales

1. **Gestion de usuarios**:

- Registro de usuarios con autenticación mediante JWT.
- Cambio de roles.
- Roles específicos para realizar diferentes acciones.

## Funcionalidades avanzadas

1. **Mecanismo de descuento**:

- Los items se despachan por lotes.
- El sistema recibe un DTO con los datos del item y la cantidad a descontar.
- Busca el item en la base de datos y actualiza los valores relacionados con la cantidad.
- Si la cantidad restante es menor a un umbral definido, el sistema notifica automáticamente al proveedor.
