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

# 🧠 API de Predicción de Ventas — InventIA

Esta API permite:
- Entrenar un modelo **LSTM multivariado global** usando datos históricos.
- Realizar predicciones por producto (`product_id`).
- Obtener explicaciones simples y avanzadas de las predicciones:
  - 📊 **Explicación simple**: comparación contra promedio histórico.
  - 🎯 **Explicación avanzada**: importancia de variables + gráfica en `base64`.

---

## 🚀 **Endpoints principales**

### 🔹 `POST /upload/`
📥 **Descripción:**  
Carga un archivo CSV con datos históricos para entrenar el modelo global.

📄 **Formato mínimo esperado del CSV:**

| Columna         | Descripción                                    |
| --------------- | ---------------------------------------------- |
| `product_id`    | ID numérico del producto                       |
| `dt`            | Fecha (`YYYY-MM-DD`)                           |
| `sale_amount`   | Ventas numéricas                               |

🔔 **Columnas opcionales (se rellenan automáticamente si faltan):**
- `discount`
- `holiday_flag`
- `avg_temperature`
- `avg_humidity`
- `media_7d` (calculada automáticamente)
- `std_7d` (calculada automáticamente)

✅ **Cómo enviar desde backend/frontend:**

#### Ejemplo frontend (JavaScript):
```javascript
const formData = new FormData();
formData.append('file', yourCSVFile);
await fetch('http://localhost:8000/upload/', { method: 'POST', body: formData });

### Respuesta JSON
{
  "status": "ok",
  "prediccion": {
    "product_id": 38,
    "fecha_prediccion": "2024-07-15",
    "prediccion": 3.45
  },
  "explicacion_simple": "Se prevé un aumento de 12% respecto al promedio histórico.",
  "explicacion_avanzada": {
    "producto_id": 38,
    "prediccion": 3.45,
    "variables_importantes": [ (no son siempre las misma)
      ["sale_amount", 0.5678],
      ["discount", 0.3210],
      ["holiday_flag", 0.2100],
      ["activity_flag", 0.1500],
      ["day_of_week", 0.1234]
    ],
    "grafica_explicabilidad_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
}
