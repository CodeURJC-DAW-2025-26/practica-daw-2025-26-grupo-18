# Smart Courses & Academy Market (SCAM)

## 👥 Miembros del Equipo

| Nombre y Apellidos            | Correo URJC                       | Usuario GitHub    |
| :---------------------------- | :-------------------------------- | :---------------- |
| Pau Calvo Jiménez             | p.calvo.2023@aulmnos.urjc.es      | PauCalvoJ         |
| Alberto Hontanilla Villanueva | a.hontanilla.2023@alumnos.urjc.es | albertohontanilla |
| Gonzalo Andrés Zurdo Patino   | ga.zurdo.2023@alumnos.urjc.es     | 51nga/El Patines  |
| Jaime Sánchez Vázquez         | j.sanchezva.2023@alumnos.urjc.es  | jaimesnh          |

---

## 🎭 **Preparación 1: Definición del Proyecto**

### **Descripción del Tema**

Una aplicacion web para el desarrollo personal. Contiene cursos divididos en lecciones, a los cuales les puedes poner una reseña, y eventos

### **Entidades**

Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. **Usuario**
2. **Curso**
3. **Suscripción**
4. **Lección**
5. **Evento**
6. **Reseña**

**Relaciones entre entidades:**

- Usuario - Curso: Un usuario puede estar suscrito a múltiples cursos y un curso puede tener múltiples usuarios (N:M).
- Curso - Lección: Un curso puede tener múltiples lecciones (1:N).
- Curso - Evento: un curso puede tener múltiples eventos (1:N).
- Curso - Reseña: un curso puede tener múltiples reseñas (1:N).
- Usuario - Suscripcion: un usuario puede tener multiples suscripciones (1:N).
- Suscrpicion - Curso: un curso puede tener varias suscripciones (N:1)

### **Permisos de los Usuarios**

Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

- **Usuario Anónimo**:
  - Permisos: Visualización de cursos y eventos, sin poder acceder a su contenido, ni poner reseñas.
  - No es dueño de ninguna entidad

- **Usuario Registrado**:
  - Permisos: Posibilidad de suscribirse a los cursos y eventos para poder acceder a su contenido y poner reseñas, al igual que suscribirse a eventos. Tambien pueden suscribirse a la pagina para poder publicar tanto cursos como eventos
  - Es dueño de: Sus cursos, sus eventos, sus reseñas y sus lecciones

- **Administrador**:
  - Permisos: Modificacion de todas las entidades de la aplicacion (Usuarios, Cursos, Lecciones, Eventos, Suscripciones y Reseñas)
  - Es dueño de: Todas las entidades de la aplicación

### **Imágenes**

Indicar qué entidades tendrán asociadas una o varias imágenes:

- **Usuario - Una imagen de avatar por usuario**
- **Curso - Una imagen de presentacion por curso**
- **Leccion - Uno o varios videos por lección**
- **Eventos - Una imagen de preview por evento**

### **Gráficos**

Indicar qué información se mostrará usando gráficos y de qué tipo serán:

- **Porcentaje de cursos y estado: Cantidad de cursos que tienes y su estado (completado, en curso) - Grafico de tarta/circular**
- **Personas que han hecho tu curso: Cantidad de personas que han hecho tu curso separadas por edades - Gráfico de barras**
- **Lecciones completadas por dia: Numero de lecciones completadas en los ultimos 7 dias - Gráfico de barras**
- **Personas dentro de un curso: Numero de personas que han interactuado con un curso por estados (visitado, en curso y completado) - Gráfico de tarta/circulo**

### **Tecnología Complementaria**

Indicar qué tecnología complementaria se empleará:

- Envío de correos electrónicos automáticos de confirmacion de compra y registro mediante JavaMailSender
- Generación de PDFs de facturas usando iText o similar
- Sistema de autenticación OAuth2 o JWT
- Generación de calendario para regustrar compras y próximos eventos mediante google calendar
- Frameworks para generar gráficos como google charts o similares
- Indicar localizacion de eventos mediante google maps js api o Leaflet

### **Algoritmo o Consulta Avanzada**

Indicar cuál será el algoritmo o consulta avanzada que se implementará:

- **Algoritmo: Sistema de recomendacion basada en tu historial de visualizaciones**
- **Descripción: Ordenar los cursos basados en etiquetas de tus cursos recientes**
- **Alternativa: Recomendacion de cursos en base a lo mas popular**

- **Algoritmo: Algoritmo de análisis de tiempo de finalizacion de curso**
- **Descripcion: Estima el tiempo promedio que un usuario tardara en completar un curso**
- **Alternativa: Análisis por características de cada usuario**

