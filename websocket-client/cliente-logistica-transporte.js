const messagesDiv = document.getElementById("messages");

// Obtener el token de la URL
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get("token");
const socket = new SockJS("http://localhost:8080/ws/logistica");
const messageCards = {};

//renderiza los datos recibidos en forma de tarjeta con un campo editable.
function createCard(message) {
  const contenidoJson = JSON.parse(message.contenido);

  const card = document.createElement("div");
  card.classList.add("card");
  card.dataset.id = message.id;

  //renderiza el remitente
  const title = document.createElement("div");
  title.classList.add("card-title");
  title.innerText = `Remitente: ${message.remitente || "N/A"}`;
  card.appendChild(title);

  // Formatear la fecha y hora desde el timestamp
  const timestamp = new Date(message.timestamp);
  const fechaCreacion = timestamp.toLocaleDateString(); // Formato de fecha
  const horaCreacion = timestamp.toLocaleTimeString(); // Formato de hora

  const fechaYhora = document.createElement("div");
  fechaYhora.classList.add("card-item");
  fechaYhora.innerHTML = `<h3>Fecha y hora de este informe:<br> ${
    fechaCreacion || "N/A"
  } - ${horaCreacion || "N/A"} </h3>`;
  card.appendChild(fechaYhora);

  // renderiza el estado del item
  const estadoItem = document.createElement("div");
  estadoItem.innerHTML = `<h3>Estado actual del item: <br>${
    message.estadoActual || "N/A"
  }</h3><br>`;
  card.appendChild(estadoItem);

  const data = contenidoJson.contenido;

  // renderizar los datos del objeto contenido del json
  for (const key in data) {
    if (data.hasOwnProperty(key)) {
      const item = document.createElement("div");
      item.classList.add("card-item");
      item.innerHTML = `<span class="card-label">${key}:</span> ${
        data[key] === null ? "N/A" : data[key]
      }`;
      card.appendChild(item);
    }
  }

  // Crear formulario para editar la propiedad "estado"
  const form = document.createElement("form");
  form.classList.add("card-item");

  const label = document.createElement("label");
  label.innerHTML = "<br> Editar estado del item: <br>";
  label.setAttribute("for", "estadoInput");
  form.appendChild(label);

  const input = document.createElement("input");
  input.type = "text";
  input.id = "estadoInput";
  input.value = "";
  form.appendChild(input);

  const button = document.createElement("button");
  button.type = "button";
  button.innerText = "Guardar";
  button.addEventListener("click", () => {
    message.estadoActual = input.value;
    // Preparar el JSON para enviar
    const updatedMessage = {
      id: message.id,
      remitente: message.remitente,
      timestamp: message.timestamp,
      contenido: contenidoJson.contenido,
      estado: message.estadoActual,
    };

    // Enviar el mensaje actualizado
    sendMessage(updatedMessage);
  });
  form.appendChild(button);
  card.appendChild(form);
  messageCards[message.id] = card; // Almacenar la referencia de esta tarjeta
  return card;
}

function showMessage(message) {
  if (messageCards[message.id]) {
    // Encontrar la tarjeta existente en el DOM
    const existingCard = document.querySelector(`[data-id="${message.id}"]`);
    if (existingCard) {
      // Crear la nueva versión de la tarjeta
      const updatedCard = createCard(message);
      // Reemplazar la tarjeta existente con la nueva
      existingCard.replaceWith(updatedCard);
      // Actualizar la referencia en messageCards
      messageCards[message.id] = updatedCard;
      console.log(`Tarjeta con ID ${message.id} reemplazada.`);
    }
  } else {
    const newCard = createCard(message);
    messagesDiv.appendChild(newCard);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    console.log(`Nueva tarjeta con ID ${message.id} mostrada.`);
  }
}

function updateCard(cardElement, message) {
  const contenidoJson = JSON.parse(message.contenido);
  // Actualizar los elementos dentro de la tarjeta con la nueva información
  cardElement.querySelector(".card-title").innerText = `Remitente: ${
    message.remitente || "N/A"
  }`;
  const timestamp = new Date(message.timestamp);
  cardElement.querySelector(
    ".card-item h3"
  ).innerHTML = `Fecha y hora de este informe:<br> ${
    timestamp.toLocaleDateString() || "N/A"
  } - ${timestamp.toLocaleTimeString() || "N/A"} </h3>`;
  cardElement.querySelector(
    ".card-item:nth-child(3) h3"
  ).innerText = `Estado actual del item: ${message.estadoActual || "N/A"}`;

  const data = contenidoJson.contenido;
  let itemIndex = 4; // Ajustar según la estructura de tu tarjeta
  for (const key in data) {
    if (data.hasOwnProperty(key)) {
      const itemElement = cardElement.querySelector(
        `.card-item:nth-child(${itemIndex})`
      );
      if (itemElement) {
        itemElement.innerHTML = `<span class="card-label">${key}:</span> ${
          data[key] === null ? "N/A" : data[key]
        }`;
      }
      itemIndex++;
    }
  }

  const estadoInput = cardElement.querySelector("#estadoInput");
  if (estadoInput) {
    estadoInput.value = message.estadoActual || "";
  }
}

function eliminarTarjeta(mensajeId) {
  const tarjetaAEliminar = document.querySelector(
    `.card[data-id="${mensajeId}"]`
  );
  if (tarjetaAEliminar && messagesDiv.contains(tarjetaAEliminar)) {
    messagesDiv.removeChild(tarjetaAEliminar);
    console.log(`Tarjeta con ID ${mensajeId} eliminada del DOM.`);
  } else {
    console.log(`No se encontró tarjeta con ID ${mensajeId} para eliminar.`);
  }
}

function connect() {
  stompClient = Stomp.over(socket);
  stompClient.connect(
    {
      Authorization: `Bearer ${token}`,
      "X-Requested-With": "XMLHttpRequest",
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
    },
    function (frame) {
      const sessionId = stompClient.ws._transport.url.split("/")[5];
      stompClient.subscribe("/queue/" + sessionId);
      stompClient.subscribe("/topic/logistica");
      stompClient.subscribe("/topic/mensajes", function (message) {
        const mensajeRecibido = JSON.parse(message.body);
        showMessage(mensajeRecibido);
      });
      // suscripción para mensajes eliminados
      stompClient.subscribe("/topic/mensajes-eliminados", function (message) {
        const mensajeIdEliminado = message.body;
        console.log("Mensaje eliminado recibido, ID:", mensajeIdEliminado);
        eliminarTarjeta(mensajeIdEliminado);
      });
      // Enviar un mensaje al backend para solicitar el historial
      stompClient.send("/app/historialmensajes", {}, "");
    },
    function (error) {
      console.error("Error de conexión:", error);
    }
  );
}

document.addEventListener("DOMContentLoaded", () => {
  const cargarHistorialBtn = document.getElementById("cargarHistorial");
  cargarHistorialBtn.addEventListener("click", () => {
    stompClient.send("/app/historialmensajes", {}, "");
  });
});

function sendMessage(updatedMessage) {
  // Enviar el objeto actualizado al servidor WebSocket
  stompClient.send(
    "/app/chat.editar", // Endpoint del servidor para manejar el mensaje
    {},
    JSON.stringify(updatedMessage) // Convertir el objeto a JSON
  );
}

connect();
