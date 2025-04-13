document.addEventListener("DOMContentLoaded", () => {
  const mensajeInput = document.getElementById("mensajeInput");
  const enviarBtn = document.getElementById("enviarBtn");
  const mensajesLista = document.getElementById("mensajesLista");

  const socket = new SockJS("http://localhost:8080/ws"); // Usamos SockJS
  const stompClient = Stomp.over(socket);

  stompClient.connect("guest", "guest", () => {
    // Credenciales RabbitMQ
    console.log("Conectado a WebSocket");

    stompClient.subscribe("/topic/mensajes", (mensaje) => {
      mostrarMensaje(JSON.parse(mensaje.body).contenido);
    });

    // Suscribirse a notificaciones generales (para compradores)
    stompClient.subscribe("/topic/compradores", (notification) => {
      mostrarMensaje(JSON.parse(notification.body));
    });

    // Suscribirse a notificaciones personales
    stompClient.subscribe("/user/queue/notificaciones", (notification) => {
      mostrarMensaje(JSON.parse(notification.body));
    });

    enviarBtn.addEventListener("click", () => {
      const mensaje = mensajeInput.value;
      if (mensaje) {
        stompClient.send(
          "/app/enviarMensaje",
          {},
          JSON.stringify({ contenido: mensaje })
        );
        mensajeInput.value = "";
      }
    });
  });

  function mostrarMensaje(mensaje) {
    const nuevoMensaje = document.createElement("li");
    nuevoMensaje.textContent = mensaje;
    mensajesLista.appendChild(nuevoMensaje);
  }
});