## 🛠 **Preparación 2: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](https://youtu.be/Ja-Dd7REkR0)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**

Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación]
<img width="1739" height="729" alt="Diagrama de navegación 1" src="https://github.com/user-attachments/assets/1717623d-bc5f-4065-aa92-df2811624a49" />

Diagrama de navegación 1: En esta primera secuencia se muestra la pantalla inicial (sin iniciar sesión). A partir de esta se puede navegar hacia iniciar sesión,o ver cursos o eventos (sin poder suscribirse a estos a menos que se inicie sesión). En caso de haber iniciado sesión se puede acecder como administrador al dashboard

<img width="1156" height="898" alt="Diagrama de navegación 2" src="https://github.com/user-attachments/assets/00a50a0a-97fb-44e0-b079-d3957d90de56" />

Diagrama de navegación 2: En esta segunda imagen se muestra como se desarrolla el transcurso de visualizar los cursos disponibles o cursos suscrito. A partir de esta pestaña se puede acceder al curso y en caso de no tenerlo comprado acceder a la pestaña de pago.

<img width="572" height="631" alt="Diagrama de navegación 3" src="https://github.com/user-attachments/assets/ae46da2e-6203-4245-bcf0-e560b3c1c250" />

Diagrama de navegación 3: En esta tercera imagen se muestra el transucros de ver los evento a suscribirse a uno. Tambien se pasaría a la pestaña de pago pero para no repetirla no la hemos añadido.

<img width="473" height="579" alt="Diagrama de navegación 4" src="https://github.com/user-attachments/assets/d526e476-34ed-471e-acbf-fb867109333f" />

Diagrama de navegación 4: Se muestra el paso de acceder como admin a crear curso. Tambien en la pestaña del dashboard abría dos pop-up uno para mostrar los comentarios pendientes de revisar y otro para buscar perfiles.

<img width="1382" height="966" alt="Diagrama de navegación" src="https://github.com/user-attachments/assets/6cdc8ec0-4a33-4a65-b029-481e84fd9b94" />

Diagrama de navegación completo: Aunque hayamos representado que todo sale de la pantalla principal o index, realmente se puede acceder a cursos, cursos sucrito, inicio o eventos desde cualquier pantalla desde el header. Desde el header tambien se puede acceder a la información de perfil. Para que el documento estuviera más limpio preferimos no representar todas estas flechas.

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**

![Página Principal]
<img width="1807" height="864" alt="Página principal" src="https://github.com/user-attachments/assets/b8d3eed5-a9cf-42c6-9a9d-9a4de3e92b06" />

> Página que se abre por defecto. A través de ella se puede acceder a cursos, eventos o a iniciar sesión.

> ![Inicio de sesión]
> <img width="1802" height="866" alt="Iniciio de sesión" src="https://github.com/user-attachments/assets/e2f55c3f-a802-4064-b609-14c6f28145d4" />

> Permite iniciar sesión si o registrar un nuevo usuario

> ![Admin dashboard]
> <img width="1804" height="865" alt="Admin Dashboard" src="https://github.com/user-attachments/assets/6b277289-9999-4d03-a754-18ddbae06934" />

> Pestaña donde el administrador podrá realizar sus funcionalidades

> ![Crear curso]
> <img width="1817" height="859" alt="Crear curso" src="https://github.com/user-attachments/assets/a08da16a-c6a7-4a74-a33f-5dec84fc575f" />

> Pestaña donde se introducirá la información para crear un curso

> ![Eventos disponibles y cursos disponibles]
> <img width="1810" height="862" alt="Eventos disponibles y cursos disponibles 1" src="https://github.com/user-attachments/assets/fe6d38b9-b69f-4ac3-a6e2-fe193ccaba10" />
> <img width="1811" height="862" alt="Eventos disponibles y cursos disponibles 2" src="https://github.com/user-attachments/assets/bc52977a-a8d7-4c75-ad0b-35102c73fd3d" />

> Al ser exactamente el mismo diseño se incluyen las dos a la vez. Mostrarán los cursos o eventos disponibles.

> ![Cursos suscrito]
> <img width="1807" height="864" alt="Cursos suscrito" src="https://github.com/user-attachments/assets/1e59187f-df65-43f3-bd6c-78e787670126" />

> Mostrará los cursos a lso que esta suscrito el usuario.

> ![Evento y curso]
> <img width="1809" height="863" alt="Evento y curso 1" src="https://github.com/user-attachments/assets/5d7ff18d-3a68-4dad-b835-7aadb1e86c7f" />
> <img width="1806" height="864" alt="Evento y curso 2" src="https://github.com/user-attachments/assets/047cdfef-2afd-4d4b-bb55-d7b6c8a78105" />

