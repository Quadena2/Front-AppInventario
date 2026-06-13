# App Inventario - Cliente Móvil 📱📦

Aplicación nativa para Android diseñada para la gestión y visualización de un inventario de productos. Este cliente móvil se conecta a un servidor en la nube y consume datos mediante múltiples protocolos para ofrecer una experiencia fluida y en tiempo real.

##  Características Principales

* Consumo REST: Peticiones HTTP seguras para la carga principal de datos utilizando Retrofit.
* Consultas Optimizadas: Integración con endpoints de GraphQL para peticiones selectivas y eficientes.
* Sincronización en Tiempo Real: Conexión activa mediante WebSockets para recibir actualizaciones instantáneas del servidor sin necesidad de recargar la pantalla.

##  Tecnologías y Herramientas

* Entorno de Desarrollo: Android Studio
* Lenguaje: Java / Kotlin
* Librerías de Red: Retrofit2, OkHttp
* Formatos de datos: JSON

##  Instalación y Configuración

Sigue estos pasos para compilar el proyecto en tu entorno local:

1. **Clonar el repositorio:**
    ```bash
   git clone https://github.com/[TU-USUARIO]/inventario-mobile.git
    ```
2. **Abrir el proyecto:**

   Abre Android Studio, selecciona "File > Open" y busca la carpeta clonada.

3. **Configurar la conexión al servidor:**

   El proyecto está configurado para apuntar a un backend desplegado en Railway. 

4. **Compilar:**

   Conecta tu dispositivo físico mediante USB/Wi-Fi o inicia el emulador, y presiona Run (o usa el atajo Shift + F10).

##  Contexto del Proyecto

Este sistema fue desarrollado como parte de un trabajo grupal para la materia de Dispositivos Móviles 2 (INF-254) de la UMSA. El proyecto corresponde al "Tema 7: Servicios Web", y está enfocado principalmente en el análisis, implementación y consumo de la arquitectura de web services integrando múltiples protocolos (REST, GraphQL y WebSockets) en un entorno móvil.

* Docente: Dr. Juan Marcos Miranda Nina, Ph.D.