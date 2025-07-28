from fastapi import FastAPI
from routers import predict, upload, agent
from middleware import middleware
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(
    title="Servicio de Predicción",
    version="0.1.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Luego cualquier middleware adicional
app.add_middleware(middleware.MultipartRequestMiddleware, max_upload_size_mb=200)

# Incluir rutas
app.include_router(predict.router, prefix="/predict", tags=["Predicción"])
app.include_router(upload.router, prefix="/upload", tags=["Carga de Datos"])
app.include_router(agent.router, prefix="/agent", tags=["Agente LLM"])
