Juntate App

Juntate es una aplicación social nativa de Android, construida con Kotlin y Jetpack Compose. 
Es una plataforma diseñada para conectar a las personas, permitiéndoles crear, encontrar y unirse a eventos deportivos locales. Ya sea fútbol, 
running o una sesión de gimnasio, Juntate ayuda a los usuarios a encontrar su equipo y organizar su próxima actividad.


Capturas de Pantalla

Pantalla "OnboardingScreen":

<img width="323" height="690" alt="image" src="https://github.com/user-attachments/assets/83d3d65e-6dc4-4d99-95b8-15e78bb6e958" />

Pantalla "LoginScreen":

<img width="324" height="686" alt="image" src="https://github.com/user-attachments/assets/e68606b5-7cc6-4d8e-801d-19f30a5628a3" />

Pantalla "RegisterScreen":

<img width="324" height="689" alt="image" src="https://github.com/user-attachments/assets/66d34cc5-d8c4-4ab2-a7d1-8e7e3e967010" />

Pantalla "HomeScreen":

<img width="334" height="692" alt="image" src="https://github.com/user-attachments/assets/11376f40-ec40-4d36-a71a-aaecd4d108bf" />

Pantalla "EventHistoryScreen":

<img width="331" height="701" alt="image" src="https://github.com/user-attachments/assets/f0b3f548-5f2d-40ce-b151-0002dc432757" />

Pantalla "ProfileScreen":

<img width="320" height="684" alt="image" src="https://github.com/user-attachments/assets/377f5718-9511-4a91-8457-d6aaa228152e" />

Pantalla "FutbolScreen":

<img width="318" height="693" alt="image" src="https://github.com/user-attachments/assets/e15a30d2-e651-461b-b3fa-87a15eb45bb9" />

Pantalla "FutEventScreen":

<img width="319" height="696" alt="image" src="https://github.com/user-attachments/assets/aec4b276-6351-4b62-b9eb-e1874861d1f2" />

Pantalla "EventDetailsScreen":

<img width="319" height="688" alt="image" src="https://github.com/user-attachments/assets/2e9360b1-f918-4ecd-b27e-c227353a6ac8" />

Pantalla "ChatScreen":

<img width="327" height="684" alt="image" src="https://github.com/user-attachments/assets/04407296-7679-4023-9615-ab05c6ebeeed" />

Pantalla "ReportPlayerScreen":

<img width="330" height="693" alt="image" src="https://github.com/user-attachments/assets/d789ddaf-39d5-4b0d-8f33-5abca0f0a146" />

Pantalla "ConfirmReportScreen":

<img width="335" height="711" alt="image" src="https://github.com/user-attachments/assets/471d1c43-546c-4fd9-9b4d-30731b97fbf1" />

Características Principales:

1. Autenticación de Usuarios: Registro e inicio de sesión completos usando Firebase Authentication (Email/Contraseña).
2. Perfiles de Usuario: Los usuarios pueden gestionar su perfil, incluyendo nombre, foto de perfil, ubicación (localidad) y sus niveles de habilidad (Principiante, Intermedio, Avanzado) para cada deporte.
3. Creación de Eventos: Formulario detallado para crear nuevos eventos de Fútbol, Running y Gym, especificando:
- Nombre, fecha, hora y notas.
- Localidad y barrio.
- Nivel de habilidad requerido.
- Número de participantes necesarios.

Filtro de Eventos Inteligente:

1. Las pantallas de deportes (FutbolScreen, etc.) solo muestran eventos futuros (de hoy en adelante).
2. Los eventos se filtran automáticamente según la localidad guardada en el perfil del usuario.
3. Los eventos creados por el propio usuario siempre son visibles.
4. Caso Especial "Bogotá D.C.": Si la ubicación del usuario es "Bogotá D.C.", la app anula el filtro y muestra todos los eventos de todas las localidades.

Gestión de Participantes: 

- Funcionalidad completa para unirse (Join), salirse (Leave) y eliminar (Delete) eventos.

Historial de Eventos:

- Una pantalla dedicada (EventHistoryScreen) con dos pestañas:
  - Actuales: Muestra todos los eventos a los que el usuario está unido y que aún no han sucedido.
  - Historial: Muestra todos los eventos que ya finalizaron.

Chat Grupal en Tiempo Real:

1. Cada evento tiene su propia sala de chat en vivo.
2. Solo los participantes del evento pueden leer y enviar mensajes.
3. Soporte para enviar mensajes de texto e imágenes (subidos a Firebase Storage).

