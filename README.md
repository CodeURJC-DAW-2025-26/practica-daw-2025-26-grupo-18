# Smart Courses & Academy Market (SCAM)

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| Pau Calvo Jiménez | p.calvo.2023@aulmnos.urjc.es | PauCalvoJ |
| Alberto Hontanilla Villanueva | a.hontanilla.2023@alumnos.urjc.es | albertohontanilla |
| Gonzalo Andrés Zurdo Patino | ga.zurdo.2023@alumnos.urjc.es | 51nga |
| Jaime Sánchez Vázquez | j.sanchezva.2023@alumnos.urjc.es | jaimesnh |

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

* **Usuario Anónimo**: 
  - Permisos: Visualización de cursos y eventos, sin poder acceder a su contenido, ni poner reseñas.
  - No es dueño de ninguna entidad

* **Usuario Registrado**: 
  - Permisos: Posibilidad de suscribirse a los cursos y eventos para poder acceder a su contenido y poner reseñas, al igual que suscribirse a eventos. Tambien pueden suscribirse a la pagina para poder publicar tanto cursos como eventos
  - Es dueño de: Sus cursos, sus eventos, sus reseñas y sus lecciones

* **Administrador**: 
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
<img width="1802" height="866" alt="Iniciio de sesión" src="https://github.com/user-attachments/assets/e2f55c3f-a802-4064-b609-14c6f28145d4" />

> Permite iniciar sesión si o registrar un nuevo usuario



> ![Admin dashboard]
<img width="1804" height="865" alt="Admin Dashboard" src="https://github.com/user-attachments/assets/6b277289-9999-4d03-a754-18ddbae06934" />

> Pestaña donde el administrador podrá realizar sus funcionalidades



> ![Crear curso]
<img width="1817" height="859" alt="Crear curso" src="https://github.com/user-attachments/assets/a08da16a-c6a7-4a74-a33f-5dec84fc575f" />

> Pestaña donde se introducirá la información para crear un curso



> ![Eventos disponibles y cursos disponibles]
<img width="1810" height="862" alt="Eventos disponibles y cursos disponibles 1" src="https://github.com/user-attachments/assets/fe6d38b9-b69f-4ac3-a6e2-fe193ccaba10" />
<img width="1811" height="862" alt="Eventos disponibles y cursos disponibles 2" src="https://github.com/user-attachments/assets/bc52977a-a8d7-4c75-ad0b-35102c73fd3d" />

> Al ser exactamente el mismo diseño se incluyen las dos a la vez. Mostrarán los cursos o eventos disponibles.



> ![Cursos suscrito]
<img width="1807" height="864" alt="Cursos suscrito" src="https://github.com/user-attachments/assets/1e59187f-df65-43f3-bd6c-78e787670126" />

> Mostrará los cursos a lso que esta suscrito el usuario.



> ![Evento y curso]
<img width="1809" height="863" alt="Evento y curso 1" src="https://github.com/user-attachments/assets/5d7ff18d-3a68-4dad-b835-7aadb1e86c7f" />
<img width="1806" height="864" alt="Evento y curso 2" src="https://github.com/user-attachments/assets/047cdfef-2afd-4d4b-bb55-d7b6c8a78105" />

> De nuevo comparten diseño. Mostrarán la información de un curso o un evento.



> ![Pago]
<img width="1828" height="854" alt="Pago" src="https://github.com/user-attachments/assets/34599d08-1639-43b6-94f6-7bbfc9b76d0e" />

Mostrará el contenido del carrito y saltará un pop-up para rellenar la informaciónd el pago



>![Perfil]
<img width="1804" height="865" alt="Perfil" src="https://github.com/user-attachments/assets/5cb123c4-cb0a-4d96-a0fe-3dee7f02cb91" />

>Mostrará la información del usuario y permitirá editarla.



## 🛠 **Práctica 1: Web con HTML generado en servidor y AJAX**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Solo si ha cambiado.

#### **Capturas de Pantalla Actualizadas**

Solo si han cambiado.

### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

#### **Pasos para ejecutar la aplicación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

2. **AQUÍ INDICAR LO SIGUIENTES PASOS**

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin`, contraseña: `admin`
- **Usuario Registrado**: usuario: `user`, contraseña: `user`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](images/database-diagram.png)

> [Descripción opcional: Ej: "El diagrama muestra las 4 entidades principales: Usuario, Producto, Pedido y Categoría, con sus respectivos atributos y relaciones 1:N y N:M."]

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/classes-diagram.png)

> [Descripción opcional del diagrama y relaciones principales]

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 2: Incorporación de una API REST a la aplicación web, despliegue con Docker y despliegue remoto**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/complete-classes-diagram.png)

### **Instrucciones de Ejecución con Docker**

#### **Requisitos previos:**
- Docker instalado (versión 20.10 o superior)
- Docker Compose instalado (versión 2.0 o superior)

#### **Pasos para ejecutar con docker-compose:**

1. **Clonar el repositorio** (si no lo has hecho ya):
   ```bash
   git clone https://github.com/[usuario]/[repositorio].git
   cd [repositorio]
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **Construcción de la Imagen Docker**

#### **Requisitos:**
- Docker instalado en el sistema

#### **Pasos para construir y publicar la imagen:**

1. **Navegar al directorio de Docker**:
   ```bash
   cd docker
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**

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

🌐 **URL de acceso**: `https://[nombre-app].etsii.urjc.es:8443`

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | admin | admin123 |
| Usuario Registrado | user1 | user123 |
| Usuario Registrado | user2 | user123 |

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 3: Implementación de la web con arquitectura SPA**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](URL_del_video)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Preparación del Entorno de Desarrollo**

#### **Requisitos Previos**
- **Node.js**: versión 18.x o superior
- **npm**: versión 9.x o superior (se instala con Node.js)
- **Git**: para clonar el repositorio

#### **Pasos para configurar el entorno de desarrollo**

1. **Instalar Node.js y npm**
   
   Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/)
   
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

3. **Navegar a la carpeta del proyecto React**
   ```bash
   cd frontend
   ```

4. **AQUÍ LOS SIGUIENTES PASOS**

### **Diagrama de Clases y Templates de la SPA**

Diagrama mostrando los componentes React, hooks personalizados, servicios y sus relaciones:

![Diagrama de Componentes React](images/spa-classes-diagram.png)

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

