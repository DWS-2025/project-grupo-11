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
  - Implementación de la API (RestControllers, Mappers y DTOs).
  - Implementación de la BBDD utilizando Repositorios.
  - Arreglos en el funcionamiento de la web, base de datos y API.
- **Commits más significativos:**  
  1. [RestControllers, DTOs y Mappers terminados](https://github.com/DWS-2025/project-grupo-11/commit/b0722ef9d8c5ea227cbe2784bea0a53a1bc6f6e2)  
  2. [Cambios para comenzar a usar la BBDD](https://github.com/DWS-2025/project-grupo-11/commit/17547b6ce8c3227683883c94628d75c3c933b94a)  
  3. [Adición SimpleDTOs para solucionar referencias circulares](https://github.com/DWS-2025/project-grupo-11/commit/ef6b1ffe7bde85881fc6e3dfa918f08f4498e21d)  
  4. [Cambios en las clases para seguir integrando la base de datos](https://github.com/DWS-2025/project-grupo-11/commit/7e3293e5d3b69ed2d7716a7994e73282b50e7278)  
  5. [Arreglo Funcionamiento Mappers](https://github.com/DWS-2025/project-grupo-11/commit/56827cdb21d13ea9aef59b9e9203ab57c0ae3dbf)  
- **Ficheros más editados:**  
  - `src/main/java/grupo11/bcf_store/controller/rest/ProductRestController.java`  
  - `src/main/java/grupo11/bcf_store/service/ProductService.java`  
  - `src/main/java/grupo11/bcf_store/controller/rest/OrderRestController.java`  
  - `src/main/java/grupo11/bcf_store/service/CartService.java`  
  - `src/main/java/grupo11/bcf_store/model/Cart.java`  

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