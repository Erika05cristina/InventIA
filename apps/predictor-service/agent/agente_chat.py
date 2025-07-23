import os
from dotenv import load_dotenv

from langchain.chat_models import ChatOpenAI
from langchain.agents import Tool, initialize_agent
from langchain.agents.agent_types import AgentType
from .memory import get_memoria_para, print_historial  # 👈 importa print_historial
from .tools import consultar_estadisticas, consultar_explicacion
from .memory import get_memoria_para

load_dotenv()

llm = ChatOpenAI(
    temperature=0,
    model="gpt-4",
    openai_api_key=os.getenv("OPENAI_API_KEY")
)

tools = [
    Tool(
        name="estadisticas_por_fecha",
        func=consultar_estadisticas,
        description="Devuelve estadísticas de stock para una fecha en formato YYYY-MM-DD"
    ),
    Tool(
        name="explicacion_por_producto",
        func=consultar_explicacion,
        description= "Devuelve una explicación avanzada para una predicción."
        " Usa esta herramienta si el usuario pregunta 'por qué', 'explica', 'razón' o similares."
        " Necesita los campos product_id y fecha, que pueden deducirse del contexto si el usuario ya preguntó por la demanda o el promedio de stock."
   
    )
]

def responder_chat(mensaje: str, user_id: str) -> str:
    memoria = get_memoria_para(user_id)

    agent = initialize_agent(
        tools=tools,
        llm=llm,
        agent=AgentType.OPENAI_FUNCTIONS,
        memory=memoria,
        verbose=True
    )
    print_historial(user_id)
    return agent.run(mensaje)