> De nuevo comparten diseño. Mostrarán la información de un curso o un evento.

> ![Pago]
> <img width="1828" height="854" alt="Pago" src="https://github.com/user-attachments/assets/34599d08-1639-43b6-94f6-7bbfc9b76d0e" />

Mostrará el contenido del carrito y saltará un pop-up para rellenar la informaciónd el pago

> ![Perfil]
> <img width="1804" height="865" alt="Perfil" src="https://github.com/user-attachments/assets/5cb123c4-cb0a-4d96-a0fe-3dee7f02cb91" />

> Mostrará la información del usuario y permitirá editarla.

## 🛠 **Práctica 1: Web con HTML generado en servidor y AJAX**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](https://youtu.be/2OFbI6SM4Eo)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Diagrama de Navegación actualizado
<img width="1739" height="729" alt="Diagrama de navegación actualizado" src="https://github.com/user-attachments/assets/39c846f5-182a-43e5-b5d7-4e62cea90af5" />

#### **Capturas de Pantalla Actualizadas**

Pantalla admindashboard modificada
<img width="1899" height="912" alt="admindashobard" src="https://github.com/user-attachments/assets/baf01dc4-7d01-4154-9841-1ca6d564452e" />

### **Instrucciones de Ejecución**

#### **Requisitos Previos**

- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]/scam-g18
   ```

2. **Crear la base de datos MySQL**
   - Asegúrate de tener MySQL arrancado en local.
   - Desde la carpeta raíz del repositorio, ejecuta:

   ```bash
   mysql -u root -p < daw_g18.sql
   ```

3. **Configurar credenciales y variables de entorno**
   - Revisa `scam-g18/src/main/resources/application.properties` y ajusta si es necesario:
     - `spring.datasource.username`
     - `spring.datasource.password`
   - Configura OAuth de Google (si vas a usar login con Google):
     - `GOOGLE_CLIENT_ID`
     - `GOOGLE_CLIENT_SECRET`

4. **Ejecutar la aplicación**
   - En Windows:

   ```bash
   .\\mvnw.cmd spring-boot:run
   ```

   - En Linux/Mac:

   ```bash
   ./mvnw spring-boot:run
   ```

5. **Abrir en el navegador**
   ```
   https://localhost:8443
   ```
   > La aplicación usa HTTPS con certificado autofirmado, por lo que el navegador puede mostrar un aviso de seguridad.

#### **Credenciales de prueba**

- **Usuario Admin**: usuario: `admin`, contraseña: `adminpass`
- **Usuario Creador**: usuario: `content_lead`, contraseña: `leadpass`
- **Usuario Creador**: usuario: `mentor_ai`, contraseña: `mentorpass`
- **Usuario Creador**: usuario: `coach_growth`, contraseña: `coachpass`
- **Usuario Creador**: usuario: `finance_master`, contraseña: `financepass`
- **Usuario Registrado**: usuario: `learner1`, contraseña: `pass1`
- **Usuario Registrado**: usuario: `learner2`, contraseña: `pass2`
- **Usuario Registrado**: usuario: `learner3`, contraseña: `pass3`
- **Usuario Registrado**: usuario: `learner4`, contraseña: `pass4`
- **Usuario Registrado**: usuario: `learner5`, contraseña: `pass5`
- **Usuario Registrado**: usuario: `learner6`, contraseña: `pass6`
- **Usuario Registrado**: usuario: `learner7`, contraseña: `pass7`
- **Usuario Registrado**: usuario: `learner8`, contraseña: `pass8`
- **Usuario Registrado**: usuario: `learner9`, contraseña: `pass9`
- **Usuario Registrado**: usuario: `learner10`, contraseña: `pass10`
- **Usuario Registrado**: usuario: `learner11`, contraseña: `pass11`
- **Usuario Registrado**: usuario: `learner12`, contraseña: `pass12`
- **Usuario Registrado**: usuario: `learner13`, contraseña: `pass13`
- **Usuario Registrado**: usuario: `learner14`, contraseña: `pass14`
- **Usuario Registrado**: usuario: `learner15`, contraseña: `pass15`
- **Usuario Registrado**: usuario: `learner16`, contraseña: `pass16`
- **Usuario Registrado**: usuario: `learner17`, contraseña: `pass17`
- **Usuario Registrado**: usuario: `learner18`, contraseña: `pass18`

### **DEstructura base de datos**

Representación gráfica de la base de datos:

<img width="1730" height="1372" alt="pauCALVO" src="https://github.com/user-attachments/assets/2bd8bfd1-4dc0-471b-b04b-f41617a1a759" />

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores y secciones:

<img width="1209" height="827" alt="51nga" src="https://github.com/user-attachments/assets/c5bf4d3a-6ffa-43be-9c8a-9624d3dc3456" />

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - Pau Calvo Jiménez**

Responsable de la arquitectura de gestión de eventos, integración de mapas interactivos y geocodificación. Lideró el desarrollo del sistema de autenticación, perfiles de usuario y las funcionalidades de seguridad como el baneo de usuarios.

| Nº  |                                                                       Commits                                                                        |                                                                                        Files                                                                                        |
| :-: | :--------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  | Gestión completa de eventos y motor de formularios ([c4a5348](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/c4a5348)) | [EventController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/EventController.java) |
|  2  |    Implementación de registro, login y perfiles ([6a3925a](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/6a3925a))    |      [UserService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/service/UserService.java)       |
|  3  |      Sistema de baneo de usuarios y seguridad ([4a8f6ca](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/4a8f6ca))      | [AdminController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/AdminController.java) |
|  4  |      Desarrollo de plantillas base y gráficos ([4684a44](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/4684a44))      |                  [header.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/resources/templates/header.html)                   |
|  5  |      Modelado de eventos, sesiones y detalle ([20dfcc7](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/20dfcc7))       |             [Event.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/model/Event.java)              |

---

#### **Alumno 2 - Alberto Hontanilla Villanueva**

Especializado en la gestión y refactorización del sistema de cursos, incluyendo la edición avanzada con validaciones, el seguimiento de progreso de lecciones y la integración de autenticación OAuth2 con Google.

| Nº  |                                                                     Commits                                                                     |                                                                                         Files                                                                                         |
| :-: | :---------------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  | Refactorización profunda de gestión de cursos ([a502e77](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/a502e77)) |     [CourseService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/service/CourseService.java)      |
|  2  |   Edición de cursos y lógica de validación ([b9500ea](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/b9500ea))    | [CourseController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/CourseController.java) |
|  3  |   Integración de registro vía Google OAuth2 ([fc0f08c](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/fc0f08c))   |  [LoginController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/LoginController.java)  |
|  4  |   Consolidación de modelos de Imagen y Tags ([812fc75](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/812fc75))   |                [Tag.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/model/Tag.java)                 |
|  5  |     Seguimiento de progreso en lecciones ([bdb6858](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/bdb6858))      |                   [course.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/resources/templates/course.html)                    |

---

#### **Alumno 3 - Gonzalo Andrés Zurdo Patino**

Arquitecto fundamental de los cimientos del proyecto, responsable de los modelos iniciales de cursos, eventos y perfiles. Implementó el sistema central de carrito de la compra y el sistema de estadísticas.

| Nº  |                                                                  Commits                                                                   |                                                                                                           Files                                                                                                            |
| :-: | :----------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |  Cimientos del proyecto y modelos base ([0f3ad3d](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/0f3ad3d))   |                  [ProfileController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/ProfileController.java)                   |
|  2  |   Perfiles de usuario y seguridad base ([ca0d5c1](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/ca0d5c1))   |                                     [profile.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/resources/templates/profile.html)                                     |
|  3  |     Integración de carrito y pedidos ([a47fc46](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/a47fc46))     |                          [CartService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/service/CartService.java)                          |
|  4  |    Implementación de las estadísticas ([53fd21b](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/53fd21b))    | [CourseService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/53fd21bfff43faca9f1079911d4b84f680c7a84f/scam-g18/src/main/java/es/codeurjc/scam_g18/service/CourseService.java#L517-L565) |
|  5  | Unificación de servicios y controladores ([38ee6b5](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/38ee6b5)) |                     [CartController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/CartController.java)                      |

---

#### **Alumno 4 - Jaime Sánchez Vázquez**

Lideró el desarrollo avanzado del panel de administración, motores de búsqueda con filtrado por etiquetas, gestión completa de facturación y el sistema de suscripciones Premium.

| Nº  |                                                                   Commits                                                                    |                                                                                          Files                                                                                           |
| :-: | :------------------------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |    Filtrado avanzado y mejoras de Admin ([e1c717e](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/e1c717e))    |        [AdminService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/service/AdminService.java)        |
|  2  | Motor de búsqueda y filtrado por etiquetas ([268005e](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/268005e)) |          [TagService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/service/TagService.java)          |
|  3  | Refactorización del núcleo administrativo ([0800ca5](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/0800ca5))  |   [AdminController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/AdminController.java)    |
|  4  |  Gestión de facturas y procesos de compra ([2d8b2de](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/2d8b2de))  | [DatabaseInitializer.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/service/DatabaseInitializer.java) |
|  5  |  Implementación de suscripciones Premium ([518d800](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/518d800))   |         [Subscription.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/model/Subscription.java)         |

---

## 🛠 **Práctica 2: Incorporación de una API REST a la aplicación web, despliegue con Docker y despliegue remoto**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](https://youtu.be/BGjn1VLPkfA)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**

📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**

📖 **[Documentación API REST (HTML)](https://rawcdn.githack.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/3209f70743181cbb227b3d6b5cb24b62e28ce25e/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

<img width="2288" height="1897" alt="Diagrama-Clases-P2-G18" src="https://github.com/user-attachments/assets/da2a383c-1ef9-48cf-8fd4-9c764ec49f16" />

### **Instrucciones de Ejecución con Docker**

#### **Requisitos previos:**

- Docker instalado (versión 20.10 o superior)
- Docker Compose instalado (versión 2.0 o superior)
- (Opcional) Git Bash o WSL para ejecutar los scripts `.sh` en Windows

#### **Pasos para ejecutar con docker-compose:**

1. **Clonar el repositorio** (si no lo has hecho ya):

   ```bash
   git clone https://github.com/[usuario]/[repositorio].git
   cd [repositorio]
   ```

2. **(Opcional) Configurar variables de entorno para la base de datos**:
   - El fichero `docker/docker-compose.yml` ya define valores por defecto:
     - `DB_NAME=test`
     - `DB_USER=root`
     - `DB_PASSWORD=SC@M2026-2027`
   - Si quieres personalizarlos, crea un archivo `.env` en la raíz del repositorio con:

   ```env
   DB_NAME=scam_db
   DB_USER=root
   DB_PASSWORD=tu_password
   ```

3. **Levantar la aplicación y MySQL con Docker Compose**:

   ```bash
   docker compose -f docker/docker-compose.yml up -d
   ```

4. **Comprobar que los contenedores están en ejecución**:

   ```bash
   docker compose -f docker/docker-compose.yml ps
   ```

5. **Ver logs de la aplicación (si lo necesitas)**:

   ```bash
   docker compose -f docker/docker-compose.yml logs -f web
   ```

6. **Abrir la aplicación**:

   ```
   https://localhost:8443
   ```

   > La aplicación usa HTTPS con certificado autofirmado y el navegador puede mostrar aviso de seguridad.

7. **Parar y eliminar contenedores**:
   ```bash
   docker compose -f docker/docker-compose.yml down
   ```
   Para borrar también el volumen de MySQL:
   ```bash
   docker compose -f docker/docker-compose.yml down -v
   ```

#### **Ejecución directamente desde Docker Hub (sin clonar repositorio)**

Puedes ejecutar la práctica usando las imágenes publicadas en Docker Hub:

- `jaimesnh/scam-g18`
- `jaimesnh/scam-g18-compose`

1. **Descargar el artefacto OCI que contiene `docker-compose.yml`**:

   ```bash
   docker pull jaimesnh/scam-g18-compose:latest
   ```

2. **Extraer `docker-compose.yml` del artefacto a tu máquina**:

   ```bash
   docker create --name scam_compose_tmp jaimesnh/scam-g18-compose:latest
   docker cp scam_compose_tmp:/docker-compose.yml ./docker-compose.yml
   docker rm scam_compose_tmp
   ```

3. **(Opcional) Crear `.env` en el mismo directorio para personalizar la BD**:

   ```env
   DB_NAME=test
   DB_USER=root
   DB_PASSWORD=SC@M2026-2027
   ```

4. **Arrancar los servicios con Docker Compose**:

   ```bash
   docker compose up -d
   ```

5. **Comprobar estado y logs**:

   ```bash
   docker compose ps
   docker compose logs -f web
   ```

6. **Abrir la aplicación**:

   ```
   https://localhost:8443
   ```

7. **Parar y limpiar**:
   ```bash
   docker compose down
   ```
   Para borrar también el volumen de MySQL:
   ```bash
   docker compose down -v
   ```

> Nota: el servicio `web` del compose ya referencia `jaimesnh/scam-g18:latest`, por lo que no necesitas hacer `docker pull` manual de esa imagen (aunque puedes hacerlo si quieres forzar descarga previa).

### **Construcción de la Imagen Docker**

#### **Requisitos:**

- Docker instalado en el sistema

#### **Pasos para construir y publicar la imagen:**

1. **Navegar al directorio de Docker**:

   ```bash
   cd docker
   ```

2. **Construir imagen local con el script del proyecto**:

   ```bash
   ./create_image.sh scam-g18 latest
   ```

   Esto construye la imagen usando `docker/DockerFile` y el contexto `scam-g18/`.

3. **(Opcional) Publicar la imagen en Docker Hub**:

   ```bash
   ./publish_image.sh <dockerhub_user> scam-g18 latest
   ```

   Ejemplo:

   ```bash
   ./publish_image.sh jaimesnh scam-g18 latest
   ```

4. **(Opcional) Publicar `docker-compose.yml` como artefacto OCI**:
   ```bash
   ./publish_docker-compose.sh <dockerhub_user> scam-g18-compose latest
   ```

### **Despliegue en Máquina Virtual**

#### **Requisitos:**

- Acceso a la máquina virtual (SSH)
- Clave privada para autenticación
- Conexión a la red correspondiente o VPN configurada

#### **Pasos para desplegar:**

1. **Conectar a la máquina virtual**:

   ```bash
   ssh -i [ruta/a/clave.key] [usuario]@[IP-o-dominio-VM]
   ```

   Ejemplo:

   ```bash
   ssh -i ssh-keys/app.key vmuser@10.100.139.XXX
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **URL de la Aplicación Desplegada**

