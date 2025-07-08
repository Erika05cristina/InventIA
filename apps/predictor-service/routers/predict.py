from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
import os

from services.predictor import (
    run_prediction,
    predecir_ventas,
    predecir_en_lote
)

router = APIRouter()
UPLOAD_FILE_PATH = "data/uploads/train.csv"

# --------- Modelos de entrada ---------

class PredictByProductRequest(BaseModel):
    product_id: int
    fecha: str  # Formato: YYYY-MM-DD

class PredictAllRequest(BaseModel):
    fecha: str

# --------- Endpoint: predicción general para pruebas ---------

@router.post("/run")
def predict():
    if not os.path.exists(UPLOAD_FILE_PATH):
        raise HTTPException(status_code=404, detail=" Archivo de datos no encontrado")

    result = run_prediction(UPLOAD_FILE_PATH)
    return {"status": "ok", "predictions": result}

# --------- Endpoint: predicción por producto ---------

@router.post("/by-product")
def predict_by_product(request: PredictByProductRequest):
    if not os.path.exists(UPLOAD_FILE_PATH):
        raise HTTPException(status_code=404, detail="Archivo de datos no encontrado")

    try:
        pred = predecir_ventas(request.product_id, request.fecha, UPLOAD_FILE_PATH)
        return {
            "status": "ok",
            "product_id": request.product_id,
            "fecha": request.fecha,
            "prediccion": float(pred)
        }
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

# --------- Endpoint: predicción para todos los productos ---------

@router.post("/all-products")
def predict_all(request: PredictAllRequest):
    if not os.path.exists(UPLOAD_FILE_PATH):
        raise HTTPException(status_code=404, detail="Archivo de datos no encontrado")

    try:
        df_resultados = predecir_en_lote(request.fecha, UPLOAD_FILE_PATH)
        return {
            "status": "ok",
            "fecha": request.fecha,
            "predicciones": df_resultados
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
