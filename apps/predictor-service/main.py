from fastapi import FastAPI
from routers import predict

app = FastAPI(title="Servicio de Predicción")

app.include_router(predict.router, prefix="/predict", tags=["Predicción"])
