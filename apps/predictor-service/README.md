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