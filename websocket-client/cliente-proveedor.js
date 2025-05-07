let stompClient = null;
let authToken = null;
let currentUsername = null;
let userRoles = null;

const inputNombreComercial = document.getElementById("nombreComercial");
const sendButton = document.getElementById("boton-enviar");
const messagesDiv = document.getElementById("mensajes");
const inputForm = document.getElementById("form-item");
const inputEstadoActual = document.getElementById("estado-actual");

function isProveedor() {
  return userRoles && userRoles.includes("PROVEEDOR");
}

// Obtener el token de la URL
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get("token");

// Verificar que el token esté presente
if (!token) {
  console.error("Token no recibido:", data);
  throw new Error("Invalid server response - missing token");
}

// Decodificar el token para obtener los roles
const tokenParts = token.split(".");
if (tokenParts.length !== 3) {
  throw new Error("Invalid token format");
}

const payload = JSON.parse(atob(tokenParts[1]));
console.log("Token payload:", payload);

authToken = token;
userRoles = payload.roles || []; // Obtener roles del token

console.log("Roles extraídos del token:", userRoles);

function connect() {
  console.log(authToken);

  if (!authToken || !userRoles || !isProveedor()) {
    alert(
      "Este usuario no es un proveedor o no está autenticado correctamente"
    );
  }

  const socket = new SockJS("http://localhost:8080/ws/proveedor");
  stompClient = Stomp.over(socket);

  const headers = {
    Authorization: `Bearer ${authToken}`,
    "X-Requested-With": "XMLHttpRequest",
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
  };

  stompClient.connect(headers, onConnected, onError);
}

function onConnected() {
  console.log("Conectado");
  stompClient.subscribe("/topic/logistica");
  stompClient.subscribe("/topic/proveedor", onProveedorMessageReceived);
}

function onError(error) {
  console.error("Error al conectar al WebSocket:", error);
}

function onProveedorMessageReceived(payload) {
  const notification = JSON.parse(payload.body);
  console.log("Mensaje de Proveedor Recibido:", notification);

  const messageElement = document.createElement("p");
  const timestamp = new Date().toLocaleTimeString();
  messageElement.innerHTML = `
        <strong>${timestamp}</strong> - 
        <span style="color: #1565c0">${notification.type}</span>: 
        ${notification.message}
    `;
  messagesDiv.appendChild(messageElement);
  messagesDiv.scrollTop = messagesDiv.scrollHeight; // Auto-scroll al último mensaje
}

function sendMessage() {
  const formData = new FormData(inputForm);
  const messageContent = {};
  const estadoActual = inputEstadoActual.value;

  // Excluir específicamente el campo estadoActual al procesar el FormData
  for (const [key, value] of formData.entries()) {
    if (key !== "estadoActual") {
      const inputElement = inputForm.querySelector(`[name="${key}"]`);
      if (inputElement) {
        const type = inputElement.type.toLowerCase();

        // Si el valor está vacío
        if (!value || value.trim() === "") {
          if (key === "caducidad") {
            messageContent[key] = "00/00/0000";
          } else if (type === "number" || type === "range") {
            messageContent[key] = 0;
          } else if (key === "esFragil") {
            messageContent[key] = false;
          } else {
            messageContent[key] = "0";
          }
        } else {
          // Si el valor no está vacío
          if (type === "number" || type === "range") {
            messageContent[key] = parseFloat(value) || 0;
          } else if (key === "esFragil") {
            messageContent[key] = value === "true";
          } else {
            messageContent[key] = value;
          }
        }
      }
    }
  }

  // Decodificar el token para obtener el nombre de usuario
  let remitente = null;
  try {
    const tokenParts = authToken.split(".");
    const payload = JSON.parse(atob(tokenParts[1]));
    remitente = payload.sub;
  } catch (error) {
    console.error("Error al decodificar el token:", error);
    remitente = "SYSTEM";
  }

  const mensaje = {
    estado: estadoActual, // Usar el valor guardado
    contenido: messageContent,
    remitente: remitente,
  };

  console.log(mensaje);
  stompClient.send("/app/chat.enviar", {}, JSON.stringify(mensaje));
}

sendButton.addEventListener("click", sendMessage);
inputForm.addEventListener("submit", (event) => {
  event.preventDefault();
  sendMessage();
});
connect();
