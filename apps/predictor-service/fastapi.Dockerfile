FROM python:3.11-slim
WORKDIR /app
COPY ./requirements.txt .
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

COPY . .
# Comando para ejecutar la app en producción
# Lanza Gunicorn, que a su vez usa workers de Uvicorn.
# Asume que tu instancia de FastAPI se llama 'app' en el archivo 'main.py' dentro de la carpeta 'app'
CMD ["gunicorn", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "main:app", "-b", "0.0.0.0:8000"]