🌐 **URL de acceso**: `https://appweb18.dawgis.etsii.urjc.es:8443`

#### **Credenciales de Usuarios de Ejemplo**

| Rol                | Usuario  | Contraseña |
| :----------------- | :------- | :--------- |
| Administrador      | admin    | adminpass  |
| Usuario Registrado | learner1 | pass1      |
| Usuario Registrado | learner2 | pass2      |

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - Jaime Sánchez Vázquez (jaimesnh)**

Responsable principal de la dockerización y despliegue de la práctica, del endurecimiento de seguridad para endpoints API y de la evolución de la capa REST con DTOs/mappers en los endpoints del pefil, panel de administrador y login. Por ultimo ha sido el responsable de la documentación OpenAPI y Postman.

| Nº  |                                                                               Commits                                                                               |                                                                                              Files                                                                                               |
| :-: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |                Documentación de la API con OpenAPI ([3209f70](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/3209f70))                |                                     [api-docs.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/api-docs/api-docs.html)                                      |
|  2  |   Configuración Docker y conexión de aplicación a base de datos ([1044dd2](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/1044dd2))   |                                 [docker-compose.yml](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/docker/docker-compose.yml)                                  |
|  3  | Endpoints de autenticación (login/logout/registro/disponibilidad) ([7e98721](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/7e98721)) | [LoginRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/auth/LoginRestController.java) |
|  4  |     Evolución de la API de perfil con DTOs y nuevos contratos ([cf2c32a](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/cf2c32a))     | [ProfileRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/ProfileRestController.java)  |
|  5  |        Refactor REST admin con DTOs y mappers compartidos ([4294564](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/4294564))         |   [AdminRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/AdminRestController.java)    |

