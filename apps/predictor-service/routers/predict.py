from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from services.predictor import run_prediction
import os

router = APIRouter()

class PredictRequest(BaseModel):
    file_path: str  # Ej: "data/uploads/ventas_junio.csv"

@router.post("/run")
def predict(request: PredictRequest):
    if not os.path.exists(request.file_path):
        raise HTTPException(status_code=404, detail="Archivo no encontrado")
    result = run_prediction(request.file_path)
    return {"status": "ok", "predictions": result}
