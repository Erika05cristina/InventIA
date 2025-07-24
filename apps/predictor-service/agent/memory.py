# agent/memory.py
from langchain.memory import ConversationBufferMemory

_memorias_por_usuario = {}

def get_memoria_para(user_id: str):
    if user_id not in _memorias_por_usuario:
        _memorias_por_usuario[user_id] = ConversationBufferMemory(
            memory_key="chat_history",
            return_messages=True
        )
    return _memorias_por_usuario[user_id]

def print_historial(user_id: str):
    mem = _memorias_por_usuario.get(user_id)
    if mem:
        print(f"\n🧠 Historial de {user_id}:")
        for m in mem.chat_memory.messages:
            print(f"  - {m.type}: {m.content}")
        print("-" * 40)
    else:
        print(f"(Sin historial para usuario {user_id})")
