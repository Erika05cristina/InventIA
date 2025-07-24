import requests
import re
import dateparser
from dateparser.search import search_dates


def extraer_fecha(pregunta: str) -> str | None:
    resultado = search_dates(pregunta, languages=["es"])
    if resultado:
        fecha = resultado[0][1]
        if fecha.year < 2025:
            fecha = fecha.replace(year=2025)
        return fecha.strftime("%Y-%m-%d")

    fecha_directa = dateparser.parse(pregunta, languages=["es"])
    if fecha_directa:
        if fecha_directa.year < 2025:
            fecha_directa = fecha_directa.replace(year=2025)
        return fecha_directa.strftime("%Y-%m-%d")

    return None


def obtener_productos() -> list[dict]:
    try:
        response = requests.get("http://localhost:8080/products/all")
        if response.status_code == 200:
            return response.json()
    except Exception as e:
        print(f"❗ Error al obtener productos: {e}")
    return []


def obtener_nombre_producto(product_id: int) -> str | None:
    try:
        url = f"http://localhost:8080/products/by-id?productId={product_id}"
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            return data.get("nombre", None)
    except Exception:
        pass
    return None


def buscar_product_id_por_nombre(pregunta: str) -> int | None:
    productos = obtener_productos()
    pregunta_lower = pregunta.lower()

    for producto in productos:
        nombre = producto["nombre"].lower()
        if nombre in pregunta_lower:
            return producto["id"]
    return None


def consultar_estadisticas(pregunta: str) -> dict:
    fecha_str = extraer_fecha(pregunta)

    if not fecha_str:
        return {
            "mensaje": "❗ No se detectó ninguna fecha en tu pregunta. Por favor indícala explícitamente, como 'ayer' o 'el 25 de julio'."
        }

    url = f"http://localhost:8080/dashboard/statistics?fecha={fecha_str}&tipo=grupo"

    try:
        response = requests.get(url)
        if response.status_code == 404:
            return {"mensaje": f"No se encontró ninguna predicción para la fecha {fecha_str}."}
        if response.status_code != 200:
            return {"mensaje": f"Ocurrió un error al consultar las estadísticas para {fecha_str} (código {response.status_code})."}

        data = response.json()

        # Agregar nombres
        id_mayor = data.get("productoMayorDemandaId")
        id_menor = data.get("productoMenorDemandaId")
        data["productoMayorDemandaNombre"] = obtener_nombre_producto(id_mayor) or f"Producto ID {id_mayor}"
        data["productoMenorDemandaNombre"] = obtener_nombre_producto(id_menor) or f"Producto ID {id_menor}"

        return data

    except requests.exceptions.RequestException as e:
        return {"mensaje": f"Error de conexión con el backend: {str(e)}"}


def consultar_explicacion(pregunta: str) -> dict:
    pregunta_lower = pregunta.lower()

    # Intentar detectar por nombre
    product_id = buscar_product_id_por_nombre(pregunta)

    # Intentar detectar por número si no se encontró por nombre
    if product_id is None:
        match = re.search(r"(producto\s*(número|nro|num|#)?\s*)?(\d{1,5})", pregunta_lower)
        if match:
            product_id = int(match.group(3))
        else:
            return {"mensaje": "❗ No se identificó el producto en tu pregunta. Puedes decir algo como 'producto 267' o el nombre exacto."}

    fecha_str = extraer_fecha(pregunta)
    if not fecha_str:
        return {"mensaje": "❗ No se encontró una fecha en tu pregunta. Ej: 'el 25 de julio'"}

    url = "http://localhost:8000/predict/by-product"
    payload = {
        "product_id": product_id,
        "fecha_prediccion": fecha_str
    }

    try:
        response = requests.post(url, json=payload)
        if response.status_code != 200:
            return {"mensaje": f"Error {response.status_code} al consultar la explicación."}

        data = response.json()

        # Eliminar imagen base64 si existe
        if "explicacion_avanzada" in data:
            data["explicacion_avanzada"].pop("grafica_explicabilidad_base64", None)

        # Reemplazar ID por nombre
        nombre = obtener_nombre_producto(product_id)
        if nombre:
            data["nombre_producto"] = nombre
            if "explicacion_simple" in data:
                data["explicacion_simple"] = data["explicacion_simple"].replace(f"producto {product_id}", nombre)

        return data

    except requests.exceptions.RequestException as e:
        return {"mensaje": f"Error de conexión al backend: {str(e)}"}
