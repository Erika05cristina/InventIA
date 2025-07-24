# apps/predictor-service/agent/agente_llm.py
import os
from dotenv import load_dotenv
from langchain.chat_models import ChatOpenAI
from langchain.agents import Tool, initialize_agent
from langchain.agents.agent_types import AgentType
from .tools import consultar_estadisticas

load_dotenv()

llm = ChatOpenAI(
    temperature=0,
    model="gpt-4",  # o gpt-3.5-turbo
    openai_api_key=os.getenv("OPENAI_API_KEY")
)

tools = [
    Tool(
        name="estadisticas_por_fecha",  # ✅ nombre válido
        func=consultar_estadisticas,
        description="Devuelve estadísticas de stock para una fecha en formato YYYY-MM-DD"
    )
]


agent = initialize_agent(
    tools=tools,
    llm=llm,
    agent=AgentType.OPENAI_FUNCTIONS,
    verbose=True
)

def responder_pregunta(pregunta: str) -> str:
    return agent.run(pregunta)
