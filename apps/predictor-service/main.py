from fastapi import FastAPI
from routers import predict, upload  # <- asegúrate de importar upload
from middleware import middleware

app = FastAPI(
    title="Servicio de Predicción",
    version="0.1.0"
)

app.include_router(predict.router, prefix="/predict", tags=["Predicción"])
app.add_middleware(middleware.MultipartRequestMiddleware, max_upload_size_mb=200)
app.include_router(upload.router, prefix="/upload", tags=["Carga de Datos"])  # <- Asegúrate de incluirlo