---

#### **Alumno 2 - Pau Calvo Jiménez**

Implementación de controladores REST para las entidades principales de Cursos y Eventos, permitiendo operaciones CRUD, gestión de suscripciones y acceso al contenido multimedia bajo la nueva arquitectura de API REST.

| Nº  |                                                                         Commits                                                                          |                                                                                             Files                                                                                             |
| :-: | :------------------------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |   CourseRestController para gestión total de cursos ([68297bb](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/68297bb))    | [CourseRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/CourseRestController.java) |
|  2  | EventRestController para gestión y búsqueda de eventos ([b6214a6](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/b6214a6)) |  [EventRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/EventRestController.java)  |
|  3  |  Implementación inicial de endpoints CRUD para cursos ([ca16ce0](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/ca16ce0))  | [CourseRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/CourseRestController.java) |

---

#### **Alumno 3 - Alberto Hontanilla Villanueva**

Refactorización y robustecimiento de la API de registro y comprobación de disponibilidad de usuarios en la migración a la arquitectura REST. Validación y manejo de envío de correos.

| Nº  |                                                                             Commits                                                                              |                                                                                                 Files                                                                                                  |
| :-: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |    Simplificación de endpoint de disponibilidad de usuario ([3f7cbe8](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/3f7cbe8))     | [RegisterRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/auth/RegisterRestController.java) |
|  2  |     Validación de campos requeridos en la API de registro ([6df8b3f](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/6df8b3f))      | [RegisterRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/auth/RegisterRestController.java) |
|  3  | Manejo robusto de excepciones y de fallos de servicio de email ([4594e48](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/4594e48)) | [RegisterRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/auth/RegisterRestController.java) |
|  4  |    Refactorización general del flujo de registro y errores ([6169654](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/6169654))     | [RegisterRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/auth/RegisterRestController.java) |
|  5  |   Implementación inicial del controlador REST para registro ([de602c0](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/de602c0))    |   [RegisterRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/RegisterRestController.java)    |

