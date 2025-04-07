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

- 🧑‍💻 **Alejandro Torres Montes**  
  📧 *a.torresmo.2023@alumnos.urjc.es*  
  🌐 [GitHub: @Torressss7](https://github.com/Torressss7)

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
- **Usuario por defecto:**  
  - ✅ Gestionar productos.  
  - ✅ Visualizar y eliminar pedidos de cualquier usuario.  
  - ✅ Gestionar su carrito de compras.  
  - ✅ Realizar pedidos.  
  - ✅ Ser dueño de sus propios pedidos y carrito.  

---

### **🖼️ Imágenes**
- Cada **Product** tiene asociada una imagen.  

---

## 📊 **Diagrama de entidades**
![Diagrama UML](uml-diagram.png)  

---

## 🤝 **Desarrollo colaborativo**
### **📌 Participación de los miembros del equipo**

#### 🧑‍💻 **Mario Serrano García**  
- **Tareas realizadas:**  
  - Implementación de funcionalidades clave.  
- **Commits más significativos:**  
  1. [Implementación](https://github.com/)  
  2. [Implementación](https://github.com/)  
  3. [Implementación](https://github.com/)  
  4. [Implementación](https://github.com/)  
  5. [Implementación](https://github.com/)  
- **Ficheros más editados:**  
  - `src/main/resources/templates/index.html`  
  - `src/main/resources/templates/index.html`  
  - `src/main/resources/templates/index.html`  
  - `src/main/resources/templates/index.html`  
  - `src/main/resources/templates/index.html`  

#### 🧑‍💻 **Carlos Gutiérrez Carpintero**  
- **Tareas realizadas:**  
  - Creación, edición y eliminación de productos.  
  - Migración a MySQL.  
  - Corrección de errores en la web, base de datos y API.  
- **Commits más significativos:**  
  1. [Edición de productos](https://github.com/DWS-2025/project-grupo-11/commit/cb86b52a7a04707dd7b36c337b425c20ac9c3883)  
  2. [Imágenes en base de datos](https://github.com/DWS-2025/project-grupo-11/commit/9a14714f369487d03d81569e7129a712b32ff7c0)  
  3. [Consultas de productos](https://github.com/DWS-2025/project-grupo-11/commit/584385d4179b5cf45435c781f220f0f5fae31223)  
  4. [Paginación con JavaScript](https://github.com/DWS-2025/project-grupo-11/commit/618a357db346d1af5bf31bce511dbba1a502ef44)  
  5. [Corrección de errores en carrito y pedidos](https://github.com/DWS-2025/project-grupo-11/commit/195a26f25db4845e52c19701d47cbda8e941dca3)  
- **Ficheros más editados:**  
  - `src/main/java/grupo11/bcf_store/controller/work/ProductWorkController.java`  
  - `src/main/java/grupo11/bcf_store/service/ProductService.java`  
  - `src/main/java/grupo11/bcf_store/repository/ProductRepository.java`  
  - `src/main/resources/static/js/clothes.js`  
  - `src/main/resources/templates/clothes.html`