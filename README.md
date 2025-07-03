# Inventia

# Modelo de Predicción de Demanda para Stock en Tiendas

Este proyecto tiene como objetivo predecir la cantidad de producto necesaria para reponer stock en tiendas, utilizando datos históricos de los últimos 14 días para optimizar la gestión de inventarios y evitar faltantes o excesos de productos.

---

## Descripción

El modelo de predicción permite a los usuarios cargar datos históricos de ventas (últimos 14 días) para obtener una estimación de la cantidad de producto que deben reponer en sus tiendas. El sistema también incluye un asistente IA para responder preguntas sobre las predicciones y un historial de consultas para su análisis.

---

## Tecnologías

- **Frontend:** Angular
- **Backend:** FastAPI/Django
- **Base de Datos:** [Base de datos utilizada]
- **Autenticación:** Firebase Authentication
- **Modelo Predictivo:** [Modelo utilizado para la predicción]
- **Asistente IA:** OpenAI API
- **Infraestructura:** Docker, CI/CD, Firebase
- **Despliegue:** [Plataforma de despliegue]

---

## Flujo de Trabajo

1. **Autenticación:**
   - Los usuarios se autentican mediante Firebase Authentication para obtener un token de acceso.

2. **Carga de Datos y Predicción:**
   - El usuario carga los datos históricos de los últimos 14 días en el frontend.
   - El backend procesa los datos, ejecuta el modelo predictivo y devuelve la cantidad de stock recomendada.

3. **Interacción con Asistente IA:**
   - Los usuarios pueden hacer preguntas sobre las predicciones a través de un chat integrado.
   - El backend envía las consultas a la API de OpenAI para generar respuestas contextuales.

4. **Historial y Seguimiento:**
   - Los usuarios pueden consultar el historial de preguntas y respuestas relacionadas con las predicciones de stock.

---