---

#### **Alumno 4 - Gonzalo Andrés Zurdo Patino**

Desarrollo de la API REST para la gestión del carrito de compras y del proceso de checkout de la aplicación. Implementación de los endpoints relacionados con visualización de estadísticas, y mappers iniciales.

| Nº  |                                                                           Commits                                                                            |                                                                                                 Files                                                                                                 |
| :-: | :----------------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |    API REST para gestión integral del carrito de compra ([e501b31](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/e501b31))    |       [CartRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/CartRestController.java)       |
|  2  |     Controladores REST para consultas de estadísticas ([3c16799](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/3c16799))      | [StatisticsRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/StatisticsRestController.java) |
|  3  |   Implementación de la capa de DTOs y lógica de pedidos ([c116545](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/c116545))    |                    [OrderDTO.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/dto/OrderDTO.java)                     |
|  4  | Preparación de mappers base para Cursos, Eventos y Pedidos ([262b469](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/262b469)) |           [CartController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/CartController.java)           |
|  5  | Setup inicial de la API REST mediante GlobalRestController ([63e16c2](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/63e16c2)) |     [GlobalRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/GlobalRestController.java)     |

---

## 🛠 **Práctica 3: Implementación de la web con arquitectura SPA**

### **Vídeo de Demostración**

📹 **[Enlace al vídeo en YouTube](URL_del_video)**

> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Preparación del Entorno de Desarrollo**

#### **Requisitos Previos**

- **Node.js**: versión 20.x o superior
- **npm**: versión 10.x o superior (se instala con Node.js)
- **Git**: para clonar el repositorio
- **Backend de la aplicación**: recomendable tenerlo arrancado para consumir la API desde la SPA

#### **Pasos para configurar el entorno de desarrollo**

1. **Instalar Node.js y npm**

   Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/).

   Verifica la instalación:

   ```bash
   node --version
   npm --version
   ```

2. **Clonar el repositorio** (si no lo has hecho ya)

   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

3. **Navegar a la carpeta del frontend**

   ```bash
   cd frontend/scam-g18
   ```

4. **Instalar dependencias**

   ```bash
   npm install
   ```

5. **Arrancar el backend si quieres usar la SPA conectada a datos reales**
   - Opción local en Windows:

   ```bash
   cd ../../backend/scam-g18
   mvnw.cmd spring-boot:run
   ```

   - Opción local en Linux/Mac:

   ```bash
   cd ../../backend/scam-g18
   ./mvnw spring-boot:run
   ```

   - Opción con Docker Compose:

   ```bash
   cd ../../backend/docker
   docker compose up -d
   ```

6. **Arrancar la SPA en modo desarrollo**

   ```bash
   cd frontend/scam-g18
   npm run dev
   ```

7. **Abrir la aplicación**
   - Vite mostrará una URL local, normalmente `http://localhost:5173`.
   - Si el navegador muestra errores de datos, comprueba que el backend esté en ejecución.

8. **Comprobar el build de producción**

   ```bash
   npm run build
   ```

9. **Validar el tipado del proyecto**

   ```bash
   npm run typecheck
   ```

10. **Vista previa del build generado**

```bash
npm run start
```

### **Diagrama de Clases y Templates de la SPA**

Diagrama mostrando los componentes React, hooks personalizados, servicios y sus relaciones:

![Diagrama de Componentes React](images/spa-classes-diagram.png)

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - Pau Calvo Jiménez**

Responsable de las pantallas de alta y edición de cursos y eventos, la integración de mapas en los detalles y la navegación de páginas de suscripción y compra dentro de la SPA.

| Nº  |                                                                               Commits                                                                               |                                                                                                                                                                                                                             Files                                                                                                                                                                                                                             |
| :-: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |   Diseño de rutas y formularios de gestión de cursos y eventos ([2cd62df](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/2cd62df))    | [CourseForm.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/CourseForm.tsx), [EventForm.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/EventForm.tsx), [courses.$id.edit.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/courses.$id.edit.tsx) |
|  2  |     Implementación de listados y búsqueda de cursos y eventos ([83f9600](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/83f9600))     |                                                                                        [courses.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/courses.tsx), [events.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/events.tsx)                                                                                         |
|  3  |           AppLayout y navegación principal de la SPA ([42d07d3c](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/42d07d3c))            |                                                                                                                                                            [index.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/index.tsx)                                                                                                                                                            |
|  4  | Detalle de cursos y eventos con Leaflet y navegación contextual ([ed41e5dd](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/ed41e5dd)) |                                                                                [courses.$id.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/courses.$id.tsx), [events.$id.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/events.$id.tsx)                                                                                 |

