# apps/predictor-service/agent/tools.py
import requests
import re
import dateparser
from dateparser.search import search_dates

def consultar_estadisticas(pregunta: str) -> dict:
    """
    Extrae una fecha desde la pregunta del usuario, la normaliza y consulta estadísticas.
    """
    resultado = search_dates(pregunta, languages=["es"])

    if not resultado:
        return {
            "mensaje": "❗ No se detectó ninguna fecha en tu pregunta. Por favor indícala explícitamente, como 'ayer' o 'el dia y mes'."
        }

    fecha_parseada = resultado[0][1]
    fecha_str = fecha_parseada.strftime("%Y-%m-%d")
    url = f"http://localhost:8080/dashboard/statistics?fecha={fecha_str}&tipo=grupo"

    try:
        response = requests.get(url)
        if response.status_code == 404:
            return {"mensaje": f"No se encontró ninguna predicción para la fecha {fecha_str}."}
        if response.status_code != 200:
            return {"mensaje": f"Ocurrió un error al consultar las estadísticas para {fecha_str} (código {response.status_code})."}
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"mensaje": f"Error de conexión con el backend: {str(e)}"}


def consultar_explicacion(pregunta: str) -> dict:
    """
    Extrae product_id y fecha desde la pregunta del usuario para consultar explicación.
    """
    # Detectar product_id
    producto_match = re.search(r"(producto\s*(número|nro|num|#)?\s*)?(\d{1,5})", pregunta, re.IGNORECASE)
    if not producto_match:
        return {"mensaje": "❗ No se encontró un producto en tu pregunta. Ej: 'producto 267'"}

    product_id = int(producto_match.group(3))

    # Detectar fecha
    resultado_fecha = search_dates(pregunta, languages=["es"])
    if not resultado_fecha:
        return {"mensaje": "❗ No se encontró una fecha en tu pregunta. Ej: 'el 25 de julio'"}

    fecha_str = resultado_fecha[0][1].strftime("%Y-%m-%d")
    url = f"http://localhost:8080/explain?fecha={fecha_str}&product_id={product_id}"

    try:
        response = requests.get(url)
        if response.status_code == 404:
            return {"mensaje": f"No hay explicación para el producto {product_id} en la fecha {fecha_str}."}
        if response.status_code != 200:
            return {"mensaje": f"Error {response.status_code} al consultar la explicación."}
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"mensaje": f"Error de conexión al backend: {str(e)}"}
