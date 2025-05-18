# 🛍️ **Burgos CF Store**  
¡Bienvenido a la tienda del Burgos CF!  

---

## 👥 **Equipo de desarrollo**
### **Integrantes**
- 🧑‍💻 **Mario Serrano García**  
  📧 *m.serranog.2023@alumnos.urjc.es*  
  🌐 [GitHub: @marioserraano](https://github.com/marioserraano)

- 🧑‍💻 **Carlos Gutiérrez Carpintero**  
  📧 *c.gutierrezc.2023@alumnos.urjc.es*  
  🌐 [GitHub: @Carlosgcov](https://github.com/Carlosgcov)

---

## 🌟 **Aspectos principales de la aplicación**
### **📋 Entidades principales**
La aplicación gestiona las siguientes entidades:  
- **👤 User:** Usuarios registrados con carrito y pedidos.  
- **🛒 Cart:** Carrito de compras asociado a un usuario.  
- **📦 Order:** Pedido realizado por un usuario.  
- **🛍️ Product:** Productos disponibles en la tienda.  

### **🔗 Relaciones entre entidades**
- Un **User** tiene un **Cart** y puede tener múltiples **Orders**.  
- Un **Order** puede contener múltiples **Products**.  
- Un **Product** puede estar asociado a múltiples **Orders**.  

---

### **🔐 Permisos de los usuarios**
- **Usuario anónimo:**   
  - ✅ Ver información general de la web.
  - ✅ Acceder a la página de contacto.
  - ✅ Visualizar productos.  
  - ✅ Iniciar sesión de un usuario existente.  
  - ✅ Registrarse como nuevo usuario.
  - **Usuario registrado:**  
  - ✅ Añadir y eliminar pedidos a su carrito.  
  - ✅ Realizar y borrar sus pedidos.  
  - ✅ Borrar su cuenta.
  - ✅ Cambiar su usuario y contraseña.
  - ✅ Cambiar su nombre y su descripción.  
  - ✅ Subir y descargar una foto de su DNI.  
  - **Usuario administrador:**  
  - ✅ Añadir, editar y borrar productos.  
  - ✅ Visualizar y eliminar pedidos de cualquier usuario.  
  - ✅ Visualizar carritos de compra de otros usuarios.  
  - ✅ Eliminar cualquier usuario.  

---

### **🖼️ Imágenes**
- Cada **Product** tiene asociada una imagen.  
- Cada **User** puede subir una imágen de su DNI. 

---

## 📊 **Diagrama de entidades**
![Diagrama UML](uml-diagram.png)  

---

## 🤝 **Desarrollo colaborativo**
### **📌 Participación de los miembros del equipo**

#### 🧑‍💻 **Mario Serrano García**  
- **Tareas realizadas:**  
  - Implementación de la seguridad de la API.
  - Creación de métodos para las nuevas funcionalidades.
  - Arreglo de errores en la web y API.
- **Commits más significativos:**  
  1. [Adición archivos y cambios Rest Security](https://github.com/DWS-2025/project-grupo-11/commit/711db0c0c27dce7fdbfceee5cd3b3d02807c5685)
  2. [Seguridad métodos API](https://github.com/DWS-2025/project-grupo-11/commit/85eec2b1271fa5f1b84fd9c88ea73d9c77d6d359)
  3. [Inicio Spring Security](https://github.com/DWS-2025/project-grupo-11/commit/b1274404776fa0a5c7c19676d49f7536a855d461)
  4. [Inicio Rest Security](https://github.com/DWS-2025/project-grupo-11/commit/a63d5f184c1d74155f0ca9b82edc285242dcbb63)
  5. [Mejoras API Rest](https://github.com/DWS-2025/project-grupo-11/commit/59865e8813597365be18440fff018eaf62eab1ae)
- **Ficheros más editados:**  
  - `src/main/java/grupo11/bcf_store/controller/rest/OrderRestController.java`  
  - `src/main/java/grupo11/bcf_store/controller/rest/UserRestController.java`  
  - `src/main/java/grupo11/bcf_store/security/SecurityConfiguration.java`  
  - `src/main/java/grupo11/bcf_store/service/UserService.java`  
  - `src/main/java/grupo11/bcf_store/controller/web/UserWebController.java`

#### 🧑‍💻 **Carlos Gutiérrez Carpintero**  
- **Tareas realizadas:**  
  - Funcionalidades de texto enriquecido y ficheros guardados en disco.  
  - Creación de nuevos métodos de los usuarios.  
  - Corrección de errores y vulnerabilidades.  
- **Commits más significativos:**  
  1. [Comienzo funcionamiento de sesiones y usuarios](https://github.com/DWS-2025/project-grupo-11/commit/bc88239ee89f367ec101b5a688b3c92f88e06daf)
  2. [Asociación de carrito y pedido con el id del usuario. Registro de nuevos usuarios y su cartid. No mostrar botones si no se tiene permisos de ADMIN o si no se está registrado.](https://github.com/DWS-2025/project-grupo-11/commit/9b430a259d9e5dffa5e562c7049cdfbc6e9550bc)
  3. [Añadida funcionalidad de subir DNI de un usuario como archivo en disco.](https://github.com/DWS-2025/project-grupo-11/commit/6bfd1ed176e214849d100effbfa3aba53d255a74)
  4. [Mejora y protección de subida de imágenes en BBDD y en disco](https://github.com/DWS-2025/project-grupo-11/commit/f918932d96082cbc8df7531b7a25668f255a4efc)
  5. [Arreglada la modificación de credenciales e información de usuarios y corrección de problemas.](https://github.com/DWS-2025/project-grupo-11/commit/c96a5bc09c087c4abd74dae0fa04ef032449f99f)
- **Ficheros más editados:**  
  - `src/main/java/grupo11/bcf_store/security/SecurityConfiguration.java`  
  - `src/main/java/grupo11/bcf_store/controller/web/UserWebController.java`  
  - `src/main/resources/templates/private.html`  
  - `src/main/resources/templates/admin.html`  
  - `src/main/java/grupo11/bcf_store/service/UserService.java`
