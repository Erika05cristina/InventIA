from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from services.predictor import predecir_por_producto
from services.explainability import generar_explicacion
import pandas as pd

router = APIRouter()
DATA_FILE = "data/uploads/train.csv"

class PredictByProductRequest(BaseModel):
    product_id: int
    fecha_prediccion: str  # solo para devolver en respuesta

@router.post("/by-product")
def predict_by_product(request: PredictByProductRequest):
    try:
        # Predicción
        result = predecir_por_producto(DATA_FILE, request.product_id, request.fecha_prediccion)

        # Explicabilidad sencilla:
        df = pd.read_csv(DATA_FILE)
        promedio_historico = df[df["product_id"] == request.product_id]["sale_amount"].mean()
        explicacion = generar_explicacion(result["prediccion"], promedio_historico)

        return {
            "status": "ok",
            "prediccion": result,
            "explicacion": explicacion
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
