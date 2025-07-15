from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from services.predictor import predecir_por_producto, predecir_en_lote
from services.explainability import generar_explicacion, explicar_prediccion_producto
import pandas as pd
import numpy as np
import joblib
from keras.models import load_model
from services.data_preparation import prepare_dataframe


router = APIRouter()
DATA_FILE = "data/uploads/train.csv"

class PredictByProductRequest(BaseModel):
    product_id: int
    fecha_prediccion: str

class PredictAllRequest(BaseModel):
    fecha_prediccion: str


@router.post("/by-product")
def predict_by_product(request: PredictByProductRequest):
    try:
        # Predicción
        result = predecir_por_producto(DATA_FILE, request.product_id, request.fecha_prediccion)

        # Explicabilidad sencilla:
        df = pd.read_csv(DATA_FILE)
        promedio_historico = df[df["product_id"] == request.product_id]["sale_amount"].mean()
        explicacion_simple = generar_explicacion(result["prediccion"], promedio_historico)

        # Explicabilidad avanzada (gráfica base64)
        scaler = joblib.load("modelos/scaler.save")
        model = load_model("modelos/model_rnn.keras")
        df = prepare_dataframe(df)
        df = df[df["product_id"] == request.product_id].sort_values("dt")

        SEQ_FEATURES = ["sale_amount", "discount", "holiday_flag", "avg_temperature", "avg_humidity", "media_7d", "std_7d"]
        features = scaler.transform(df[SEQ_FEATURES].values)

        x_eval, product_ids_eval = [], []

        for i in range(len(features) - 14):
            x_eval.append(features[i:i+14])
            product_ids_eval.append(request.product_id)

        x_eval = np.array(x_eval)
        product_ids_eval = np.array(product_ids_eval)

        # Usamos siempre el último índice disponible para explicabilidad:
        idx = len(x_eval) - 1

        from services.explainability import compute_integrated_gradients_rnn
        explicacion_avanzada = explicar_prediccion_producto(
            idx, model, x_eval, product_ids_eval, compute_integrated_gradients_rnn
        )

        return {
            "status": "ok",
            "prediccion": result,
            "explicacion_simple": explicacion_simple,
            "explicacion_avanzada": explicacion_avanzada
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    
# --------- Endpoint: predicción para todos los productos ---------

@router.post("/all-products")
def predict_all(request: PredictAllRequest):
    try:
        resultados = predecir_en_lote(request.fecha_prediccion, DATA_FILE)
        return {
            "status": "ok",
            "fecha": request.fecha_prediccion,
            "predicciones": resultados
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
