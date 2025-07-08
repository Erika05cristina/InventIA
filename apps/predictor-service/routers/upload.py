from fastapi import APIRouter, UploadFile, File, HTTPException
import os
import shutil

router = APIRouter()

@router.post("/")
async def upload_file(file: UploadFile = File(...)):
    if not file.filename.endswith(".csv"):
        raise HTTPException(status_code=400, detail="Solo se permiten archivos CSV")

    save_path = "data/uploads/train.csv"
    os.makedirs("data/uploads", exist_ok=True)
    
    with open(save_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    return {"message": "Archivo cargado con éxito", "file_path": save_path}
