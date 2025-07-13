# 🧠 Servicio de Predicción con FastAPI + Modelo LSTM (.keras)

Este microservicio permite predecir ventas por producto a partir de datos históricos, usando FastAPI y un modelo LSTM entrenado con Keras.

---

## 🚀 Requisitos

- Python 3.12
- Git

---

## ⚙️ Instalación

```bash
# Clonar el repositorio
git clone https://github.com/Erika05cristina/InventIA
cd InventIA/apps/predictor-service

# Crear entorno virtual con nombre personalizado
python -m venv env_predictor
source env_predictor/bin/activate  # En Windows: env_predictor\Scripts\activate

# Instalar dependencias
pip install -r requirements.txt

# Ejecutar 
uvicorn main:app --reload

## 🧠 API de Predicción de Ventas — InventIA

Esta API permite:
- Entrenar un modelo global de predicción de ventas usando datos históricos multivariados.
- Realizar predicciones por producto (`product_id`).
- Incluir explicaciones sencillas de las predicciones.

---

## 🚀 **Endpoints principales**

### 🔹 `POST /upload/`
- 📥 **Descripción:**
  Permite subir un archivo CSV con datos históricos para entrenar o actualizar el modelo global.

- 📄 **Formato mínimo esperado del CSV:**

  | Columna         | Descripción                                    |
  | --------------- | ---------------------------------------------- |
  | `product_id`    | ID numérico del producto                       |
  | `dt`            | Fecha (`YYYY-MM-DD`)                           |
  | `sale_amount`   | Ventas numéricas                               |

  🔔 **Columnas opcionales soportadas (rellenadas automáticamente si faltan):**
  - `discount`
  - `holiday_flag`
  - `avg_temperature`
  - `avg_humidity`
  - `media_7d` (si no existe se calcula)
  - `std_7d` (si no existe se calcula)

### 🔹 `POST /predict/by-product`
- 📥 **Descripción:**
  Devuelve la predicción de ventas para un `product_id` específico a partir de los datos cargados previamente.

- 🔑 **Request JSON:**
  ```json
  {
    "product_id": 38,
    "fecha_prediccion": "2024-07-15"
  }