Reporte de Usuarios:

1. Desde la pantalla de detalles del evento, los usuarios pueden reportar a otros participantes.
2. Los reportes se guardan en una colección reports en Firestore para moderación.

Frontend (App Android):

1. Lenguaje: Kotlin
2. UI: Jetpack Compose
3. Diseño y Prototipado: Figma
4. Manejo de Estado: Android ViewModel (con StateFlow y collectAsState)
5. Navegación: NavHost (Navigation Compose)
6. Asincronía: Kotlin Coroutines
7. Carga de Imágenes: Coil

Backend (Firebase):

1. Autenticación: Firebase Authentication
2. Base de Datos (Tiempo Real): Cloud Firestore
3. Almacenamiento de Archivos: Firebase Storage (para fotos de perfil y multimedia del chat)
4. Seguridad: Reglas de Seguridad de Firestore y Storage.

Configuración de Firebase:

Este proyecto depende en gran medida de una configuración específica de Firebase.
- Estructura de Firestore:
  La base de datos se organiza de la siguiente manera:

1. /users/{userId}

- uid
- name
- email
- location
- profilePictureUrl
- futbolevel
- runningLevel
- gymLevel

2. /events/{eventId} (Colección para Fútbol)

- eventName
- eventLocality
- eventNeighborhood
- eventDate
- eventTime
- eventLevel
- eventNotes
- sport
- createdByUid
- createdAt (timestamp)
- eventTimestamp (timestamp)
- participants (array)
- requiredParticipants

2.5 /messages/{messageId} (Subcolección de Chat)

- senderUid
- senderName
- messageText
- mediaUrl
- timestamp

3. /running_events/{eventId} (Colección para Running)

- (Misma estructura que /events)

3.5. /messages/{messageId} (Subcolección de Chat)

- (Misma estructura que /events/messages)

4. /gym_events/{eventId} (Colección para Gym)

- (Misma estructura que /events)

4.5 /messages/{messageId} (Subcolección de Chat)

- (Misma estructura que /events/messages)

5. /reports/{reportId} (Colección para Reportes)

- reporterUid
- reportedUid
- reportedName
- reasons (array)
- otherReasonText
- createdAt (timestamp)

Índices de Firestore Requeridos:

Para que las consultas de filtrado y ordenación funcionen, se deben crear los siguientes 6 índices en la consola de Firestore (en la pestaña "Índices"):

1. Colección: events
  - Campos: eventTimestamp (Ascendente)

2. Colección: running_events
  - Campos: eventTimestamp (Ascendente)

3. Colección: gym_events
  - Campos: eventTimestamp (Ascendente)

4. Colección: events
  - Campos: participants (Matriz), eventTimestamp (Descendente)

5. Colección: running_events
  - Campos: participants (Matriz), eventTimestamp (Descendente)

6. Colección: gym_events
  - Campos: participants (Matriz), eventTimestamp (Descendente)

Reglas de Seguridad de Firestore Database:

<img width="1000" height="802" alt="image" src="https://github.com/user-attachments/assets/12607af4-74ea-4f7a-a14b-6b65631f0a14" />

Reglas de Seguridad de Storage:

1. Para permitir la subida de fotos de perfil y multimedia del chat, se deben usar las siguientes reglas en Firebase Storage > Reglas:
<img width="959" height="346" alt="image" src="https://github.com/user-attachments/assets/7a3a566d-5aa6-43cc-a900-a4ddddf6b034" />

Cómo Ejecutar Localmente:

1. Clonar el repositorio: git clone https://github.com/Alejoooxx/Juntate.git
2. Abrir el proyecto en Android Studio.
3. Ir a la Consola de Firebase y crear un nuevo proyecto.
4. Habilitar Authentication (con el proveedor Email/Contraseña), Cloud Firestore (en modo de producción) y Firebase Storage.
5. Crear una aplicación de Android en el proyecto de Firebase con el ID de paquete: com.example.juntate.
6. Descargar el archivo google-services.json y colocarlo en la carpeta app/ del proyecto de Android Studio.
7. En la consola de Firestore, crear los 6 índices listados arriba.
8. En la consola de Storage, aplicar las Reglas de Seguridad listadas arriba.
9. En la consola de Firestore, aplicar las Reglas de Seguridad (las proporcionadas anteriormente) para permitir la lectura y escritura en todas las colecciones.
10. Ejecutar la aplicación en un emulador que tenga Google Play Services (obligatorio para la autenticación de Firebase).

