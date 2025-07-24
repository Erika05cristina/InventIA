# apps/predictor-service/routers/agent.py
from fastapi import APIRouter, Query
from agent.agente_llm import responder_pregunta
from pydantic import BaseModel
from agent.agente_chat import responder_chat

router = APIRouter()
class ChatRequest(BaseModel):
    user_id: str
    pregunta: str

@router.post("/chat")
def chat_agente(req: ChatRequest):
    try:
        respuesta = responder_chat(req.pregunta, req.user_id)
        return {"respuesta": respuesta}
    except Exception as e:
        return {"error": str(e)}


@router.get("/ask")
def preguntar_al_agente(question: str = Query(..., description="Pregunta sobre predicciones")):
    """
    Endpoint para interactuar con el agente LLM.
    """
    try:
        respuesta = responder_pregunta(question)
        return {"respuesta": respuesta}
    except Exception as e:
        return {"error": str(e)}