---

#### **Alumno 2 - Alberto Hontanilla Villanueva**

Encargado de refactorizar la autenticación y la cabecera de la aplicación, simplificar el flujo de login y mantener la coherencia de navegación entre rutas públicas y privadas.

| Nº  |                                                                          Commits                                                                          |                                                                                                                                                                                                                    Files                                                                                                                                                                                                                    |
| :-: | :-------------------------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |   Base de layout global y estado compartido de la SPA ([dc0299a](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/dc0299a))   |                                                                           [index.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/index.tsx), [globalStore.ts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/stores/globalStore.ts)                                                                            |
|  2  | Navegación principal del header y Home con React Router ([ff27e2e](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/ff27e2e)) |                                                                                [Header.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/Header.tsx), [home.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/home.tsx)                                                                                 |
|  3  |      Refactor de autenticación y perfil de usuario ([4239e5f](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/4239e5f))      | [authStore.ts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/stores/authStore.ts), [authService.ts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/services/authService.ts), [profile.$id.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/profile.$id.tsx) |
|  4  |     Formulario de login y validaciones funcionales ([3053cce](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/3053cce))      |                                                                                                                                                     [login.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/login.tsx)                                                                                                                                                     |

---

#### **Alumno 3 - Gonzalo Andrés Zurdo Patino**

Lideró la base funcional de la SPA con la navegación inicial, la integración del carrito y la primera estructura visual de la aplicación, dejando preparadas las pantallas principales para el resto del equipo.

| Nº  |                                                                          Commits                                                                           |                                                                                                                                                                                                                                               Files                                                                                                                                                                                                                                               |
| :-: | :--------------------------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  | Implementación de rutas iniciales de registro y acceso ([79b68438](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/79b68438)) |                                                                                                          [login.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/login.tsx), [register.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/register.tsx)                                                                                                           |
|  2  |     Carrito de compra y flujo de checkout de la SPA ([e591ed7](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/e591ed7))      |                                      [cart.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/cart.tsx), [cartService.ts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/services/cartService.ts), [CartDTO.ts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/dtos/CartDTO.ts)                                      |
|  3  |    Integración del carrito con charts y seguridad JWT ([69959c8](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/69959c8))    | [Chart.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/Chart.tsx), [chartService.ts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/services/chartService.ts), [SecurityConfiguration.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/scam-g18/src/main/java/es/codeurjc/scam_g18/security/SecurityConfiguration.java) |
|  4  |       Vista de detalle de curso y perfil dinámico ([42c77e1](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/42c77e1))        |                                                                                                 [courses.$id.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/courses.$id.tsx), [profile.$id.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/routes/profile.$id.tsx)                                                                                                  |

---

#### **Alumno 4 - Jaime Sánchez Vázquez**

Se centró en la estructura general de la SPA, los helpers de activos públicos, los formularios de cursos y eventos, la parte de administración, la gestión de perfil y la integración del frontend dentro del backend para el despliegue.

| Nº  |                                                                            Commits                                                                             |                                                                                                                                                                                        Files                                                                                                                                                                                        |
| :-: | :------------------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |           Helper para rutas de recursos públicos ([26e0830d](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/26e0830d))           |                                                 [Header.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/Header.tsx), [index.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/index.tsx)                                                  |
|  2  |  Evolución de servicios y formularios de cursos y eventos ([bc64588e](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/bc64588e))  |                                         [CourseForm.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/CourseForm.tsx), [EventForm.tsx](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/frontend/scam-g18/app/components/EventForm.tsx)                                          |
|  3  | Refactor del dashboard de administración y cabecera global ([9a410475](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/9a410475)) |        [AdminService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/scam-g18/src/main/java/es/codeurjc/scam_g18/service/AdminService.java), [UserService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/scam-g18/src/main/java/es/codeurjc/scam_g18/service/UserService.java)         |
|  4  |        Actualización de la API y gestión de perfil ([cf2c32a2](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/cf2c32a2))         | [ProfileRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/scam-g18/src/main/java/es/codeurjc/scam_g18/controller/ProfileRestController.java), [ProfileDTO.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/scam-g18/src/main/java/es/codeurjc/scam_g18/dto/ProfileDTO.java) |
|  5  |        Integración del frontend dentro del backend ([733c748a](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/commit/733c748a))         |                                                             [DockerFile](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/docker/DockerFile), [create_image.sh](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-18/blob/main/backend/docker/create_image.sh)                                                              |
