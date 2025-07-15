from fastapi import APIRouter, UploadFile, File, HTTPException, BackgroundTasks
import os
import shutil
from services.trainer import entrenar_modelo_global

router = APIRouter()
UPLOAD_DIR = "data/uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

@router.post("/")
async def upload_file(file: UploadFile = File(...), background_tasks: BackgroundTasks = None):
    if not file.filename.endswith(".csv"):
        raise HTTPException(status_code=400, detail="Solo se permiten archivos CSV")

    save_path = os.path.join(UPLOAD_DIR, "train.csv")
    with open(save_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    background_tasks.add_task(entrenar_modelo_global, save_path)

    return {
        "message": "Archivo cargado correctamente. Entrenamiento en segundo plano.",
        "file_path": save_path
    }
